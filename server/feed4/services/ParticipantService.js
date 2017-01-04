'use strict';

exports.addParticipant = function(args, callback) {
  /**
   * parameters expected in the args:
  * body (Participant)
  **/
  const pool = require('../repositories/mysql').pool;
  const mysql = require('mysql');
  pool.getConnection(function(err, conn) {
      if (err) {
	  callback(err, null);
	  conn.release();
	  return;
      }
      conn.query(mysql.format("call UpsertParticipant(?)", [args.body.value.name]), function (err, rows) {
	  if (err) {
	      callback(err, null);
	      conn.release();
	      return;
	  }
	  var result = rows[0].map(function(row) {
	      return {'id':row['id'],'name':args.body.value.name};
	  });
	  callback(null, result);
	  conn.release();
      });
  });
  
}

exports.getParticipant = function(args, callback) {
  /**
   * parameters expected in the args:
  * id (Long)
  **/
  const mysql = require('mysql');
  const pool = require('../repositories/mysql').pool;
  const redis = require('../repositories/redis');
  const key = 'Participant::'.concat(args.id.value);
  redis.getCache(function(cache) {
      cache.get(key, function (err, reply) {
	  if (err) {
	      callback(err, null);
	      cache.quit();
	      return;
	  }
	  if (reply == null) {
	      pool.getConnection(function(err, conn) {
		  if (err) {
		      callback(err, null);
		      cache.quit();
		      return;
		  }
		  conn.query(mysql.format("call FetchParticipant(?)", [args.id.value]), function (err, rows) {
		      if (err) {
			  callback(err, null);
			  cache.quit();
			  conn.release();
			  return;
		      }
		      var result = rows[0].map(function(row) {
			  return {'id':args.id.value,'name':row['Moniker']};
		      });
		      const retVal = JSON.stringify(result || {});
		      cache.set(key, retVal);
		      callback(null, result);
		      conn.release();
		      cache.quit();
		  });
	      });
	  } else {
	      callback(null, JSON.parse(reply));
	  }
      });
  });
  
}

