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
//check user status
EventSchema.methods.getParticipationStatus = function(userId) {
	// 1= participates 0=unknown -1=not participating
	if(this.isGuest(userId)){
		if(this.organizer._id.toString() === userId){
			return 1;
		}
		return this.guests.filter(function(guest){return guest.guestId.toString() === userId;})[0].status;
	}
	return 0;
};

EventSchema.methods.setParticipationStatus = function(user, participationStatus){
	if(this.isGuest(user.id)){

		var guest = this.guests.filter(function(guest){return guest.guestId.toString() === user.id;})[0];

		if(!guest){
			//he is organizer
			return;
		}else{
			var guestIndex = this.guests.indexOf(guest);
			this.guests[guestIndex].status = participationStatus;
			return;
		}
	}else{
		event.guests.push({guestId: user.id, status: participationStatus, imgUrl: user.imgUrl, name: user.name, username: user.username });
		return;
	}
};


module.exports = mongoose.model('Event', EventSchema);