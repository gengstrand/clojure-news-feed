'use strict';

var Link = require('./util');

function linkify(response) {
    return {
	"from": Link.to_link(response.from),
	"occurred": response.occurred, 
	"subject": response.subject, 
	"story": response.story
    };
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
      const retVal = response.map(function(o) {
	  return linkify(o);
      });
      res.end(JSON.stringify(retVal));
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
	    return Link.to_link(p);
	});
      res.end(JSON.stringify(retVal));
    });
  
}

