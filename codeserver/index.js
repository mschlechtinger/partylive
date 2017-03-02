'use strict'

const bodyParser = require('body-parser');
const express = require('express');
const routes =  require('./routes');
const app = express();

app.use(bodyParser.json());
app.use(routes);

app.listen(1337, function(){
	console.log('server is running')
});