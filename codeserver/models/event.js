'use strict';

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const EventSchema = new Schema({
	title: String,
	description: String,
	imgUrl: String,
	organizer: Schema.Types.ObjectId,
	location: {latitude: Number, longitude: Number},
	public: Boolean,
	guests: [ { guestId: Schema.Types.ObjectId, status: String, name: String, imgUrl: String } ],
	guestCount: { type: Number, min: 0},
	startDate: Date,
	bringItems: [ {title: String, quantity: {type: Number,min: 1}, remaining: {type: Number,min: 0}, assignees: [Schema.Types.ObjectId]} ]
}, { strict: true });

//calculate remaining slots for bringItems
EventSchema.pre('validate', function (next) {
    for (var i = this.bringItems.length - 1; i >= 0; i--) {
    	if(this.bringItems[i].quantity){
    		this.bringItems[i].remaining = this.bringItems[i].quantity - this.bringItems[i].assignees.length;
    	}	
    }
    next();
});


module.exports = mongoose.model('Event', EventSchema);