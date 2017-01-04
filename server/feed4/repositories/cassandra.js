'use strict';

const cassandra = require('cassandra-driver');
const client = new cassandra.Client({ contactPoints: [process.env.NOSQL_HOST], keyspace: 'activity' });
exports.client = client;

