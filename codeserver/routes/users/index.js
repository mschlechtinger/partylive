'use strict';

const express = require('express');
const router = express.Router();

//distribute requests for path myserver/users/
router.use('/:userId', require('./userId'));

module.exports = router;