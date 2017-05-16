package com.jmyoo.mobile.convertor.currencyconvertor;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jmyoo.mobile.convertor.currencyconvertor.data.api.ApiController;
import com.jmyoo.mobile.convertor.currencyconvertor.data.base.Callback;
import com.jmyoo.mobile.convertor.currencyconvertor.data.manager.RewardsManager;
import com.plasticmobile.joomo.joomo.JoomoManager;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static String TAG = "Application";
    private Waiter waiter;  //Thread which controls idle time
    private boolean isIdle = false;
    private static Context appContext;

    public static JoomoManager manager;
    public static Context getAppContext() {
        return appContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = this;
        setContentView(R.layout.activity_main);
        this.instantiateThread();
        launchApp();





//        waiter.touch();
    }


    public void instantiateThread() {
        Log.d(TAG, "IDLE TIME INITIALIZED");
        int timeout = 1;//30;
        //Set Time in Milliseconds
        waiter = new Waiter(timeout * 60 * 1000);
        waiter.start();
    }


    public class Waiter extends Thread {
        private long lastUsed;
        private long period;
        private boolean stop;
        private Handler mUiHandler = new Handler();

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


                if (idle > period) {
                    idle = 0;
                    lastUsed = System.currentTimeMillis();
                    isIdle = true;
                    updateExchangeRate();
                }

            }
            while (!stop);
            Log.d(TAG, "Finishing Waiter thread");
        }

        private void updateExchangeRate() {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "updateExchangeRate");


                    ((ApiController) manager.getAPIManager()).getProductCategories(new Callback() {
                        @Override
                        public void completed(Object object) {
//                            ArrayList<ProductCategory> productCategories = (ArrayList<ProductCategory>) object;
//                            seperateProductCategories(productCategories);
//                            setHasProducts(productCategories);
//
//                            if (callback != null)
//                                callback.completed(null);
                        }

                        @Override
                        public void failure(int errCode, String message) {
//                            if (callback != null)
//                                callback.failure(errCode, message);
                        }

                        @Override
                        public void onSessionFailure(boolean valid) {
//                            callback.onSessionFailure(valid);

                        }
                    });





                }
            });

        }

        public synchronized void touch() {

            if (stop) {
                stop = false;

                if (waiter.getState() == State.TERMINATED ) {
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


    private void launchApp() {
        com.plasticmobile.joomo.utils.Log.d(TAG, "setting up rewards manager");

//        HttpsTrustManager.setup(this);
        RewardsManager.getInstance().setup(MainActivity.getAppContext(), new Callback() {
            @Override
            public void completed(Object object) {
                
                    continueLaunch();
      
                }

            @Override
            public void failure(int errCode, String message) {
                
            }

            @Override
            public void onSessionFailure(boolean valid) {

            }
        });
    }

    private void continueLaunch() {
        ((ApiController) manager.getAPIManager()).getProductCategories(new Callback() {
            @Override
            public void completed(Object object) {
//                            ArrayList<ProductCategory> productCategories = (ArrayList<ProductCategory>) object;
//                            seperateProductCategories(productCategories);
//                            setHasProducts(productCategories);
//
//                            if (callback != null)
//                                callback.completed(null);
            }

            @Override
            public void failure(int errCode, String message) {
//                            if (callback != null)
//                                callback.failure(errCode, message);
            }

            @Override
            public void onSessionFailure(boolean valid) {
//                            callback.onSessionFailure(valid);

            }
        });
    }



}
