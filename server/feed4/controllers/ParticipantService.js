'use strict';

exports.addParticipant = function(args, res, next) {
  /**
   * parameters expected in the args:
  * body (Participant)
  **/
      var Participant = require('../services/ParticipantService');
    Participant.addParticipant(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

exports.getParticipant = function(args, res, next) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
      var Participant = require('../services/ParticipantService');
    Participant.getParticipant(args, function (err, response) {
      if (err) {
        return next(err.message);
      }
      res.setHeader('Content-Type', 'application/json');
      res.end(JSON.stringify(response));
    });
  
}

