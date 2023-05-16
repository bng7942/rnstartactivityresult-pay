/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, uri, action, byTran, byTID) => {
    alert(`RNStartActivityForResult : ${JSON.stringify(RNStartActivityForResult)}`)
    return RNStartActivityForResult.startActivityForResult(key, uri, action, byTran, byTID);
};

export default startActivityForResult;