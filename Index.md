# OpenGPX - You Are The Search Engine. #

Current Version: **1.1.0** (Release 15.10.2011) [ChangeLog](ChangeLog.md)

**Info**: ScreenShots, PlannedFeatures, CompassNavi, KnownIssues, [Einführung](http://cache.ath.cx/opengpx/) (deutsch)

## Usage ##

Variant 1:

  1. Copy gpx files to the folder /sdcard/gpx (or /sdcard/download)
  1. [Optional](Optional.md) Create / Edit the text file /sdcard/gpx/info.txt (e.g. your personal cache search order)
  1. Run OpenGPX

Variant 2 (faster imports):

  1. Create a cache database by using the [[Cache Database Management](Client.md)] tool
  1. Copy database.db4o to /sdcard/gpx/database

Variant 3 (Online):

  1. Use the "Search Online" feature (requires a free account at http://www.bcaching.com)

## Features ##

  * Lists all caches found in all GPX/LOC files (sorted by name or distance)
  * Uses WebView for HTML cache descriptions
  * Supports [geocaching.com](http://www.geocaching.com) (single GPX and pocket queries), [opencaching.de](http://opencaching.de), [geocaching.hu](http://geocaching.hu), [GeoToad](http://code.google.com/p/geotoad/), [GCTour](http://gctour.madd.in/) files
  * [[Cache Database Management](Client.md)] for managing caches with clients
  * Multiple cache database support
  * Shows the main information about each cache (info, description, waypoints, logs)
  * Extracts coordinates (Waypoints) from description and logs
  * Internal image viewer for spoiler images (filenames have to contain one word of the cache name or the cache code)
  * Uses [[CompassNavi](CompassNavi.md)] activity for simple compass / GPS navigation
  * Radar view (context menu on Waypoints) - requires [Radar](http://www.cyrket.com/package/com.google.android.radar) library!
  * [Google map](http://map.google.com) / [OpenStreetMap](http://www.andnav.org/) view (All caches, all waypoints of one cache, single waypoint view [menu on Waypoints](context.md))
  * Detailed waypoint information (incl. distance and bearing to other waypoints)
  * Navigation to waypoints with AndNav2
  * Caching Tools (Calculator, Groundspeak en/decode, Caesar en/decode, Morse en/decode, Prime factorization, Text2Number, Roman numbers, Checksum, Sum Ascii, Vanity numbers). Calculation tool supports variables (a=5, b=a+2)
  * Online Search via bcaching.com (requires a free account at http://www.bcaching.com)
  * Log finds vie bcaching.com field notes

## Reviews & Blog Entries ##

  * [Androids.lv](http://www.androids.lv/2009/09/paperless-geocaching-opengpx/) (latvian)
  * [BlogAusGraz](http://blogausgraz.wordpress.com/2009/10/05/schnitzeljagt-mit-dem-android-geocaching/) (german)
  * [Google Produkt Kompass](http://google-produkt-kompass.blogspot.com/2009/10/geocoaching-mit-android.html) (german, actually just a short mention at the end of the article)
  * [Einführung in OpenGPX](http://cache.ath.cx/opengpx/) (german)

## Notes ##

### Crash logs ###

  * <strike>Crash exceptions will be uploaded to <a href='http://flux.dnsdojo.org/opengpx/trace'>http://flux.dnsdojo.org/opengpx/trace</a> (if a network connection is available)</strike> (disabled)

### Snapshots ###

  * I'm regularily building development snapshots for testing. If you want to access those snapshots, I'll invite you to the Dropbox folder, just [drop me a note](mailto:martin.preishuber@digiforge.at). If you're no Dropbox user, I'd be happy to invite you (and earn 250mb) ;-))