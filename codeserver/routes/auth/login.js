'use strict';

const passport = require('passport');
const express = require('express');
const router = express.Router();
const Account = require('../../models/account');

//handle request for path myserver/auth/login
router.post('/', passport.authenticate('local'), function(req, res){
	if (req.isAuthenticated()){
		
		var oldDeviceId;
		//*** save the current deviceId for firebase-PushNotifications
		Account.findById(req.user.id, function(err, acc){
			oldDeviceId = acc.deviceId;
			console.log("oldDeviceId: " + oldDeviceId);
			if (err) { res.status(500).json({error:err.message}); return; };
			
			if (oldDeviceId == req.body.deviceId) { } else {
				Account.update({_id : req.user.id}, {$set : {"deviceId" : req.body.deviceId}}, function(err, result) {
				    if (err) { 
				    	res.status(500).json({error:err.message}); 
				    	return; 
				    };
				    if (result) {
				    	console.log("DeviceId changed!")
				    	// everything worked, deviceId changed
				    }
				});
			};
		});
		//*** end
		
		console.log('User is logged in: \n'+ req.user.name);
		res.status(200).json({"status":"User logged in", "userId": req.user.id});
	} else {
		console.log('No User is logged in');
		res.status(401).json({error: 'User Login failed'});
	}
});

//exports the router as a node module
module.exports = router;