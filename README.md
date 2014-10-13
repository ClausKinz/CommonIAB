Cordova/PhoneGap Common in-app billing/purchases Plugin
==================================

The essential purpose of In-app purchases are purchases made from within a mobile application. 
Users typically make an in-app purchase in order to access special content or features using in an app purchases such as special options, power-ups, virtual money, special characters, etc. The purchasing process is completed directly from within the app and is seamless to the user in most cases, with the mobile platform provider facilitating the purchase and taking a share of the money spent (usually in the range of 30% or so), with the rest going to the app developer.

### Examples of in-app purchases
In-app purcahses are ideally suited for application that use aditional optional model, such as access to aditional content, special options, subscribtions.

## Plugin's Purpose
The purpose of the plugin is to create an Android stores independent javascript interface for [Cordova][cordova] based on [OpenIAB][openiab] forked library. 

### Supported Android Stores:
- [Google play][google_play]
- [Samsung][samsung_store]
- [Nokia][nokia_store]
- [Yandex.Store][yandex_store]
- [SlideME][slideme_store]
- [Appland][appland_store]
- [Apptoid][appltoid_store]
- [AppMall][appmall_store]

### Supported Platforms
- **Android** *(SDK >=10)*<br>
See [In-app purchases Guide][android_notification_guide] for detailed informations and screenshots.

### Requirements
- Phonegap 3.0, Android 2.6+
- Purchasing and querying managed in-app items:
    - Google Play client version 3.10.10 or higher, In-App billing v3.0
    - Samsung In-App Purchase v 2.0
    - Nokia In-App Payment API version ("3").
    - Open Stores (Yandex, SlideMe, Appland, Apptoid, AppMail) in-app billing protocol v 1.0

## Installation
The plugin can either be installed into the local development environment.

### Adding the Plugin to your project
Through the [Command-line Interface][CLI]:
```bash
# ~~ from master ~~
cordova plugin add https://github.com/ClausKinz/CommonIAB.git && cordova prepare
```

### Removing the Plugin from your project
Through the [Command-line Interface][CLI]:
```bash
cordova plugins rm org.commoniab
```
### PhoneGap Build
Add the following xml to your config.xml to always use the latest version of this plugin:
```xml
<gap:plugin name="org.commoniab" />
```
or to use an specific version:
```xml
<gap:plugin name="org.commoniab" version="0.0.5" />
```
More informations can be found [here][PGB_plugin].

## ChangeLog
#### Version 0.0.5 (not yet released)
- [feature:] initial implementation 

#### Further informations
- See [RELEASENOTES.md][changelog] to get the full changelog for the plugin.
- See the [v0.1.x TODO List][todo_list] for upcomming changes and other things.

## Using the plugin
The plugin creates  objects ```CommonIAB``` and ```ConfigIAB```. ```ConfigIAB``` uses to set up  In-App billing parameters to initialize the library. ```CommonIAB``` is basis class to manage In-App purchases.
More information is available on [Wiki page][wiki_page]. 

### Plugin initialization
The plugin and its methods are not available before the *deviceready* event has been fired.

```javascript
document.addEventListener('deviceready', function () {
    // CommonIAB is now available
}, false);
```

The plugin must use ```ConfigIAB``` class to set up initial parameters for correct working of In-App Billing.<br>
After finish to set up billing parameters, you have to execute CommonIAB.init
`CommonIAB.init(successCallback, errorCallback, options)`<br>
Initializes the In-App billing library.
A callback to be called when the function finishes The function passes one argument to callback: {String|Object} result to processing
 * **Parameters:**
   * `successCallback` — `Function` — Success handler for processing result
   * `errorCallback` — `Function` — Error handler for processing error
   * `options` — `String` — Json representation of config object. Use ```ConfigIAB.toJson()```

