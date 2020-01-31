'use strict';

var Link = require('../controllers/util');

exports.addOutbound = function(args, callback) {
  /**
  * parameters expected in the args:
  * body (Outbound)
  **/
  const cassandra = require('../repositories/cassandra').client;
  const elastic = require('../repositories/elastic');
  const cql = 'insert into Outbound (ParticipantID, Occurred, Subject, Story) values (?, now(), ?, ?)';
  const from = Link.extract_id(args.body.value.from);
  cassandra.execute(cql, [from, args.body.value.subject, args.body.value.story], {prepare: true}, function(err, rows) {
      if (err) {
	  callback(err, null);
      }
      var Inbound = require('./InboundService');
      var Friend = require('./FriendService');
      Friend.getFriend(args, function (err, response) {
	  if (err) {
              callback(err, null);
	  }
	  response.forEach(function(friend) {
	      const infeed = {
		  "from": from,
		  "to": friend.to, 
		  "occurred": Date.now(), 
		  "subject": args.body.value.subject, 
		  "story": args.body.value.story
	      };
	      Inbound.addInbound(infeed, function(err, item) {
		  if (err) {
		      callback(err, null);
		  }
	      });
	  });
      });
      const result = {
	  "from": from,
	  "occurred": Date.now(), 
	  "subject": args.body.value.subject, 
	  "story": args.body.value.story
      };
      elastic.index(from, args.body.value.story);
      callback(null, result);
  });
  
}

exports.getOutbound = function(args, callback) {
  /**
  * parameters expected in the args:
  * id (Long)
  **/
  const cassandra = require('../repositories/cassandra').client;
  const query = 'select toTimestamp(occurred) as occurred, subject, story from Outbound where participantid = ? order by occurred desc';
  const from = parseInt(args.id.value);
  cassandra.execute(query, [from], {prepare: true}, function(err, rows) {
      if (err) {
	  callback(err, null);
      }
      const result = rows.rows.map(function(row) {
	  return {
	      "from": from,
	      "occurred": row.occurred, 
	      "subject": row.subject, 
	      "story": row.story
	  };
      });
      callback(null, result);
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
