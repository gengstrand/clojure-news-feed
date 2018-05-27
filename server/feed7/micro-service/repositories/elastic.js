'use strict';

const http = require('http');
exports.index = function(from, story) {
    const entropy = Math.random() * 1000000;
    const key = Date.now().toString().concat('-').concat(entropy.toString());
    var doc = JSON.stringify({ 'id': key, 'sender': from, 'story': story });
    var options = {
	hostname: process.env.SEARCH_HOST,
	port: 9200,
	path: process.env.SEARCH_PATH.concat('/').concat(from).concat('-').concat(key),
	method: 'PUT',
	headers: {
	    'Content-type': 'application/json',
	    'Content-Length': Buffer.byteLength(doc)
	}
    };
    var req = http.request(options);
    req.write(doc);
    req.end();
};
exports.search = function(terms, callback) {
    var options = {
	hostname: process.env.SEARCH_HOST,
	port: 9200,
	path: process.env.SEARCH_PATH.concat('/_search?q=').concat(terms),
	method: 'GET',
	headers: {
	    'Content-type': 'application/json',
	    'accept': 'application/json'
	}
    };
    var req = http.request(options, function(response) {
	var resp = '';
	response.on('data', function(chunk) {
	    resp += chunk;
	});
	response.on('end', function() {
	    const r = JSON.parse(resp);
	    if (r['hits']) {
		const outer = r['hits'];
		if (outer['hits']) {
		    const senders = outer['hits'].map(function(hit) {
			if (hit['_source']) {
			    const s = hit['_source'];
			    if (s['sender']) {
				return s['sender'];
			    }
			}
		    });
		    callback(null, senders);
		}
	    }
	});
    });
    req.on('error', function (err) {
	callback(err, null);
    });
    req.end();
};
