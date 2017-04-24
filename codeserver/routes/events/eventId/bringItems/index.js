'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../../../authentication/authenticationCheck');
const EventModel = require('../../../../models/event');

//handle Requests for myserver/events/:eventId/bringItems/:itemId
router.put('/:itemId', authenticationCheck, function(req, res) {

	EventModel.findById(req.params.eventId, function(err, event){
	    if(err) return res.status(500).json(err);
	    if(!event) return res.status(404).json({error: 'Event not found'});
	    //if(event.organizer._id.toString() !== req.user.id || user in guests) return res.status(403).json({error: 'Cannot bring items to events you are not a guest of.'});

	    //make the user bring items
	    var bringItem = event.bringItems.filter(function(item){return item._id.toString() === req.params.itemId;})[0];

	    if(!bringItem) return res.status(404).json({error:"bring item does not exist"});

	    var itemIndex = event.bringItems.indexOf(bringItem);

	    var assignees = event.bringItems[itemIndex].assignees;
	    
	    var assignedUser = assignees.filter(function(assignee){return assignee.guestId.toString() === req.user.id;})[0];
	    if(!assignedUser){
	    	assignees.push({guestId: req.user.id, amount: req.body.amount});
	    }else{
	    	var assignedUserIndex = assignees.indexOf(assignedUser);
	    	assignees[assignedUserIndex].amount = req.body.amount;
	    }


	    event.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(200).json({status:"Put successful",remaining: event.bringItems[itemIndex].remaining});
	    });
	});
});

//exports the router as a node module
module.exports = router;