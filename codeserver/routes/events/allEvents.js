'use strict';

const express = require('express');
const mongoose = require('mongoose');
const router = express.Router();
const authenticationCheck = require('../../authentication/authenticationCheck');
const EventSchema = require('../../models/event');
const Account = require('../../models/account');

//handle request for path myserver/events/
router.get('/', authenticationCheck, function(req, res) {
	//see the events that start in the past eight hours and in the future
	var visibleFromDate = new Date();
	visibleFromDate.setTime(visibleFromDate.getTime() - (8*60*60*1000));

	EventSchema.find({
		$and:[//is the user allowed to see the event AND is the event relevant?
			{
				$or:[
				{
					'organizer': req.user.id		//user is organizer
				},{
					'guests.guestId': req.user.id	//user is invited
				},{
					'public': true					//event is public
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
				var currentEvent = events[i];
				//calculate how many guests accepted the meeting
				currentEvent.guests = currentEvent.guests.filter(function(event){
					return event.status === 'Accepted';
				});
				currentEvent.guestCount = currentEvent.guests.length;
				//TODO try to show most relevant users for user
				currentEvent.guests = currentEvent.guests.slice(0,5);

			outputEvents.push(currentEvent);
			}
	    	res.status(200).json(outputEvents);
	});
  });

router.post('/', authenticationCheck, function(req, res) {
	var body = req.body;
	//TODO insert proper input validation here
	if (!body.title || !body.startDate || !body.location ) {
		return res.status(422).json({error:'Input validation failed'});
	}
	var guests = [];
	var guestIds = [];

	for (var i = body.guests.length - 1; i >= 0; i--) {
		guestIds.push( new mongoose.Types.ObjectId( body.guests[i] ) );
	}

	Account.find({_id: {$in: guestIds}}, 'username name imgUrl', function(err, accounts){
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

			guests.push(guest);
		}
		
		var newEvent = new EventSchema({ 	
			title: body.title,
			startDate: body.startDate,
			location: body.location,
			description: body.description,
			imgUrl: body.imgUrl,
			organizer: req.user.id,
			public: body.public,
			guests: guests,
			bringItems: body.bringItems
		});

		newEvent.save(function(err, createdEvent){
			if(err) {
				console.log(err);
				return res.status(500).json({error:err.message});
			}
			res.status(201).json(createdEvent);
		});
	});
});
//exports the router as a node module
module.exports = router;