#### Example code
Call the following code inside onDeviceReady(), because only after device ready you will have the plugin working.
```javascript
     function onDeviceReady() {
        config();
    }

    /** Default success handler for operations.
     * @param {String|Object} result Data to process
     */
	function commonSuccessHandler (result) {
    	var strResult = "";
        if(typeof result === 'object') {
        	strResult = JSON.stringify(result);
        } else {
        	strResult = result;
        }
        alert("SUCCESS: \r\n"+strResult );
	}
			
    /** Default error handler for operations.
     * @param {String|Object} error Error to process
     */
	function commonErrorHandler (error) {
		alert("ERROR: \r\n"+error );
	}

    /** Configures and initializes in app purchase for different stores.
     *  To configure using ConfigIAB class, to init using CommonIAB
     *  @see #ConfigIAB
     *  @see #CommonIAB.init
     */
	function config() {
		ConfigIAB.addPreferredStoreNames(ConfigIAB.StoreNameEnum.YANDEX);
		ConfigIAB.addPreferredStoreNames(ConfigIAB.StoreNameEnum.GOOGLE);
	  	YANDEX_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlZWMVFADb5IVQEBAQb66OHobeIvbVO2mmVW77/tWEi73P0aGIzm1QXi6t3vBFroeniYvKmhpmfdmVn27WshPz0G3NZeJANX/Fppm0yxv3PPeP6+AFnzXQpi+WCByTQf8YQxpv9oKFMhemdL5BRLE/XP0L5i9QJwccBSqaIKTBi4eN3+qaS1xp9DU95Mf7TK748LencM8fZfkCdahj0Zp9O53ZDvLLKiZdKV3DDgqiHewR68Cw4nY1mWyM/RkNBdtvFgmZvD6rhAjGmoQyjNbg8keuX1krwNHZxWz6YYRKsmlr3iP6dKSYGtDYmv6qPOVnAxRYpD8Bf95HQ9quk04jwIDAQAB";
	  	GOOGLE_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkfzN/N+50a3yV94N8QK8TTdvex44EbcOCYew3Nrl2iHHnzcVMT7Dz43tLDQIzUFfyKbnoXYv2Wgfg4OZcaHm6DeLs9PcvotlBNsL8quE0JRmf/sDAKtceLonKrus3nvQyKCQn+yzxRYSX5LwDjmwo92y8g1WlmrjV1OcwgmhuPq8WwEILtxszzqO4fp+T15q2lwnjiaJdeFG3d2d17b1mzTFHV8yCPvZV+0FsEmeISwYGZSwW4AUFU1JhbeXbHUILVT+1TkPaXPJ0XohpdMSB2ov9o6K43a2IhyTEwDxBq38VWWLmu+hBpX33775ssU+WpHJpcxG6fU9eVolkRAYFQIDAQAB";
	  	SLIDEME_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApLMWn4JfC/nej9ZtWQWwRjBRr1gH9l8q5H2c2b04thzbgGS9pmv/N4KsSZhB23Da+yQUMbFHktDzeMVGFC23WOb/aZlvremzZ6kUN/fF67XfH+Znp03thCXpE4I/TWKqWjLtc8dw1wpSsPvG7wRPmiAut9zsXpARuH2FCecxhnTocouCtDjbzVFhf3r6905Q0lTGyRg7t6mDXDHOEhA3rAO/RwRi8IE0XeDamNHUlzjKzUtDPopJQjG5hWVkw32LIeil1mDxPhV5y6sRNlZGuSYcS/QiaYO++JYCKhmQn2VQG775vY+bVsCrwnK+ZDpn7bAnL3WeB/VMMDd6Phb66wIDAQAB";

		ConfigIAB.addStoreKeys(ConfigIAB.StoreNameEnum.GOOGLE, GOOGLE_PUBLIC_KEY);
		ConfigIAB.addStoreKeys(ConfigIAB.StoreNameEnum.YANDEX, YANDEX_PUBLIC_KEY);
		ConfigIAB.addStoreKeys(ConfigIAB.StoreNameEnum.SLIDE_ME, SLIDEME_PUBLIC_KEY);
		ConfigIAB.setVerifyMode(ConfigIAB.verifyModeEnum.VERIFY_SKIP);
        CommonIAB.init(commonSuccessHandler, commonErrorHandler, ConfigIAB.toJson());
	}
```

### Set up SKUs
Each Android Store has own rules of named SKUs. Thus to implification of operations between different stores and application logic, we use conception of "local SKU". "local SKU" is used to manage SKUs for different stores. ComminIAB does association between local sku and selected android store automaticaly (on depends of what store is used for your app). 

