@AUTHOR	Rune Sætre
@DATE	2011.11.15
@FILE	CityExplorer/README.txt

Permissions Needed:
-------------------
android.permission.INTERNET: To download the Google Maps
android.permission.ACCESS_NETWORK_STATE: To check if wifi is available for showing the Google Maps
android.permission.ACCESS_COARSE(FINE)_LOCATION: To show right area of the map

http://stackoverflow.com/questions/4907769/locationmanager-is-the-network-provider-always-enabled


Steps for compiling the Android app in your debug environment:
--------------------------------------------------------------
Follow the instructions here to make your own debug key:
http://code.google.com/android/add-ons/google-apis/mapkey.html

Alternative 1 (recommended)
- All users use the same debug.keystore
- Copy Git/CityExplorer/debug.keystore to $HOME/.android
- Check android:apiKey in the file CityExplorer/res/layout/maplayout.xml
  It should be "05MLhk0p9hp3JBgHGNN8QZVa-D8D3Pz6h4TuRjA"

Alternative 2 (the old way - every developer has to do it every time code is fetched from another PC):
- Replace the key in following line from CityExplorer/res/layout/maplayout.xml
- android:apiKey="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  with the key for the machine you compile on

BUGS in Android Development Toolkit (ADT)
-----------------------------------------
When "Graphical Layout" tab is "suddenly" replaced by "Design" (several times per day)
 it always helps deleting the project from the Workspace, and re-importing it from Git
(Don't forget to "commit" and "push" first!)

This then "always" leads to: org.xmlpull.v1.XmlPullParserException: Binary XML file line #5:
 <item> tag requires a 'drawable' attribute or child tag defining a drawable
which can be solved by restarting eclipse

When R.java gets completely out of sync, just run Project -> Clean... every time you think
 you have fixed the problematic res/*.xml files...

BUGS in EGit
============
Resource is out of sync with the file system: '/CityExplorer/.git/refs/remotes/origin/master'.
-Fixed by making a small change, "commit" and "push"

When conflicts are reported, but merge tool unavailable:
See: https://bugs.eclipse.org/bugs/show_bug.cgi?id=354099
Use Shell to:
  git merge origin/master


Howto debug SQL DB:
http://developer.android.com/guide/topics/data/data-storage.html#netw

