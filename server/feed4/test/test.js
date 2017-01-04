const expect = require('chai').expect;
const mockery = require('mockery');
const args = {'id': {'value': 1}};
const mysqlParticipantMock = {
    format: function(sql, args) {
	return 'call FetchParticipant(1)';
    }, 
    createPool: function(options) {
	return {getConnection: function(callback) {
	    const conn = {
		query: function(sql, callback) {
		    return [[{'id': 1, 'name': 'smith'}]];
		}
	    };
	    callback(null, conn);
	}};
    }
};
const mysqlFriendsMock = {
    format: function(sql, args) {
	return 'call FetchFriends(1)';
    }, 
    createPool: function(options) {
	return {getConnection: function(callback) {
	    const conn = {
		query: function(sql, callback) {
		    return [[{'id': 1, 'from': 1, 'to': 3}]];
		}
	    };
	    callback(null, conn);
	}};
    }
};
const redisCacheMissMock = {
    createClient: function(options) {
	return {
	    get: function(key, callback) {
		callback(null, null);
	    }, 
	    set: function(key, value) {
	    }, 
	    quit: function() {
	    }
	};
    } 
};
const cassandraMock = {
    Client: function(options) {
	return {
	    execute: function(sql, args, options, callback) {
		return {
		    'from': 1, 
		    'to': 3,
		    'occurred': Date.now(), 
		    'subject': 'test', 
		    'story': 'this is a test'
		};
	    }
	};
    }
};
describe('participant service', function() {
    before(function() {
	mockery.enable();
	mockery.registerAllowable('../repositories/mysql');
	mockery.registerAllowable('../repositories/redis');
	mockery.registerAllowable('../services/ParticipantService');
	mockery.registerMock('mysql', mysqlParticipantMock);
	mockery.registerMock('redis', redisCacheMissMock);
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
	mockery.registerAllowable('../repositories/mysql');
	mockery.registerAllowable('../repositories/redis');
	mockery.registerAllowable('../services/FriendService');
	mockery.registerMock('mysql', mysqlFriendsMock);
	mockery.registerMock('redis', redisCacheMissMock);
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
	mockery.registerAllowable('../repositories/cassandra');
	mockery.registerAllowable('../services/InboundService');
	mockery.registerMock('cassandra-driver', cassandraMock);
    });
    it('fetches inbound feed', function() {
	const args = {'id': {'value': 1}};
	const inboundService = require('../services/InboundService');
	inboundService.getInbound(args, function(err, feed) {
	    expect(err).to.equal(null);
	    expect(feed.from).to.equal(1);
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
	mockery.registerAllowable('../services/InboundService');
	mockery.registerAllowable('../services/OutboundService');
	mockery.registerAllowable('../services/FriendService');
	mockery.registerAllowable('../repositories/cassandra');
	mockery.registerAllowable('../repositories/mysql');
	mockery.registerAllowable('../repositories/redis');
	mockery.registerAllowable('../repositories/elastic');
	mockery.registerMock('mysql', mysqlFriendsMock);
	mockery.registerMock('redis', redisCacheMissMock);
	mockery.registerMock('cassandra-driver', cassandraMock);
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
