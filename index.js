/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, uri, action, byTran, byTID, byInstall, byAmt, byTaxAmt, bySfeeAmt, byFreeAmt, oriDate, byOrgAuth, idno) => {
    
    return RNStartActivityForResult.startActivityForResult(key, uri, action, byTran, byTID, byInstall, byAmt, byTaxAmt, bySfeeAmt, byFreeAmt, oriDate, byOrgAuth, idno);
};

export default startActivityForResult;