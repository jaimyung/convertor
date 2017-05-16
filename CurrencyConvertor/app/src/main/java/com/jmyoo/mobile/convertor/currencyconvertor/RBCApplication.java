package com.jmyoo.mobile.convertor.currencyconvertor;

import android.app.Application;
import android.content.Context;
import android.os.Handler;


import com.google.android.gms.analytics.Tracker;
import com.plasticmobile.joomo.utils.Log;
import com.rbc.mobile.rewards.common.constants.GlobalConstants;
import com.rbc.mobile.rewards.common.utilities.UnsafeOkHttpClient;
import com.rbc.mobile.rewards.common.viper.base.router.BaseActivityRouter;
import com.rbc.mobile.rewards.common.viper.base.router.BaseRouterConstants;
import com.rbc.mobile.rewards.common.viper.base.view.BaseFragmentActivity;
import com.rbc.mobile.rewards.data.api.ApiController;
import com.rbc.mobile.rewards.data.base.Callback;
import com.rbc.mobile.rewards.data.managers.RewardsManager;
import com.rbc.mobile.rewards.data.utils.BuildConfigReflector;
import com.rbc.mobile.rewards.data.utils.SecureContainerHelper;
import com.rbc.mobile.rewards.modules.logingroup.login.renderinterface.LoginTimedOutInterface;
import com.rbc.mobile.rewards.modules.logingroup.login.views.LoginFragment;
import com.rbc.mobile.rewards.modules.rootgroup.root.views.RootActivity;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * RBCApplication is the entry point of the app. It holds libraries initialization mainly.
 *
 * @author PlasticMobile
 *         Created on 21-09-15
 *         Copyright © 2015 RBC. All rights reserved.
 */
public class RBCApplication extends Application {

    private static String TAG = "RBCApplication";
    private Waiter waiter;  //Thread which controls idle time

    private BaseFragmentActivity mCurrentActivity = null;
    private BaseActivityRouter mRequesterRouter = null;
    private static Context appContext;
    private LoginTimedOutInterface loginTimedOutListener;
    private Tracker mTracker;
    private boolean isIdle = false;


    public static Context getRBCContext() {
        return appContext;
    }

    public void setCurrentActivity(BaseFragmentActivity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;

        if (mRequesterRouter != null)
            mRequesterRouter.notifyActivity(mCurrentActivity);

        Log.d(TAG, "initialize()");

        Log.d(TAG, "Version Name: " + BuildConfig.VERSION_NAME);
        Log.d(TAG, "Version Code: " + BuildConfig.VERSION_CODE);

    }

    public void touch() {
        if (waiter != null) {
            waiter.touch();
        }
    }

    public void setLoginTimedOutListener(LoginTimedOutInterface loginTimedOutListener) {
        this.loginTimedOutListener = loginTimedOutListener;
    }

    public void instantiateThread() {
        Log.d(TAG, "IDLE TIME INITIALIZED");
        int timeout = ApiController.configData.AppConfig.SessionConfig.IdleTimeout == 0 ? 10 : ApiController.configData.AppConfig.SessionConfig.IdleTimeout;
        //Set Time in Milliseconds
        waiter = new Waiter(timeout * 60 * 1000);
        waiter.start();
    }

    ///
    // Execute this before calling startActivity in any router with Activity as its View
    ///
    public void setRequesterRouter(BaseActivityRouter router) {
        mRequesterRouter = router;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
//        SecureContainerHelper.performInitialization(this, true);
//        SecureContainerHelper.createSecureContainerDomain();
//
//        Picasso.Builder builder = new Picasso.Builder(this);
//        String env = (String) BuildConfigReflector.getBuildConfigValue(this, "FLAVOR");
//        if(env == null)
//            builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
//        else {
//            if (env.toLowerCase().equals("prod")) {
//                builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
//            } else {
//                builder.downloader(
//                        new OkHttpDownloader(
//                                // use unsafe client to by pass SSL error
//                                UnsafeOkHttpClient.getUnsafeOkHttpClient()
//                        )
//                );
//            }
//        }
//        Picasso built = builder.build();
////        built.setIndicatorsEnabled(true);
////        built.setLoggingEnabled(true);
//        Picasso.setSingletonInstance(built);

    }

