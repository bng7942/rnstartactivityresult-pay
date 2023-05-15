/**
 * @providesModule RNStartActivityForResult-pay
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResultPay || {};

var startActivityForResult = (key, options) => {
    return RNStartActivityForResult.startActivityForResult(key, options);
};

export default startActivityForResult;