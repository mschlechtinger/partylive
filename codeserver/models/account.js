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
		get: name => name.givenName + " " + name.middleName + " " + name.familyName
		},
	imgUrl: String,
	events: [Schema.Types.ObjectId],
	lastLoginDate: {type: Date, default: Date.now}
}, { strict: true });

Account.plugin(passportLocalMongoose, {
	userNameField: 'email'
});

module.exports = mongoose.model('Account', Account);