'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../../authentication/authenticationCheck');
const EventModel = require('../../../models/event');
const fileHandler = require('../../../files/fileHandler');

//distribute request for paths myserver/events/:eventId /guests and /bringItems
router.use('/bringItems', require('./bringItems'));

//handle request for path myserver/events/:eventId
router.get('/', authenticationCheck, function(req, res) {
	EventModel.findById(req.params.eventId, function (err,event) {
		if(err) return res.status(500).json(err);
		if(!event) return res.status(404).json({error:"Event not found"});

		var outputEvent = {};

		var guests = event.guests;
		outputEvent.guestCount = guests.length;

		if(guests){
			outputEvent.guests = [];

			for (var j = guests.length - 1; j >= 0; j--) {
				var guest = guests[j];
				if(guest.imgUrl){
					guest.imgUrl = fileHandler.getFileUrl(guest.imgUrl, req.user.id, "jpg");	
				}
				outputEvent.guests.push(guest);
			}
		}	

		outputEvent._id = event.id;
		outputEvent.title = event.title;
		outputEvent.description = event.description;
		outputEvent.location = event.location;
		outputEvent.bringItems = event.bringItems;
		outputEvent.publicEvent = event.publicEvent;
		outputEvent.startDate = event.startDate;
		outputEvent.participationStatus = event.getParticipationStatus(req.user.id);
		outputEvent.imgUrl = fileHandler.getFileUrl(event.imgUrl, req.user.id,"jpg");
		outputEvent.organizer = event.organizer;
		outputEvent.organizer.imgUrl = fileHandler.getFileUrl(event.organizer.imgUrl, req.user.id,"jpg");

    	res.status(200).json(outputEvent);
	});
});

router.delete('/', authenticationCheck, function (req, res) {
    EventModel.findById(req.params.eventId, function (err,event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    if(event.organizer._id.toString() !== req.user.id) return res.status(403).json({error: 'User is not the organizer of the event'});
	    
	    event.remove();
	    res.status(204).send();
  	});
});

router.put('/', authenticationCheck, fileHandler.uploadImage, function(req, res){
	EventModel.findById(req.params.eventId, function(err, event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    if(event.organizer._id.toString() !== req.user.id) return res.status(403).json({error: 'User is not the organizer of the event'});
	    
	    //TODO add input validation

	    event.title = req.body.title;
	    event.startDate = req.body.startDate;
	    event.description = req.body.description;
	    event.imgUrl = req.image;
	    event.publicEvent = req.body.publicEvent;
	    event.location = req.body.location;
	    event.bringItems = req.body.bringItems;
	    
	    //handle new guestids from String input
	    var guests = [];
	    for (var i = req.body.guests.length - 1; i >= 0; i--) {
	    	var guest = req.body.guests[i];
	    	//TODO parse from account document
	    	if(guest.constructor === String) guest = {guestId: guest, status: 0};

	    	guests.push(guest);
	    }
	    event.guests = guests;

	    event.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});
router.put('/image', authenticationCheck, fileHandler.uploadFile, function(req, res){
	if(!req.uploadedFileId) return res.status(500).json({error: "Error while uploading file"});
	
	EventModel.findById(req.params.eventId, function(err, event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    //if(event.organizer._id.toString() !== req.user.id) return res.status(403).json({error: 'Cannot change image of events of other users.'});
	    
	    //TODO add input validation
	   
	   	event.imgUrl = req.uploadedFileId;

	    event.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});

router.put('/participationStatus', authenticationCheck, function(req, res) {
		EventModel.findById(req.params.eventId, function(err, event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    
	    event.setParticipationStatus(req.user, req.body.participationStatus);

	    event.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});

//exports the router as a node module
module.exports = router;