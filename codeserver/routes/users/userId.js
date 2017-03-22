'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../authentication/authenticationCheck');
const UserModel = require('../../models/account');

//distribute request for paths myserver/events/:eventId /guests and /bringItems
//router.use('/guests', require('./guests'));
//router.use('/bringItems', require('./bringItems'));

//handle request for path myserver/events/:eventId
router.get('/', authenticationCheck, function(req, res) {
	UserModel.findById(req.params.userId,'_id name imgUrl', function (err, user) {
		if(err) return res.status(500).json(err);

    	res.status(200).json(user);
	});
});

router.put('/', authenticationCheck, function(req, res){
	    if(req.params.userId !== req.user.id) return res.status(403).json({error: 'Cannot change other users.'});
	
	UserModel.findById(req.params.userId, function(err, user){
	    if(err) return res.status(500).json(err);
	    if(!user) return res.status(404).json({error: 'User not found'});
	    if(user.id.toString() !== req.user.id) return res.status(403).json({error: 'Cannot change other users.'});
	    
	    //TODO add input validation
	   
	    user.name = {"familyName":req.body.name.familyName, "middleName": req.body.name.middleName, "givenName":req.body.name.givenName };

	   	user.imgUrl = req.body.imgUrl;

	    user.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});
//exports the router as a node module
module.exports = router;