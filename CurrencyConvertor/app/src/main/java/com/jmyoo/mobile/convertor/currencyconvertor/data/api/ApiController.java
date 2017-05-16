package com.jmyoo.mobile.convertor.currencyconvertor.data.api;

import android.content.Context;

import com.jmyoo.mobile.convertor.currencyconvertor.data.api.request.CategoryListRequest;
import com.jmyoo.mobile.convertor.currencyconvertor.data.utils.Utilities;
import com.plasticmobile.joomo.api.APIManager;
import com.plasticmobile.joomo.api.APIResponseErrorListener;
import com.plasticmobile.joomo.api.APIResponseListener;
import com.plasticmobile.joomo.parser.JsonParser;
import com.plasticmobile.joomo.utils.Log;

import com.jmyoo.mobile.convertor.currencyconvertor.data.base.BaseRequest;
import com.jmyoo.mobile.convertor.currencyconvertor.data.base.Callback;


/**
 * ApiManager
 */
public class ApiController extends APIManager {

    public enum ERROR_CODE {NETWORK_ERROR, UNKNOWN_ERROR}

    private static String TAG = "ApiManager";

    private Context context;

    public String intelliSessionId;

    public static boolean initialized = false;

    private final String failedErrorMessage = "Sorry, we’re having technical issues at the moment.  Please try again later";

    private static final String DEFAULT_HOTEL_LIMIT = "30";
    private static final String DEFAULT_FLIGHT_HOTEL_LIMIT = "26";

    public static String newConfig;

    private final int LONG_TIMEOUT_MS = 60000;


    public ApiController(Context context) {
        super(context);
        this.context = context;
    }

    public static void setNewConfig(String JsonDebug) {
        newConfig = JsonDebug;
    }

    public static String getNewConfig() {
        return newConfig;
    }

    public void initialize() {

    }

    public void setContext(Context context) {
        this.context = context;

    }

    public void getProductCategories(final Callback callback) {
        Log.d(TAG, "getProductCategories START");

        APIResponseListener responseListener = new APIResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "getProductCategories response:" + response);
                response = Utilities.encodedToUTF8(response);
               /* JsonParser jsonParser = new JsonParser(CategoryListResponse.class);
                CategoryListResponse categoryListResponse = (CategoryListResponse) jsonParser.parse(response);
                if (categoryListResponse != null && categoryListResponse.Data != null) {
                    callback.completed(categoryListResponse.Data);
                } else {
                    Log.d(TAG, "response failed: Unable to parse JSON");
                }*/
            }
        };

        APIResponseErrorListener errorListener = new APIResponseErrorListener() {
            @Override
            public void onError(boolean b, String s, int i, String s1) {
                Log.d(TAG, "getProductCategories onError:" + s);
            }
        };


        CategoryListRequest categoryListRequest = new CategoryListRequest("http://api.fixer.io/latest");
        categoryListRequest.requestFullURL(responseListener, errorListener);

    }


}
