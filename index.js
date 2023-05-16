/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, uri, action, extras) => {
    return RNStartActivityForResult.startActivityForResult(key, uri, action, extras);
};

export default startActivityForResult;