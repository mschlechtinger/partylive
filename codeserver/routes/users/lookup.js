'use strict';

const express = require('express');
const router = express.Router({mergeParams: true});
const authenticationCheck = require('../../authentication/authenticationCheck');
const UserModel = require('../../models/account');
const fileHandler =require('../../files/fileHandler');

//handle request for path myserver/users/lookup
router.post('/', authenticationCheck, function(req, res) {
	UserModel.find({username: req.body.username},'_id name imgUrl', function (err, user) {
		if(err) return res.status(500).json(err);
		if(!user[0]) return res.status(404).json({error: "No user found with that name"});

		var response = user[0];
		response.imgUrl = fileHandler.getFileUrl(response.imgUrl, req.user.id, "jpg");

    	res.status(200).json(response);
	});
});


//exports the router as a node module
module.exports = router;