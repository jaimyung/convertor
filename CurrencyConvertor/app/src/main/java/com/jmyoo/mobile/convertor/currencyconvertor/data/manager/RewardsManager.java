package com.jmyoo.mobile.convertor.currencyconvertor.data.manager;

import android.content.Context;

import com.plasticmobile.joomo.joomo.JoomoManager;
import com.plasticmobile.joomo.utils.Log;
import com.jmyoo.mobile.convertor.currencyconvertor.data.api.ApiController;
import com.jmyoo.mobile.convertor.currencyconvertor.data.base.Callback;

/**
 * Callback
 */
public class RewardsManager {
    public static String TAG = "RewardManager";

    private static RewardsManager ourInstance = new RewardsManager();

    protected static Context context;
    public static JoomoManager manager;



    public static RewardsManager getInstance() {
        return ourInstance;
    }

    public Context getContext() {
        return context;
    }

    public void setup(Context context, Callback callback){
        Log.d(TAG, "REWARDS MANAGER SET UP");
        RewardsManager.context = context;
        manager = JoomoManager.getInstance();
        ApiController apiController = new ApiController(context);
        manager.setAPIManager(apiController);
        ((ApiController) manager.getAPIManager()).initialize();

    }
}
