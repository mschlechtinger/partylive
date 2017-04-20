'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../authentication/authenticationCheck');
const UserModel = require('../../models/account');
const fileHandler =require('../../files/fileHandler');
//distribute request for paths myserver/users/:eventId /guests and /bringItems
//router.use('/guests', require('./guests'));
//router.use('/bringItems', require('./bringItems'));

//handle request for path myserver/users/:eventId
router.get('/', authenticationCheck, function(req, res) {
	UserModel.findById(req.params.userId,'_id name imgUrl', function (err, user) {
		if(err) return res.status(500).json(err);

		var response = user;
		response.imgUrl = fileHandler.getFileUrl(user.imgUrl, req.params.userId, "jpg");

    	res.status(200).json(response);
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

router.put('/image',authenticationCheck, fileHandler.uploadFile, function(req, res){
	if(!req.uploadedFileId) return res.status(500).json({error: "Error while uploading file"});
	if(req.params.userId !== req.user.id) return res.status(403).json({error: 'Cannot change other users.'});
	
	UserModel.findById(req.params.userId, function(err, user){
	    if(err) return res.status(500).json(err);
	    if(!user) return res.status(404).json({error: 'User not found'});
	    if(user.id.toString() !== req.user.id) return res.status(403).json({error: 'Cannot change other users.'});
	    
	    //TODO add input validation
	   
	   	user.imgUrl = req.uploadedFileId;

	    user.save(function(err){
	    	if(err) return res.status(500).json(err);

	    	res.status(204).send();
	    });
	});
});
//exports the router as a node module
module.exports = router;