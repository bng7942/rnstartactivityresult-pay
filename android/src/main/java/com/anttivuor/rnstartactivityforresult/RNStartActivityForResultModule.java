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
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.Iterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;

public class RNStartActivityForResultModule extends ReactContextBaseJavaModule {
    private static final String ERROR = "ERROR";
    private static final String ACTIVITY_DOES_NOT_EXIST = "ACTIVITY_DOES_NOT_EXIST";
    private static final int ACTIVITY_REQUEST_CODE = 4;
    public static final int MSG_REQUEST_OUTSIDEAPPR = 4;
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
        String byTaxAmt, String bySfeeAmt, String byFreeAmt, String byOrgDate, 
        String byOrgAuth, String byIdno, Promise promise) {

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
            intent.putExtra("byOrgDate", byOrgDate);          // 원거래일자
            intent.putExtra("byOrgAuth", byOrgAuth);          // 원거래승인번호
            intent.putExtra("byTranSerial", getTime().substring(8, 14));          // 거래일련번호
            intent.putExtra("byIdno", byIdno);          //현금/수표 식별번호
            intent.putExtra("byTaxAmt", byTaxAmt);          //세금
            intent.putExtra("bySfeeAmt", bySfeeAmt);          //봉사료
            intent.putExtra("byFreeAmt", byFreeAmt);      //비과세
            intent.putExtra("byAppCardNum", "                     ");          // APP 카드번호
            intent.putExtra("bySeumGbun", "  ");          // 세움 거래 구분 / 서명 재사용
            intent.putExtra("byBUSI", "          ");          //다중사업자번호/사업자번호

            intent.putExtra("byDate", getTime().substring(0, 14));             // 거래 요청 일자

            returnKey = key;

            currentActivity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            JSONObject jsonObj = new JSONObject();
                    
            jsonObj.put("rtn_ServerMsg1", e.getMessage());
            jsonObj.put("rtn_LEDCode", "7070");

            mPromise.resolve(jsonObj);
            // mPromise.reject(ERROR, e);
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
  
    private static WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
        WritableArray array = new WritableNativeArray();

        for (int i = 0; i < jsonArray.length(); i++) {
        Object value = jsonArray.get(i);
        if (value instanceof JSONObject) {
            array.pushMap(convertJsonToMap((JSONObject) value));
        } else if (value instanceof JSONArray) {
            array.pushArray(convertJsonToArray((JSONArray) value));
        } else if (value instanceof Boolean) {
            array.pushBoolean((Boolean) value);
        } else if (value instanceof Integer) {
            array.pushInt((Integer) value);
        } else if (value instanceof Double) {
            array.pushDouble((Double) value);
        } else if (value instanceof String) {
            array.pushString((String) value);
        } else {
            array.pushString(value.toString());
        }
        }
        return array;
    }

    private static WritableMap convertJsonToMap(JSONObject jsonObject) throws JSONException {
        WritableMap map = new WritableNativeMap();

        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
        String key = iterator.next();
        Object value = jsonObject.get(key);
        if (value instanceof JSONObject) {
            map.putMap(key, convertJsonToMap((JSONObject) value));
        } else if (value instanceof JSONArray) {
            map.putArray(key, convertJsonToArray((JSONArray) value));
        } else if (value instanceof Boolean) {
            map.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            map.putInt(key, (Integer) value);
        } else if (value instanceof Double) {
            map.putDouble(key, (Double) value);
        } else if (value instanceof String) {
            map.putString(key, (String) value);
        } else {
            map.putString(key, value.toString());
        }
        }
        return map;
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {

            if (requestCode == MSG_REQUEST_OUTSIDEAPPR) {
                try {
                    
                    if (resultCode == MSG_STATE_OK) {
                        JSONObject jsonObj = new JSONObject();
                        Log.d("VPOS-D","dKKKKKKKKKKKKKKKKKKdd");

                        jsonObj.put("rtn_Tran", data.getStringExtra("rtn_Tran"));
                        jsonObj.put("rtn_LEDCode", data.getStringExtra("rtn_LEDCode"));
                        jsonObj.put("rtn_CardBinNum", data.getStringExtra("rtn_CardBinNum"));
                        jsonObj.put("rtn_Install", data.getStringExtra("rtn_Install"));
                        jsonObj.put("rtn_AmountNum", data.getStringExtra("rtn_AmountNum"));
                        jsonObj.put("rtn_SevDate", data.getStringExtra("rtn_SevDate"));
                        jsonObj.put("rtn_SevTime", data.getStringExtra("rtn_SevTime"));
                        jsonObj.put("rtn_Authno", data.getStringExtra("rtn_Authno"));
                        jsonObj.put("rtn_CreditMbrCode", data.getStringExtra("rtn_CreditMbrCode"));
                        jsonObj.put("rtn_VANUnqTranNum", data.getStringExtra("rtn_VANUnqTranNum"));
                        jsonObj.put("rtn_IssuerName", data.getStringExtra("rtn_IssuerName"));
                        jsonObj.put("rtn_PurchaseName", data.getStringExtra("rtn_PurchaseName"));
                        jsonObj.put("rtn_ServerMsg1", data.getStringExtra("rtn_ServerMsg1"));
                        jsonObj.put("rtn_PrintMsg1", data.getStringExtra("rtn_PrintMsg1"));
                        jsonObj.put("rtn_CardTypeGubun", data.getStringExtra("rtn_CardTypeGubun"));

                        mPromise.resolve(jsonObj);
                        // mPromise = null;

                    }else if(resultCode == MSG_STATE_NG){
                        JSONObject jsonObj = new JSONObject();
                        Log.d("VPOS-D","dNNNNNNNNNNNNNNNNNNNNNNd");
                        
                        jsonObj.put("rtn_ServerMsg1", data.getStringExtra("rtn_ServerMsg1"));
                        jsonObj.put("rtn_LEDCode", data.getStringExtra("rtn_LEDCode"));

                        // String errMSG = data.getStringExtra("rtn_ServerMsg1");
                        // String errCODE = data.getStringExtra("rtn_LEDCode");
                        // String Msgebuf = "[" + errCODE + "] : " + errMSG;
                        // Toast.makeText(this, Msgebuf, Toast.LENGTH_SHORT).show();
                        
                        mPromise.resolve(jsonObj);
                        // mPromise = null;

                    }else{
                        JSONObject jsonObj = new JSONObject();
                        //Log.d(TAG,"resultCode = "+ resultCode);
                        
                        jsonObj.put("rtn_ServerMsg1", "문제발생.. 직원에게 문의하세요.");
                        jsonObj.put("rtn_LEDCode", "6060");

                        mPromise.resolve(jsonObj);
                        // mPromise = null;
                    }
                }  catch (Exception e) {
                    JSONObject jsonObj = new JSONObject();
                    
                    safePut(jsonObj, "rtn_ServerMsg1", e.getMessage());
                    safePut(jsonObj, "rtn_LEDCode", "8080");

                    mPromise.resolve(jsonObj);
                } finally {
                    mPromise = null;
                }

            }

            // try {
            //     Bundle dataBundle = data.getExtras();
            //     JSONObject jsonObj = bundleToJson(dataBundle);
            //     WritableMap result = convertJsonToMap(jsonObj);
            //     mPromise.resolve(result);
            //     mPromise = null;
            // } catch (JSONException e) {
            //     e.printStackTrace();
            //     mPromise.reject(e);
            //     mPromise = null;
            // }
        }
    };
}