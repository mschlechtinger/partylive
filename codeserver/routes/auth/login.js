'use strict';

const passport = require('passport');
const express = require('express');
const router = express.Router();
const authenticationCheck = require('../../authentication/authenticationCheck');

//handle request for path myserver/auth/login
router.post('/', passport.authenticate('local'), function(req, res){
	if (req.isAuthenticated()){
		console.log('User is logged in: \n'+ req.user);
		res.status(200).json({"status":"OK, " + req.user});
	} else {
		console.log('No User is logged in');
		res.status(401).json({error: 'User Login failed'});
	}
});

//exports the router as a node module
module.exports = router;