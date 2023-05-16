type extras = {
    byActive: string,
    byTran: string,
    byTID: string,
    byInstall: string,
    byAmt: string,
    byOrgDate: string,
    byOrgAuth: string,
    byTranSerial: string,
    byIdno: string,
    byTaxAmt: string,
    bySfeeAmt: string,
    byFreeAmt: string,
    byAppCardNum: string,
    bySeumGbun: string,
    byBUSI: string,
};
key, uri, action, extra
declare type startActivityForResult = (key: string, uri: string, action: string, extras: extras) => void

export = startActivityForResult
