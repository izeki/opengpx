package org.opengpx.lib;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.geocache.FieldNote;
import org.opengpx.lib.geocache.GCVote;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.xml.GPXFileReader;
import org.opengpx.lib.xml.LOCFileReader;
import org.opengpx.lib.xml.ZipFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.ext.DatabaseFileLockedException;
import com.db4o.ext.Db4oIOException;
import com.db4o.query.Query;

/**
 *
 * @author Martin Preishuber
 *
 */
public class CacheDatabase 
{
	private ArrayList<String> malGpxFolders = new ArrayList<String>();
	private LinkedHashMap<String, CacheIndexItem> mhmCacheIndexItems = new LinkedHashMap<String, CacheIndexItem>();
	private ArrayList<Waypoint> mOrphanedWaypoints;
	
	private LinkedHashMap<String, CacheIndexItem> searchIndexItems = new LinkedHashMap<String, CacheIndexItem>();
	
	private ObjectContainer searchCacheDB = null;
	private boolean searchCacheDBIsOpen = false;

	private ObjectContainer fieldNoteDB = null;
	private boolean fieldNoteDBIsOpen = false;
	
	// Database objects
	private ObjectContainer mDB4ODatabase = null;
	private boolean mblnDatabaseIsOpen = false;
	private String mstrDatabaseFilename;

	private String mBaseFolder;
	private String mstrBackupFolder;
	private String mstrDatabaseFolder;
	private String mstrErrorMessage;
	
	private int mintSortOrder = SORT_ORDER_NAME;
	private int mintMaxResults = Integer.MAX_VALUE;
	private double mdblReferenceLatitude;
	private double mdblReferenceLongitude;

	public static final int SORT_ORDER_NAME = 1;
	public static final int SORT_ORDER_DISTANCE = 2;

	private static final CacheDatabase mCacheDatabase = new CacheDatabase();
	// Constants for filterable strings
	private static final String CACHE_FILTER_SEPARATOR = "||";
	private static final String[] CACHE_FILTER_REMOVE_CHARS = { "\"", "'", "_", "(", ")", "?", "!" };
	
	public AtomicBoolean isUpdated = new AtomicBoolean(false);
	public AtomicBoolean isSearchUpdated = new AtomicBoolean(false);
	
	private AdvancedSearchData priorSearch = null;
	
	private static final Logger mLogger = LoggerFactory.getLogger(CacheDatabase.class);

	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	static class CacheNameComparator implements Comparator<CacheIndexItem>, Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 8534785080926464590L;

