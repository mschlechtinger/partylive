'use strict';

const NodeRSA = require('node-rsa');
const config = require('../config');
const request = require('request');
const fs = require('fs');
const uuidV4 = require('uuid/v4');

var key = new NodeRSA();
key.importKey(config.filePrivKey, config.filePrivKeyFormat);

var fileHandler = {};

fileHandler.uploadFile = function(req,res,next) {

 let tempFileData = req.files.fileData;
 let tempName = uuidV4();

//save received image in temporary folder
 tempFileData.mv(__dirname + '/tmp/' + tempName + '.jpg', function(err) {
 	//Handle errors
    if (err)
      return res.status(500).send(err);

  	//read received file in temporary folder to create ReadStream for form-data
	var formData = {  fileData: fs.createReadStream(__dirname + '/tmp/' + tempName + '.jpg')};

	//send received image to file server
	request.post({url:'http://'+ config.fileserverIp + ':' + config.fileserverPort + '/postFile', formData: formData}, function(err, httpResponse, body) {
	  //delete image from temporary folder
	  fs.unlink(__dirname + '/tmp/' + tempName + '.jpg');
	  //handle errors
	  if (err) {
	    console.error('Upload failed:', err);
	    return res.status(500).json({error: err.message});
	  }

	  console.log('File pload successful! Server responded with file id:', body.id);
	  
	  //append the id of the uploaded file to the request object
	  req.uploadedFileId = JSON.parse(body).id;
	  next();
	});
 });
};

fileHandler.getFileToken = function(fileId, userId) {
	//how long can the token be used to retrieve the file
	var accessExpirationDate = new Date();
	accessExpirationDate.setTime(accessExpirationDate.getTime() - (8*60*60*1000));

	var requestObject = JSON.stringify({id: fileId, user: userId, accessExpirationDate: accessExpirationDate});
	
		var token = key.encryptPrivate(requestObject, 'base64');

	return token;
	
};

module.exports = fileHandler;