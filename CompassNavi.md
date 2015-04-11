## Features ##


## Use CompassNavi intent ##

Calling CompassNavi from your own code:

```
  Intent intCompassNavi = new Intent("mpr.compassNavi.SHOW_NAVI");
  intCompassNavi.putExtra("latitude", 48.20852);  // Latitude in decimal format (double or float value)
  intCompassNavi.putExtra("longitude", 16.37298); // Longitude in decimal format (double or float value)
  intCompassNavi.putExtra("name", "St. Stephan's Cathedral"); // Waypoint name (string)
  startActivity(intCompassNavi);
```

Usage as RADAR:

```
  Intent intRadar = new Intent("com.google.android.radar.SHOW_RADAR");
  intRadar.putExtra("latitude", 48.20852);  // Latitude as float value
  intRadar.putExtra("longitude", 16.37298); // Longitude as float value
  startActivity(intRadar);
```

## ToDo ##

(last update: 04. Aug 2010)

  * Fix: Satellite count is 0 sometimes
  * <strike>disable screen idle time</strike> (done, Martin)
  * work without target (compass only mode)
  * add speed, g-force data ?
  * <strike>add US units (feet, inch)</strike> (done, Martin)
  * customizable coordinate format
  * routing capabilities
  * <strike>provide SHOW_RADAR intent</strike> (Done, Martin)

## ChangeLog ##

Version **0.4** (04. Aug 2010):

  * added possibility to keep screen on
  * added preferences to tune GPS update interval

Version **0.3** (17. May 2010):

  * CompassNavi listens to the SHOW\_RADAR intent (com.google.android.radar.SHOW\_RADAR)
  * new preferences layout
  * added support for US metrics (feet)