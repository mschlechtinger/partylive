'use strict';

const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const passportLocalMongoose = require('passport-local-mongoose');

const nameSchema = new Schema({
		familyName: String,
		givenName: String,
		middleName: String
		});

const Account = new Schema({
	name: {
		type: nameSchema,
		get: function(name){
			var nameString = "";
			if(name.givenName){
				nameString += name.givenName + " ";
			}
			if(name.middleName){
				nameString += name.middleName + " ";
			}
			if(name.familyName){
				nameString += name.familyName;
			}
			return nameString;
		}
		},
	imgUrl: String,
	lastLoginDate: {type: Date, default: Date.now}
}, { strict: true });

Account.plugin(passportLocalMongoose, {
	userNameField: 'email'
});

module.exports = mongoose.model('Account', Account);