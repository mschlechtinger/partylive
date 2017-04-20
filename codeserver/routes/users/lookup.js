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

		var response = user;
		response.imgUrl = fileHandler.getFileUrl(user.imgUrl, req.params.userId, "jpg");

    	res.status(200).json(response);
	});
});




//exports the router as a node module
module.exports = router;