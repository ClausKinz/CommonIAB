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
var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

var me;

channel.createSticky('onCordovaInfoReady');
// Tell cordova channel to wait on the CordovaInfoReady event
channel.waitForInitialization('onCordovaInfoReady');

/**
 * This represents in app billing for different stores,provides methods for do payments, manage purchases
 * @constructor
 */
function CommonIAB() {
    this.cordova = null;
    this.isDebugMode = false;
    me = this;
}

/** Initializes in app billing service, checks is billing supported.
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * @param {String} options Json representation of config object
 * A callback to be called when the function finishes
 * The function passes one argument to callback: {String|Object} result to processing
 */
CommonIAB.prototype.init = function(successCallback, errorCallback, options) {
    exec(successCallback, errorCallback, "CommonIABPlugin", "init", [options]);
}

/** Does association between local SKU, store name and store sku
 * It does simplification of operations between different stores(each store has own rules for SKU) and
 * application logic in your application.
 * You create common local SKU name, that will be matched with the same functionality SKU product id in different stores
 * To operate with SKU product in your application, you  use local SKU name
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * @see #CommonIAB.prototype.init
 * @param {String} sku Local SKU(product id).
 * @param {String} storeName Android store name, @see #ConfigIAB.StoreNameEnum
 * @param {String} storeSku SKU in android store
 */
CommonIAB.prototype.mapSku = function(successCallback, errorCallback, sku, storeName, storeSku){
    exec(successCallback, errorCallback, "CommonIABPlugin", "mapSku", [sku, storeName, storeSku]);
}

/**
 * Disconnect from in app billing service.
 */
CommonIAB.prototype.unbindService  = function(successCallback, errorCallback){
    exec(successCallback, errorCallback, "CommonIABPlugin", "unbind", []);
}

/**
 * Is debug mode enabled, gives more information about operations with billing
 * @return true if logging is enabled
 */
CommonIAB.prototype.isDebugLog = function() {
	return isDebugMode;
}

/**
 * Switches on debug mode for in app billing functionality
 * @param {Boolean} isDebugMode  debug mode  state
 */
CommonIAB.prototype.SetDebugMode = function(isDebugMode) {
	me.isDebugMode = isDebugMode;
    exec(null, null, "CommonIABPlugin", "SetDebugMode", [isDebugMode]);
}

/**
 * Get Product details information from the android store server using SKUs from inventory.
 * This method may block or take long to execute.
 * Do not call from a UI thread.
 * TODO: rewrite to  the non-blocking version refreshInventoryAsync
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} result in json
 * @see #CommonIAB.prototype.init
 */
CommonIAB.prototype.getProductDetails = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "CommonIABPlugin", "getProductDetails", []);
}

/**
 * Get Product details information from the android store server using inventory SKUs and additional SKUs.
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} result in json
 * @see #CommonIAB.prototype.init
 */
CommonIAB.prototype.getProductDetails = function(successCallback, errorCallback, skus){
    exec(successCallback, errorCallback, "CommonIABPlugin", "getProductDetails", [skus]);
}

/**
 * Checks subscriptions state.
 * The function passes one argument to successCallback: {Object} result in json {"result:Boolean"}
 * @see #CommonIAB.prototype.init
 */
CommonIAB.prototype.areSubscriptionsSupported = function(successCallback) {
    exec(successCallback, null, "CommonIABPlugin", "areSubscriptionsSupported", []);
}

/**
 * Purchases the product with the selected SKU and developerPayload
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} purchased item in json
 * @see #CommonIAB.prototype.init
 * @param {String} sku SKU product id
 * @param {String} developerPayload Token to verify your purchase request to store
 */
CommonIAB.prototype.purchaseProduct = function(successCallback, errorCallback, sku, developerPayload){
    exec(successCallback, errorCallback, "CommonIABPlugin", "purchaseProduct", [sku, developerPayload]);
}

/**
 * Purchases the subscription with the selected SKU and developerPayload
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} purchased item in json
 * @see #CommonIAB.prototype.init
 * @param {String} sku SKU product id
 * @param {String} developerPayload token to verify your subscription request to store
 */
CommonIAB.prototype.purchaseSubscription = function(successCallback, errorCallback, sku, developerPayload){
    exec(successCallback, errorCallback, "CommonIABPlugin", "purchaseSubscription", [sku, developerPayload]);
}

/**
 * Consumes the purchased product
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} purchased item in json
 * @see #CommonIAB.prototype.init
 * @param {String} purchase Purchased product info in json
 */
CommonIAB.prototype.consumeProduct = function(successCallback, errorCallback, purchase) {
    exec(successCallback, errorCallback, "CommonIABPlugin", "consumeProduct", [purchase]);
}

/**
 * Returns all purchases from local inventory.
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} purchased products in json
 */
CommonIAB.prototype.getPurchases = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "CommonIABPlugin", "getPurchases", []);
}

/**
 * Returns all available products for the current store.
 * @param {Function} successCallback Success handler for processing result
 * @param {Function} errorCallback Error handler for processing error
 * The function passes one argument to successCallback: {Object} available products in json
 */
CommonIAB.prototype.getAvailableProducts = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "CommonIABPlugin", "getAvailableProducts", []);
}

module.exports = new CommonIAB();

