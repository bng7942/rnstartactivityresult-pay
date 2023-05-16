/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, uri, action, byTran, byTID, byInstall, byAmt, byTaxAmt, bySfeeAmt, byFreeAmt, byOrgDate, byOrgAuth, byIdno) => {
    
    return RNStartActivityForResult.startActivityForResult(key, uri, action, byTran, byTID, byInstall, byAmt, byTaxAmt, bySfeeAmt, byFreeAmt, byOrgDate, byOrgAuth, byIdno);
};

export default startActivityForResult;