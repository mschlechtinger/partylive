'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../authentication/authenticationCheck');
const EventModel = require('../../models/event');

//distribute request for paths myserver/events/:eventId /guests and /bringItems
//router.use('/guests', require('./guests'));
//router.use('/bringItems', require('./bringItems'));

//handle request for path myserver/events/:eventId
router.get('/', authenticationCheck, function(req, res) {
	EventModel.findById(req.params.eventId, function (err,event) {
		if(err) return res.status(500).json(err);

    	res.status(200).json(event);
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

router.put('/', authenticationCheck, function(req, res){
	EventModel.findById(req.params.eventId, function(err, event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    if(event.organizer._id.toString() !== req.user.id) return res.status(403).json({error: 'User is not the organizer of the event'});
	    
	    //TODO add input validation

	    event.title = req.body.title;
	    event.startDate = req.body.startDate;
	    event.description = req.body.description;
	    event.imgUrl = req.body.imgUrl;
	    event.publicEvent = req.body.publicEvent;
	    event.location = req.body.location;
	    event.bringItems = req.body.bringItems;
	    
	    //handle new guestids from String input
	    var guests = [];
	    for (var i = req.body.guests.length - 1; i >= 0; i--) {
	    	var guest = req.body.guests[i];
	    	//TODO parse from account document
	    	if(guest.constructor === String) guest = {guestId: guest, status: 'Not Invited'};

	    	guests.push(guest);
	    }
	    event.guests = guests;

	    event.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});
//exports the router as a node module
module.exports = router;