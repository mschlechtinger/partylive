'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../../../authentication/authenticationCheck');
const EventModel = require('../../../../models/event');

//handle Requests for myserver/events/:eventId/bringItems/:itemId
router.post('/:itemId', authenticationCheck, function(req, res) {

	EventModel.findById(req.params.eventId, function(err, event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    //if(event.organizer._id.toString() !== req.user.id || user in guests) return res.status(403).json({error: 'Cannot bring items to events you are not a guest of.'});

	    //make the user bring items
	    var bringItem = event.bringItems.filter(function(item){return item._id.toString() === req.params.itemId;})[0];

	    if(!bringItem) return res.status(404).json({error:"bring item does not exist"});

	    var itemIndex = event.bringItems.indexOf(bringItem);

	    event.bringItems[itemIndex].assignees.push({guestId: req.user.id, amount: req.body.amount});
	    event.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});

//exports the router as a node module
module.exports = router;