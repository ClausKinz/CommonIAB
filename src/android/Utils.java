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
 * Utils is class to
 */
public final class Utils {
    private Utils(){}

    /**
     * Convert json data to purchase object.
     *
     * @param json Product detaul in json format
     * @return Purchase object
     */
        public  static Purchase convertJsonToPurchase(final String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        String appstoreName = jsonObject.getString(IABConst.PURCHASE_KEY_APPSTORENAME);
        String jsonPurchaseInfo = jsonObject.getString(IABConst.PURCHASE_KEY_ORIGINALJSON);

        Purchase purchasedItem;

        if (TextUtils.isEmpty(jsonPurchaseInfo)) {
            String itemType = jsonObject.getString(IABConst.PURCHASE_KEY_ITEMTYPE);
            String signature = jsonObject.getString(IABConst.PURCHASE_KEY_SIGNATURE);
            purchasedItem = new Purchase(itemType, jsonPurchaseInfo, signature, appstoreName);
        } else {
            purchasedItem = new Purchase(appstoreName);
            purchasedItem.setSku(jsonObject.getString(IABConst.PURCHASE_KEY_SKU));
        }
        purchasedItem.setPackageName(jsonObject.getString(IABConst.PURCHASE_KEY_PACKAGENAME));
        purchasedItem.setToken(jsonObject.getString(IABConst.PURCHASE_KEY_TOKEN));

        return purchasedItem;
    }
}