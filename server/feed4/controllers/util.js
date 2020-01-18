'use strict';

const path = '/participant/';
const re = new RegExp(path + '([0-9]+)');

exports.to_link = function(id) {
    return path + id;
}

exports.extract_id = function(value) {
    const m = re.exec(value);
    if (m) {
	return parseInt(m[0]);
    }
    return parseInt(value);
}

