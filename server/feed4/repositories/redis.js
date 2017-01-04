'use strict';

const redis = require('redis');
const options = {host: process.env.REDIS_HOST, port: 6379};
exports.getCache = function(callback) {
    const cache = redis.createClient(options);
    callback(cache);
};
