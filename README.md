Union College Trolley Tracker
=============================

Created as a mobile version of http://trolley.union.edu/, for more information on that website please go to
http://trolley.union.edu/about.php

This displays where the trolley on Union's campus is at any time along with the path that it takes.
This allows for students to more easily use the service.

**Using/modifying this code for yourself:**  
If you wish to use this code you **must** provide a Google Maps API key. The way to do this is by having
a file in /res/values called api_key.xml. The only thing in that file is this:

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="api_key">My Private API Key</string>
    </resources>
    
So the easiest way to get this app up and running is to make an api_key.xml file with your own key in it.  
Another way to do this would be to add a string named "api_key" to your strings.xml file with your private API Key
as the only text in the string.

For information on obtaining a Google Maps API Key, please go here:
https://developers.google.com/maps/documentation/android/mapkey