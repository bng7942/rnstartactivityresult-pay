
declare type startActivityForResult = (key:String, uri:String, action:String, 
    byTran:String, byTID:String, byInstall:String, byAmt:String, byTaxAmt:String, 
    bySfeeAmt:String, byFreeAmt:String, byOrgDate:String, byOrgAuth:String,
    byIdno:String) => void

export = startActivityForResult
