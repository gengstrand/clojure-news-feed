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
	  return;
      }
      conn.query(mysql.format("call UpsertParticipant(?)", [args.body.value.name]), function (err, rows) {
	  if (err) {
	      conn.release();
	      callback(err, null);
	      return;
	  }
	  var result = rows[0].map(function(row) {
	      return {'id':row['id'],'name':args.body.value.name,'link':'/participant/'+row['id']};
	  });
	  conn.release();
	  callback(null, result);
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
	      cache.quit();
	      callback(err, null);
	      return;
	  }
	  if (reply == null) {
	      pool.getConnection(function(err, conn) {
		  if (err) {
		      cache.quit();
		      callback(err, null);
		      return;
		  }
		  conn.query(mysql.format("call FetchParticipant(?)", [args.id.value]), function (err, rows) {
		      if (err) {
			  cache.quit();
			  conn.release();
			  callback(err, null);
			  return;
		      }
		      var result = rows[0].map(function(row) {
			  return {'id':args.id.value,'name':row['Moniker'],'link':'/participant/'+args.id.value};
		      });
		      const retVal = JSON.stringify(result || {});
		      cache.set(key, retVal);
		      conn.release();
		      cache.quit();
		      callback(null, result);
		  });
	      });
	  } else {
	      cache.quit();
	      callback(null, JSON.parse(reply));
	  }
      });
  });
  
}

