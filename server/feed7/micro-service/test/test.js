const expect = require('chai').expect;
const mockery = require('mockery');
const args = {'id': {'value': 1}};
const composerParticipantMock = {
	BusinessNetworkConnection: function() {
	};
	BusinessNetworkConnection.prototype.connect = function(cardName) {
	    return new Promise(function(resolve) {
		const definition = {
		    getFactory: function() {
			return {
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
				moniker: 'smith'
			    };
			    resolve(result);
			};
		    }
		};
		resolve(registry);
	    };
	};
}
const composerFriendsMock = {
    BusinessNetworkConnection: {
	function BusinessNetworkConnection() {
	},
	BusinessNetworkConnection.prototype.connect = function(cardName) {
	    return new Promise(function(resolve) {
		const definition = {
		    getFactory: function() {
			return {
			};
		    }
		};
		resolve(definition);
	    });
	},
	BusinessNetworkConnection.prototype.getParticipantRegistry = function(name) {
	    return new Promise(function(resolve) {
		const registry = {
		    get: function(key) {
			return new Promise(function(resolve) {
			    const result = [
				{
				    participantId: 3
				}
			    ];
			    resolve(result);
			};
		    }
		};
		resolve(registry);
	    };
	},
	BusinessNetworkConnection.prototype.buildQuery = function(sql) {
	    return null;
	},
	BusinessNetworkConnection.prototype.query = function(query, params) {
	    return new Promise(function(resolve) {
		const results = {
		};
		resolve(results);
	    }
	}
    }
}
const composerInbouundMock = {
    BusinessNetworkConnection: {
	function BusinessNetworkConnection() {
	},
	BusinessNetworkConnection.prototype.connect = function(cardName) {
	    return new Promise(function(resolve) {
		const definition = {
		    getFactory: function() {
			return {
			};
		    }
		};
		resolve(definition);
	    });
	},
	BusinessNetworkConnection.prototype.getParticipantRegistry = function(name) {
	    return new Promise(function(resolve) {
		const registry = {
		    get: function(key) {
		    }
		};
		resolve(registry);
	    };
	},
	BusinessNetworkConnection.prototype.buildQuery = function(sql) {
	    return null;
	},
	BusinessNetworkConnection.prototype.query = function(query, params) {
	    return new Promise(function(resolve) {
		const results = [
		    {
			created: Date.now(),
			subject: 'test',
			story: 'test'
		    }
		];
		resolve(results);
	    }
	}
    }
}
const composerOutboundMock = {
    BusinessNetworkConnection: {
	function BusinessNetworkConnection() {
	},
	BusinessNetworkConnection.prototype.connect = function(cardName) {
	    return new Promise(function(resolve) {
		const definition = {
		    getFactory: function() {
			return {
			    newTransaction: function(ns, t) {
				return {
				    subject: '',
				    story: '',
				    sender: ''
				};
			    },
			    newRelationship: function(ns, t, id) {
				return null;
			    }
			};
		    }
		};
		resolve(definition);
	    });
	},
	BusinessNetworkConnection.prototype.submitTransaction = function(t) {
	    return new Promise(function(resolve) {
		resolve(null);
	    };
	},
	BusinessNetworkConnection.prototype.buildQuery = function(sql) {
	    return null;
	},
	BusinessNetworkConnection.prototype.query = function(query, params) {
	    return new Promise(function(resolve) {
		const results = {
		};
		resolve(results);
	    }
	}
    }
}
describe('participant service', function() {
    before(function() {
	mockery.enable();
	mockery.registerAllowable('composer-client');
	mockery.registerMock('composer-client', composerParticipantMock);
    });
    it('fetches a participant', function() {
	const participantService = require('../services/ParticipantService');
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
	mockery.registerMock('composer-client', composerFriendsMock);
    });
    it('fetches participant friends', function() {
	const service = require('../services/FriendService');
	service.getFriend(args, function(err, friend) {
	    expect(err).to.equal(null);
	    expect(friend.from).to.equal(1);
	    expect(friend.to).to.equal(3);
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
	mockery.registerMock('composer-client', composerInboundMock);
    });
    it('fetches inbound feed', function() {
	const args = {'id': {'value': 1}};
	const inboundService = require('../services/InboundService');
	inboundService.getInbound(args, function(err, feed) {
	    expect(err).to.equal(null);
	    expect(feed.to).to.equal(3);
	    expect(feed.occurred).to.equal(Date.now());
	    expect(feed.subject).to.equal('test');
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
	mockery.registerAllowable('../repositories/elastic');
	mockery.registerMock('composer-client', composerOutboundMock);
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
	    expect(feed.from).to.equal(1);
	    expect(feed.occurred).to.equal(Date.now());
	    expect(feed.subject).to.equal('test');
	});
    });
    after(function() {
	mockery.disable();
    });
});
