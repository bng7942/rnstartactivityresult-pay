package com.anttivuor.startactivityforresult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

public class RNStartActivityForResultModule extends ReactContextBaseJavaModule {
    private static final String ERROR = "ERROR";
    private static final String ACTIVITY_DOES_NOT_EXIST = "ACTIVITY_DOES_NOT_EXIST";
    private static final int ACTIVITY_REQUEST_CODE = 4;
    public static final int MSG_STATE_OK = 1;
    public static final int MSG_STATE_NG = 2;
    private String returnKey = "";

    private Promise mPromise;

    private ReactApplicationContext reactContext;

    public RNStartActivityForResultModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "RNStartActivityForResult";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("ERROR", ERROR);
        constants.put("ACTIVITY_DOES_NOT_EXIST", ACTIVITY_DOES_NOT_EXIST);
        return constants;
    }

    private String getTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyyMMddHHmmss");
        return dayTime.format(new Date(time));
    }

    @ReactMethod
    public void startActivityForResult(String key, String uri, String action, 
        String byTran, String byTID, String byInstall, String byAmt, 
        String byTaxAmt, String bySfeeAmt, String byFreeAmt, Promise promise) {

        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        mPromise = promise;

        try {
            Log.d("VPOS-D", uri);
            String intentAction = action == null ? Intent.ACTION_VIEW : action;
            Intent intent = new Intent(Intent.ACTION_MAIN, Uri.parse(uri));
            intent.setAction(Intent.ACTION_VIEW);
            
            intent.putExtra("byActive", "OutSideAppr");
            intent.putExtra("byTran", byTran);
            intent.putExtra("byTID", byTID);              // 단말기번호
            intent.putExtra("byInstall", byInstall);          // 할부개월수
            intent.putExtra("byAmt", byAmt);              // 총금액
            intent.putExtra("byOrgDate", "");          // 원거래일자
            intent.putExtra("byOrgAuth", "");          // 원거래승인번호
            intent.putExtra("byTranSerial", getTime().substring(8, 14));          // 거래일련번호
            intent.putExtra("byIdno", "");          //현금/수표 식별번호
            intent.putExtra("byTaxAmt", "0");          //세금
            intent.putExtra("bySfeeAmt", "0");          //봉사료
            intent.putExtra("byFreeAmt", "00000000");      //비과세
            intent.putExtra("byAppCardNum", "                     ");          // APP 카드번호
            intent.putExtra("bySeumGbun", "  ");          // 세움 거래 구분 / 서명 재사용
            intent.putExtra("byBUSI", "          ");          //다중사업자번호/사업자번호

            intent.putExtra("byDate", getTime().substring(0, 14));             // 거래 요청 일자

            returnKey = key;

            currentActivity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            mPromise.reject(ERROR, e);
            mPromise = null;
        }
    }

    private static final JSONObject bundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
        try{
            json.put(key, bundle.get(key));
        }catch (Exception e){
            e.printStackTrace();
        }
        }
        return json;
    }
  
    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode == ACTIVITY_REQUEST_CODE) {
                String rtn_Tran = data.getStringExtra("rtn_Tran");
                String rtn_LEDCode = data.getStringExtra("rtn_LEDCode");
                String rtn_CardBinNum = data.getStringExtra("rtn_CardBinNum");
                String rtn_Install = data.getStringExtra("rtn_Install");
                String rtn_AmountNum = data.getStringExtra("rtn_AmountNum");
                String rtn_TaxNum = data.getStringExtra("rtn_TaxNum");
                String rtn_SevDate = data.getStringExtra("rtn_SevDate");
                String rtn_SevTime = data.getStringExtra("rtn_SevTime");
                String rtn_Authno = data.getStringExtra("rtn_Authno");
                String rtn_CreditMbrCode = data.getStringExtra("rtn_CreditMbrCode");
                String rtn_IssuerName = data.getStringExtra("rtn_IssuerName");
                String rtn_PurchaseName = data.getStringExtra("rtn_PurchaseName");
                String rtn_ServerMsg1 = data.getStringExtra("rtn_ServerMsg1");
                String rtn_PrintMsg1 = data.getStringExtra("rtn_PrintMsg1");
                String rtn_IssuerCode = data.getStringExtra("rtn_IssuerCode");
                String rtn_PurchaseCode = data.getStringExtra("rtn_PurchaseCode");
                String rtn_BalPoint = data.getStringExtra("rtn_BalPoint");
                String rtn_GaPoint = data.getStringExtra("rtn_GaPoint");
                String rtn_NuPoint = data.getStringExtra("rtn_NuPoint");
                String rtn_CardTypeGubun = data.getStringExtra("rtn_CardTypeGubun");
                String rtn_VANUnqTranNum = data.getStringExtra("rtn_VANUnqTranNum");


                // Bundle dataBundle = data.getExtras();
                // JSONObject jsonObj = bundleToJson(dataBundle);
                mPromise.resolve(
                    resultCode, rtn_Tran, rtn_LEDCode, rtn_CardBinNum, rtn_Install,
                    rtn_AmountNum, rtn_TaxNum, rtn_SevDate, rtn_SevTime, rtn_Authno,
                    rtn_CreditMbrCode, rtn_IssuerName, rtn_PurchaseName, rtn_ServerMsg1,
                    rtn_PrintMsg1, rtn_IssuerCode, rtn_PurchaseCode, rtn_BalPoint,
                    rtn_GaPoint, rtn_NuPoint, rtn_CardTypeGubun, rtn_VANUnqTranNum
                );

                mPromise = null;

                // if (resultCode == MSG_STATE_OK) {
                //     Log.d("VPOS-D","dKKKKKKKKKKKKKKKKKKdd");
                //     String AuthNo = data.getStringExtra("rtn_Authno");
                //     inputAuthNo.setText(AuthNo);

                //     Intent intent = new Intent(MainActivity.this, ReceiptActivity.class);
                //     //intent.putExtra("byActive", byData1);           // 거래 구분자
                //     intent.putExtras(data);
                //     startActivity(intent);

                // }else if(resultCode == MSG_STATE_NG){
                //     Log.d("VPOS-D","dNNNNNNNNNNNNNNNNNNNNNNd");
                //     String errMSG = data.getStringExtra("rtn_ServerMsg1");
                //     String errCODE = data.getStringExtra("rtn_LEDCode");
                //     String Msgebuf = "[" + errCODE + "] : " + errMSG;
                //     Toast.makeText(this, Msgebuf, Toast.LENGTH_SHORT).show();


                // }else{
                //     //Log.d(TAG,"resultCode = "+ resultCode);
                // }
            }

            // try {
            //     Bundle dataBundle = data.getExtras();
            //     JSONObject jsonObj = bundleToJson(dataBundle);
            //     mPromise.resolve(jsonObj);
            //     mPromise = null;
            // } catch (Exception e) {
            //     e.printStackTrace();
            //     mPromise.reject(e);
            //     mPromise = null;
            // }
        }
    };
}