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
    private static final int ACTIVITY_REQUEST_CODE = 1;
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
    public void startActivityForResult(String key, String uri, String action, Map<String, String> extras, Promise promise) {
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

            for (String keys : extras.keySet()) {
                String value = extras.get(keys);
                intent.putExtra(keys, value);
            }
            // intent.putExtra("byActive", "OutSideAppr");
            // intent.putExtra("byTran", "S0");
            // intent.putExtra("byTID", "1046889414");              // 단말기번호
            // intent.putExtra("byInstall", "00");          // 할부개월수
            // intent.putExtra("byAmt", "10");              // 총금액
            // intent.putExtra("byOrgDate", "");          // 원거래일자
            // intent.putExtra("byOrgAuth", "");          // 원거래승인번호
            // intent.putExtra("byTranSerial", getTime().substring(8, 14));          // 거래일련번호
            // intent.putExtra("byIdno", "");          //현금/수표 식별번호
            // intent.putExtra("byTaxAmt", "0");          //세금
            // intent.putExtra("bySfeeAmt", "0");          //봉사료
            // intent.putExtra("byFreeAmt", "00000000");      //비과세
            // intent.putExtra("byAppCardNum", "                     ");          // APP 카드번호
            // intent.putExtra("bySeumGbun", "  ");          // 세움 거래 구분 / 서명 재사용
            // intent.putExtra("byBUSI", "          ");          //다중사업자번호/사업자번호

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
            // if (data != null) {
            //     String returnValue = data.getStringExtra(returnKey);
            //     mPromise.resolve(data);
            //     mPromise = null;
            // }
            try {
                Bundle dataBundle = data.getExtras();
                JSONObject jsonObj = bundleToJson(dataBundle);
                mPromise.resolve(jsonObj);
                mPromise = null;
            } catch (Exception e) {
                e.printStackTrace();
                mPromise.reject(e);
                mPromise = null;
            }
        }
    };
}