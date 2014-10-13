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
var Map = require('./CommonMap');

/**
 * This represents in app billing configuration options to set up in app billing library
 * @constructor
 */function ConfigIAB() {
    /** @enum {number} */
    this.verifyModeEnum = {VERIFY_EVERYTHING:0,//verify by publicKey for GooglePlay and OpenStores
        VERIFY_SKIP:1, //If you don't use publicKey in code and verify receipt remotely
        VERIFY_ONLY_KNOWN:2}; //To make the library connect even to stores key is not specified for

    /** @enum {String} */
    this.StoreNameEnum = {GOOGLE:"com.google.play",
                          AMAZON:"com.amazon.apps",
                          SAMSUNG:"com.samsung.apps",
                          YANDEX:"com.yandex.store",
                          NOKIA:"com.nokia.nstore",
                          SLIDE_ME:"SlideME",
                          APPLAND:"Appland",
                          APPTOIDE:"cm.aptoide.p",
                          SLIDE_ME:"SlideME"
                          };

    this.preferredStoreNames = new Array();
    this.storeKeys = new CommonMap();
    this.availableStores = new Array();
    this.discoveryTimeout = undefined;
    this.checkInventoryTimeout = undefined;
    this.checkInventory = true;
    this.verifyMode = this.verifyModeEnum.VERIFY_EVERYTHING;
    this.samsungCertificationRequestCode = undefined;
}

/** Added store name to list of preferred stores.
 * Preferred store is used for checking in app functionality for selected store, if there are multiply stores are installed
 * on your device. this option works only if your application is installed using adb command.
 * @param {String} store Android store name, @see #ConfigIAB.StoreNameEnum
 */
ConfigIAB.prototype.addPreferredStoreNames = function(store) {
    this.preferredStoreNames[this.preferredStoreNames.length] = store;
};

/** Added store public key for selected store.
 * @param {String} storeName Android store name, @see #ConfigIAB.StoreNameEnum
 * @param {String} StoreKey Android store public key, this key is got from developer console of the store.
 */
ConfigIAB.prototype.addStoreKeys = function(storeName, StoreKey) {
    this.storeKeys.put(storeName, StoreKey);
};

/** Checks inventory or not.
 * @param {boolean} isEnabled  Checks inventory when the library is initialized
 */
ConfigIAB.prototype.setCheckInventory = function(isEnabled) {
    this.checkInventory = isEnabled;
};

/** Sets up discovery time to look up process for open stores
 * @param {number} timeout Amount of ms to find all OpenStores on device. the default value is 5000ms
 */
ConfigIAB.prototype.setDiscoveryTimeout = function(timeout) {
    this.discoveryTimeout = timeout;
};

/** Sets up verify mode, the library could skip receipt verification by publicKey for GooglePlay and OpenStores
 * @param {number} mode Verify mode for process billing, @see #verifyModeEnum
 */
ConfigIAB.prototype.setVerifyMode = function(mode) {
    this.verifyMode = mode;
};

/** Sets up check inventory  timeout to look up process
 * @param {number} timeout Amount of ms to check inventory. The default value is 10000ms
 */
ConfigIAB.prototype.setCheckInventoryTimeout = function(timeout) {
    this.checkInventoryTimeout = timeout;
};

/** Sets request code for Samsung certification
 * @param {number} code Request code. Must be positive value
 */
ConfigIAB.prototype.setSamsungCertificationRequestCode = function(code) {
    this.samsungCertificationRequestCode = code;
};

/** Converts config settings to JSON format
 * @return {string} json structure to pass to #CommonIAB
 */
ConfigIAB.prototype.toJson = function() {
    jsonString = '{';

    if (this.discoveryTimeout != undefined){
        jsonString += '\"discoveryTimeout\":\"' + this.discoveryTimeout + '\",';
    }
    if (this.checkInventoryTimeout != undefined){
        jsonString += '\"checkInventoryTimeout\":\"' + this.checkInventoryTimeout + '\",';
    }
    jsonString += '\"checkInventory\":\"' + this.checkInventory + '\",';
    jsonString += '\"verifyMode\":\"' + this.verifyMode + '\"';

    if (this.samsungCertificationRequestCode != undefined){
        jsonString += ',\"samsungCertificationRequestCode\":\"' + this.samsungCertificationRequestCode + '\",';
    }
    if (this.storeKeys.size) {
        jsonString += ',\"storeKeys\": {';

        for(var i = 0; i++ < this.storeKeys.size; this.storeKeys.next()) {
            jsonString += '\"'+ this.storeKeys.key() + '\":\"' + this.storeKeys.value() + '\"';
            if(i!= this.storeKeys.size){
                jsonString += ',';
            }
        }
        jsonString += '}';
    }
    if (this.preferredStoreNames.length){
        jsonString += ',\"preferredStoreNames\":' + JSON.stringify(this.preferredStoreNames);
    }
    jsonString += '}';
    return jsonString;
};

module.exports = new ConfigIAB();