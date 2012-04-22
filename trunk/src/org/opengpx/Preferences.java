package org.opengpx;

import java.util.ArrayList;
import java.util.Arrays;

import org.opengpx.lib.UnitSystem;

import org.opengpx.MapProvider;
import org.opengpx.R;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Preferences 
{

	private Context mContext;
	private SharedPreferences mSharedPreferences;

	// Preferences Keys
	// private static final String	PREFS_KEY_HOME_LATITUDE	= "HomeLatitude";
	// private static final String	PREFS_KEY_HOME_LONGITUDE = "HomeLongitude";
	public static final String PREFS_KEY_SET_HOME_COORDINATES = "SetHomeCoordinates";
	
	private static final String PREFS_KEY_HOME_COORDINATES = "HomeCoordinates";
	private static final String	PREFS_KEY_DATA_FOLDER = "DataFolder";
	private static final String	PREFS_KEY_USE_WEB_VIEW = "UseWebView";
	private static final String	PREFS_KEY_MAP_PROVIDER = "MapProvider";
	private static final String	PREFS_KEY_COORDINATE_FORMAT = "CoordinateFormat";
	private static final String	PREFS_KEY_CACHE_LIMIT = "CacheLimit";
	private static final String	PREFS_KEY_HIDE_CACHES_FOUND = "HideCachesFound";
	// private static final String	PREFS_KEY_USERNAME = "Username";
	private static final String	PREFS_KEY_DB_FILENAME = "DatabaseFilename";
	private static final String	PREFS_KEY_SORT_ORDER = "SortOrder";
	private static final String	PREFS_KEY_SCREEN_ORIENTATION = "ScreenOrientation";
	private static final String	PREFS_KEY_BCACHING_USERNAME = "BCachingUsername";
	private static final String	PREFS_KEY_BCACHING_PASSWORD = "BCachingPassword";
	private static final String	PREFS_KEY_BCACHING_MAX_CACHES = "BCachingMaxCaches";
	private static final String	PREFS_KEY_BCACHING_MAX_DISTANCE = "BCachingMaxDistance";
	// private static final String	PREFS_KEY_BCACHING_TEST_SITE = "BCachingTestSite";
	private static final String	PREFS_KEY_SHOW_EMPTYDB_HELP = "ShowEmptyDBHelp";
	private static final String PREFS_KEY_COMPASS_PROVIDER = "CompassProvider";
	private static final String PREFS_KEY_NAVIGATION_PROVIDER = "NavigationProvider";
	private static final String PREFS_KEY_MAP_KEEP_SCREEN_ON = "MapKeepScreenOn";
	private static final String PREFS_KEY_OSM_USE_MINIMAP = "osmUseMinimap";
	private static final String PREFS_KEY_UNIT_SYSTEM = "UnitSystem";
	private static final String PREFS_KEY_AUTO_UPDATE_VOTES = "AutoUpdateVotes";
	private static final String PREFS_KEY_BACKUP_GPX_FILES = "BackupGpxFiles";
	private static final String PREFS_KEY_IMPORT_PATH_LIST = "ImportPathList";
	private static final String PREFS_KEY_WAYPOINT_ACTION = "WaypointAction";
	
	// Preferences default values
	// private static final float   PREFS_DEFAULT_HOME_LATITUDE = 46.59327f;
	// private static final float   PREFS_DEFAULT_HOME_LONGITUDE = 14.27323f;
	private static final String  PREFS_DEFAULT_HOME_COORDINATES = "";
	private static final String  PREFS_DEFAULT_DATA_FOLDER = "/sdcard/gpx";
	private static final boolean PREFS_DEFAULT_USE_WEB_VIEW = true;
	private static final MapProvider PREFS_DEFAULT_MAP_PROVIDER = MapProvider.OpenStreetMap;
	private static final String  PREFS_DEFAULT_COORDINATE_FORMAT = "DM";
	private static final int     PREFS_DEFAULT_CACHE_LIMIT = 100;
	private static final boolean PREFS_DEFAULT_HIDE_CACHES_FOUND = true;
	// private static final String  PREFS_DEFAULT_USERNAME = "Unknown";
	private static final String  PREFS_DEFAULT_DB_FILENAME = "database.db4o";
	private static final int     PREFS_DEFAULT_SORT_ORDER = CacheDatabase.SORT_ORDER_NAME;
	// private static final int     PREFS_DEFAULT_SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	private static final String  PREFS_DEFAULT_BCACHING_USERNAME = "";
	private static final String  PREFS_DEFAULT_BCACHING_PASSWORD = "";
	private static final int     PREFS_DEFAULT_BCACHING_MAX_CACHES = 50;
	private static final int     PREFS_DEFAULT_BCACHING_MAX_DISTANCE = 10;
	// private static final boolean PREFS_DEFAULT_BCACHING_TEST_SITE = false;
	private static final boolean PREFS_DEFAULT_SHOW_EMPTYDB_HELP = true;
	private static final CompassProvider PREFS_DEFAULT_COMPASS_PROVIDER = CompassProvider.CompassNavi;
	private static final NavigationProvider PREFS_DEFAULT_NAVIGATION_PROVIDER = NavigationProvider.Google;
	private static final boolean PREFS_DEFAULT_MAP_KEEP_SCREEN_ON = false;
	private static final boolean PREFS_DEFAULT_OSM_USE_MINIMAP = true;
	private static final UnitSystem PREFS_DEFAULT_UNIT_SYSTEM = UnitSystem.Metric;
	private static final boolean PREFS_DEFAULT_AUTO_UPDATE_VOTES = false;
	private static final boolean PREFS_DEFAULT_BACKUP_GPX_FILES = true;
	private static final String PREFS_DEFAULT_IMPORT_PATH_LIST = "";
	private static final WaypointClickAction PREFS_DEFAULT_WAYPOINT_ACTION = WaypointClickAction.InternalMap;

	/**
	 * 
	 * @param context
	 */
	public Preferences(Context context)
	{
		this.mContext = context;
		this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		PreferenceManager.setDefaultValues(context, R.xml.preferences, true);
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	private void putString(String key, String value)
	{
		final Editor editor = this.mSharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();				
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 */
	private void putInt(String key, int value)
	{
		final SharedPreferences.Editor editor = this.mSharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDataFolder()
	{
		return this.mSharedPreferences.getString(PREFS_KEY_DATA_FOLDER, PREFS_DEFAULT_DATA_FOLDER);
	}

	/**
	 * 
	 * @return
	 */
	public String getDatabaseFilename()
	{
		return this.mSharedPreferences.getString(PREFS_KEY_DB_FILENAME, PREFS_DEFAULT_DB_FILENAME);
	}
	
	/**
	 * 
	 * @param filename
	 */
	public void setDatabaseFilename(String filename)
	{
		this.putString(PREFS_KEY_DB_FILENAME, filename);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getScreenOrientation()
	{
		final String[] arrScreenOrientationValues = this.mContext.getResources().getStringArray(R.array.screen_orientation_values);
		final ArrayList<String> alScreenOrientationValues = new ArrayList<String>(Arrays.asList(arrScreenOrientationValues));		alScreenOrientationValues.get(0);
		final String strScreenOrientation = this.mSharedPreferences.getString(PREFS_KEY_SCREEN_ORIENTATION, alScreenOrientationValues.get(0));
		final int intIndex = alScreenOrientationValues.indexOf(strScreenOrientation);
		
		int intScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED; 
		switch (intIndex)
		{
		case 0:
			intScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
			break;
		case 1:
			intScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			break;
		case 2:
			intScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			break;
		}
		
		return intScreenOrientation;
	}

	/**
	 * 
	 * @return
	 */
	public CoordinateFormat getCoordinateFormat()
	{
		final String strCoordinateFormat = this.mSharedPreferences.getString(PREFS_KEY_COORDINATE_FORMAT, PREFS_DEFAULT_COORDINATE_FORMAT); 
		return CoordinateFormat.valueOf(strCoordinateFormat);
	}
	
	/**
	 * 
	 * @return
	 */
	public Coordinates getHomeCoordinates()
	{
		final String strCoordinates = this.mSharedPreferences.getString(PREFS_KEY_HOME_COORDINATES, PREFS_DEFAULT_HOME_COORDINATES);
		final Coordinates coordinates = new Coordinates();
		coordinates.parseFromText(strCoordinates);
		return coordinates;
	}
	
	/**
	 * 
	 * @param coordinates
	 */
	public void setHomeCoordinates(Coordinates coordinates)
	{
		final CoordinateFormat coordinateFormat = this.getCoordinateFormat();
		this.putString(PREFS_KEY_HOME_COORDINATES, coordinates.toString(coordinateFormat));
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getShowEmptyDbHelp()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_SHOW_EMPTYDB_HELP, PREFS_DEFAULT_SHOW_EMPTYDB_HELP);
	}
	
	/**
	 * 
	 * @return
	 */
	public MapProvider getMapProvider()
	{
		final String strMapProvider = mSharedPreferences.getString(PREFS_KEY_MAP_PROVIDER, PREFS_DEFAULT_MAP_PROVIDER.toString());
		return MapProvider.valueOf(strMapProvider);
	}

	/**
	 * 
	 * @return
	 */
	public CompassProvider getCompassProvider()
	{
		final String strCompassProvider = mSharedPreferences.getString(PREFS_KEY_COMPASS_PROVIDER, PREFS_DEFAULT_COMPASS_PROVIDER.toString());
		return CompassProvider.valueOf(strCompassProvider);
	}

	/**
	 * 
	 * @return
	 */
	public NavigationProvider getNavigationProvider()
	{
		final String strNavigationProvider = mSharedPreferences.getString(PREFS_KEY_NAVIGATION_PROVIDER, PREFS_DEFAULT_NAVIGATION_PROVIDER.toString());
		return NavigationProvider.valueOf(strNavigationProvider);
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getMapKeepScreenOn()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_MAP_KEEP_SCREEN_ON, PREFS_DEFAULT_MAP_KEEP_SCREEN_ON);
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getOsmUseMinimap()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_OSM_USE_MINIMAP, PREFS_DEFAULT_OSM_USE_MINIMAP);		
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSortOrder()
	{
		return this.mSharedPreferences.getInt(PREFS_KEY_SORT_ORDER, PREFS_DEFAULT_SORT_ORDER);
	}
	
	/**
	 * 
	 * @param sortOrder
	 */
	public void setSortOrder(int sortOrder)
	{
		this.putInt(PREFS_KEY_SORT_ORDER, sortOrder);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCacheLimit()
	{
		final String strMaxCaches = this.mSharedPreferences.getString(PREFS_KEY_CACHE_LIMIT, "");
		Integer intMaxCaches = PREFS_DEFAULT_CACHE_LIMIT;
		if (strMaxCaches.length() > 0)
		{
			intMaxCaches = Integer.parseInt(strMaxCaches);
		}
		return intMaxCaches;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getHideCachesFound()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_HIDE_CACHES_FOUND, PREFS_DEFAULT_HIDE_CACHES_FOUND);
	}
	/**
	 * 
	 * @return
	 */
	public String getBCachingUsername()
	{
		return this.mSharedPreferences.getString(PREFS_KEY_BCACHING_USERNAME, PREFS_DEFAULT_BCACHING_USERNAME);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBCachingPassword()
	{
		return this.mSharedPreferences.getString(PREFS_KEY_BCACHING_PASSWORD, PREFS_DEFAULT_BCACHING_PASSWORD);
	}
	
	/**
	 * 
	 * @return
	 */
	/* public Boolean getUseBCachingTestSite()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_BCACHING_TEST_SITE, PREFS_DEFAULT_BCACHING_TEST_SITE);
	} */
	
	/**
	 * 
	 * @return
	 */
	public int getBCachingMaxCaches()
	{
		final String strBCachingMaxCaches = this.mSharedPreferences.getString(PREFS_KEY_BCACHING_MAX_CACHES, "");
		Integer intBCachingMaxCaches = PREFS_DEFAULT_BCACHING_MAX_CACHES;
		if (strBCachingMaxCaches.length() > 0)
		{
			intBCachingMaxCaches = Integer.parseInt(strBCachingMaxCaches);
		}
		return intBCachingMaxCaches;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getBCachingMaxDistance()
	{
		final String strBCachingMaxDistance = this.mSharedPreferences.getString(PREFS_KEY_BCACHING_MAX_DISTANCE, "");
		Integer intBCachingMaxDistance = PREFS_DEFAULT_BCACHING_MAX_DISTANCE;
		if (strBCachingMaxDistance.length() > 0)
		{
			intBCachingMaxDistance = Integer.parseInt(strBCachingMaxDistance);
		}
		return intBCachingMaxDistance;
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean getUseWebView()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_USE_WEB_VIEW, PREFS_DEFAULT_USE_WEB_VIEW);	
	}
	
	/**
	 * 
	 * @return
	 */
	public UnitSystem getUnitSystem()
	{
		final String strUnitSystem = mSharedPreferences.getString(PREFS_KEY_UNIT_SYSTEM, PREFS_DEFAULT_UNIT_SYSTEM.toString());
		return UnitSystem.valueOf(strUnitSystem);
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getAutoUpdateVotes()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_AUTO_UPDATE_VOTES, PREFS_DEFAULT_AUTO_UPDATE_VOTES);	
	}

	/**
	 * 
	 * @return
	 */
	public Boolean getBackupGpxFiles()
	{
		return this.mSharedPreferences.getBoolean(PREFS_KEY_BACKUP_GPX_FILES, PREFS_DEFAULT_BACKUP_GPX_FILES);	
	}

	/**
	 * 
	 * @return
	 */
	public String[] getImportPathList()
	{
		return this.mSharedPreferences.getString(PREFS_KEY_IMPORT_PATH_LIST, PREFS_DEFAULT_IMPORT_PATH_LIST).split("\n");
	}

	/**
	 * 
	 * @return
	 */
	public WaypointClickAction getWaypointClickAction()
	{
		final String strWaypointClickAction = mSharedPreferences.getString(PREFS_KEY_WAYPOINT_ACTION, PREFS_DEFAULT_WAYPOINT_ACTION.toString());
		return WaypointClickAction.valueOf(strWaypointClickAction);
	}
}
