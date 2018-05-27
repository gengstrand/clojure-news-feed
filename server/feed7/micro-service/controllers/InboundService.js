'use strict';

exports.getInbound = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
      var Inbound = require('../services/InboundService');
    Inbound.getInbound(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

