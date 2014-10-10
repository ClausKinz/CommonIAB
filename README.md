Cordova/PhoneGap Common in-app billing/purchases Plugin
==================================

The essential purpose of In-app purchases are purchases made from within a mobile application. 
Users typically make an in-app purchase in order to access special content or features using in an app purchases such as special options, power-ups, virtual money, special characters, etc. The purchasing process is completed directly from within the app and is seamless to the user in most cases, with the mobile platform provider facilitating the purchase and taking a share of the money spent (usually in the range of 30% or so), with the rest going to the app developer.

### Examples of in-app purchases
In-app purcahses are ideally suited for application that use aditional optional model, such as access to aditional content, special options, sunscribtion.

### Plugin's Purpose
The purpose of the plugin is to create an Android stores independent javascript interface for [Cordova][cordova] based on [openiab][openiab] forked library. 

The following Android Stores are supported:
- [Google play][google_play]
- [Samsung][samsung_store]
- [Nokia][nokia_store]
- [Yandex.Store][yandex_store]
- [SlideME][slideme_store]
- [Appland][appland_store]
- [Apptoid][appltoid_store]
- [AppMall][appmall_store]

## Supported Platforms
- **Android** *(SDK >=10)*<br>
See [In-app purchases Guide][android_notification_guide] for detailed informations and screenshots.


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
- See [CHANGELOG.md][changelog] to get the full changelog for the plugin.
- See the [v0.1.x TODO List][todo_list] for upcomming changes and other things.

## Using the plugin
The plugin creates the object ```CommonIAB``` with the following methods:

1. [CommonIAB.ini][[init]
2. [CommonIAB.mapSku][mapSku]]
2. [CommonIAB.isDebugLog][isDebugLog]
5. [CommonIAB.SetDebugMode][SetDebugMode]
6. [CommonIAB.getProductDetails][getProductDetails]
7. [CommonIAB.areSubscriptionsSupported][areSubscriptionsSupported]
8. [CommonIAB.purchaseProduct][purchaseProduct]
9. [CommonIAB.purchaseSubscription][purchaseSubscription]
10. [CommonIAB.consumeProduct][consumeProduct]
11. [CommonIAB.getPurchases][getPurchases]
12. [CommonIAB.getAvailableProducts][getAvailableProducts]

### Plugin initialization
The plugin and its methods are not available before the *deviceready* event has been fired.

```javascript
document.addEventListener('deviceready', function () {
    // window.plugin.notification.local is now available
}, false);
```


## Contributing

1. Fork it
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create new Pull Request


## License

This software is released under the [Apache 2.0 License][apache2_license].

© 2014 Claus Schmidt All rights reserved


[cordova]: https://cordova.apache.org
[android_notification_guide]: http://developer.android.com/google/play/billing/billing_overview.html
[apache_device_plugin]: https://github.com/apache/cordova-plugin-device
[CLI]: http://cordova.apache.org/docs/en/3.0.0/guide_cli_index.md.html#The%20Command-line%20Interface
[PGB]: http://docs.build.phonegap.com/en_US/3.3.0/index.html
[PGB_plugin]: https://build.phonegap.com/plugins/413
[changelog]: CHANGELOG.md
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
[examples]: #examples
[google_play]: https://play.google.com/store
[samsung_store]: http://apps.samsung.com/earth/main/getMain.as?COUNTRY_CODE=US
[yandex_store]: http://store.yandex.com/
[nokia_store]: http://developer.nokia.com/nokia-x/publish-your-app
[slideme_store]: http://slideme.org/
[appland_store]: http://www.applandinc.com/app-store/
[appltoid_store]: http://m.aptoide.com/
[appmall_store]: http://www.openmobileww.com/#!appmall/cunq
[apache2_license]: http://opensource.org/licenses/Apache-2.0
