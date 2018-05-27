'use strict';

exports.addOutbound = function(args, res, next) {
  /**
   * parameters expected in the args:
  * body (Outbound)
  **/
      var Outbound = require('../services/OutboundService');
    Outbound.addOutbound(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

exports.getOutbound = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
      var Outbound = require('../services/OutboundService');
    Outbound.getOutbound(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

exports.searchOutbound = function(args, res, next) {
  /**
   * parameters expected in the args:
  * keywords (String)
  **/
      var Outbound = require('../services/OutboundService');
    Outbound.searchOutbound(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

