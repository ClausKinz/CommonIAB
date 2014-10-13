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

interface IABConst {
    String TAG = "IABConst";

    // actions messages
    String BILLING_PLUGGIN_IS_NOT_INITIALIZED = "Billing plugin was not initialized";
    String BILLING_SUPPORTED_CALLBACK = "OnBillingSupported";
    String BILLING_NOT_SUPPORTED_CALLBACK = "OnBillingNotSupported";
    String QUERY_INVENTORY_SUCCEEDED_CALLBACK = "OnQueryInventorySucceeded";
    String QUERY_INVENTORY_FAILED_CALLBACK = "OnQueryInventoryFailed";
    String PURCHASE_SUCCEEDED_CALLBACK = "OnPurchaseSucceeded";
    String PURCHASE_FAILED_CALLBACK = "OnPurchaseFailed";
    String CONSUME_PURCHASE_SUCCEEDED_CALLBACK = "OnConsumePurchaseSucceeded";
    String CONSUME_PURCHASE_FAILED_CALLBACK = "OnConsumePurchaseFailed";
    String MAP_SKU_FAILED_CALLBACK = "OnMapSkuFailed";
    String MAP_SKU_SUCCEEDED_CALLBACK = "OnMapSkuSucceeded";

    //Keys for options json object from javascript
    String OPTION_CHECKINVENTORY = "checkInventory";
    String OPTION_VERIFYMODE = "verifyMode";
    String OPTION_DISCOVERYTIMEOUT = "discoveryTimeout";
    String OPTION_CHECKINVENTORYTIMEOUT= "checkInventoryTimeout";
    String OPTION_SAMSUNGCERTIFICATIONREQUESTCODE= "samsungCertificationRequestCode";
    String OPTION_PREFERREDSTORENAMES = "preferredStoreNames";
    String OPTION_STOREKEYS = "storeKeys";

    //Subscription result key to pass to js, JSON {"result:true/false"}
    String SUBSCRIPTION_RESULT_KEY = "result";

    // Purchase keys to transfer to purchase activity
    String PURCHASE_KEY_SKU = "sku";
    String PURCHASE_KEY_INAPP = "inapp";
    String PURCHASE_KEY_DEVPAYLOAD = "developerPayload";
    String PURCHASE_KEY_APPSTORENAME = "appstoreName";
    String PURCHASE_KEY_ORIGINALJSON = "originalJson";
    String PURCHASE_KEY_PACKAGENAME = "packageName";
    String PURCHASE_KEY_TOKEN = "token";
    String PURCHASE_KEY_ITEMTYPE = "itemType";
    String PURCHASE_KEY_SIGNATURE = "signature";

    // Yandex specific information
    String YANDEX_STORE_SERVICE = "com.yandex.store.service";
    String YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED = YANDEX_STORE_SERVICE + ".PURCHASE_STATE_CHANGED";
    int RC_REQUEST = 10001; /**< (arbitrary) request code for the purchase flow */
}