`CommonIAB.mapSku(successCallback, errorCallback, sku, storeName, storeSku)`

 Does association beetween local SKU and android store SKU
 * **Parameters:**
   * `successCallback` — `Function` — Success handler for processing result
   * `errorCallback` — `Function` — Error handler for processing error
   * `sku` — `String` — Local SKU(product id).
   * `storeName` — `String` — Android store name, see [ConfigIAB.StoreNameEnum on Wiki][wiki_page]
   * `storeSku` — `String` — SKU in android store<br>

#### Example code
```javascript
    SKU_PREMIUM = "sku_premium";
    SKU_SUBSCRIPTION = "sku_subscription";
    CommonIAB.mapSku(commonSuccessHandler, commonErrorHandler, SKU_PREMIUM,
		     ConfigIAB.StoreNameEnum.GOOGLE, "sku_premium_google");
    CommonIAB.mapSku(commonSuccessHandler, commonErrorHandler, SKU_SUBSCRIPTION,
		     ConfigIAB.StoreNameEnum.GOOGLE, "sku_subscription_google");

    CommonIAB.mapSku(commonSuccessHandler, commonErrorHandler, SKU_PREMIUM,
		     ConfigIAB.StoreNameEnum.YANDEX, "sku_premium_yandex");
    CommonIAB.mapSku(commonSuccessHandler, commonErrorHandler, SKU_SUBSCRIPTION,
		     ConfigIAB.StoreNameEnum.YANDEX, "sku_subscription_yandex");
```
### Do purchases/subscriptions
`CommonIAB.purchaseProduct(successCallback, errorCallback, sku, developerPayload)`
Purchases the product with the selected SKU and developerPayload The function passes one argument to successCallback: {Object} purchased item in json

 * **Parameters:**
   * `successCallback` — `Function` — Success handler for processing result
   * `errorCallback` — `Function` — Error handler for processing error
   * `sku` — `String` — SKU product id
   * `developerPayload` — `String` — Token to verify your purchase request to store

`CommonIAB.purchaseSubscription(successCallback, errorCallback, sku, developerPayload)`

Purchases the subscription with the selected SKU and developerPayload The function passes one argument to successCallback: {Object} purchased item in json

 * **Parameters:**
   * `successCallback` — `Function` — Success handler for processing result
   * `errorCallback` — `Function` — Error handler for processing error
   * `sku` — `String` — SKU product id
   * `developerPayload` — `String` — token to verify your subscription request to store

`CommonIAB.consumeProduct(successCallback, errorCallback, purchase)`

Consumes the purchased product The function passes one argument to successCallback: {Object} purchased item in json

 * **Parameters:**
   * `successCallback` — `Function` — Success handler for processing result
   * `errorCallback` — `Function` — Error handler for processing error
   * `purchase` — `String` — Purchased product info in json

#### Example code
```javascript
/** Purchases items with selected SKUs
 */
function purchaseProduct() {
	developerPayload = "";
	CommonIAB.purchaseProduct(purchaseProductSuccessHandler, purchaseProductErrorHandler,
		    SKU_PREMIUM, developerPayload);
}

/** Consumes selected purchased item.
 */
function consumeProduct(lastPurchasedItem) {
        CommonIAB.consumeProduct(commonSuccessHandler, commonErrorHandler, lastPurchasedItem);
}

/** Purchases subscription with selected SKUs, use real SKU.
 *  Does not have Google fake SKUs for subscription.
 */
function purchaseSubscription() {
	// need to use only actual SKUs,standard test SKUs from Google do not work.
        developerPayload = "";
	CommonIAB.purchaseSubscription(commonSuccessHandler, commonErrorHandler, SKU_SUBSCRIPTION, developerPayload);
}
```


### Manage purchases/products

### Helpfull methods

# Support

## Reporting issues

If you have an issue with the plugin please check the following first:
- You are using the latest version of the Plugin Javascript & platform-specific Java from this repository.
- You have installed the Javascript & platform-specific Java correctly.
- You have included the correct version of the cordova Javascript and CommonIABPlugin.js and got the path right.
- You have registered the plugin properly in `config.xml`.

