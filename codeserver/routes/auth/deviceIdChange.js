'use strict';

const passport = require('passport');
const express = require('express');
const router = express.Router();
const Account = require('../../models/account');

// handle request for path myserver/auth/deviceIdChange
router.post('/', passport.authenticate('local'), function(req, res){
	if (req.isAuthenticated()){
		
		//*** save the current deviceId for firebase-PushNotifications
		Account.findById(req.user.id, function(err, acc){
			if (err) { res.status(500).json({error:err.message}); return; };
			
			Account.update({_id : req.user.id}, {$set : {"deviceId" : req.body.deviceId}}, function(err, result) {
			    if (err) { 
			    	res.status(500).json({error:err.message}); 
			    	return; 
			    };
			    if (result) {
			    	console.log("DeviceId changed!") // everything worked, deviceId changed
			    }
			});
		});
		//*** end
		res.status(200).json({"status":"User logged in", "userId": req.user.id, "info" : "deviceId changed successfully"});
	} else {
		console.log('deviceId did not change');
		res.status(401).json({error: 'deviceId change failed.'});
	}
});

module.exports = router;