    public boolean isIdle() {
        return isIdle;
    }

    public class Waiter extends Thread {
        private long lastUsed;
        private long period;
        private boolean stop;
        private Handler mUiHandler = new Handler();
        private boolean silentAuth;

        public Waiter(long period) {
            this.period = period;
            stop = false;
            this.touch();
        }

        public void run() {
            long idle = 0;

            do {
                idle = System.currentTimeMillis() - lastUsed;
                Log.d(TAG, "[" + this.getId() + "] Application is idle for " + idle + " ms");
                try {
                    Thread.sleep(5000); //check every 5 seconds
                } catch (InterruptedException e) {
                    Log.d(TAG, "Waiter interrupted!");
                }


                if (idle > period && mCurrentActivity != null) {
                    idle = 0;

                    if (((RootActivity) mCurrentActivity).isLoginFragment()) {
                        lastUsed = System.currentTimeMillis();
                        continue;
                    }

                    isIdle = true;
                    silentAuth = com.rbc.mobile.rewards.data.utils.PreferenceManager.getInstance(mCurrentActivity).getBoolean(com.rbc.mobile.rewards.data.utils.PreferenceManager.SILENT_AUTH);
                    redirectUser(silentAuth);

                    stopThread();
                }

            }
            while (!stop);
            Log.d(TAG, "Finishing Waiter thread");
        }

        private void redirectUser(final boolean silentAuth) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (!silentAuth) {
                        RewardsManager.loginManager.logout(new Callback() {
                            @Override
                            public void completed(Object object) {
                            }

                            @Override
                            public void failure(int errCode, String message) {
                            }

                            @Override
                            public void onSessionFailure(boolean valid) {

                            }
                        });

                        if (!GlobalConstants.MOBILIZER_FAILED_SHOWN) {

                            com.rbc.mobile.rewards.data.utils.PreferenceManager.getInstance(appContext).setBoolean(com.rbc.mobile.rewards.data.utils.PreferenceManager.QUICK_VIEW_NEEDS_DISPLAY, true);
                            if (loginTimedOutListener != null) {
//                                loginTimedOutListener.onMobilizerTimedOut();
                            }

//                            GlobalConstants.MOBILIZER_FAILED_SHOWN = true;

                            RewardsManager.sessionManager.logoutAccount(true);
                            mRequesterRouter.showFragment(LoginFragment.newInstance(mRequesterRouter, LoginFragment.SCREEN_TYPE.LOGIN_SIGN_IN, false), BaseRouterConstants.ANIMATION_TYPE.NO_ANIMATION);
                            RewardsManager.sessionManager.cancelTimers();
                            RewardsManager.cacheManager.cancelCacheTimer();
                        }
                    } else {
                        Log.i("Current Activity", "Calling Home");
                        lastUsed = System.currentTimeMillis();
                        ((RootActivity) mCurrentActivity).clearUpToTabs();
                        ((RootActivity) mCurrentActivity).navigateToHome();
                        RewardsManager.sessionManager.logoutAccount(false);
                    }
                }
            });

        }

        public synchronized void touch() {

            if (stop) {
                stop = false;

                if (waiter.getState() == State.TERMINATED && !((RootActivity) mCurrentActivity).isLoginFragment()) {
                    instantiateThread();
                    isIdle = false;
                }
            }

            lastUsed = System.currentTimeMillis();
        }

        public synchronized void forceInterrupt() {
            this.interrupt();
        }

        //soft stopping of thread
        public synchronized void stopThread() {
            stop = true;
        }

        public synchronized void setPeriod(long period) {
            this.period = period;
        }
    }
}
