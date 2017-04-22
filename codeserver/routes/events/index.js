'use strict';

const express = require('express');
const router = express.Router();

//distribute requests for path myserver/events/
router.use('/', require('./allEvents'));
router.use('/:eventId', require('./eventId'));

module.exports = router;