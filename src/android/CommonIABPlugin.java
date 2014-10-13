/* Copyright (c) 2014 Claus Schmidt
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.commoniab;

import java.util.TimeZone;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONStringer;
import org.json.JSONException;
import org.json.JSONObject;

import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.SkuManager;
import org.onepf.oms.appstore.googleUtils.*;
import org.onepf.oms.util.Logger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.provider.Settings;

/**
 * CommonIABPlugin is class to manage in app purchases API beetween javascript and native code(java)
 * This class uses CommonIAB class, that consists of in app purchases implementations for different stores.
 */
public class CommonIABPlugin extends CordovaPlugin {
    static final String TAG = "CommonIABPlugin";
    // actions/api of in app purchases plugin
    private static final String ACTION_INIT = "init";
    private static final String ACTION_MAP_SKU = "mapSku";
    private static final String ACTION_UNBIND = "unbind";
    private static final String ACTION_SET_DEBUG_MODE = "SetDebugMode";
    private static final String ACTION_GET_PRODUCT_DETAILS = "getProductDetails";
    private static final String ACTION_GET_PURCHASES = "getPurchases";
    private static final String ACTION_GET_AVAILABLE_PRODUCTS = "getAvailableProducts";
    private static final String ACTION_ARE_SUBSCRIPTIONS_SUPPORTED = "areSubscriptionsSupported";
    private static final String ACTION_PURCHASE_PRODUCT = "purchaseProduct";
    private static final String ACTION_PURCHASE_SUBSCRIPTION = "purchaseSubscription";
    private static final String ACTION_CONSUME_PRODUCT = "consumeProduct";

    /**
     * Constructor.
     */
    public CommonIABPlugin() {
        Log.d(TAG, "CommonIABPlugin ***************************");
    }


    /**
     * Sets the context of the Command. This can then be used to do things like
     * get file paths associated with the Activity.
     *
     * @param cordova The context of the main Activity.
     * @param webView The CordovaWebView Cordova is running in.
     */
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        CommonIAB.getInstance().setCordovaInterface(this.cordova);
    }

    /**
     * The final call you receive before your activity is destroyed.
     */
    public void onDestroy() {
        CommonIAB.getInstance().unbindService();
        super.onDestroy();
    }


    /**
     * Executes the request from javascript and returns callback or exception.
     *
     * @param action            The action to execute.
     * @param args              JSONArry of arguments for the plugin.
     * @param callbackContext   The callback id used when calling back into JavaScript.
     * @return                  True if the action was valid, false if not.
     */
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        CommonIAB.getInstance().setCallbackContext(callbackContext);
        Log.d(TAG, "action:" + action);
        int argLength = args.length();
        if (action.equals(ACTION_INIT)) {
            if (argLength > 0) {
                JSONObject argAsJson = new JSONObject(args.getString(0));
                CommonIAB.getInstance().initIABLibrary(argAsJson);
            } else {
                Log.d(TAG, "option parameter is not defined");
                return false;
            }
        } else if (ACTION_MAP_SKU.equals(action)) {
            CommonIAB.getInstance().mapSku(args.getString(0), args.getString(1), args.getString(2));
        } else if (ACTION_UNBIND.equals(action)) {
            CommonIAB.getInstance().unbindService();
        } else if (ACTION_SET_DEBUG_MODE.equals(action)) {
            if (argLength > 0) {
                boolean isDebugEnabled = "true".equals(args.getString(0)) ? true : false;
                CommonIAB.getInstance().setDebugMode(isDebugEnabled);
            }
        } else {
            if(CommonIAB.getInstance().getHelper() == null) {
                callbackContext.error(IABConst.BILLING_PLUGGIN_IS_NOT_INITIALIZED);
                return false;
            }
            if (ACTION_GET_PRODUCT_DETAILS.equals(action)) {
                CommonIAB.getInstance().getProductDetails(args);
            } else if (ACTION_GET_PURCHASES.equals(action)) {
                return CommonIAB.getInstance().getPurchases();
            } else if (ACTION_GET_AVAILABLE_PRODUCTS.equals(action)) {
                return CommonIAB.getInstance().getAvailableProducts();
            } else if (ACTION_ARE_SUBSCRIPTIONS_SUPPORTED.equals(action)) {
                JSONObject result = new JSONObject();
                result.put(IABConst.SUBSCRIPTION_RESULT_KEY, CommonIAB.getInstance().areSubscriptionsSupported());
                callbackContext.success(result);
            } else if (ACTION_PURCHASE_PRODUCT.equals(action)) {
                CommonIAB.getInstance().purchaseProduct(args.getString(0), args.getString(1));
            } else if (ACTION_PURCHASE_SUBSCRIPTION.equals(action)) {
                CommonIAB.getInstance().purchaseSubscription(args.getString(0), args.getString(1));
            } else if (ACTION_CONSUME_PRODUCT.equals(action)) {
                CommonIAB.getInstance().consumeProduct(args.getString(0));
            } else {
                return false;
            }
        }
        return true;
    }
}
