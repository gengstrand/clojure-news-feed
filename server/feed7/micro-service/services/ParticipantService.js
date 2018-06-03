'use strict';
function generateId() {
   return Math.abs(Math.floor(Math.random() * Math.floor(10000000)));
}
exports.addParticipant = function(args, callback) {
  /**
   * parameters expected in the args:
  * body (Participant)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	const factory = bizNetworkDefinition.getFactory();
	const id = generateId();
	bizNetworkConnection.getParticipantRegistry('info.glennengstrand.Broadcaster')
	  .then((participantRegistry) => {
	      var p = factory.newResource('info.glennengstrand', 'Broadcaster', 'PID:' + id);
	      p.moniker = args.body.value.name;
	      participantRegistry.add(p);
	      var retVal = {'id':id,'name':p.moniker};
	      callback(null, retVal);
	  });
    });
}

exports.getParticipant = function(args, callback) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	bizNetworkConnection.getParticipantRegistry('info.glennengstrand.Broadcaster')
	  .then((participantRegistry) => {
	      participantRegistry.get('PID:' + args.id.value)
		.then((result) => {
		    var retVal = {'id':args.id.value,'name': result.moniker};
		    callback(null, retVal);
		});
	  });
    });
}

