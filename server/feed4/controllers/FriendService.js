'use strict';

var Link = require('./util');

function linkify(response) {
    return response.map(function(f) {
	return {
	    'id': f.id,
	    'from': Link.to_link(f.from),
	    'to': Link.to_link(f.to)
	};
    });
}

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
      res.end(JSON.stringify(linkify(response)));
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
      res.end(JSON.stringify(linkify(response)));
    });
  
}

