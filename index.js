/**
 * @providesModule RNStartActivityForResult
 */

import { NativeModules } from "react-native";
var RNStartActivityForResult = NativeModules.RNStartActivityForResult || {};

var startActivityForResult = (key, uri, action, byTran, byTID, byInstall, byAmt, byOrgDate,
    byOrgAuth, byTranSerial, byIdno, byTaxAmt, bySfeeAmt, byFreeAmt) => {
        
    return RNStartActivityForResult.startActivityForResult(key, uri, action, 
    byTran, byTID, byInstall, byAmt, byOrgDate, byOrgAuth, byTranSerial, byIdno, byTaxAmt,
    bySfeeAmt, byFreeAmt);
};

export default startActivityForResult;