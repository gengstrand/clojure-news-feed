'use strict';

function linkify(response) {
    return response.map(function(o) {
	  return {
	      "from": '/participant/' + o.from,
	      "occurred": o.occurred, 
	      "subject": o.subject, 
	      "story": o.story
	  };
    });
}

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
      res.end(JSON.stringify(linkify(response)));
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
      res.end(JSON.stringify(linkify(response)));
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
	var retVal = response.map(function(p) {
	    return '/participant/' + p;
	});
      res.end(JSON.stringify(retVal));
    });
  
}