		public int compare(CacheIndexItem object1, CacheIndexItem object2)
		{
			return object1.name.compareTo(object2.name);
		}
	}

	/**
	 * 
	 */
	private CacheDatabase()
	{
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean openFieldNoteDatabase()
	{
		if (fieldNoteDBIsOpen) closeFieldNoteDatabase();
		
		final String fname = String.format("%s%s%s", mstrDatabaseFolder, File.separator, "FieldNoteDatabase.db4o");
		
		try
		{
			final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.common().objectClass(FieldNote.class);
			
			fieldNoteDB = Db4oEmbedded.openFile(config, fname);
			fieldNoteDBIsOpen = true;
		}
		catch (DatabaseFileLockedException dfle)
		{
			this.mstrErrorMessage = dfle.toString();
			this.fieldNoteDB = null;
			this.fieldNoteDBIsOpen = false;
			return false;
		}
		catch (Db4oIOException dbioe)
		{
			this.mstrErrorMessage = dbioe.toString();
			this.fieldNoteDB = null;
			this.fieldNoteDBIsOpen = false;
			return false;						
		}
		
		return true;
	}

	public void closeFieldNoteDatabase()
	{
		if (this.fieldNoteDB != null)
		{
			// Commit pending changes
			this.fieldNoteDB.commit();
			this.fieldNoteDB.close();

			this.fieldNoteDBIsOpen = false;
		}		
	}
	
	private String getSearchDBFileName()
	{
		return String.format("%s%s%s", mstrDatabaseFolder, File.separator, "SearchDatabase.db4o");
	}
	
	public boolean openSearchDatabase()
	{
		if (searchCacheDBIsOpen) closeSearchDatabase();
		
		final String fname = getSearchDBFileName();
		
		try
		{
			final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			// Make sure that all sub-classes are updated on updates
			config.common().objectClass(Cache.class).cascadeOnUpdate(true);
			config.common().objectClass(UserDefinedVariables.class).cascadeOnUpdate(true);
			// Set some indexes for performance reasons
			config.common().objectClass(CacheIndexItem.class).objectField("code").indexed(true);
			config.common().objectClass(Cache.class).objectField("code").indexed(true);
			config.common().objectClass(GCVote.class).objectField("waypoint").indexed(true);
			
			searchCacheDB = Db4oEmbedded.openFile(config, fname);
			searchCacheDBIsOpen = true;
		}
		catch (DatabaseFileLockedException dfle)
		{
			this.mstrErrorMessage = dfle.toString();
			this.searchCacheDB = null;
			this.searchCacheDBIsOpen = false;
			return false;
		}
		catch (Db4oIOException dbioe)
		{
			this.mstrErrorMessage = dbioe.toString();
			this.searchCacheDB = null;
			this.searchCacheDBIsOpen = false;
			return false;						
		}
		
		return true;
	}

	public void closeSearchDatabase()
	{
		if (this.searchCacheDB != null)
		{
			// Commit pending changes
			this.searchCacheDB.commit();
			this.searchCacheDB.close();

			this.searchCacheDBIsOpen = false;
		}		
	}

	public void addFieldNote(FieldNote note)
	{
		final ObjectSet<FieldNote> duplicateNote = fieldNoteDB.queryByExample(note);
		
		if (duplicateNote.size() == 0)
		{
			fieldNoteDB.store(note);
			fieldNoteDB.commit();
		}
	}
	
	public ObjectSet<FieldNote> getFieldNotes()
	{
		final Query q = fieldNoteDB.query();
		q.constrain(FieldNote.class);
		
		return q.execute();
	}
	
	public void deleteFieldNotes(ObjectSet<FieldNote> notes)
	{
		for (FieldNote note : notes)
		{
			fieldNoteDB.delete(note);
		}
		
		fieldNoteDB.commit();
	}
	
	/**
	 * 
	 */
	public boolean openDatabase(String filename)
	{
		// make sure that database isn't opened twice
		if (this.mblnDatabaseIsOpen) this.close();

		this.mstrDatabaseFilename = filename;
		final String strDatabaseFilename = String.format("%s%s%s", this.mstrDatabaseFolder, File.separator, this.mstrDatabaseFilename);
		try 
		{
			final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			// Make sure that all sub-classes are updated on updates
			config.common().objectClass(Cache.class).cascadeOnUpdate(true);
			config.common().objectClass(UserDefinedVariables.class).cascadeOnUpdate(true);
			// Set some indexes for performance reasons
			config.common().objectClass(CacheIndexItem.class).objectField("code").indexed(true);
			config.common().objectClass(Cache.class).objectField("code").indexed(true);
			config.common().objectClass(GCVote.class).objectField("waypoint").indexed(true);
			
			this.mDB4ODatabase = Db4oEmbedded.openFile(config, strDatabaseFilename);
			this.mblnDatabaseIsOpen = true;
			return true;
		}
		catch (DatabaseFileLockedException dfle)
		{
			this.mstrErrorMessage = dfle.toString();
			this.mDB4ODatabase = null;
			this.mblnDatabaseIsOpen = false;
			return false;
		}
		catch (Db4oIOException dbioe)
		{
			this.mstrErrorMessage = dbioe.toString();
			this.mDB4ODatabase = null;
			this.mblnDatabaseIsOpen = false;
			return false;						
		}
	}

	/**
	 * 
	 * @param cache
	 */
	public void addSearchCache(Cache cache)
	{
		addSearchCache(cache, true, 0.0f);
	}

	/**
	 * 
	 * @param cache
	 * @param gcvote
	 */
	public void addSearchCache(Cache cache, float gcvote)
	{
		this.addSearchCache(cache, true, gcvote);
	}

	/**
	 * 
	 * @param cache
	 * @param commit
	 * @param gcvote
	 */
	public void addSearchCache(Cache cache, boolean commit, float gcvote)
	{
		// Check whether the cache has already been imported
		final Cache cacheDuplicate = getCacheFromSearch(cache.code);
		if (cacheDuplicate != null)
		{
			searchCacheDB.delete(cacheDuplicate);
		}
		
		// add cache to db4o database
		searchCacheDB.store(cache);

		// Check, whether the cache index already exists
		final CacheIndexItem existingCII = getSearchCacheIndexItem(cache.code);
		if (existingCII != null)
			searchCacheDB.delete(existingCII);

		// add cache information to cache index
		searchCacheDB.store(this.createCacheIndexItem(cache, gcvote));

		// Commit changes
		if (commit) searchCacheDB.commit();
	}
	
	public boolean clearSearchCacheData()
	{
		// Clear DB4O Database by re-creating it
		closeSearchDatabase();

		final String strDatabaseFilename = getSearchDBFileName();
		final boolean result = new File(strDatabaseFilename).delete();

		return (openSearchDatabase() && result);
	}
	
	public void saveSearchCacheToDB(String cacheCode)
	{
		final Cache cache = getCacheFromSearch(cacheCode);
		final CacheIndexItem cii = getSearchCacheIndexItem(cacheCode);
		if ((cache != null) && (cii != null)) addCache(cache, cii, true);
	}

	public ArrayList<String> getSearchCacheCodes()
	{
		return new ArrayList<String>(searchIndexItems.keySet());
	}
	
	public void saveAllSearchCachesToDB()
	{
		for (String code : getSearchCacheCodes())
		{
			saveSearchCacheToDB(code);
		}

		commit();
	}
	
	public AdvancedSearchData getPriorSearch()
	{
		return priorSearch;
	}
	
	public void setPriorSearch(AdvancedSearchData data)
	{
		priorSearch = data;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public ImportResult readXmlFiles(boolean backupGpxFiles)
	{
		final ImportResult ir = new ImportResult();
		for (String folderName : this.malGpxFolders)
		{			
			final File filePath = new File(folderName);
			if (filePath.isDirectory())
			{
				mLogger.debug("Reading gpx/loc/zip files in folder " + folderName);
				
				final String[] arrFilenames = filePath.list();
				for (String strFilename : arrFilenames)
				{
					Boolean blnFileRead = false;
					Boolean blnSuccessful = false;
					String strFullFilename = "";
					if (strFilename.toLowerCase().endsWith("gpx") && !(strFilename.startsWith(".")))
					{
						strFullFilename = String.format("%s%s%s", folderName, File.separator, strFilename);
						final GPXFileReader gpxFileReader = new GPXFileReader();
						blnSuccessful = gpxFileReader.read(strFullFilename);
						blnFileRead = true;
					} 
					else if (strFilename.toLowerCase().endsWith("loc") && !(strFilename.startsWith(".")))
					{
						strFullFilename = String.format("%s%s%s", folderName, File.separator, strFilename);
						final LOCFileReader locFileReader = new LOCFileReader();
						blnSuccessful = locFileReader.read(strFullFilename);
						blnFileRead = true;
					}
					else if (strFilename.toLowerCase().endsWith("zip") && !(strFilename.startsWith(".")))
					{
						strFullFilename = String.format("%s%s%s", folderName, File.separator, strFilename);
						final ZipFileReader zipFileReader = new ZipFileReader(this.mBaseFolder);
						blnSuccessful = zipFileReader.read(strFullFilename);
						blnFileRead = true;						
					}
					if (blnFileRead)
					{
						// Move file to backup folder
						if (this.mstrBackupFolder.length() > 0)
						{
							final File file = new File(strFullFilename);
							boolean fileResult = true;
							if (backupGpxFiles)
								fileResult = file.renameTo(new File(this.mstrBackupFolder, file.getName()));
							else
								fileResult = file.delete();
							if (!fileResult)
								mLogger.warn("File operation on " + strFullFilename + " failed ...");
						}
						if (blnSuccessful)
						{
							ir.successful++;
						} 
						else
						{
							ir.failed++;
							ir.filesFailed.add(strFilename);
						}
					}
				}
			}
			else
			{
				mLogger.warn("Invalid import folder " + folderName);
			}
		}

		// Add orphaned waypoints to caches
		this.addOrphanedWaypointsToCaches();

		// Commit all changes
		// this.mDB4ODatabase.commit();

		return ir;
	}

	/**
	 * 
	 * @param cache
	 */
	public void addCache(Cache cache)
	{
		this.addCache(cache, true);
	}

	/**
	 * 
	 * @param cache
	 * @param cii
	 */
	public void addCache(Cache cache, CacheIndexItem cii)
	{
		this.addCache(cache, cii, true);
	}

	/**
	 * 
	 * @param cache
	 * @param commit
	 */
	public void addCache(Cache cache, boolean commit)
	{
		final CacheIndexItem cii = this.createCacheIndexItem(cache);
		this.addCache(cache, cii, commit);
	}

	/**
	 * 
	 * @param cache
	 * @param cii
	 * @param commit
	 */
	public void addCache(Cache cache, CacheIndexItem cii, boolean commit)
	{
		// Check whether the cache has already been imported
		final Cache cacheDuplicate = this.getCache(cache.code);
		if (cacheDuplicate != null)
			this.mDB4ODatabase.delete(cacheDuplicate);
		// add cache to db4o database
		this.mDB4ODatabase.store(cache);
		
		// Check, whether the cache index already exists
		final CacheIndexItem existingCII = this.getCacheIndexItem(cache.code);
		if (existingCII != null)
			this.mDB4ODatabase.delete(existingCII);
		// add cache information to cache index
		this.mDB4ODatabase.store(cii);

		// Commit changes
		if (commit) this.mDB4ODatabase.commit();
	}
	
	public void commit()
	{
		this.mDB4ODatabase.commit();
	}

	/**
	 * 
	 * @param cacheCode
	 */
	public void deleteCache(String cacheCode)
	{
		// Delete cache
		Cache cache = this.getCache(cacheCode);
		if (cache != null)
			this.mDB4ODatabase.delete(cache);

		// Delete cache index items
		CacheIndexItem cii = this.getCacheIndexItem(cacheCode);
		if (cii != null)
			this.mDB4ODatabase.delete(cii);
		
		// Remove item from HashMap
		this.mhmCacheIndexItems.remove(cacheCode);
		
		// Commit changes
		this.mDB4ODatabase.commit();
	}
	
	/**
	 * 
	 * @param cache
	 * @return
	 */
	private CacheIndexItem createCacheIndexItem(Cache cache)
	{
		return this.createCacheIndexItem(cache, 0.0f);
	}
	
	/**
	 * 
	 * @param cache
	 * @return
	 */
	private CacheIndexItem createCacheIndexItem(Cache cache, float gcvote)
	{
		Waypoint headerWaypoint = cache.getHeaderWaypoint();

		CacheIndexItem cii = new CacheIndexItem();
		cii.code = cache.code;
		cii.type = cache.getCacheType().toString();
		cii.container = cache.getContainerType().toString();
		cii.name = cache.name;
		cii.difficulty = cache.difficulty;
		cii.terrain = cache.terrain;
		cii.latitude = headerWaypoint.latitude;
		cii.longitude = headerWaypoint.longitude;
		cii.isArchived = cache.isArchived;
		cii.isAvailable = cache.isAvailable;
		cii.vote = gcvote;
		return cii;
	}

	/**
	 * 
	 * @param waypoint
	 */
	public void addOrphanedWaypoint(Waypoint waypoint)
	{
		if (this.mOrphanedWaypoints == null)
			this.mOrphanedWaypoints = new ArrayList<Waypoint>();
		
		this.mOrphanedWaypoints.add(waypoint);
	}

	/**
	 * 
	 */
	private void addOrphanedWaypointsToCaches()
	{
		if (this.mOrphanedWaypoints != null)
		{ 
			// ObjectPickler objectPickler = ObjectPickler.getInstance();
			HashMap<String, ArrayList<Waypoint>> alCacheWaypointMapping = new HashMap<String, ArrayList<Waypoint>>();
			
			// Create a list of mappings of GC -> Waypoints (so that GC gets read & written only once)
			for (Waypoint waypoint : this.mOrphanedWaypoints)
			{
				String strCacheCode = String.format("GC%s", waypoint.name.substring(2));
				if (!alCacheWaypointMapping.containsKey(strCacheCode))
				{
					ArrayList<Waypoint> alWaypoints = new ArrayList<Waypoint>();
					alCacheWaypointMapping.put(strCacheCode, alWaypoints);
				}
				alCacheWaypointMapping.get(strCacheCode).add(waypoint);
			}

			// Add waypoints to caches
			for (Map.Entry<String, ArrayList<Waypoint>> entry : alCacheWaypointMapping.entrySet())
			// for (String strCacheCode : alCacheWaypointMapping.keySet())
			{
				final String strCacheCode = entry.getKey();
				final Cache cache = this.getCache(strCacheCode);
				if (cache != null)
				{
					// for (String strWaypointFilename : alCacheWaypointMapping.get(strCacheCode))
					// for (Waypoint waypoint : alCacheWaypointMapping.get(strCacheCode))
					for (Waypoint waypoint : entry.getValue())
					{
						cache.addWaypoint(waypoint);
					}
					this.mDB4ODatabase.store(cache);
					this.mDB4ODatabase.commit();
				}				
			}

			this.mOrphanedWaypoints.clear();
			this.mOrphanedWaypoints = null;
		}
	}

	/**
	 * 
	 */
	public void readCacheIndex()
	{
		this.mhmCacheIndexItems.clear();
		
		Query queryItems = this.mDB4ODatabase.query();
		// Read items sorted by cache name
		queryItems.constrain(CacheIndexItem.class);
		queryItems.descend("name").orderAscending();
		ObjectSet<?> result = queryItems.execute();
		while (result.hasNext())
		{
			CacheIndexItem cii = (CacheIndexItem) result.next();
			this.mhmCacheIndexItems.put(cii.code, cii);
		}
	}

	public void readSearchIndex()
	{
		searchIndexItems.clear();
		
		Query queryItems = searchCacheDB.query();
		// Read items sorted by cache name
		queryItems.constrain(CacheIndexItem.class);
		queryItems.descend("name").orderAscending();
		ObjectSet<?> result = queryItems.execute();
		while (result.hasNext())
		{
			CacheIndexItem cii = (CacheIndexItem) result.next();
			searchIndexItems.put(cii.code, cii);
		}
	}

	/**
	 * 
	 * @return
	 */
	public int indexSize()
	{
		if (this.mDB4ODatabase != null)
		{
			List<CacheIndexItem> ciis = this.mDB4ODatabase.query(CacheIndexItem.class);
			return ciis.size();
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int databaseSize()
	{
		if (this.mDB4ODatabase != null)
		{
			List<Cache> caches = this.mDB4ODatabase.query(Cache.class);
			return caches.size();
		}
		else
		{
			return -1;
		}
	}

	/**
	 * 
	 * @return
	 */
	public int voteSize()
	{
		if (this.mDB4ODatabase != null)
		{
			List<GCVote> votes = this.mDB4ODatabase.query(GCVote.class);
			return votes.size();
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int cacheVariableSize()
	{
		if (this.mDB4ODatabase != null)
		{
			List<UserDefinedVariables> variables = this.mDB4ODatabase.query(UserDefinedVariables.class);
			return variables.size();
		}
		else
		{
			return -1;
		}
	}
	
	/**
	 * 
	 */
	public void close()
	{
		if (this.mDB4ODatabase != null)
		{
			// Commit pending changes
			this.mDB4ODatabase.commit();
			this.mDB4ODatabase.close();

			this.mblnDatabaseIsOpen = false;
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public ArrayList<String> getCacheCodes()
	{
		if (this.mintSortOrder == SORT_ORDER_NAME)
			return new ArrayList<String>(this.mhmCacheIndexItems.keySet());
		else if (this.mintSortOrder == SORT_ORDER_DISTANCE)
			return this.getCacheCodesSortedByDistance();
		else
			return new ArrayList<String>(this.mhmCacheIndexItems.keySet());
	}

	public ArrayList<String> getSearchList()
	{
		if (this.mintSortOrder == SORT_ORDER_NAME)
		{
			return getSearchCacheCodesSortedByName();
		}
		else if (this.mintSortOrder == SORT_ORDER_DISTANCE)
		{
			return getSearchCacheCodesSortedByDistance();
		}		
		else return getSearchCacheCodesSortedByName();
	}
	
	public int getSearchIndexSize()
	{
		return searchIndexItems.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getFilterableList()
	{
		ArrayList<String> alFilterableList = new ArrayList<String>();

		if (this.mintSortOrder == SORT_ORDER_NAME)
		{
			final Set<String> setKeySet = this.mhmCacheIndexItems.keySet();
			for (String strCacheCode : setKeySet)
			{
				alFilterableList.add(this.getFilterableName(strCacheCode));
			}
		}
		else if (this.mintSortOrder == SORT_ORDER_DISTANCE)
		{
			final ArrayList<String> alKeySet = this.getCacheCodesSortedByDistance();
			for (String strCacheCode : alKeySet)
			{
				alFilterableList.add(this.getFilterableName(strCacheCode));
			}
		}

		return alFilterableList;
	}

	/**
	 * 
	 * @param strCacheCode
	 * @return
	 */
	public String getFilterableName(String strCacheCode)
	{
		String strCacheName = this.mhmCacheIndexItems.get(strCacheCode).name;
		// Remove some characters from the filterable string
		for (String strReplaceMe : CACHE_FILTER_REMOVE_CHARS)
		{
			strCacheName = strCacheName.replace(strReplaceMe, "");
		}
		return strCacheName.concat(CACHE_FILTER_SEPARATOR).concat(strCacheCode);
	}
	/**
	 * 
	 * @param filterableString
	 * @return
	 */
	public String getCacheCodeFromFilterable(String filterableString)
	{
		return filterableString.substring(filterableString.lastIndexOf(CACHE_FILTER_SEPARATOR) + CACHE_FILTER_SEPARATOR.length()); 
	}
	
	/**
	 * 
	 * @param filterableString
	 * @return
	 */
	public CacheIndexItem getCacheIndexItemForFilter(String filterableString)
	{
		return this.getCacheIndexItem(this.getCacheCodeFromFilterable(filterableString));
	}

	/**
	 * 
	 * @param filterableString
	 * @return
	 */
	public CacheIndexItem getSearchCacheIndexItemForFilter(String filterableString)
	{
		String key = filterableString;
		
		if (searchIndexItems.containsKey(key))
		{
			return searchIndexItems.get(key);
		}
		else 
		{
			return null;
		}
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getCacheCodesSortedByDistance()
	{
		final TreeMap<Double, String> tmSortedItems = new TreeMap<Double, String>();
		final Coordinates coords = new Coordinates(this.mdblReferenceLatitude, this.mdblReferenceLongitude);
		// add items to a sorted treemap
		for (String strCacheCode : this.mhmCacheIndexItems.keySet())
		{
			final CacheIndexItem cii = this.mhmCacheIndexItems.get(strCacheCode);
			final Coordinates coordsCompare = new Coordinates(cii.latitude, cii.longitude);
			final double dblDistance = coords.getDistanceTo(coordsCompare);
			tmSortedItems.put(dblDistance, strCacheCode);	
		}
		
		int intCounter = 0;
		ArrayList<String> alCacheCodes = new ArrayList<String>();
		for (String strCacheCode : tmSortedItems.values())
		{
			if (intCounter < this.mintMaxResults)
			{
				alCacheCodes.add(strCacheCode);
				intCounter++;
			}
		}
		
		return alCacheCodes;
	}
	
	private ArrayList<String> getSearchCacheCodesSortedByName()
	{
		TreeSet<CacheIndexItem> sortedCacheList = new TreeSet<CacheIndexItem>(new CacheNameComparator());
		ArrayList<String> sortedCacheCodes = new ArrayList<String>();
		
		for (CacheIndexItem cii : searchIndexItems.values())
		{
			sortedCacheList.add(cii);
		}
		
		for (CacheIndexItem cii : sortedCacheList)
		{
			sortedCacheCodes.add(cii.code);
		}
		
		return sortedCacheCodes;
	}
	
	private ArrayList<String> getSearchCacheCodesSortedByDistance()
	{
		TreeMap<Double, String> tmSortedItems = new TreeMap<Double, String>();
		final Coordinates coords = new Coordinates(this.mdblReferenceLatitude, this.mdblReferenceLongitude);
		// add items to a sorted treemap
		for (String strCacheCode : searchIndexItems.keySet())
		{
			final CacheIndexItem cii = searchIndexItems.get(strCacheCode);
			final Coordinates coordsCompare = new Coordinates(cii.latitude, cii.longitude);
			final double dblDistance = coords.getDistanceTo(coordsCompare);
			tmSortedItems.put(dblDistance, strCacheCode);	
		}
		
		int intCounter = 0;
		ArrayList<String> alCacheCodes = new ArrayList<String>();
		for (String strCacheCode : tmSortedItems.values())
		{
			if (intCounter < this.mintMaxResults)
			{
				alCacheCodes.add(strCacheCode);
				intCounter++;
			}
		}
		
		return alCacheCodes;
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 */
	public Cache getCache(String cacheCode)
	{
		if (this.mDB4ODatabase != null)
		{
			final Query queryCache = this.mDB4ODatabase.query();
			queryCache.constrain(Cache.class);
			queryCache.descend("code").constrain(cacheCode);
			final ObjectSet<?> result = queryCache.execute();
			if (result.size() >= 1)
				return (Cache) result.next();
			else
				return null;
		}
		else
			return null;
	}
	
	public Cache getCacheFromSearch(String cacheCode)
	{
		if (searchCacheDBIsOpen && searchCacheDB != null)
		{		
			final Query queryCache = searchCacheDB.query();
			queryCache.constrain(Cache.class);
			queryCache.descend("code").constrain(cacheCode);
			ObjectSet<?> result = queryCache.execute();
			if (result.size() >= 1)
				return (Cache) result.next();
			else
				return null;
		}
		else
			return null;
	}

	/* private CacheIndexItem getCacheIndexItemFromSearch(String cacheCode)
	{
		if (searchCacheDBIsOpen && searchCacheDB != null)
		{		
			final Query queryCache = searchCacheDB.query();
			queryCache.constrain(CacheIndexItem.class);
			queryCache.descend("code").constrain(cacheCode);
			ObjectSet<?> result = queryCache.execute();
			if (result.size() >= 1)
				return (CacheIndexItem) result.next();
			else
				return null;
		}
		else
			return null;
	} */
	
	public CacheIndexItem getSearchCacheIndexItem(String cacheCode)
	{
		return searchIndexItems.get(cacheCode);
	}
	
	/**
	 * 
	 * @param cacheCode
	 * @return
	 */
	public CacheIndexItem getCacheIndexItem(String cacheCode)
	{
		if (this.mhmCacheIndexItems.containsKey(cacheCode))
		{
			return this.mhmCacheIndexItems.get(cacheCode);
		}
		else
		{
			Query queryCacheIndexItem = this.mDB4ODatabase.query();
			queryCacheIndexItem.constrain(CacheIndexItem.class);
			queryCacheIndexItem.descend("code").constrain(cacheCode);
			ObjectSet<?> result = queryCacheIndexItem.execute();
			if (result.size() >= 1)
				return (CacheIndexItem) result.next();
			else
				return null;			
		}
	}
	
	/**
	 * 
	 * @param cacheCode
	 * @return
	 */
	public UserDefinedVariables getVariables(String cacheCode)
	{
		Query queryUDV = this.mDB4ODatabase.query();
		queryUDV.constrain(UserDefinedVariables.class);
		queryUDV.descend("id").constrain(cacheCode);
		ObjectSet<?> result = queryUDV.execute();
		if (result.size() == 1)
			return (UserDefinedVariables) result.next();
		else
			return new UserDefinedVariables(cacheCode);
	}
	
	/**
	 * 
	 * @param udv
	 */
	public void storeVariables(UserDefinedVariables udv)
	{
		this.mDB4ODatabase.store(udv);
		this.mDB4ODatabase.commit();
	}

	/**
	 * 
	 * @param cacheCode
	 * @return
	 */
	public GCVote getVote(String cacheCode)
	{
		if (this.mDB4ODatabase != null)
		{		
			final Query queryVote = this.mDB4ODatabase.query();
			queryVote.constrain(GCVote.class);
			queryVote.descend("waypoint").constrain(cacheCode);
			final ObjectSet<?> result = queryVote.execute();
			if (result.size() >= 1)
				return (GCVote) result.next();
			else
				return null;
		}
		else
			return null;
	}

	/**
	 * 
	 * @param votes
	 */
	public void addCacheVotes(HashMap<String, GCVote> votes)
	{
		// System.out.println("Votes size: " + votes.size());
		if (votes.size() > 0)
		{
			for (Map.Entry<String, GCVote> entry : votes.entrySet())
			// for (String cacheCode : votes.keySet())
			{
				final String cacheCode = entry.getKey();
				final CacheIndexItem cii = this.getCacheIndexItem(cacheCode);
				// System.out.println(cii.code);
				if (cii != null)
				{
					// final GCVote vote = votes.get(cacheCode);
					final GCVote vote = entry.getValue();
	
					// Update index item
					cii.vote = vote.voteAverage;
					this.mDB4ODatabase.store(cii);
	
					// Get existing vote object
					final GCVote voteDatabase = this.getVote(cacheCode);
					if (voteDatabase != null)
					{
						this.mDB4ODatabase.delete(voteDatabase);
						// voteDatabase = new GCVote();
					}
	
					this.mDB4ODatabase.store(vote);
				}
			}
			
			// Commit data
			this.mDB4ODatabase.commit();
		}
	}

	/**
	 * 
	 * @param value
	 */
	public void addGpxFolder(String folderName)
	{
		Boolean blnFound = false;
		for (String path : this.malGpxFolders)
		{
			if (path.equals(folderName))
				blnFound = true;
		}
		if (!blnFound)
			this.malGpxFolders.add(folderName);
	}

	/**
	 * 
	 * @param folderList
	 */
	public void addSourceFolder(final String[] folderList)
	{
		for (String folderName : folderList)
		{
			this.addGpxFolder(folderName);
		}
	}
	
	/**
	 * 
	 */
	public boolean clear()
	{
		// Clear DB4O Database by re-creating it
		this.close();

		final String strDatabaseFilename = String.format("%s%s%s", this.mstrDatabaseFolder, File.separator, this.mstrDatabaseFilename);
		final boolean result = new File(strDatabaseFilename).delete();

		return (this.openDatabase(this.mstrDatabaseFilename) && result);
	}
	
	/**
	 * 
	 * @return
	 */
	public int size()
	{
		return this.mhmCacheIndexItems.size();
	}
	
	/**
	 * 
	 * @return
	 */
	/* public ArrayList<CacheIndexItem> getCacheIndexItems()
	{
		return new ArrayList<CacheIndexItem>(this.mCacheIndexItems.values());
	} */
	
	/**
	 * 
	 * @param cacheCode
	 * @return
	 */
	/* public CacheIndexItem getCacheIndexItem(String cacheCode)
	{
		return this.mCacheIndexItems.get(cacheCode);
	} */
	
	public void updateWaypoint(Waypoint wp)
	{
		this.mDB4ODatabase.store(wp);
		this.mDB4ODatabase.commit();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getGpxFolders()
	{
		return this.malGpxFolders;
	}
	
	/**
	 * 
	 * @param path
	 */
	public void setBackupFolder(String path)
	{		
		this.checkOrCreateFolder(path);
		this.mstrBackupFolder = path;
	}

	/**
	 * 
	 * @param path
	 */
	public void setBaseFolder(String path)
	{
		this.mBaseFolder = path;
	}
	
	/**
	 * 
	 * @param path
	 */
	public void setDatabaseFolder(String path)
	{
		this.checkOrCreateFolder(path);
		this.mstrDatabaseFolder = path;
	}
	
	/**
	 * 
	 * @param path
	 */
	private boolean checkOrCreateFolder(String path)
	{
		final File folder = new File(path);
		boolean result = true;
		if (!folder.exists())
			result = folder.mkdirs();
		return result;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getInformation()
	{
		final StringBuilder sbInfo = new StringBuilder();
		sbInfo.append(String.format("Database: %s", this.mstrDatabaseFilename));
		sbInfo.append(String.format("\nCache index size: %d", this.indexSize()));
		sbInfo.append(String.format("\nCache database size: %d", this.databaseSize()));
		sbInfo.append(String.format("\nCache variables: %d", this.cacheVariableSize()));
		sbInfo.append(String.format("\nCache votes: %d", this.voteSize()));
		return sbInfo.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String getErrorMessage()
	{
		return this.mstrErrorMessage;
	}

	/**
	 * 
	 * @param sortOrder
	 */
	public void setSortOrder(int sortOrder)
	{
		this.mintSortOrder = sortOrder;
	}

	/**
	 * 
	 * @return
	 */
	public int getSortOrder()
	{
		return this.mintSortOrder;
	}
	
	/**
	 * 
	 * @param results
	 */
	public void setMaxResults(int results)
	{
		this.mintMaxResults = results;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setReferenceCoordinates(double latitude, double longitude)
	{
		this.mdblReferenceLatitude = latitude;
		this.mdblReferenceLongitude = longitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDatabaseFilename()
	{
		return this.mstrDatabaseFilename;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isDatabaseOpen()
	{
		return this.mblnDatabaseIsOpen;
	}
	
	public boolean isSearchDatabaseOpen()
	{
		return searchCacheDBIsOpen;
	}
	
	/**
	 * 
	 * @return
	 */
	public static CacheDatabase getInstance()
	{
		return mCacheDatabase;
	}
}
