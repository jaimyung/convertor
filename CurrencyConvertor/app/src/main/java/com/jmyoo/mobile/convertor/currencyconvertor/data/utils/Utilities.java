package com.jmyoo.mobile.convertor.currencyconvertor.data.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.widget.Toast;

import com.google.gson.Gson;
import com.plasticmobile.joomo.utils.Log;


import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();
    public static boolean alertDisplayed = false;

    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    private static final String PORT = ":";

    public static String getParamsFromURL(String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                if (url.contains("?")) {
                    String[] parts = url.split("\\?");
                    return parts[1];
                } else {
                    return "";
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static void callNumber(Context mContext, String phone) {
        if (mContext != null) {

            if (!phone.contains("tel:"))
                phone = "tel:" + phone.trim();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phone));
            mContext.startActivity(intent);
        }
    }

    public static void handlePDF(Context mContext, String url) {

        try {
            Uri uriUrl = Uri.parse(url);
            Intent intentUrl = new Intent(Intent.ACTION_VIEW, uriUrl);
            mContext.startActivity(intentUrl);

        } catch (Exception e) {
            Toast.makeText(mContext, "No PDF Viewer Installed", Toast.LENGTH_LONG).show();
        }

    }

    public static void handleDialogForWebView(Context mContext, String message, final JsResult result) {
        new AlertDialog.Builder(mContext)
                .setMessage(message)
                .setCancelable(false)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        result.cancel();
                    }
                })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                .create()
                .show();
    }

    public static boolean isFrench() {
        if (Locale.getDefault().getLanguage().equals("en")) return false;
        else return true;
    }

    public static String getLanguageCode() {
        return isFrench() ? "FR" : "EN";
    }

    public static String getLanguageString() {
        return isFrench() ? "FRENCH" : "ENGLISH";
    }

    private static void launch(Context context, String packageName, String marketUri) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(marketUri));
            context.startActivity(intent);
        }
    }

    public static void launchScheme(Context context, String packageName, String marketUri, String url) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {

            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);


        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(marketUri));
            context.startActivity(intent);
        }
    }




    public static Locale getLocale() {
        return (Locale.getDefault().getLanguage().equalsIgnoreCase("FR")) ? Locale.CANADA_FRENCH : Locale.CANADA;
    }


    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } else {
            return true;
        }
    }

    public static void createSimpleNetworkErrorDialog(Activity con, @StringRes int titleId, @StringRes int content) {
        if (con != null && !con.isFinishing() && !alertDisplayed) {
            alertDisplayed = true;
            new AlertDialog.Builder(con)
                    .setTitle(titleId)
                    .setMessage(content)
                    .setCancelable(false)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            alertDisplayed = false;
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDisplayed = false;
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDisplayed = false;
                                    dialog.dismiss();
                                }
                            })
                    .create()
                    .show();
        }
    }

    public static String encodedToUTF8(String response) {
        try {
            response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
        return response;
    }


    public static String toJSON(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }


    public static String removeScriptContent(String message) {
            String scriptRegex = "<(/)?[ ]*script[^>]*>";
            Pattern pattern2 = Pattern.compile(scriptRegex);

            if(message != null) {
                Matcher matcher2 = pattern2.matcher(message);
                StringBuffer str = new StringBuffer(message.length());
                while(matcher2.find()) {
                    matcher2.appendReplacement(str, Matcher.quoteReplacement(" "));
                }
                matcher2.appendTail(str);
                message = str.toString();
            }
            return message;
    }
}
