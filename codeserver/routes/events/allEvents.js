'use strict';

const express = require('express');
const router = express.Router();
const authenticationCheck = require('../../authentication/authenticationCheck');
const EventSchema = require('../../models/event');

//handle request for path myserver/events/
router.get('/', authenticationCheck, function(req, res) {
	//see the events that start in the past eight hours and in the future
	var visibleFromDate = new Date();
	visibleFromDate.setTime(visibleFromDate.getTime() - (8*60*60*1000));
	console.log(visibleFromDate);
	EventSchema.find({
		$and:[//is the user allowed to see the event AND is the event relevant?
			{
				$or:[{
					//user is organizer
					'organizer': req.user.id
				},{
					//user is invited
					'guests.guestId': req.user.id
				},{
					//event is public
					'public': true
				}]
			},{
				'startDate': {$gte: visibleFromDate	}
			}]
		}, function (err,events) {
			if(err) return res.status(500).json({error: 'Database error'});
	    	res.status(200).json(events);
	});
  });

router.post('/', authenticationCheck, function(req, res) {
	var body = req.body;
	//insert proper input validation here
	if (!body.title || !body.startDate || !body.location ) {
		return res.status(422).json({error:'Input validation failed'});
	}
	var guests = [];
	for (var i = body.guests.length - 1; i >= 0; i--) {
		var guest = { guestId: body.guests[i], status: 'Not Invited'};
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
			return res.status(500).json({error:'Could not save to database'});
		}
		res.status(201).json(createdEvent);
	});

});
//exports the router as a node module
module.exports = router;