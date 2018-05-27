'use strict';

exports.addOutbound = function(args, callback) {
  /**
   * parameters expected in the args:
  * body (Outbound)
  **/
  const elastic = require('../repositories/elastic');
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	const factory = bizNetworkDefinition.getFactory();
	var transaction = factory.newTransaction('info.glennengstrand', 'Broadcast');
	transaction.sender = factory.newRelationship('info.glennengstrand', 'Broadcaster', 'PID:' + args.body.value.from);
	transaction.subject = args.body.value.subject;
	transaction.story = args.body.value.story;
	bizNetworkConnection.submitTransaction(transaction)
	  .then((result) => {
	      const retVal = {
		  "from": args.body.value.from,
		  "occurred": Date.now(), 
		  "subject": args.body.value.subject, 
		  "story": args.body.value.story
	      };
	      elastic.index(args.body.value.from, args.body.value.story);
	      callback(null, retVal);
	  });
    });
}

exports.getOutbound = function(args, callback) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	var query = bizNetworkConnection.buildQuery("SELECT info.glennengstrand.Outbound WHERE (sender == _$broadcaster)");
	bizNetworkConnection.query(query, { broadcaster: 'resource:info.glennengstrand.Broadcaster#PID:' + args.id.value })
	  .then((results) => {
	      const retVal = results.map(function(result) {
		  return {
		      "from": args.id.value,
		      "occurred": result.created, 
		      "subject": result.subject, 
		      "story": result.story
		  };
	      });
	      callback(null, retVal);
	  });
    });
}

exports.searchOutbound = function(args, callback) {
  /**
   * parameters expected in the args:
  * keywords (string)
  **/
  const elastic = require('../repositories/elastic');
  elastic.search(args.keywords.value, function(err, senders) {
      callback(err, senders);
  });
}
