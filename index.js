/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, uri, action, extra) => {
    return RNStartActivityForResult.startActivityForResult(key, uri, action, extra);
};

export default startActivityForResult;