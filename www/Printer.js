var exec = require('cordova/exec');

exports.maestro = function(arg0, success, error) {
    exec(success, error, "Printer", "print", [arg0]);
};

exports.other = function(arg0, success, error) {
    exec(success, error, "Printer", "coolMethod", [arg0]);
};