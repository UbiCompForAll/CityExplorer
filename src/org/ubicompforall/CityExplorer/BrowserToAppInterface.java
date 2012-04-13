/***
 * From StackOverflow
 * 
 * Please DO NOT use your own custom scheme like that!!! URI schemes are a network global namespace. Do you own the "anton:" scheme world-wide? No? Then DON'T use it
 * One option is to have a web site, and have an intent-filter for a particular URI on that web site. For example, this is what Market does to intercept URIs on its web site:

        <intent-filter>
          <action android:name="android.intent.action.VIEW" />
          <category android:name="android.intent.category.DEFAULT" />
          <category android:name="android.intent.category.BROWSABLE" />
          <data android:scheme="http" android:host="market.android.com"
                android:path="/search" />
        </intent-filter>

Alternatively, there is the "intent:" scheme. This allows you to describe nearly any Intent as a URI, which the browser will try to launch when clicked.
To build such a scheme, the best way is to just write the code to construct the Intent you want launched, and then print the result of

  intent.toUri(Intent.URI_INTENT_SCHEME).

You can use an action with this intent for to find any activity supporting that action.
The browser will automatically add the BROWSABLE category to the intent before launching it, for security reasons; it also will strip any explicit component you have supplied for the same reason.

The best way to use this, if you want to ensure it launches only your app, is with your own scoped action and using Intent.setPackage() to say the Intent will only match your app package.

Trade-offs between the two:

    http URIs require you have a domain you own. The user will always get the option to show the URI in the browser.
     It has very nice fall-back properties where if your app is not installed, they will simply land on your web site.

    intent URIs require that your app already be installed and only on Android phones.
    The allow nearly any intent (but always have the BROWSABLE category included and not supporting explicit components).
    They allow you to direct the launch to only your app without the user having the option of instead going to the browser or any other app.
 */

package org.ubicompforall.CityExplorer;


/***
 * @author satre
 * 
 */
public class BrowserToAppInterface {

}
