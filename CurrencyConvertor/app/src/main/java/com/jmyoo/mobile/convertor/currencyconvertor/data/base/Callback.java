package com.jmyoo.mobile.convertor.currencyconvertor.data.base;

/**
 * Callback
 * @author PlasticMobile
 * Created on 05-10-15
 * Copyright © 2015 RBC. All rights reserved.
 */
public abstract class Callback {

    public abstract void completed(Object object);
    public abstract void failure(int errCode, String message);

    public abstract void onSessionFailure(boolean valid);
}
