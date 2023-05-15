/**
 * @providesModule RNStartActivityForResult
 */

var {NativeModules} = require("react-native");
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, options) => {
    return RNStartActivityForResult.startActivityForResult(key, options);
};

module.exports = startActivityForResult;