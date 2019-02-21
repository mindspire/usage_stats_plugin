var exec = require('cordova/exec');

exports.getUsage = function(success, error) {
    exec(success, error, 'Temperature', 'getUsage');
};
