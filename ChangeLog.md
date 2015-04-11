Version **1.2.0** (not released yet):

  * performance improvments in XML reader
  * improvments to zip file reading
  * fixed gpx file handler (if path contains multiple dots)
  * fixed field note upload to bcaching.com
  * added loc file handler
  * improved loc file support (added difficulty, terrain, container)
  * german translation added (wip)
  * added support for gpx 1.0.2 features (wip)
  * added personal notes
  * added field note history
  * added support for encrypted log entries
  * added option to hide caches with 'Found it' field notes

Version **1.1.0** (15. October 2011):

  * internal refactoring of classes (moved all classes to the org.opengpx namespace)
  * fixed date parsing for opencaching.us gpx files
  * fixed parsing of opencaching.de gpx files
  * fixed bcaching.com detail download
  * removed bcaching.com test site in preferences
  * added new welcome dialog
  * added possibility to specify multiple import folders
  * added logo to cache list background
  * osmdroid updated to latest version
  * added option to choose waypoint click action
  * added possibility to read gpx/loc/png/jpg files from zip files
  * re-write of preferences
  * added zip- and gpx-file association with opengpx

Version **1.0.1** (8. December 2010)

  * added GCVotes in bcaching.com search results
  * added possibility to update gcvotes when importing gpx files
  * added option to delete gpx files after importing
  * added progressbar when adding items to Google Maps (fixes ANR error)
  * limit caches shown on Google Maps to <Cache filter limit> caches.
  * fixed OSM minimap size on high res displays
  * added Scalebar to OSM view

Version **1.0.0** (26. November 2010)

  * added App2SD support (Froyo only)
  * added Compass & Navigation provider preferences
  * osmdroid map follows current position
  * added compass to osmdroid
  * fixed cache/waypoint icon resizing when zooming in/out
  * added internal Navigation view
  * replaced OSM zoom icons by internal ZoomControls
  * added minimap to OSM view (enable/disable via preferences)
  * new difficulty / terrain icons
  * fixed waypoint labeling in map views
  * fixed bug when adding waypoint ([Bug #3064759](https://code.google.com/p/opengpx/issues/detail?id=3064759))
  * fixed reading of caches with multiple waypoints from bcaching.com
  * added unit systems (metric, imperial)
  * updated waypoint information dialog
  * added possibility to update GCVotes

Version **0.8** (30. July 2010)

  * lots of bug fixes in bcaching integration
  * archived and disabled caches are marked properly now
  * changed default map provider to Google
  * Google maps does show cache / waypoint icons now
  * fixed image size (spoiler images) on higher resolution displays
  * added cache type icon to cache description
  * replaced andnav by osmdroid (included in apk file)
  * added diff/terr icons to cache description (not perfect yet)
  * new preferences activity (standard Android preference design)
  * added links to cache owner profile
  * fixed delete cache bug (https://sourceforge.net/tracker/?func=detail&aid=3025316&group_id=246463&atid=1391323)
  * added my location to maps

Version **0.7** (17. May 2010)

  * db4o updated to latest 7.12 build (#14217)
  * required android version changed to 1.6
  * converted GPX reader to use vtd-xml for increased performance
  * added Google Maps navigation support
  * added basic support for BCaching.com queries
  * prevented the app from using location data from more than 5 minutes ago
  * added the ability to set your home coordinates from GPS/Network location
  * fixed a problem with commas and dots in coordinate format with german locale
  * added full BCaching.com advanced search support
  * added BCaching.com field notes support, online and offline supported
  * enabled the sort functions in the search list view
  * added find by name/GCID for BCaching.com
  * added the ability to upload and clear the field notes database
  * added GPS accuracy to some of the screens
  * added the ability to save search results to the main database for later offline use
  * fixed icon size on higher res devices

Version **0.6** (10. Feb. 2010)

  * gps is being disable in onPause() method
  * added add/delete waypoint functionality
  * fixed tools don't throw an error on empty texts
  * added clear & help buttons to calculation tool
  * added possibility to lock screen orientation
  * added LCM (least common multiple) & GCD (greatest common divisor) implementations to calculation tool
  * updated db4o to version 7.12
  * menu button does show context sensitive menu's now (different menus at cache description, waypoints ...)
  * small improvements in text2number tool

Version **0.5** (20. Oct. 2009)

  * replaced cache code by cache name in cache deletion dialog
  * improved performance when opening a cache
  * added more information to markers in map views (cache type, container, difficulty, terrain)
  * added "Exit" menu option
  * fixed crashes with variable names 'e' and 'pi' (standard mathematical constants)
  * enabled text filter in cache list (had to add a keyboard menu entry for that)
  * added support for gctour gpx files (which is actually a bug fix for wrong data types for log IDs, thanks goes to Josias Thöny for reporting & fixing!)
  * enabled fast scrolling in cache list

Version **0.4** (20. Sept. 2009)

  * added [[CompassNavi](CompassNavi.md)] as replacement for the Radar component
  * added possibility to delete caches
  * improved cache / container / log type parsing
  * added support for geotoad gpx files
  * some minor bug fixes

Version **0.3** (09. Sept. 2009)

  * fixed a problem when switching to an already running instance
  * added sorting by name and distance (GPS or network location)
  * using current position as mapcenter for Google maps
  * added an alert dialog (yes/no) for clearing the database
  * fixed info.txt path (is using datapath property now)
  * added 3 missing log type icons
  * added support for multiple databases
  * fixed database errors when rotating the display

Version **0.2** (02. Sept. 2009)

  * added username to settings (not used yet)
  * changed cache storage from files to [DB4O](http://www.db4o.com)
  * command toolbox is somewhat nicer now (better alignment, human readable names)
  * added possibility to use variables in calculations
  * radar view is being show when clicking on a waypoint
  * added "not implemented" messages to not implemented features
  * added a close button to the about box
  * added Satellite, Traffic and Street view to Google Map view
  * added detailed waypoint informaton (incl. distance to other waypoints)
  * added navigation to waypoint (AndNav2/OSM only!)
  * added support for geocaching.hu gpx files
  * added client (Windows, Mac, Linux) cache database management tool
  * some minor fixes (cache types and container types)

Version **0.1** (26. Aug. 2009):

  * initial release