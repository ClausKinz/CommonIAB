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
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.provider.Settings;

/**
 * CommonIAB is class to manage all actions with in app purchases for different stores in cardova/phonegap framework
 * This class uses openiab library from https://github.com/onepf/OpenIAB with some aditional features.
 */
public class CommonIAB {
    static final String TAG = "CommonIAB";

    private OpenIabHelper mHelper;
    private CallbackContext mCallbackContext;
    private static Inventory mInventory;
    static AtomicBoolean sendRequest = new AtomicBoolean(false);
    private CordovaInterface mCordova;

    // creates CommonIAB as singleton
    private static class SingletonHolder {
        public static final CommonIAB HOLDER_INSTANCE = new CommonIAB();
    }

    public static CommonIAB getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    /**
     * Registers broadcast receiver for Yandex Store.
     * @return void
     */
    private BroadcastReceiver mBillingReceiver = new BroadcastReceiver() {
        private static final String TAG = "YandexBillingReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (IABConst.YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
                mHelper.handleActivityResult(IABConst.RC_REQUEST, Activity.RESULT_OK, intent);
            }
        }
    };

    /**
     * Listener that executes when query inventory finishes.
     * @return , if query request  is successfully executed, resul in Json format will be returned by success callback,
     *           otherwise will be returned by error callback
     */
    IabHelper.QueryInventoryFinishedListener mQueryInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                mCallbackContext.error(IABConst.QUERY_INVENTORY_FAILED_CALLBACK + result.getMessage());
                return;
            }
            mInventory = inventory;
            Log.d(TAG, "Query inventory was successful.");
            List<Purchase>purchaseList = mInventory.getAllPurchases();

            JSONArray jsonPurchaseList = new JSONArray();
            try{
                for (Purchase purchaseItem : purchaseList) {
                    jsonPurchaseList.put(new JSONObject(purchaseItem.getOriginalJson()));
                }
            }catch (JSONException e){
                mCallbackContext.error(e.getMessage());
            }
            mCallbackContext.success(IABConst.QUERY_INVENTORY_SUCCEEDED_CALLBACK + jsonPurchaseList);
        }
    };

    /**
     * Purchase callback, it is excuted when in app purchase is finished.
     * @return , if query request  is successfully executed, resul in Json format will be returned by success callback,
     *           otherwise will be returned by error callback
     */
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            mCordova.getActivity().sendBroadcast(new Intent(CommonIABProxyActivity.ACTION_FINISH));
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                Log.e(TAG, "Error purchasing: " + result);
                mCallbackContext.error(IABConst.PURCHASE_FAILED_CALLBACK + result.getResponse()+"|"+result.getMessage());
                return;
            }
            Log.d(TAG, "Purchase successful.");
            String jsonPurchase = purchase.ToJsonString();
            mCallbackContext.success(IABConst.PURCHASE_SUCCEEDED_CALLBACK + jsonPurchase);
        }
    };

    /**
     * Consume callback, it is excuted when in app consume is finished.
     * @return , if query request  is success executed, resul in Json format will be returned by success callback,
     *           otherwise will be returned by error callback
     */
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            purchase.setSku(SkuManager.getInstance().getSku(purchase.getAppstoreName(), purchase.getSku()));
            if (result.isFailure()) {
                Log.e(TAG, "Error while consuming: " + result);
                mCallbackContext.error(IABConst.CONSUME_PURCHASE_FAILED_CALLBACK + result.getMessage());
                return;
            }
            Log.d(TAG, "Consumption successful. Provisioning.");
            String jsonPurchase = purchase.ToJsonString();
            mCallbackContext.success(IABConst.CONSUME_PURCHASE_SUCCEEDED_CALLBACK + jsonPurchase);
        }
    };

    /**
     * Gets instance of IABHelper
     * @return , instance of IABHelper
     */
    public OpenIabHelper getHelper() { return mHelper; }

    /**
     * Set up callback to return results in cardova env
     */
    public void setCallbackContext(CallbackContext callback) { mCallbackContext = callback;}

    /**
     * Set up callback to return results in cardova env
     */
    public void setCordovaInterface(CordovaInterface cordova) { mCordova = cordova;}

    /**
     * Initialize in app purchase library, setup of the lib is done asynchronously.
     * @return void Callback will be executed once setup completes
     */
    public void initIABLibrary(JSONObject jsonOptions) throws JSONException {
        OpenIabHelper.Options.Builder builder = new OpenIabHelper.Options.Builder();
        builder.setCheckInventory(jsonOptions.getBoolean(IABConst.OPTION_CHECKINVENTORY));
        builder.setVerifyMode(jsonOptions.getInt(IABConst.OPTION_VERIFYMODE));

        if (jsonOptions.has(IABConst.OPTION_DISCOVERYTIMEOUT)) {
            builder.setDiscoveryTimeout(jsonOptions.getInt(IABConst.OPTION_DISCOVERYTIMEOUT));
        } else {
            Log.d(TAG, IABConst.OPTION_DISCOVERYTIMEOUT + " is not defined.");
        }

        if (jsonOptions.has(IABConst.OPTION_CHECKINVENTORYTIMEOUT)) {
            builder.setCheckInventoryTimeout(jsonOptions.getInt(IABConst.OPTION_CHECKINVENTORYTIMEOUT));
        } else {
            Log.d(TAG, IABConst.OPTION_CHECKINVENTORYTIMEOUT + " is not defined.");
        }

        if (jsonOptions.has(IABConst.OPTION_SAMSUNGCERTIFICATIONREQUESTCODE)) {
            builder.setSamsungCertificationRequestCode(jsonOptions.getInt(IABConst.OPTION_SAMSUNGCERTIFICATIONREQUESTCODE));
        } else {
            Log.d(TAG, IABConst.OPTION_SAMSUNGCERTIFICATIONREQUESTCODE + " is not defined.");
        }

        if (jsonOptions.has(IABConst.OPTION_PREFERREDSTORENAMES)) {
            JSONArray prefStores = jsonOptions.getJSONArray(IABConst.OPTION_PREFERREDSTORENAMES);
            for (int i = 0, len = prefStores.length(); i < len; i++){
                builder.addPreferredStoreName(prefStores.getString(i));
                Log.d(TAG, "key: " + prefStores.getString(i));
            }
        } else {
            Log.d(TAG, IABConst.OPTION_PREFERREDSTORENAMES + " are not defined.");
        }

        if (jsonOptions.has(IABConst.OPTION_STOREKEYS)) {
            JSONObject storeKeys = jsonOptions.getJSONObject(IABConst.OPTION_STOREKEYS);
            for(int i = 0; i<storeKeys.names().length(); i++){
                builder.addStoreKey(storeKeys.names().getString(i),
                        storeKeys.getString(storeKeys.names().getString(i)));
                Log.v(TAG, "key = " + storeKeys.names().getString(i) + " value = " +
                        storeKeys.get(storeKeys.names().getString(i)));
            }
        } else {
            Log.d(TAG, IABConst.OPTION_STOREKEYS + " are not defined.");
        }
        initWithOptions(builder.build());
    }

    /**
     * Gets purchase callback.
     * @return  Purchase callback.
     */
    public IabHelper.OnIabPurchaseFinishedListener getPurchaseFinishedListener() {
        return mPurchaseFinishedListener;
    }

    /**
     * Starts setup of in app purchase library.
     * Initialization is executed by UI Thread
     * @param options Options settings for in app purchase library
     * @return void   Callback will be executed once setup completes
     */
    public void initWithOptions(final OpenIabHelper.Options options) {
        mHelper = new OpenIabHelper(mCordova.getActivity(), options);
        createBroadcasts();
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                if (result.isFailure()) {
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    mCallbackContext.error(IABConst.BILLING_NOT_SUPPORTED_CALLBACK + result.getMessage());
                    return;
                }
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mQueryInventoryListener);
                mCallbackContext.success(IABConst.BILLING_SUPPORTED_CALLBACK);
            }
            });
    }

    /**
     * Registers broadcast receiver for Yandex Store.
     * @return void
     */
    private void createBroadcasts() {
        IntentFilter filter = new IntentFilter(IABConst.YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED);
        mCordova.getActivity().registerReceiver(mBillingReceiver, filter);
    }

    /**
     * Unbinds all listners, broadcast recivers from your application.
     */
    public void unbindService() {
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
            mCordova = null;
        }
        try {
            mCordova.getActivity().unregisterReceiver(mBillingReceiver);
        } catch (Exception ex) {
            Log.d(TAG, "destroyBroadcasts exception:\n" + ex.getMessage());
        }
    }

    /**
     * Gets list of all purchases in JSON format.
     *
     * @return true, if the request is executed, false, if not.
     *               List of all purchases returns using success callback in JSON format
     * */
    public boolean getPurchases() throws JSONException {
        // Get the list of owned items
        List<Purchase>purchaseList = mInventory.getAllPurchases();
        JSONArray jsonPurchaseList = new JSONArray();
        try{
            for (Purchase purchase : purchaseList) {
                jsonPurchaseList.put(new JSONObject(purchase.ToJsonString()));
            }
        }catch (JSONException e){
            mCallbackContext.error(e.getMessage());
            return false;
        }
        mCallbackContext.success(jsonPurchaseList);
        return true;
    }

     /**
     * Gets the list of available purchases in Json format.
     *
     * @return true, if the request is executed, false, if not.
      *              List of available products returns using success callback in JSON format
      */
    public boolean getAvailableProducts() {
        List<SkuDetails>skuList = mInventory.getAllProducts();

        // Convert the java list to json
        JSONArray jsonSkuList = new JSONArray();
        try{
            for (SkuDetails sku : skuList) {
                Log.d(TAG, "SKUDetails: Title: " + sku.getTitle());
                jsonSkuList.put(sku.toJson());
            }
        }catch (JSONException e){
            mCallbackContext.error(e.getMessage());
            return false;
        }
        mCallbackContext.success(jsonSkuList);
        return true;
    }

    /**
     * Does association between local SKU, store name and store SKU
     * It does simplification of operations between different stores(each store has own rules for SKU) and
     * application logic in your application.
     * @param sku       Local SKU(product id).
     * @param storeName Android store name, @see #ConfigIAB.StoreNameEnum
     * @param storeSku  SKU in android store
     * @return          if sku is added, resul will be returned by success callback, otherwise will be returned by
     *                  error callback
     */
    public void mapSku(String sku, String storeName, String storeSku)  {
        try {
            SkuManager.getInstance().mapSku(sku, storeName, storeSku);
            mCallbackContext.success(IABConst.MAP_SKU_SUCCEEDED_CALLBACK);
        //TODO: need to change openiab implementation, remove wrong exception approuch
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            mCallbackContext.error(IABConst.MAP_SKU_FAILED_CALLBACK + e.toString());
        }
    }

    /**
     * Gets Loggable mode status
     * @return  if debug mode is enabled, returns true, false if not
     */
    public boolean isLoggable() {
        return Logger.isLoggable();
    }

    /**
     * Sets up Loggable mode
     * @param enabled status of debug mode.if true, switches on aditional information in adb console.
     */
    public void setDebugMode(boolean enabled) {
          Logger.setLoggable(enabled);
    }

    /**
     * Gets product details of available SKUs, sends request to android store to get this information.
     * @param additionalSKUs  Aditional SKUs that absent in inventory
     * @return                SKU details in Json format in success calback
     */
    public void getProductDetails(final JSONArray additionalSKUs) {
        if (additionalSKUs.equals(null)) {
            mHelper.queryInventoryAsync(mQueryInventoryListener);
        } else {
            final List<String> SKUs = new ArrayList<String>();
            try {
                for (int i = 0; i < additionalSKUs.length(); i++) {
                    SKUs.add(additionalSKUs.getString(i));
                    Log.d(TAG, "Product SKU Added: " + additionalSKUs.getString(i));
                }
                mHelper.queryInventoryAsync(true, SKUs, mQueryInventoryListener);
            } catch (JSONException e) {
                Log.d(TAG, e.toString());
            }
        }
    }

    /**
     * Checks Subscription support status
     * @return , if subscription is suported, return true, false, if not
     */
    public boolean areSubscriptionsSupported() {
        return mHelper.subscriptionsSupported();
    }

    /**
     * Purchases product
     * @param sku              SKU of product(local) {@see MapSKU}
     * @param developerPayload String token using to sending your purchase request to Google Play. it uses for
     *                         aditional security/validation your purchase
     * @return                 SKU purchase in Json format in success calback
     */
    public void purchaseProduct(final String sku, final String developerPayload) {
        startProxyPurchaseActivity(sku, true, developerPayload);
    }

    /**
     * Purchases subscription
     * @param sku              Subscription SKU
     * @param developerPayload String token {@see purchaseProduct}
     * @return                  SKU purchased item in Json format in success calback
     */
    public void purchaseSubscription(final String sku, final String developerPayload) {
        startProxyPurchaseActivity(sku, false, developerPayload);
    }

    /**
     * Starts purchase activity
     * @param sku               SKU of product/subscription
     * @param inapp             Is product purchase, otherwise, subscription
     * @param developerPayload  String token {@see purchaseProduct}
     * @return                  SKU purchased item in Json format in success calback
     */
    private void startProxyPurchaseActivity(final String sku,final  boolean inapp, final String developerPayload) {
        Log.d(TAG,"startProxyPurchaseActivity" + sku +" inapp " + inapp);
        sendRequest.set(true);
        Intent intent = new Intent(mCordova.getActivity(), CommonIABProxyActivity.class);
        intent.putExtra(IABConst.PURCHASE_KEY_SKU, sku);
        intent.putExtra(IABConst.PURCHASE_KEY_INAPP, inapp);
        intent.putExtra(IABConst.PURCHASE_KEY_DEVPAYLOAD, developerPayload);
        // Launch proxy purchase Activity - it will close itself down when we have a response
        mCordova.getActivity().startActivity(intent);
    }

    /**
     * Consumes product
     * @param json Product detaul in json format
     * @return SKU Purchase in Json format in success calback, otherwise, error string in error callback
     */
    public void consumeProduct(final String json) {
        try {
            Purchase purchasedItem = Utils.convertJsonToPurchase(json);
            mHelper.consumeAsync(purchasedItem, mConsumeFinishedListener);
        } catch (JSONException e) {
            mCallbackContext.error(IABConst.CONSUME_PURCHASE_FAILED_CALLBACK + " Invalid json: " + json + ". " + e);
        }
    }
}
