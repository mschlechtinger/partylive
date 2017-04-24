'use strict';

const express = require('express');
const mongoose = require('mongoose');
const router = express.Router();
const authenticationCheck = require('../../authentication/authenticationCheck');
const admin = require('../../firebase/firebaseConnection')
const EventSchema = require('../../models/event');
const Account = require('../../models/account');
const fileHandler = require('../../files/fileHandler');

//handle request for path myserver/events/
router.get('/', authenticationCheck, function(req, res) {

	const maxGuestImgs = 4;
	//see the events that start in the past eight hours and in the future
	var visibleFromDate = new Date();
	visibleFromDate.setTime(visibleFromDate.getTime() - (8*60*60*1000));

	EventSchema.find({
		$and:[//is the user allowed to see the event AND is the event relevant?
			{
				$or:[
				{
					'organizer._id': req.user.id		//user is organizer
				},{
					'guests.guestId': req.user.id	//user is invited
				},{
					'publicEvent': true					//event is public
				}]
			},
			{
				'startDate': {$gte: visibleFromDate	}
			}]
		}, function (err, events) {
			if(err) return res.status(500).json({error: 'Database error'});
			//format the events for frontend
			var outputEvents = [];

			for (var i = events.length - 1; i >= 0; i--) {
				var outputEvent = {};
				var currentEvent = events[i];
				//calculate how many guests accepted the meeting
				var guests = currentEvent.guests.filter(function(event){
					return event.status === 'Accepted';
				});
				outputEvent.guestCount = guests.length;
				//TODO try to show most relevant users for user
				outputEvent.guestImgs = [];
				for (var j = guests.length - 1; j >= 0; j--) {
					if(guests[j].imgUrl){
						var imgUrl = fileHandler.getFileUrl(guests[j].imgUrl, req.user.id, "jpg");
						if(outputEvent.guestImgs.length < maxGuestImgs && imgUrl){
							outputEvent.guestImgs.push(imgUrl);
						}	
					}
				}

				outputEvent._id = currentEvent.id;
				outputEvent.title = currentEvent.title;
				outputEvent.publicEvent = currentEvent.publicEvent;
				outputEvent.location = currentEvent.location;
				outputEvent.imgUrl = fileHandler.getFileUrl(outputEvent.imgUrl, req.user.id,"jpg");
				outputEvent.organizer = currentEvent.organizer;
				outputEvent.organizer.imgUrl = fileHandler.getFileUrl(outputEvent.organizer.imgUrl, req.user.id,"jpg");
				outputEvents.push(outputEvent);
			}
	    	res.status(200).json(outputEvents);
	});
  });

router.post('/', authenticationCheck, fileHandler.uploadImage, function(req, res) {
	console.log("in der Post Methode!")
	var body = req.body;
	//TODO insert proper input validation here
	if (!body.title || !body.startDate || !body.location ) {
		return res.status(422).json({error:'Input validation failed'});
	}
	var guests = [];
	var guestIds = [];
	var deviceIds = [];

	
	if(body.guests){
		for (var i = body.guests.length - 1; i >= 0; i--) {
			var guest = body.guests[i];
			if(guest._id){
				guestIds.push( new mongoose.Types.ObjectId( guest._id ) );
			}
		}
	}

	Account.find({_id: {$in: guestIds}}, '_id username name imgUrl deviceId', function(err, accounts){
		if(err) return res.status(500).json({error:err.message});

		for (var i = accounts.length - 1; i >= 0; i--) {
			var account = accounts[i];
			var guest = {};

			if(account.name) {
				guest.name = account.name;
			}else{
				guest.name = account.username;
			}
			guest.imgUrl = account.imgUrl;
			guest.status = "Accepted";
			
			if(account.deviceId) {
				deviceIds.push(account.deviceId); // *** for firebase-Push-Notifications
			}

			guest.guestId = account._id;
			guests.push(guest);
		}
		
		var newEvent = new EventSchema({ 	
			title: body.title,
			startDate: body.startDate,
			location: body.location,
			description: body.description,
			imgUrl: req.image,
			organizer: {_id: req.user.id, name: req.user.name, imgUrl:  req.user.imgUrl},
			publicEvent: body.publicEvent,
			guests: guests,
			bringItems: body.bringItems
		});

		newEvent.save(function(err, createdEvent){
			if(err) {
				console.log(err);
				return res.status(500).json({error: err.message});
			}
			// send push Notifications to all invited clients
			for (var i = 0; i <= deviceIds.length - 1; i++) {
				pushNewEvent(deviceIds[i], req.user.name, body.title, body.startDate);
			}
			console.log("Ende der POST methode!")
			res.status(201).json({eventId:createdEvent.id});
		});
	});
});
//exports the router as a node module
module.exports = router;


//*** send Push-Notifications via firebase Notification-Service
function pushNewEvent(deviceId, organizer, eventName, startDate) {
	var payload = {
			"notification" : {
				"title" : organizer + " lädt dich zu seiner Party ein!",
				"body" : eventName + " findet " + startDate + " statt. Gib bescheid, ob du teilnimmst."
			}, 
	};
	admin.messaging().sendToDevice(deviceID, payload)
	.then(function(response) {
		console.log("Successfully sent message:\n", response);
	})
	.catch(function(error) {
		console.log("Error sending message:", error);
	});	
};
