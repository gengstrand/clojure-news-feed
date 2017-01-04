'use strict';

var url = require('url');


var Participant = require('./ParticipantService');


module.exports.addParticipant = function addParticipant (req, res, next) {
  Participant.addParticipant(req.swagger.params, res, next);
};

module.exports.getParticipant = function getParticipant (req, res, next) {
  Participant.getParticipant(req.swagger.params, res, next);
};
