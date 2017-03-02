'use strict'

const express = require('express');
const router = express.Router();

//handle request for path myserver/musics/
router.get('/', function(req, res){ 
	
	res.status(200).json({"test":"hallo Peter :D"})
});

router.post('/', function(req, res) {
	var input = JSON.stringify(req.body);
	res.status(200).json({"status":"You posted" + input});
})
//exports the router as a node module
module.exports = router;