'use strict';

const express = require('express');
const router = express.Router();
const passport = require('passport');
const Account = require('../../models/account');


//handle request for path myserver/auth/register
router.post('/', function(req, res) {
  console.log('registering user');
  Account.register(new Account({username: req.body.username}), req.body.password, function(err) {
    if (err) {
      console.log('error while user register!', err);
      res.status(409).json({error: 'User already exists'});
    }

    console.log('user registered!');
    res.status(200).json({"status":"You registered the user " + req.body.username});
    passport.authenticate('local')(req, res, function () {
          console.log('User logged in!');
        });
  });
});

//exports the router as a node module
module.exports = router;