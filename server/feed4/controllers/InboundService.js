'use strict';

var Link = require('./util');

function linkify(response) {
    return response.map(function(i) {
	  return {
	      "from": Link.to_link(i.from),
	      "to": Link.to_link(i.to),
	      "occurred": i.occurred, 
	      "subject": i.subject, 
	      "story": i.story
	  };
    });
}

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
      res.end(JSON.stringify(linkify(response)));
    });
  
}

