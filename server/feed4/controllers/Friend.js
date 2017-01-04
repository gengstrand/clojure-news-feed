'use strict';

var url = require('url');


var Friend = require('./FriendService');


module.exports.addFriend = function addFriend (req, res, next) {
  Friend.addFriend(req.swagger.params, res, next);
};

module.exports.getFriend = function getFriend (req, res, next) {
  Friend.getFriend(req.swagger.params, res, next);
};
