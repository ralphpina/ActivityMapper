# ActivityMapper
Maps your activity. Uses Parse for persistence and MapBox to display maps.

## Running
To run this project you will need to add your own ```sdk_keys.xml``` file in ```mobile/src/main/res/values/sdk_keys.xml```. This file is gitignored, so it will not sync when you pull this repo. As you can see, this file is used to store keys to your MapBox and Parse accounts.

```xml
<?xml version="1.0" encoding="utf-8"?>		
<resources>		
  <string name="map_box_sdk_token">Your MapBox SDK Token</string>		
  <string name="parse_application_id">Your Parse Application Id</string>		
  <string name="parse_client_key">Your Parse Client Key</string>		
</resources>
```

## About
This is just a side project. It is used to show potential companies my work, and to learn about various Android subjects I am interested in. Yes this app may be consider creepy since it will collect your location and activity type and sync it to a cloud provider. However, since you'll be building it with your own Parse keys you'll have full control of your data. 

## Technologies Used
- MapBox to display your activity locations.
- Parse to persist the data the app collects.
- Google Play Service's FusedLocationManager and ActivityRecognition APIs to collect the data.
- A persistent service to track the data.
- A Broadcast receiever to start the service on boot in case the phone is turned off.

***Testing:***

Testing is famously difficult in Android. While this app is nowhere cutting age in this area, it is being tested.
- Android Instrumentation testing using newer libraries and JUnit 4.
- Android JUnit testing - not much of it.
- While I have used Robolectric and Robotium in the past, and I am a big fan, I am not currently using them and instead using the stock tools provided by the Android team.

##TODO
- ~~Build model for Parse~~
- ~~Service to track location and activity~~
- ~~Activity recognition using Google Play Services~~
- ~~Persist data to Parse~~
- Add Marshmallow permissions manager
- ~~Display data for last 24 hours in map~~
- ~~Different color lines in map for different activities~~
- Different filters for times and activities
- Android Wear activity to display last 24 hours
- Adding Dagger 2 for testing
- ~~Mock Parse during testing~~
- Filter FusedLocationManager to throw out bad data. It shouldn't have these jagged lines: https://www.evernote.com/l/ABAMV20CcElCeKgdn9WAZ0tFC8Fn9deF-Qc
- Submit to the Play Store as beta?
