'use strict';

var url = require('url');


var Outbound = require('./OutboundService');


module.exports.addOutbound = function addOutbound (req, res, next) {
  Outbound.addOutbound(req.swagger.params, res, next);
};

module.exports.getOutbound = function getOutbound (req, res, next) {
  Outbound.getOutbound(req.swagger.params, res, next);
};

module.exports.searchOutbound = function searchOutbound (req, res, next) {
  Outbound.searchOutbound(req.swagger.params, res, next);
};
