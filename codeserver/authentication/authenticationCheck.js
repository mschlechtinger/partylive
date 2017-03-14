'use strict';

module.exports = function (req, res, next) {
    // if user is authenticated in the session, carry on
    if (req.user) {
        return next();
    }
    else {
        return res.status(401).json({
        error: 'User not authenticated'
      });
    }
};