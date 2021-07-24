'use strict';

const path = '/participant/';
const re = new RegExp(path + '([0-9]+)');

exports.to_link = function(id) {
    return path + id;
}

exports.extract_id = function(value) {
    const m = re.exec(value);
    if (m) {
	return parseInt(m[1]);
    }
    return parseInt(value);
}

exports.format_date = function(value) {
    var month = '' + (value.getMonth() + 1);
    var day = '' + value.getDate();
    const year = value.getFullYear();
    if (month.length < 2) {
	month = '0' + month;
    }
    if (day.length < 2) {
	day = '0' + day;
    }
    return [year, month, day].join('-');
}
