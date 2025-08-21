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
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
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

    private final ReactApplicationContext reactContext;

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

    /** JSONObject.put(...)의 JSONException을 내부에서 처리하는 안전한 put */
    private static void safePut(JSONObject obj, String key, Object value) {
        try {
            obj.put(key, value != null ? value : JSONObject.NULL);
        } catch (JSONException ignored) {
            // 필요시 Log.e("RNStartActivityForResult", "JSON put error: " + key, ignored);
        }
    }

    /** Intent가 null일 수도 있으므로 안전하게 extra를 꺼내는 헬퍼 */
    private static String getExtra(Intent data, String key) {
        return data != null ? data.getStringExtra(key) : null;
    }

    @ReactMethod
    public void startActivityForResult(
            String key,
            String uri,
            String action,
            String byTran,
            String byTID,
            String byInstall,
            String byAmt,
            String byTaxAmt,
            String bySfeeAmt,
            String byFreeAmt,
            String byOrgDate,
            String byOrgAuth,
            String byIdno,
            Promise promise) {

        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.reject(ACTIVITY_DOES_NOT_EXIST, "Activity doesn't exist");
            return;
        }

        mPromise = promise;

        try {
            Log.d("VPOS-D", uri);
            String intentAction = (action == null ? Intent.ACTION_VIEW : action);
            Intent intent = new Intent(Intent.ACTION_MAIN, Uri.parse(uri));
            intent.setAction(intentAction);

            intent.putExtra("byActive", "OutSideAppr");
            intent.putExtra("byTran", byTran);
            intent.putExtra("byTID", byTID);                   // 단말기번호
            intent.putExtra("byInstall", byInstall);           // 할부개월수
            intent.putExtra("byAmt", byAmt);                   // 총금액
            intent.putExtra("byOrgDate", byOrgDate);           // 원거래일자
            intent.putExtra("byOrgAuth", byOrgAuth);           // 원거래승인번호
            intent.putExtra("byTranSerial", getTime().substring(8, 14)); // 거래일련번호
            intent.putExtra("byIdno", byIdno);                 // 현금/수표 식별번호
            intent.putExtra("byTaxAmt", byTaxAmt);             // 세금
            intent.putExtra("bySfeeAmt", bySfeeAmt);           // 봉사료
            intent.putExtra("byFreeAmt", byFreeAmt);           // 비과세
            intent.putExtra("byAppCardNum", "                     ");     // APP 카드번호
            intent.putExtra("bySeumGbun", "  ");               // 세움 거래 구분 / 서명 재사용
            intent.putExtra("byBUSI", "          ");           // 다중사업자번호/사업자번호
            intent.putExtra("byDate", getTime().substring(0, 14));       // 거래 요청 일자

            returnKey = key;

            currentActivity.startActivityForResult(intent, ACTIVITY_REQUEST_CODE);
        } catch (Exception e) {
            JSONObject jsonObj = new JSONObject();
            safePut(jsonObj, "rtn_ServerMsg1", e.getMessage());
            safePut(jsonObj, "rtn_LEDCode", "7070");

            if (mPromise != null) {
                mPromise.resolve(jsonObj);
                mPromise = null;
            }
        }
    }

    private static JSONObject bundleToJson(Bundle bundle) {
        JSONObject json = new JSONObject();
        if (bundle == null) return json;
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            try {
                json.put(key, bundle.get(key));
            } catch (Exception e) {
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
                    JSONObject jsonObj = new JSONObject();

                    if (resultCode == MSG_STATE_OK) {
                        Log.d("VPOS-D", "dKKKKKKKKKKKKKKKKKKdd");

                        safePut(jsonObj, "rtn_Tran",           getExtra(data, "rtn_Tran"));
                        safePut(jsonObj, "rtn_LEDCode",        getExtra(data, "rtn_LEDCode"));
                        safePut(jsonObj, "rtn_CardBinNum",     getExtra(data, "rtn_CardBinNum"));
                        safePut(jsonObj, "rtn_Install",        getExtra(data, "rtn_Install"));
                        safePut(jsonObj, "rtn_AmountNum",      getExtra(data, "rtn_AmountNum"));
                        safePut(jsonObj, "rtn_SevDate",        getExtra(data, "rtn_SevDate"));
                        safePut(jsonObj, "rtn_SevTime",        getExtra(data, "rtn_SevTime"));
                        safePut(jsonObj, "rtn_Authno",         getExtra(data, "rtn_Authno"));
                        safePut(jsonObj, "rtn_CreditMbrCode",  getExtra(data, "rtn_CreditMbrCode"));
                        safePut(jsonObj, "rtn_VANUnqTranNum",  getExtra(data, "rtn_VANUnqTranNum"));
                        safePut(jsonObj, "rtn_IssuerName",     getExtra(data, "rtn_IssuerName"));
                        safePut(jsonObj, "rtn_PurchaseName",   getExtra(data, "rtn_PurchaseName"));
                        safePut(jsonObj, "rtn_ServerMsg1",     getExtra(data, "rtn_ServerMsg1"));
                        safePut(jsonObj, "rtn_PrintMsg1",      getExtra(data, "rtn_PrintMsg1"));
                        safePut(jsonObj, "rtn_CardTypeGubun",  getExtra(data, "rtn_CardTypeGubun"));

                        if (mPromise != null) mPromise.resolve(jsonObj);
                    } else if (resultCode == MSG_STATE_NG) {
                        Log.d("VPOS-D", "dNNNNNNNNNNNNNNNNNNNNNNd");

                        safePut(jsonObj, "rtn_ServerMsg1", getExtra(data, "rtn_ServerMsg1"));
                        safePut(jsonObj, "rtn_LEDCode",    getExtra(data, "rtn_LEDCode"));

                        if (mPromise != null) mPromise.resolve(jsonObj);
                    } else {
                        // 알 수 없는 코드/취소 or data == null
                        safePut(jsonObj, "rtn_ServerMsg1", "문제발생.. 직원에게 문의하세요.");
                        safePut(jsonObj, "rtn_LEDCode",    "6060");

                        if (mPromise != null) mPromise.resolve(jsonObj);
                    }
                } catch (Exception e) {
                    safePut(jsonObj, "rtn_ServerMsg1", e.getMessage());
                    safePut(jsonObj, "rtn_LEDCode", "8080");

                    if (mPromise != null) mPromise.resolve(jsonObj);
                } finally {
                    mPromise = null;
                }
            }

            // 필요 시 아래를 사용해 번들을 통째로 넘길 수 있습니다.
            // try {
            //     Bundle dataBundle = (data != null ? data.getExtras() : null);
            //     JSONObject jsonObj = bundleToJson(dataBundle);
            //     WritableMap result = convertJsonToMap(jsonObj);
            //     if (mPromise != null) mPromise.resolve(result);
            // } catch (JSONException e) {
            //     e.printStackTrace();
            //     if (mPromise != null) mPromise.reject(e);
            // } finally {
            //     mPromise = null;
            // }
        }
    };
}
