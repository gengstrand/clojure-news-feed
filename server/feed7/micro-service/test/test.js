const expect = require('chai').expect;
const mockery = require('mockery');

function BusinessNetworkConnection() {
}
BusinessNetworkConnection.prototype.connect = function(cardName) {
    return new Promise(function(resolve) {
	const definition = {
	    getFactory: function() {
		return {
		    newTransaction: function(ns, t) {
			return {
			    sender: '',
			    subject: '',
			    story: ''
			};
		    },
		    newRelationship: function(ns, t, id) {
			return ns + '.' + t + '#' + id;
		    }
		};
	    }
	};
	resolve(definition);
    });
};
BusinessNetworkConnection.prototype.getParticipantRegistry = function(name) {
    return new Promise(function(resolve) {
	const registry = {
	    get: function(key) {
		return new Promise(function(resolve) {
		    const result = {
			moniker: 'smith',
			friends: [
			    {
				participantId: 3
			    }
			]
		    };
		    resolve(result);
		});
	    }
	};
	resolve(registry);
    });
}
BusinessNetworkConnection.prototype.buildQuery = function(sql) {
	    return 'SELECT info.glennengstrand.Inbound WHERE (recipient == _$broadcaster)';
},
BusinessNetworkConnection.prototype.query = function(query, params) {
    return new Promise(function(resolve) {
	const results = [
	{
	    participantId: 3, 
	    created: Date.now(),
	    subject: 'test',
	    story: 'test'
	}
	];
	resolve(results);
    });
};
BusinessNetworkConnection.prototype.submitTransaction = function(t) {
	return new Promise(function(resolve) {
	    const result = {
	    };
	    resolve(result);
	});
};
const composerMock = {
    BusinessNetworkConnection
}
const elasticMock = {
    index: function(from, story) {
    }
}
describe('participant service', function() {
    before(function() {
	mockery.enable();
	mockery.registerAllowable('composer-client');
	mockery.registerAllowable('../services/ParticipantService');
	mockery.registerMock('composer-client', composerMock);
    });
    it('fetches a participant', function() {
	const participantService = require('../services/ParticipantService');
	const args = {'id': {'value': 1}};
	participantService.getParticipant(args, function(err, participant) {
	    expect(err).to.equal(null);
	    expect(participant.id).to.equal(1);
	    expect(participant.name).to.equal('smith');
	});
    });
    after(function() {
	mockery.disable();
    });
});
describe('friends service', function() {
    before(function() {
	mockery.enable();
	mockery.registerAllowable('composer-client');
	mockery.registerAllowable('../services/FriendService');
	mockery.registerMock('composer-client', composerMock);
    });
    it('fetches participant friends', function() {
	const service = require('../services/FriendService');
	const args = {'id': {'value': 2}};
	service.getFriend(args, function(err, friend) {
	    expect(err).to.equal(null);
	    expect(friend[0].from).to.equal(2);
	    expect(friend[0].to).to.equal(3);
	});
    });
    after(function() {
	mockery.disable();
    });
});
describe('inbound service', function() {
    before(function() {
	mockery.enable();
	mockery.registerAllowable('composer-client');
	mockery.registerAllowable('../services/InboundService');
	mockery.registerMock('composer-client', composerMock);
    });
    it('fetches inbound feed', function() {
	const args = {'id': {'value': 1}};
	const inboundService = require('../services/InboundService');
	inboundService.getInbound(args, function(err, feed) {
	    expect(err).to.equal(null);
	    expect(feed[0].to).to.equal(1);
	    expect(feed[0].subject).to.equal('test');
	});
    });
    after(function() {
	mockery.disable();
    });
});
describe('outbound service', function() {
    before(function() {
	mockery.enable();
	mockery.registerAllowable('composer-client');
	mockery.registerAllowable('../services/OutboundService');
	mockery.registerAllowable('../repositories/elastic');
	mockery.registerMock('composer-client', composerMock);
	mockery.registerMock('../repositories/elastic', elasticMock);
    });
    it('saves outbound feed', function() {
	const postArgs = {
	    'body': {
		'value': {
		    'from':'1', 
		    'subject':'test', 
		    'story':'testing'
		}}};
	const outboundService = require('../services/OutboundService');
	outboundService.addOutbound(postArgs, function(err, feed) {
	    expect(err).to.equal(null);
	    expect(feed.from).to.equal('1');
	    expect(feed.occurred).to.equal(Date.now());
	    expect(feed.subject).to.equal('test');
	});
    });
    after(function() {
	mockery.disable();
    });
});
