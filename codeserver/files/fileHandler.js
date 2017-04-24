'use strict';

const NodeRSA = require('node-rsa');
const config = require('../config');
const request = require('request');
const fs = require('fs');
const uuidV4 = require('uuid/v4');
const URLSafeBase64 = require('urlsafe-base64');

var key = new NodeRSA();
key.importKey(config.filePrivKey, config.filePrivKeyFormat);

var key2 = new NodeRSA();
key2.importKey(config.filePubKey, config.filePubKeyFormat);

var fileHandler = {};

fileHandler.isURL = function(str) {
     var urlRegex = '^(?!mailto:)(?:(?:http|https|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?$';
     var url = new RegExp(urlRegex, 'i');
     return str.length < 2083 && url.test(str);
};

fileHandler.isBase64 = function(str) {
	var base64regex = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/;

	return base64regex.test(str);
};
fileHandler.uploadImage = function(req,res,next) { 
	var image = req.body.imgUrl;
	if(!image) return next();
	
	if(fileHandler.isURL(image)) {
		req.image = image;
		next();
	}

	else {
		if(fileHandler.isBase64(image)) {

		  var binaryData = new Buffer(image, 'base64').toString('binary');

			let tempName = uuidV4();

			require("fs").writeFile(__dirname + '/tmp/' + tempName + '.jpg', binaryData, "binary", function(err) {

			 	//Handle errors
			    if (err){
			 	  console.log(err); // writes out file without error, but it's not a valid image
			      return res.status(500).send(err.message);
			    }

			  	//read received file from temporary folder to create ReadStream for request form-data
				var formData = {  fileData: fs.createReadStream(__dirname + '/tmp/' + tempName + '.jpg')};

				//send received image to file server
				request.post({url:'http://'+ config.fileserverIp + ':' + config.fileserverPort + '/', formData: formData}, function(err, httpResponse, body) {
				  //delete image from temporary folder
				  fs.unlink(__dirname + '/tmp/' + tempName + '.jpg');
				  //handle errors
				  if (err) {
				    console.error('Upload failed:', err);
				    return res.status(500).json({error: err.message});
				  }

				  //append the id of the uploaded file to the request object
				  req.image = JSON.parse(body).id;
				  next();
				});
			});
		} else{
			next();
		}
	}
};

fileHandler.uploadFile = function(req,res,next) {

 let tempFileData = req.files.fileData;
 let tempName = uuidV4();

//save received image in temporary folder
 tempFileData.mv(__dirname + '/tmp/' + tempName + '.jpg', function(err) {
 	//Handle errors
    if (err)
      return res.status(500).send(err);

  	//read received file from temporary folder to create ReadStream for request form-data
	var formData = {  fileData: fs.createReadStream(__dirname + '/tmp/' + tempName + '.jpg')};

	//send received image to file server
	request.post({url:'http://'+ config.fileserverIp + ':' + config.fileserverPort + '/', formData: formData}, function(err, httpResponse, body) {
	  //delete image from temporary folder
	  fs.unlink(__dirname + '/tmp/' + tempName + '.jpg');
	  //handle errors
	  if (err) {
	    console.error('Upload failed:', err);
	    return res.status(500).json({error: err.message});
	  }

	  //append the id of the uploaded file to the request object
	  req.uploadedFileId = JSON.parse(body).id;
	  next();
	});
 });
};

fileHandler.getFileUrl = function(fileId, userId, fileType) {
	//check if fileId is an Id
	if(!fileId || fileHandler.isURL(fileId)){
		return fileId;
	}
	//how long can the token be used to retrieve the file
	var accessExpirationDate = new Date();
	accessExpirationDate.setTime(accessExpirationDate.getTime() - (8*60*60*1000));
	//collect request information
	//var requestObject = JSON.stringify({id: fileId, user: userId, fileType: fileType, accessExpirationDate: accessExpirationDate});
	var requestObject = JSON.stringify({id: fileId, fileType: fileType});
	//encrypt request token with private Key
	var tokenBuffer = key.encryptPrivate(requestObject, 'buffer');
	var tokenUrl = URLSafeBase64.encode(tokenBuffer);
	//append token to Url of file server

	var fileUrl = config.publicServiceAddress + config.filePath + '/' + tokenUrl;

	return fileUrl;
};

module.exports = fileHandler;