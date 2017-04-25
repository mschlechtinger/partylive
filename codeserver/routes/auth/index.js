'use strict';

const express = require('express');
const router = express.Router();

//distribute requests for path myserver/auth/
router.use('/register', require('./register'));
router.use('/login', require('./login'));
router.use('/logout', require('./logout'));
router.use('/deviceIdChange', require('./deviceIdChange'));

module.exports = router;