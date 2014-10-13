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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;

/**
 * Proxy activity  is created when purchase is started
 */
public class CommonIABProxyActivity extends Activity {
    static final String ACTION_FINISH = "org.apache.cordova.commoniab.ACTION_FINISH";
    private BroadcastReceiver broadcastReceiver;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(CommonIAB.TAG, "Finish broadcast was received");
                finish();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(ACTION_FINISH));

        if (CommonIAB.sendRequest.get()) {
            Intent i = getIntent();
            String sku = i.getStringExtra(IABConst.PURCHASE_KEY_SKU);
            String developerPayload = i.getStringExtra(IABConst.PURCHASE_KEY_DEVPAYLOAD);
            boolean inapp = i.getBooleanExtra(IABConst.PURCHASE_KEY_INAPP, true);
            try {
                Log.d(CommonIAB.TAG, "activity: mhelper:"+ CommonIAB.getInstance().getHelper());
                if (inapp) {
                    CommonIAB.getInstance().getHelper().launchPurchaseFlow(this, sku, IABConst.RC_REQUEST,
                            CommonIAB.getInstance().getPurchaseFinishedListener(), developerPayload);
                } else {
                    CommonIAB.getInstance().getHelper().launchSubscriptionPurchaseFlow(this, sku, IABConst.RC_REQUEST,
                            CommonIAB.getInstance().getPurchaseFinishedListener(), developerPayload);
                }
            } catch (java.lang.IllegalStateException e) {
                CommonIAB.getInstance().getPurchaseFinishedListener().onIabPurchaseFinished(new IabResult(
                        IabHelper.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE,
                        "Cannot start purchase process. Billing unavailable."), null);
            }
            CommonIAB.sendRequest.set(false);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(CommonIAB.TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data);
        // Pass on the activity result to the helper for handling
        if (!CommonIAB.getInstance().getHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(CommonIAB.TAG, "onActivityResult handled by IABUtil.");
        }

        finish();
    }
}
