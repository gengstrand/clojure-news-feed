'use strict';

var Link = require('../controllers/util');

exports.addInbound = function(feedItem, callback) {
  /**
   * parameters expected in the args:
  * feedItem (Inbound)
  **/
  const cassandra = require('../repositories/cassandra').client;
  const cql = 'insert into Inbound (ParticipantID, FromParticipantID, Occurred, Subject, Story) values (?, ?, now(), ?, ?)';
  const from = Link.extract_id(feedItem.from);  
  const to = Link.extract_id(feedItem.to);  
  cassandra.execute(cql, [to, from, feedItem.subject, feedItem.story], {prepare: true}, function(err, rows) {
      if (err) {
	  callback(err, null);
      }
      callback(null, feedItem);
  });
  
}

exports.getInbound = function(args, callback) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  const cassandra = require('../repositories/cassandra').client;
  const query = 'select toTimestamp(occurred) as occurred, fromparticipantid, subject, story from Inbound where participantid = ? order by occurred desc';
  cassandra.execute(query, [parseInt(args.id.value)], {prepare: true}, function(err, rows) {
      if (err) {
	  callback(err, null);
      }
      const result = rows.rows.map(function(row) {
	  return {
	      "from": row.fromparticipantid,
	      "to": args.id.value,
	      "occurred": row.occurred, 
	      "subject": row.subject, 
	      "story": row.story
	  };
      });
      callback(null, result);
  });
  
}

