package com.jmyoo.mobile.convertor.currencyconvertor.data.api.request;

import com.plasticmobile.joomo.api.APIManager;
import com.jmyoo.mobile.convertor.currencyconvertor.data.base.BaseRequest;

import java.util.Locale;

/**
 * CategoryListRequest
 */
public class CategoryListRequest extends BaseRequest {

    public CategoryListRequest(String url){
        super(url + "?", APIManager.Method.GET);
        mParams.setParam("lang", (Locale.getDefault().getLanguage().equalsIgnoreCase("FR")) ? LANG_FR : LANG_ENG);
    }

}
