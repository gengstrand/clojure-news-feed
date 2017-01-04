'use strict';

exports.addFriend = function(args, res, next) {
  /**
   * parameters expected in the args:
  * body (Friend)
  **/
      var Friend = require('../services/FriendService');
    Friend.addFriend(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

exports.getFriend = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
      var Friend = require('../services/FriendService');
    Friend.getFriend(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

