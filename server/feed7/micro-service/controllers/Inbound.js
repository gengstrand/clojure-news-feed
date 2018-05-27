'use strict';

var url = require('url');


var Inbound = require('./InboundService');


module.exports.getInbound = function getInbound (req, res, next) {
  Inbound.getInbound(req.swagger.params, res, next);
};
