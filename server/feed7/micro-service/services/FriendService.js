'use strict';

exports.addFriend = function(args, callback) {
  /**
   * parameters expected in the args:
  * body (Friend)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	const factory = bizNetworkDefinition.getFactory();
	var transaction = factory.newTransaction('info.glennengstrand', 'Friend');
	transaction.from = factory.newRelationship('info.glennengstrand', 'Broadcaster', 'PID:' + args.body.value.from);
	transaction.to = factory.newRelationship('info.glennengstrand', 'Broadcaster', 'PID:' + args.body.value.to);
	bizNetworkConnection.submitTransaction(transaction)
	  .then((result) => {
	      const retVal = {
		  'id':null,
		  'from':args.body.value.from, 
		  'to': args.body.value.to };
	      callback(null, retVal);
	  });
    });
}

exports.getFriend = function(args, callback) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  const BusinessNetworkConnection = require('composer-client').BusinessNetworkConnection;
  const bizNetworkConnection = new BusinessNetworkConnection();
  bizNetworkConnection.connect(process.env.CARD_NAME)
    .then((bizNetworkDefinition) => {
	var query = bizNetworkConnection.buildQuery('SELECT info.glennengstrand.Friendship WHERE (from == _$broadcaster)');
	const serializer = bizNetworkDefinition.getSerializer();
	bizNetworkConnection.query(query, { broadcaster: 'resource:info.glennengstrand.Broadcaster#PID:' + args.id.value })
	  .then((friends) => {
	      const retVal = friends.map(function(friend) {
		const f = serializer.toJSON(friend);
		const top = f.to.split(':');  
		return {'id': null, 
			'from': args.id.value,
			'to': parseInt(top[2]) };
	      });
  	      callback(null, retVal);
	  });
    });
}

