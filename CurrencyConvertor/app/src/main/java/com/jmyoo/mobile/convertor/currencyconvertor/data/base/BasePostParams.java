package com.jmyoo.mobile.convertor.currencyconvertor.data.base;

import java.util.Locale;

/**
 * BasePostParams
 */
public class BasePostParams {
    public String Lang = (Locale.getDefault().getLanguage().toUpperCase(Locale.getDefault()).equals("FR")) ? "FR" : "EN";
}
