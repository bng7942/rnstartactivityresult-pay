/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, options) => {
    alert(`RNStartActivityForResult : ${JSON.stringify(RNStartActivityForResult)}`)
    return RNStartActivityForResult.startActivityForResult(key, options);
};

export default startActivityForResult;