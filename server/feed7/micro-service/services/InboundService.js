'use strict';
function generateId() {
   return Math.abs(Math.floor(Math.random() * Math.floor(10000000)));
}
exports.addInbound = function(feedItem, callback) {
  /**
   * parameters expected in the args:
  * feedItem (Inbound)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	const factory = bizNetworkDefinition.getFactory();
	const id = generateId();
	bizNetworkConnection.getParticipantRegistry('info.glennengstrand.Inbound')
	  .then((inboundRegistry) => {
	      var inb = factory.newResource('info.glennengstrand', 'Inbound', id);
	      inb.created = new Date();
	      inb.subject = feedItem.subject;
	      inb.story = feedItem.story;
	      inb.recipient = factory.newRelationship('info.glennengstrand', 'Broadcaster', feedItem.to);
	      inboundRegistry.add(inb)
		.then((result) => {
		    callback(null, feedItem);
		});
	  });
    });
}

exports.getInbound = function(args, callback) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	var query = bizNetworkConnection.buildQuery('SELECT info.glennengstrand.Inbound WHERE (recipient == _$broadcaster)');
	bizNetworkConnection.query(query, { broadcaster: 'resource:info.glennengstrand.Broadcaster#PID:' + args.id.value })
	  .then((results) => {
	      const retVal = results.map(function(result) {
		  return {
		      "from": null, 
		      "to": args.id.value,
		      "occurred": result.created, 
		      "subject": result.subject, 
		      "story": result.story
		  };
	      });
	      callback(null, retVal);
	  });
    });
}

