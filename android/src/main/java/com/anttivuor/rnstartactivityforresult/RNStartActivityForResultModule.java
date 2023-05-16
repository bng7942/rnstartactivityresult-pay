package com.anttivuor.startactivityforresult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import java.util.Set;

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
    public void startActivityForResult(String key, String uri, String action, ReadableMap extra, Promise promise) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        mPromise = promise;

        try {
            
            String intentAction = action == null ? Intent.ACTION_VIEW : action;
            Intent intent = new Intent(Intent.ACTION_MAIN, Uri.parse(uri));
            intent.setAction(Intent.ACTION_VIEW);

            Log.d("VPOS-D", "uri", uri.toString());
            Log.d("VPOS-D", "byActive", extra.getString("byActive").toString());
            Log.d("VPOS-D", "byTran", extra.getString("byTran").toString());
            Log.d("VPOS-D", "byTID", extra.getString("byTID").toString());
            Log.d("VPOS-D", "byAmt", extra.getString("byAmt").toString());
            Log.d("VPOS-D", "byOrgDate", extra.getString("byOrgDate").toString());
            Log.d("VPOS-D", "byOrgAuth", extra.getString("byOrgAuth").toString());
            Log.d("VPOS-D", "byTranSerial", extra.getString("byTranSerial").toString());
            Log.d("VPOS-D", "byIdno", extra.getString("byIdno").toString());
            Log.d("VPOS-D", "byTaxAmt", extra.getString("byTaxAmt").toString());
            Log.d("VPOS-D", "bySfeeAmt", extra.getString("bySfeeAmt").toString());
            Log.d("VPOS-D", "byFreeAmt", extra.getString("byFreeAmt").toString());
            Log.d("VPOS-D", "byAppCardNum", extra.getString("byAppCardNum").toString());
            Log.d("VPOS-D", "bySeumGbun", extra.getString("bySeumGbun").toString());
            Log.d("VPOS-D", "byBUSI", extra.getString("byBUSI").toString());

            intent.putExtra("byActive", extra.getString("byActive").toString());
            intent.putExtra("byTran", extra.getString("byTran").toString());
            intent.putExtra("byTID", extra.getString("byTID").toString());              // 단말기번호
            intent.putExtra("byInstall", extra.getString("byInstall").toString());          // 할부개월수
            intent.putExtra("byAmt", extra.getString("byAmt").toString());              // 총금액
            intent.putExtra("byOrgDate", extra.getString("byOrgDate").toString());          // 원거래일자
            intent.putExtra("byOrgAuth", extra.getString("byOrgAuth").toString());          // 원거래승인번호
            intent.putExtra("byTranSerial", extra.getString("byTranSerial").toString());          // 거래일련번호
            intent.putExtra("byIdno", extra.getString("byIdno").toString());          //현금/수표 식별번호
            intent.putExtra("byTaxAmt", extra.getString("byTaxAmt").toString());          //세금
            intent.putExtra("bySfeeAmt", extra.getString("bySfeeAmt").toString());          //봉사료
            intent.putExtra("byFreeAmt", extra.getString("byFreeAmt").toString());      //비과세
            intent.putExtra("byAppCardNum", extra.getString("byAppCardNum").toString());          // APP 카드번호
            intent.putExtra("bySeumGbun", extra.getString("bySeumGbun").toString());          // 세움 거래 구분 / 서명 재사용
            intent.putExtra("byBUSI", extra.getString("byBUSI").toString());          //다중사업자번호/사업자번호

            intent.putExtra("byDate", getTime().substring(0, 14));             // 거래 요청 일자

            returnKey = key;

            currentActivity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            mPromise.reject(ERROR, e);
            mPromise = null;
        }
    }

    private final ActivityEventListener mActivityEventListener = new BaseActivityEventListener() {
        @Override
        public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (data != null) {
                String returnValue = data.getStringExtra(returnKey);
                mPromise.resolve(data);
                mPromise = null;
            }
        }
    };
}