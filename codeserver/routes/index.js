'use strict';

const express = require('express');
const router = express.Router();

//distribute requests for path myserver/
router.use('/auth', require('./auth'));
router.use('/events', require('./events'));

module.exports = router;