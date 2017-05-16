package com.jmyoo.mobile.convertor.currencyconvertor.data.base;

import android.widget.Toast;

import com.jmyoo.mobile.convertor.currencyconvertor.MainActivity;
import com.jmyoo.mobile.convertor.currencyconvertor.data.api.ApiController;
import com.jmyoo.mobile.convertor.currencyconvertor.data.utils.Utilities;
import com.plasticmobile.joomo.api.APIManager;
import com.plasticmobile.joomo.api.APIRequestParams;
import com.plasticmobile.joomo.api.APIResponseErrorListener;
import com.plasticmobile.joomo.api.APIResponseGlobalErrorListener;
import com.plasticmobile.joomo.api.APIResponseListener;
import com.plasticmobile.joomo.joomo.JoomoManager;
import com.plasticmobile.joomo.parser.JsonParser;
import com.plasticmobile.joomo.utils.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;


/**
 * BaseRequest
 */
public class BaseRequest {

    private final String TAG = "BaseRequest";

    public static final String LANG_ENG = "en";
    public static final String LANG_FR = "fr";

    protected final String mRelativeUrl;
    private final APIManager.Method mMethod;
    protected APIRequestParams mParams;
    protected HashMap<String, String> mHeaders;
    private Object mTag;
    private String requestId;

    public BaseRequest(String relativeUrl, APIManager.Method method) {
        mRelativeUrl = relativeUrl;
        mMethod = method;
        mParams = new APIRequestParams();
        mHeaders = new HashMap<>();

//        requestId = UUID.randomUUID().toString();
//        mHeaders.put("RequestId", requestId);
        mHeaders.put("Content-Type", "application/json");
        mHeaders.put("Cache-Control", "no-cache, no-store, must-revalidate");
        mHeaders.put("Pragma", "no-cache"); // HTTP 1.0.
        mHeaders.put("Expires", "0"); // Proxies.
//        mParams.setParam("lang", (Locale.getDefault().getLanguage().toUpperCase().equals("FR")) ? LANG_FR : LANG_ENG);
    }

    public void setLanguage(String lang) {
        mParams.setParam("Lang", lang);
    }


    public void requestFullURL(APIResponseListener responseListener, APIResponseErrorListener errorListener) {

        if (Utilities.isNetworkAvailable(MainActivity.getAppContext()) ){
            if (getPostParams() != null) {
                setupRequestParams(getPostParams());
            }

            APIResponseGlobalErrorListener apiResponseGlobalErrorListener = new APIResponseGlobalErrorListener() {
                @Override
                public boolean onGlobalError(String s, int i, String s1) {
                    Log.e(TAG, mRelativeUrl + " ERROR: " + s);
                    return false;
                }
            };

            JoomoManager manager = JoomoManager.getInstance();
            ApiController sdmapiManager = (ApiController) manager.getAPIManager();
            sdmapiManager.startRequestWithFullURL(mRelativeUrl, mMethod, mParams, mHeaders, 0, responseListener, errorListener, apiResponseGlobalErrorListener, mTag);
        } else {
            Toast.makeText(MainActivity.getAppContext(),"Network connection error",Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * Setup POST function parameters
     * @param params
     */
    private void setupRequestParams(BasePostParams params) {
        JsonParser parser = new JsonParser(null);
        String json = parser.create(params);
        mParams.clear();  // we can't set this twice, so clear previous setting first
        mParams.setParam(json, "");
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public BasePostParams getPostParams() {
        return null;
    }

    public String getRequestId(){
        return requestId;
    }
}
