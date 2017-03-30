'use strict';

const express = require('express');
const router = express.Router();

const NodeRSA = require('node-rsa');
const config = require('../config');
const uuidV4 = require('uuid/v4');

var key = new NodeRSA();
key.importKey(config.filePubKey, config.filePubKeyFormat);

//handle requests for path myserver/files
router.post('/getFile', function(req,res,next){
	var decryptedContent = key.decryptPublic(req.body.content, 'utf8');
	console.log("request decrypted:", req.body);
	var requestObject = JSON.parse(decryptedContent);

	var options = {
	    root: __dirname + '/../files/',
	    dotfiles: 'deny',
	    headers: {
	        'x-timestamp': Date.now(),
	        'x-sent': true
	    }
	};

	var fileName = requestObject.id;

  res.sendFile(fileName, options, function (err) {
    if (err) {
      next(err);
    } else {
      console.log('Sent:', fileName);
    }
  });

});

router.post('/postFile', function(req, res) {

  if (!req.files)
    return res.status(400).send('No files were uploaded.');
 
 const fileId = uuidV4();

 let fileData = req.files.fileData;

  fileData.mv(__dirname + '/../files/' + fileId + '.jpg', function(err) {
    if (err)
      return res.status(500).send(err);
 
    res.status(200).json({status: 'File uploaded!', id: fileId});
  });
});
module.exports = router;