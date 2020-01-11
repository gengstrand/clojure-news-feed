'use strict';

function linkify(response) {
    return response.map(function(i) {
	  return {
	      "from": '/participant/' + i.from,
	      "to": '/participant/' + i.to,
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

