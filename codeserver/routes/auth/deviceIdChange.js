'use strict';

const passport = require('passport');
const express = require('express');
const router = express.Router();
const Account = require('../../models/account');
const authenticationCheck = require('../../authentication/authenticationCheck');

// handle request for path myserver/auth/deviceIdChange
router.post('/', authenticationCheck, function(req, res){
		
	//*** save the current deviceId for firebase-PushNotifications
	Account.findById(req.body.userId, function(err, acc){
		if (err) { res.status(500).json({error:err.message}); return; };
		
		Account.update({_id : req.body.userId}, {$set : {"deviceId" : req.body.deviceId}}, function(err, result) {
		    if (err) { 
		    	res.status(500).json({error:err.message}); 
		    	return; 
		    };
			if (result) {
			   	console.log("DeviceId changed!") // everything worked, deviceId changed
			   	res.status(200).json({"status":"User logged in", "userId": req.user.id, "info" : "deviceId changed successfully"});
			} else {
				console.log('deviceId did not change');
				res.status(401).json({error: 'deviceId change failed.'});
			}
		});
	});
});

module.exports = router;