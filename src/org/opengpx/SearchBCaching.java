package org.opengpx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.opengpx.lib.AdvancedSearchData;
import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.geocache.FieldNote;
import org.opengpx.lib.geocache.LogEntry;
import org.opengpx.lib.geocache.TravelBug;
import org.opengpx.lib.geocache.Waypoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// import android.util.Log;

import com.bcaching.georg.Communication;
import org.opengpx.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchBCaching
{
	private Communication							comm;
	private Hashtable<String, String>	params	= new Hashtable<String, String>();
	private CacheDatabase							cacheDatabase;
	// private int												bcMaxCaches;
	// private int												bcMaxDistance;
	
	private static final Logger mLogger = LoggerFactory.getLogger(SearchBCaching.class);

	public SearchBCaching(Preferences preferences) throws Exception
	{
		final String bcUsername = preferences.getBCachingUsername();
		final String bcPassword = preferences.getBCachingPassword();
		// bcMaxCaches = Integer.parseInt(settings.getString(OpenGPX.PREFS_KEY_BCACHING_MAX_CACHES, OpenGPX.PREFS_DEFAULT_BCACHING_MAX_CACHES));
		// bcMaxDistance = Integer.parseInt(settings.getString(OpenGPX.PREFS_KEY_BCACHING_MAX_DISTANCE, OpenGPX.PREFS_DEFAULT_BCACHING_MAX_DISTANCE));
		final boolean bcTestSite = preferences.getUseBCachingTestSite();

		mLogger.debug("test site: " + bcTestSite);
		
		init(bcTestSite);
		setLoginInfo(bcUsername, bcPassword);
	}

	public SearchBCaching(boolean isTest)
	{
		init(isTest);
	}

	private void init(boolean isTest)
	{
		cacheDatabase = CacheDatabase.getInstance();

		if (isTest)
		{
			mLogger.debug("using http://test.bcaching.com/api ...");
			comm = new Communication("http://test.bcaching.com/api");
		}
		else
		{
			mLogger.debug("using http://www.bcaching.com/api ...");
			comm = new Communication("http://www.bcaching.com/api");
		}
	}

	public void setLoginInfo(String user, String password) throws Exception
	{
		comm.setLoginCredentials(user, password, false);
	}

	public String sendFieldNote(FieldNote note) throws Exception
	{
		List<FieldNote> notes = new ArrayList<FieldNote>();
		notes.add(note);

		return sendFieldNoteList(notes);
	}

	public String sendFieldNoteList(List<FieldNote> notes) throws Exception
	{
		return comm.sendFieldNotes(notes);
	}

	public void doFindQuery(LocationInfo locationInfo, int maxDistance, int maxCaches) throws Exception
	{
		doFindQuery(locationInfo, maxDistance, maxCaches, null);
	}

	public void doFindQuery(LocationInfo locationInfo, int maxDistance, int maxCaches, String name) throws Exception
	{
		doFindQuery(locationInfo, maxDistance, maxCaches, name, null);
	}

	public void doFindQuery(LocationInfo locationInfo, int maxDistance, int maxCaches, String name, AdvancedSearchData data) throws Exception
	{
		if (!cacheDatabase.isSearchDatabaseOpen()) 
			cacheDatabase.openSearchDatabase();

		params.clear();
		params.put("a", "find");
		params.put("lat", Double.toString(locationInfo.latitude));
		params.put("lon", Double.toString(locationInfo.longitude));
		params.put("fmt", "json");
		params.put("maxcount", Integer.toString(maxCaches));
		params.put("maxdistance", Integer.toString(maxDistance));

		if (name != null) params.put("find", URLEncoder.encode(name));
		if (data != null) addAdvancedSearchParams(data);

		InputStream in = comm.SendRequest(params);
		parseJsonSummaryData(in);
		
		cacheDatabase.readSearchIndex();
	}

	protected void addAdvancedSearchParams(AdvancedSearchData data)
	{
		params.put("diff", data.difficultyFrom + "," + data.difficultyTo);
		params.put("terr", data.terrainFrom + "," + data.terrainTo);
		params.put("dist", Float.toString(data.maxDistance));
		params.put("type", data.getCacheTypeString());
		params.put("cont", data.getContainerSizeString());
		params.put("active", data.isActive.getTypeCode());
		params.put("found", data.isFound.getTypeCode());
		params.put("ignor", data.isIgnored.getTypeCode());
		params.put("own", data.isOwned.getTypeCode());
		params.put("mywpts", data.hasWpts.getTypeCode());
		params.put("mycoords", data.hasAltCoords.getTypeCode());
		params.put("hastbs", data.hasTBs.getTypeCode());
	}

	public void doSingleDetailQuery(String code) throws Exception
	{
		final List<String> list = new ArrayList<String>();
		list.add(code);
		doDetailQuery(list);
	}

	public void doDetailQuery(List<String> idList) throws Exception
	{
		if (!cacheDatabase.isSearchDatabaseOpen()) 
			cacheDatabase.openSearchDatabase();

		StringBuilder ids = new StringBuilder();

		for (String id : idList)
		{
			ids.append(id + ",");
		}

		params.clear();
		params.put("a", "detail");
		params.put("ids", ids.toString());
		params.put("wpts", "1");
		params.put("logs", "20");
		params.put("tbs", "1");
		params.put("desc", "html");
		params.put("fmt", "json");

		InputStream in = comm.SendRequest(params);
		parseJsonDetailData(in);
		
		cacheDatabase.readSearchIndex();
	}

	@SuppressWarnings("unused")
	private void outputDataFromStream(InputStream in) throws IOException
	{
		byte[] buf = new byte[512];
		int count = 0;
		while ((count = in.read(buf, 0, buf.length)) != -1)
		{
			System.out.println(new String(buf, 0, count));
		}

		in.close();
	}

	public void parseJsonSummaryData(InputStream in) throws IOException, JSONException
	{
		BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")), 1024);
		StringBuilder lvResult = new StringBuilder();
		String line;

		while ((line = rd.readLine()) != null)
		{
			lvResult.append(line + "\n");
		}
	
		// JSONObject requires
		// 		"myobj" : Date(1234567890)
		// instead of
		//		"myobj" : new Date(1234567890)
		String strJSON = lvResult.toString();
		strJSON = strJSON.replace(" : new Date(", " : Date(");
		
		// JSONObject obj = new JSONObject(lvResult.toString());
		final JSONObject obj = new JSONObject(strJSON);
		final JSONArray data = obj.getJSONArray("data");
		
		if (data.length() == 0)
		{
			// no results
		}
		else
		{
			for (int i = 0; i < data.length(); i++)
			{
				Cache c = new Cache();
				Waypoint wpt = new Waypoint();
				c.addWaypoint(wpt, false);

				c.isBcachingSummary = true;

				final JSONObject cache = data.getJSONObject(i);
				c.id = cache.getInt("id");
				c.name = cache.getString("name");
				c.code = cache.getString("wpt");
				c.parseCacheTypeString(cache.getString("type"));
				c.parseContainerTypeString(cache.getString("cont"));
				c.difficulty = cache.getDouble("diff");
				c.terrain = cache.getDouble("terr");
				c.isAvailable = cache.getBoolean("avail");
				c.placedBy = cache.getString("placedBy");

				float gcvote = 0.0f;
				if (cache.has("gcvote")) gcvote = (float) cache.getDouble("gcvote");
				// System.out.println(gcvote);
				
				// int gcvcnt = 0;
				// if (cache.has("gcvote")) gcvcnt = cache.getInt("gcvcnt");
				// System.out.println(gcvcnt);
				
				wpt.name = cache.getString("wpt");
				wpt.latitude = cache.getDouble("lat");
				wpt.longitude = cache.getDouble("lon");
				wpt.parseTypeString("geocache");
				wpt.symbol = cache.getString("type");

				// Set some default values
				c.country = "Unknown";
				c.state = "";

				cacheDatabase.addSearchCache(c, gcvote);
			}
		}
	}

	public void parseJsonDetailData(InputStream in) throws IOException, JSONException
	{
		BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")), 1024);
		StringBuilder lvResult = new StringBuilder();
		String line;

		while ((line = rd.readLine()) != null)
		{
			lvResult.append(line + "\n");
		}

		// JSONObject requires
		// 		"myobj" : Date(1234567890)
		// instead of
		//		"myobj" : new Date(1234567890)
		String strJSON = lvResult.toString();
		strJSON = strJSON.replace(" : new Date(", " : Date(");

		// JSONObject obj = new JSONObject(lvResult.toString());
		final JSONObject obj = new JSONObject(strJSON);
		final JSONArray data = obj.getJSONArray("data");

		if (data.length() == 0)
		{
			// no results
		}
		else
		{
			Cache c = new Cache();
			c.isBcachingSummary = false;
			float gcvote = 0.0f;
			
			for (int i = 0; i < data.length(); i++)
			{
				Waypoint wpt = new Waypoint();
				c.addWaypoint(wpt, false);

				final JSONObject cache = data.getJSONObject(i);
				// System.out.println(cache);

				wpt.name = cache.getString("wpt");
				wpt.latitude = cache.getDouble("lat");
				wpt.longitude = cache.getDouble("lon");
				wpt.symbol = cache.getString("type");

				if (cache.has("id"))
				{
					// System.out.println("yes (Cache)");

					wpt.parseTypeString("geocache");

					c.id = cache.getInt("id");
					c.name = cache.getString("name");
					c.code = cache.getString("wpt");
					c.parseCacheTypeString(cache.getString("type"));
					c.parseContainerTypeString(cache.getString("cont"));
					c.difficulty = cache.getDouble("diff");
					c.terrain = cache.getDouble("terr");
					c.isAvailable = cache.getBoolean("avail");
					c.placedBy = cache.getString("placedBy");
					c.isArchived = cache.getBoolean("arch");
					c.country = cache.getString("ctry");
					c.state = cache.getString("state");
					// the JSONObject does contain "null" values for missing caches
					if (c.state.equalsIgnoreCase("null")) c.state = "";
					c.hint = cache.optString("hint");
					c.hint = c.hint.replaceAll("\r", "");
					if (c.hint.equalsIgnoreCase("null")) c.hint = "";
					c.owner = cache.getString("owner");
					c.shortDescription = cache.optString("shortDesc");
					c.longDescription = cache.optString("longDesc");
					c.shortDescriptionIsHtml = true;
					c.longDescriptionIsHtml = true;
					if (cache.has("gcvote")) gcvote = (float) cache.getDouble("gcvote");
					
					final Date dtCacheTime = parseJsonDate(cache.getString("time"));
					wpt.time = dtCacheTime;
					wpt.description = "Header coordinates";

					final JSONArray logList = cache.optJSONArray("logs");
	
					if (logList != null)
					{
						for (int l = 0; l < logList.length(); l++)
						{
							LogEntry log = new LogEntry();
							c.addLogEntry(log);
	
							JSONObject jlog = logList.getJSONObject(l);
							log.finder = jlog.getString("finder");
							log.id = jlog.getString("id");
							log.text = jlog.getString("text");
							log.text.replaceAll("\r", "");
							log.parseTypeString(jlog.getString("type"));
							log.time = parseJsonDate(jlog.getString("date"));
						}
					}
	
					final JSONArray tbList = cache.optJSONArray("inventory");
	
					if (tbList != null)
					{
						for (int t = 0; t < tbList.length(); t++)
						{
							TravelBug tb = new TravelBug();
							c.addTravelBug(tb);
	
							JSONObject jtb = tbList.getJSONObject(t);
							tb.name = jtb.getString("name");
							tb.reference = jtb.getString("ref");
						}
					}
	
					final JSONArray wptList = cache.optJSONArray("waypoints");
	
					if (wptList != null)
					{
						for (int w = 0; w < wptList.length(); w++)
						{
							Waypoint wp = new Waypoint();
							c.addWaypoint(wp, false);
	
							JSONObject jwp = wptList.getJSONObject(w);
							wp.name = jwp.getString("wpt");
							wp.latitude = jwp.getDouble("lat");
							wp.longitude = jwp.getDouble("lon");
							wp.description = jwp.getString("desc");
							// Log.d(TAG, jwp.getString("type"));
							wp.parseTypeString(jwp.getString("type"));
							wp.symbol = jwp.getString("type");
							wp.time = dtCacheTime;
						}
					}
				} 
				else
				{
					wpt.parseTypeString(cache.getString("type"));
					wpt.description = "Waypoint " + Integer.toString(i);
				}
			}
			
			// System.out.println(gcvote);
			cacheDatabase.addSearchCache(c, gcvote);
		}
	}

	public Date parseJsonDate(String in)
	{
		String date = in.substring(in.indexOf("(") + 1);
		date = date.substring(0, date.lastIndexOf(")"));
		return new Date(Long.parseLong(date));
	}
}
