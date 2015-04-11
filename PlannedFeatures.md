## ToDo ##

**OpenGPX:** (last update: 5. Feb. 2012)

  * implement cache filter field (top of list)
  * add cache database stats (gc, opencaching caches)
  * change textview for info.txt to edittext
  * make text viewer / editor a window
  * <strike>handle encrypted logs (e.g. muri quelle)</strike> (done)
  * <strike>add notes to caches</strike> (done)
  * <strike>improve cache details for LOC files (Waypoint type)</strike> (done)
  * <strike>Filter out finds</strike> (done)
  * <strike>Mark caches found</strike> (done)
  * <strike>associate gpx/loc files with opengpx</strike> (done)
  * <strike>add</strike> / edit <strike>user-defined waypoints</strike> (use variables in coordinates?) (partly done)
  * multi-language support (multiple strings.xml)
  * use Location class for distance / bearing calculation
  * <strike>implement "follow me" on google maps (center to current position), show my position</strike> (done)
  * update cache stats when filtering / sorting / changing DB
  * <strike>add gcvotes (store separate from caches for performance reasons)</strike> (done)
  * download spoiler pics
  * <strike>fully support gcvotes</strike> (done)
  * calculate distance between all waypoints (waypoint oder?, check with cache "Vindobona")
  * filter cache types
  * toggle gps status ?
  * <strike>mark disabled / archived caches</strike> (done)
  * <strike>lock screen orientation</strike> (done)
  * import / export caches (send to user user?)
  * read caches from emails ?
  * <strike>Online Search</strike> (done)
  * <strike>BCaching Integration</strike> (done)
  * Open / import GPX files directly from websites
  * <strike>Better support for higher resolutions (icon size, font size)</strike> (done)
  * <strike>Use Google Maps as default map provider</strike> (done)
  * <strike>import caches from zip files</strike> (done)
  * <strike>use cache icons in maps</strike> (done)

## Internal changes ##

  * Implement Command interface and change commands to separate classes
  * <strike>Implement MapViewer interface</strike> (done)
  * add Logging (slf4j)
  * use webview for opencaching.de html logs