If you still cannot get something to work:
- Make the simplest test program you can to demonstrate the issue, including completely self-contained, i.e. it is using no extra libraries beyond cordova & CommonIABPlugin.js;

Then you can post the issue to the [raise a new issue](https://github.com/ClausKinz/CommonIAB/issues/new).

## Community

If you have any questions about the plugin please post it to the [http://stackoverflow.com/ with CommonIAB tag](http://stackoverflow.com/).

## Support priorities

**High priority:**

1. Stability is first: immediate resolution or workaround for stability issues (crashing) is the goal.
2. Correctness: any issue with correctness should result in a new testcase together with the bug fix.

**Low priority:** issues with the API or application integration will be given lower priority.


## Contributing

**WARNING:** Please do NOT propose changes from your `master` branch. In general changes will be rebased using `git rebase` or `git cherry-pick` and not merged.

- Testimonials of apps that are using this plugin would be especially helpful.
- Reporting issues at [https://github.com/ClausKinz/CommonIAB / issues](https://github.com/ClausKinz/CommonIAB/issues) can help improve the quality of this plugin.
- Patches with bug fixes are helpful, especially when submitted with test code.
- Other enhancements welcome for consideration, when submitted with test code and will work for all supported android stores. Increase of complexity should be avoided.
- All contributions may be reused by [@ClausKinz (Claus Schmidt)](https://github.com/ClausKinz) under another license in the future. Efforts will be taken to give credit for major contributions but it will not be guaranteed.

#How to involved:
1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request

## Code Style  for Contributors
Please use the following Code Style:
- [Google Code Style Guidelines for Contributors][code_style_java]
- [Google JavaScript Style Guide][code_style_js]

## Major branches

- `common-src` - source for Android version
- `master-src` - source for Android version
- `master-rc` - pre-release version, including source for CommonIAB library classes
- `master` - version for release, will be included in Cordova build.


## License

This software is released under the [Apache 2.0 License][apache2_license].

© 2014 Claus Schmidt All rights reserved


[cordova]: https://cordova.apache.org
[android_notification_guide]: http://developer.android.com/google/play/billing/billing_overview.html
[apache_device_plugin]: https://github.com/apache/cordova-plugin-device
[CLI]: http://cordova.apache.org/docs/en/3.0.0/guide_cli_index.md.html#The%20Command-line%20Interface
[PGB]: http://docs.build.phonegap.com/en_US/3.3.0/index.html
[PGB_plugin]: https://build.phonegap.com/plugins/413
[changelog]: RELEASENOTES.md
[todo_list]: ../../issues/1
[init]: #initializes_the_library_using_ConfigIAB
[mapSku]: #does_association_between_local_sku_store_sku
[unbindService]: #removes_event_handler_and_dispose_the_library
[isDebugLog]: #checks_debug_mode_indication
[SetDebugMode]: #set_on_off_debug_mode
[getProductDetails]: #gets_all_owned_items_from_android_store
[areSubscriptionsSupported]: #are_subscriptions_supported
[purchaseProduct]: #purchases_product_with_associated_sku
[purchaseSubscription]: #purchases_subscription_with_selected_sku
[consumeProduct]: #consumes_the_product_subscription
[getPurchases]: #returns_all_purchases_from_inventory
[getAvailableProducts]: #get_available_products_from_inventory
[openiab]: http://onepf.org
[code_style_java]:http://source.android.com/source/code-style.html
[code_style_js]:https://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml#Wrapper_objects_for_primitive_types
[examples]: #examples
[google_play]: https://play.google.com/store
[samsung_store]: http://apps.samsung.com/mars/main/getMain.as?COUNTRY_CODE=USA
[yandex_store]: http://store.yandex.com/
[nokia_store]: http://developer.nokia.com/nokia-x/publish-your-app
[slideme_store]: http://slideme.org/
[appland_store]: http://www.applandinc.com/app-store/
[appltoid_store]: http://m.aptoide.com/
[appmall_store]: http://www.openmobileww.com/#!appmall/cunq
[apache2_license]: http://opensource.org/licenses/Apache-2.0
[wiki_page]:https://github.com/ClausKinz/CommonIAB/wiki
