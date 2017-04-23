'use strict';

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const EventSchema = new Schema({
	title: String,
	description: String,
	imgUrl: String,
	organizer: {_id: Schema.Types.ObjectId, name: String, imgUrl: String },
	location: {latitude: Number, longitude: Number},
	publicEvent: Boolean,
	guests: [ { guestId: Schema.Types.ObjectId, status: String, name: String, imgUrl: String } ],
	guestCount: { type: Number, min: 0},
	startDate: Date,
	bringItems: [ {
					title: String, 
					quantity: {type: Number,min: 1}, 
					remaining: {type: Number,min: 0}, 
					assignees: [ { guestId: Schema.Types.ObjectId, amount: Number}] 
				}]
	},
	{ strict: true });

//calculate remaining slots for bringItems
EventSchema.pre('validate', function (next) {
	var bringItems = this.bringItems;
    for (var i = bringItems.length - 1; i >= 0; i--) {
    	if(bringItems[i].quantity){
    		var assignees = bringItems[i].assignees;

    		var remaining = bringItems[i].quantity;
    		for(var j = assignees.length - 1; j >= 0; j--){
    			remaining -= assignees[j].amount;
    		}
    		this.bringItems[i].remaining = remaining;
    	}	
    }
    next();
});

//check if a user is either guest or organizer
EventSchema.methods.isGuest = function(userId) {
	return this.organizer._id.toString() === userId || !!this.guests.filter(function(guest){return guest.guestId.toString() === userId;})[0];
};


module.exports = mongoose.model('Event', EventSchema);