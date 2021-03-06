package org.opengpx;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opengpx.OpenGpxPreferenceActivity;
import org.opengpx.Preferences;
import org.opengpx.lib.map.GoogleElevation;
import org.opengpx.lib.map.GoogleMapViewer;
import org.opengpx.lib.map.LocusMapViewer;
import org.opengpx.lib.map.MapProvider;
import org.opengpx.lib.map.MapViewer;
import org.opengpx.lib.map.OruxMapViewer;
import org.opengpx.lib.map.OsmMapViewer;
import org.opengpx.lib.map.GoogleElevation.GoogleLocation;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CacheIndexItem;
import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.geocache.FieldNote;
import org.opengpx.lib.geocache.GCVote;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.geocache.helpers.FieldNoteList;
import org.opengpx.lib.ImportResult;
import org.opengpx.lib.xml.GCVoteReader;

import com.db4o.ObjectSet;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Martin Preishuber
 * 
 */
public class CacheListActivity extends ListActivity
{
	private CacheDatabase mCacheDatabase;
	private ProgressDialog mProgressDialog;
	private CacheListAdapter mCacheListAdapter;
	private Preferences mPreferences;
	private ImportResult mImportResult;

	private GpsLocationListener	mLocationListener = null;
	private Resources mResources;

	private final Logger mLogger = LoggerFactory.getLogger(CacheListActivity.class);
	
	private static final int MENU_DELETE = Menu.FIRST;

	private long mlngLoadingTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// mLogger.debug("onCreate");

		setTitle(R.string.app_title);
		setContentView(R.layout.cachelist);

		// mLogger.debug("onCreate");
		this.mResources = this.getResources();
		this.mPreferences = new Preferences(this);

		// Handle zip and/or gpx/loc files
		final FileSystemIntegration fsi = FileSystemIntegration.getInstance();
		fsi.handleIntent(getIntent(), this.mPreferences);

		this.mLocationListener = new GpsLocationListener((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		
		this.setRequestedOrientation(this.mPreferences.getScreenOrientation());

		this.mCacheDatabase = CacheDatabase.getInstance();
		this.mCacheDatabase.setSortOrder(this.mPreferences.getSortOrder());
		
		this.registerForContextMenu(this.getListView());

		this.loadCacheList();
	}

	@Override
	public void onPause()
	{
		super.onPause();

		mLocationListener.disableListener();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		// mLogger.debug("onResume");
		
		mLocationListener.enableListener();

		if (this.mCacheDatabase.isUpdated.compareAndSet(true, false))
		{
			// mLogger.debug("loading cache list ...");
			loadCacheList();
		} 
		else
		{
			// This may happen if you change the sort order in the search online function
			// Then it's necessary to set the location info!
			if (this.mCacheDatabase.getSortOrder() == CacheDatabase.SORT_ORDER_DISTANCE)
			{
				// Set reference coordinates
				final LocationInfo locationInfo = this.getLastKnownLocation();
				// Note: this function requires mCacheDatabase and mCacheListAdapter to be
				// initialized!
				// On startup (onCreate) this method is being executed before 
				// mCacheListAdapter has been initialized
				if ((this.mCacheDatabase != null) && (this.mCacheListAdapter != null))
					this.setGpsDatabaseProperties(locationInfo);
			}
			
			if ((this.mCacheDatabase != null) && (this.mCacheListAdapter != null))
			{	
				// Update cache list (e.g. after marking a cache as found)
				// mLogger.debug("reload cache list ...");
				this.mCacheListAdapter.updateListContent(this.mCacheDatabase.getFilterableList(), this.getListView().getTextFilter());
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		mLocationListener.disableListener();
	}

	/**
     * 
     */
	private void loadCacheList()
	{
		mProgressDialog = ProgressDialog.show(this, this.mResources.getString(R.string.cache_list_reading_caches), this.mResources.getString(R.string.cache_list_please_wait), true, false);

		Thread thread = new Thread(null, doBackgroundInitialization, "BgInit");
		thread.start();
	}

	/**
     * 
     */
	private Runnable doBackgroundInitialization	= new Runnable()
	{
		public void run()
		{
			final long lngStartTime = Calendar.getInstance().getTimeInMillis();

			// Read gpx/loc files
			mCacheDatabase.addGpxFolder(mPreferences.getDataFolder());
			mCacheDatabase.addSourceFolder(mPreferences.getImportPathList());

			boolean updateGCVotes = mPreferences.getAutoUpdateVotes();
			final ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			updateGCVotes = updateGCVotes && (cm.getActiveNetworkInfo() != null);

			mLogger.debug("Reading xml files ...");
			mImportResult = mCacheDatabase.readXmlFiles(mPreferences.getBackupGpxFiles());
			mCacheDatabase.readCacheIndex();

			// Update GCvotes
			if ((mImportResult.successful > 0) && (updateGCVotes))
			{
				mLogger.debug("Updating votes ...");
				final ArrayList<String> alCacheCodes = mCacheDatabase.getCacheCodes();
				// Get votes
				final GCVoteReader gcvr = new GCVoteReader();
				final HashMap<String, GCVote> gcVotes = gcvr.getVotes(alCacheCodes);
				// Add votes to database
				mCacheDatabase.addCacheVotes(gcVotes);
			}
			
			// Update waypoint elevation
			boolean updateElevation = (cm.getActiveNetworkInfo() != null);
			updateElevation = false; // not allowed here due to Google License restriction
			if ((mImportResult.successful > 0) && (updateElevation))
			{
				mLogger.debug("Updating waypoint elevation ...");
				final ArrayList<String> alCacheCodes = mCacheDatabase.getCacheCodes();
				for (String cacheCode : alCacheCodes)
				{
					final Cache cache = mCacheDatabase.getCache(cacheCode);
					final ArrayList<GoogleLocation> locations = new ArrayList<GoogleLocation>();
					for (final Waypoint wp : cache.getWaypoints())
					{
						if (wp.elevation == Integer.MIN_VALUE)
							locations.add(new GoogleLocation(wp.latitude, wp.longitude));
					}
					if (locations.size() > 0)
					{
						final GoogleElevation googleElevation = new GoogleElevation(true);
						final Hashtable<GoogleLocation, Double> locationElevation = googleElevation.getElevation(locations);  
						for (final Waypoint wp : cache.getWaypoints())
						{
							for (final GoogleLocation location : locationElevation.keySet())
							{
								if (location.equals(wp.latitude, wp.longitude))
								{
									final Double elev = locationElevation.get(location);
									if (!Double.isNaN(elev))
									{
										wp.elevation = (int) Math.round(elev);
										mCacheDatabase.updateWaypoint(wp);
									}
								}
							}
						}
					}
				}
			}
			
			mlngLoadingTime = Calendar.getInstance().getTimeInMillis() - lngStartTime;
			mLogger.debug("Done (" + mlngLoadingTime + "ms)");
			
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					doFinalizeRead();
				}
			});
		}
	};
	
	/**
     * 
     */
	private void doFinalizeRead()
	{
		// Clear text filter
		if (this.getListView().hasTextFilter()) this.getListView().clearTextFilter();

		// Create a new Data Adapter (for whatever reason it's not possible to
		// update the existing one)
		this.mCacheListAdapter = new CacheListAdapter(this, this.mCacheDatabase.getFilterableList()); 
		this.setListAdapter(this.mCacheListAdapter);

		if (this.mCacheDatabase.getSortOrder() == CacheDatabase.SORT_ORDER_DISTANCE)
		{
			// Set reference coordinates
			final LocationInfo locationInfo = this.getLastKnownLocation();
			// Note: this function requires mCacheDatabase and mCacheListAdapter to be
			// initialized!
			this.setGpsDatabaseProperties(locationInfo);
		}

		try
		{
			mProgressDialog.dismiss();
		}
		catch (IllegalArgumentException iae)
		{
			// Ignore "View not attached to window" errors here
		}

		final TextView tvStats = (TextView) this.findViewById(R.id.CacheStats);
		tvStats.setText(String.format("Caches: %d (%s: %dms)", this.mCacheDatabase.indexSize(), this.mResources.getString(R.string.cache_list_time), mlngLoadingTime));
		if (this.mCacheDatabase.size() == 0 && mPreferences.getShowEmptyDbHelp())
		{
			// Show some reminder on how to start
			final AlertDialog welcomeDialog = new WelcomeDialog(this).create();			
			welcomeDialog.show();
		}

		// Show import results
		if ((mImportResult.successful > 0) || (mImportResult.failed > 0))
		{
			final StringBuilder sbResult = new StringBuilder();
			sbResult.append(this.mResources.getString(R.string.cache_list_indexing_result));
			sbResult.append("\n");
			if (mImportResult.successful > 0) sbResult.append(String.format("\n%s: %d", this.mResources.getString(R.string.cache_list_successful), mImportResult.successful));
			if (mImportResult.failed > 0) sbResult.append(String.format("\n%s: %d", this.mResources.getString(R.string.cache_list_failed), mImportResult.successful));
			Toast.makeText(this, sbResult.toString(), Toast.LENGTH_LONG).show();
		}
	}

	/**
     * 
     */
	private void showDbOpenError()
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Unable to open db4o database");
		builder.setMessage(this.mCacheDatabase.getErrorMessage());
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
			}
		});
		final AlertDialog errorDialog = builder.create();
		errorDialog.show();
	}

	/**
     * 
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.cachelist, menu);
		return true;
	}

	/**
     * 
     */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle(R.string.cache_options);
		menu.add(0, MENU_DELETE, Menu.NONE, R.string.delete_cache);
	}

	/**
     * 
     */
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final String strCacheCode = this.mCacheDatabase.getCacheCodeFromFilterable(this.mCacheListAdapter.getItem(menuInfo.position));

		switch (item.getItemId())
		{
		case MENU_DELETE:
			this.deleteCache(strCacheCode);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param strCacheCode
	 */
	private void deleteCache(final String strCacheCode)
	{
		final CacheIndexItem cii = this.mCacheDatabase.getCacheIndexItem(strCacheCode);

		final String deleteQuestion = this.mResources.getString(R.string.delete_cache_question);
		new AlertDialog.Builder(this).setTitle(R.string.question).setMessage(String.format(deleteQuestion, cii.name)).setIcon(
				android.R.drawable.ic_dialog_alert).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				final String strFilterableName = mCacheDatabase.getFilterableName(strCacheCode);
				mCacheDatabase.deleteCache(strCacheCode);
				mCacheListAdapter.remove(strFilterableName);
			}
		}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
			}
		}).show();
	}

	/**
     * 
     */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.MenuInfoFile:
			Intent intent = new Intent(this, TextViewerActivity.class);
			final String strInfoFilename = String.format("%s%sinfo.txt", this.mPreferences.getDataFolder(), File.separator);
			intent.putExtra("filename", strInfoFilename);
			this.startActivity(intent);
			return true;
		case R.id.MenuPreferences:
			Intent intPreferences = new Intent(this, OpenGpxPreferenceActivity.class);
			this.startActivity(intPreferences);
			return true;
		case R.id.MenuDatabaseClear:
			this.clearDatabase();
			return true;
		case R.id.MenuDatabaseInfo:
			final String databaseInfo = this.mCacheDatabase.getInformation(this.mResources.getString(R.string.database), this.mResources.getString(R.string.cache_index_size), this.mResources.getString(R.string.cache_database_size), this.mResources.getString(R.string.cache_variables), this.mResources.getString(R.string.cache_votes), this.mResources.getString(R.string.field_notes), this.mResources.getString(R.string.personal_notes));
			Toast.makeText(this, databaseInfo, Toast.LENGTH_LONG).show();
			return true;
		case R.id.MenuDatabaseSelect:
			this.showDatabaseSelectionDialog();
			return true;
		case R.id.MenuDatabaseUpdateVotes:
			this.updateVotesWithProgressDialog();
			return true;
		case R.id.MenuSearchOnline:
			this.searchOnline();
			return true;
		case R.id.MenuAbout:
			this.showAboutDialog();
			return true;
		case R.id.MenuCacheMap:
			if (this.mCacheDatabase.size() > 0)
			{
				final MapProvider mapProvider = this.mPreferences.getMapProvider();
				final LocationInfo locationInfo = this.getLastKnownLocation();
				
				// Check whether we filter out finds
				ArrayList<String> cachesFound = null;
		        if (this.mPreferences.getHideCachesFound())
		        {
		        	final FieldNoteList fieldNoteList = new FieldNoteList();
		        	cachesFound = fieldNoteList.getCacheCodes(FieldNote.LogType.FOUND);
		        }

				MapViewer mapViewer;
				if (mapProvider == MapProvider.Google)
				{
					// Set database properties for sorting caches properly
					this.mCacheDatabase.setMaxResults(this.mPreferences.getCacheLimit());
					this.mCacheDatabase.setReferenceCoordinates(locationInfo.latitude, locationInfo.longitude);

					mapViewer = new GoogleMapViewer(this);
					mapViewer.addCaches(this.mCacheDatabase.getCacheCodesSortedByDistance(cachesFound));
				}
				else if (mapProvider == MapProvider.OpenStreetMap)
				{
					mapViewer = new OsmMapViewer(this);
					mapViewer.addCaches(this.mCacheDatabase.getCacheCodes(cachesFound));
				}
				else if (mapProvider.equals(MapProvider.Locus))
				{
					mapViewer = new LocusMapViewer(this);
					mapViewer.addCaches(this.mCacheDatabase.getCacheCodes(cachesFound));
				}
				else
				{
					final OruxMapViewer oruxMapViewer = new OruxMapViewer(this);
					if (mapProvider == MapProvider.OruxMapsOnline)
						oruxMapViewer.setUseOfflineMap(false);
					else
						oruxMapViewer.setUseOfflineMap(true);
						
					mapViewer = oruxMapViewer;
					mapViewer.addCaches(this.mCacheDatabase.getCacheCodes(cachesFound));
				}
				mapViewer.setCenter(locationInfo.latitude, locationInfo.longitude, locationInfo.provider);
				mapViewer.setUnitSystem(this.mPreferences.getUnitSystem());
				mapViewer.startActivity();			
			}
			else
			{
				Toast.makeText(this, R.string.no_caches_available, Toast.LENGTH_SHORT).show();
			}

			return true;
		case R.id.SortByDistance:
			this.sortCachesByDistance();
			return true;
		case R.id.SortByName:
			this.sortCachesByName();
			return true;
		case R.id.MenuShowKeyboard:
			final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(this.getListView(), 0);
			return true;
		case R.id.MenuExit:
			this.finish();
			return true;
		case R.id.MenuUploadFieldNotes:
			this.uploadFieldNotes();
			return true;
		case R.id.MenuDeleteFieldNotes:
			mCacheDatabase.deleteFieldNotes(mCacheDatabase.getFieldNotes());
			Toast.makeText(this, this.mResources.getString(R.string.field_notes_deleted), Toast.LENGTH_LONG).show();
			return true;
		case R.id.MenuShowFieldNotes:
			this.showFieldNotes();
			return true;
		}
		return false;
	}
	
	private void uploadFieldNotes()
	{
		final Context context = this;
		showUploadingDialog();
		
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					SearchBCaching search = new SearchBCaching(mPreferences);
					final String result = search.sendFieldNoteList(mCacheDatabase.getFieldNotes());
					
					if (result != null)
					{
						mCacheDatabase.deleteFieldNotes(mCacheDatabase.getFieldNotes());
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("Upload Result").create();
								final TextView view = new TextView(alertDialog.getContext());
								view.setText(result);
								alertDialog.setView(view);
								alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
									}
								});

								alertDialog.show();
							}
						});
					}
					else
					{
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("Upload Result").create();
								final TextView view = new TextView(alertDialog.getContext());
								view.setText("Error Uploading Field Notes!");
								alertDialog.setView(view);
								alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog, int whichButton)
									{
									}
								});

								alertDialog.show();
							}
						});
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							dismissProgressDialog();
						}
					});					
				}
			}
		});
		
		t.start();
	}
	
	private void showUploadingDialog()
	{
		mProgressDialog = ProgressDialog.show(this, "Uploading Field Note", "Please wait - this may take a while ...", true, false);
	}

	private void dismissProgressDialog()
	{
		try
		{
			if (mProgressDialog != null) mProgressDialog.dismiss();
		}
		catch (IllegalArgumentException iae)
		{
			// Ignore "View not attached to window" errors here
		}
	}
	
	/**
	 * 
	 */
	private void showFieldNotes()
	{
		final ObjectSet<FieldNote> fieldNotes = this.mCacheDatabase.getFieldNotes(true);
		if (fieldNotes.isEmpty())
		{
			Toast.makeText(this, R.string.no_field_notes, Toast.LENGTH_LONG).show();
		}
		else
		{
			final Intent intent = new Intent(this, FieldNoteHistoryActivity.class);
			this.startActivity(intent);
		}
	}
	
	/**
	 * 
	 */
	private void searchOnline()
	{
		Intent intent = new Intent(this, SearchListActivity.class);
		this.startActivity(intent);
	}

	/**
     * 
     */
	private void clearDatabase()
	{
		new AlertDialog.Builder(this).setTitle(R.string.question).setMessage(R.string.clear_database_question).setIcon(android.R.drawable.ic_dialog_info).setPositiveButton(
				R.string.yes, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						mCacheListAdapter.clear();
						boolean blnResult = mCacheDatabase.clear();
						if (!blnResult) showDbOpenError();

						// Make sure that cache list is cleared as well
						loadCacheList();
					}
				}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
			}
		}).show();
	}

	/**
	 * 
	 * @author Martin Preishuber
	 * 
	 */
	private static class Db4oFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			final String strLowerCaseName = name.toLowerCase();
			final Boolean blnHeaderCorrect = (!name.startsWith("."));
			final Boolean blnExtensionCorrect = strLowerCaseName.endsWith(".db4o");
			
			final Boolean isInternalDatabase = strLowerCaseName.startsWith("searchdatabase") || strLowerCaseName.startsWith("fieldnotedatabase");
			
			return (blnExtensionCorrect && blnHeaderCorrect && !isInternalDatabase);
		}
	}

	/**
     * 
     */
	private void showDatabaseSelectionDialog()
	{
		final String strDataPath = this.mPreferences.getDataFolder();
		final String strDatabasePath = String.format("%s%sdatabase", strDataPath, File.separator);

		final File folder = new File(strDatabasePath);
		boolean result = true;
		// this is just a precaution
		if (!folder.exists())
			result = folder.mkdirs();

		final CharSequence[] items = folder.list(new Db4oFilter());
		if ((items == null) || (!result))
		{
			String strError = String.format("Unable to access folder '%s'.", strDatabasePath);
			Toast.makeText(this, strError, Toast.LENGTH_LONG).show();
		}
		else if (items.length == 0)
		{
			Toast.makeText(this, "No database available. Please restart!", Toast.LENGTH_LONG).show();
		}
		else
		{
			this.mstrSelectedDatabase = this.mCacheDatabase.getDatabaseFilename();
			int intSelectedItem = 0;
			for (int i = 0; i < items.length; i++)
			{
				if (this.mstrSelectedDatabase.equals(items[i])) intSelectedItem = i;
			}

			new AlertDialog.Builder(this).setTitle("Database").setSingleChoiceItems(items, intSelectedItem, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					mstrSelectedDatabase = (String) items[which];
				}
			}).setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					selectDatabase(mstrSelectedDatabase);
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
				}
			}).show();
		}
	}

	private String	mstrSelectedDatabase; // This is for the cache selection
																				// dialog

	/**
	 * 
	 * @param database
	 */
	private void selectDatabase(String database)
	{
		if (!database.equalsIgnoreCase(this.mCacheDatabase.getDatabaseFilename()))
		{
			// Save database in preferences
			this.mPreferences.setDatabaseFilename(database);

			this.mCacheDatabase.close();
			this.mCacheDatabase.openDatabase(database);

			this.loadCacheList();
		}
		else
		{
			Toast.makeText(this, "Database already active.", Toast.LENGTH_SHORT).show();
		}
	}

	/**
     * 
     */
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		final String strCacheCode = this.mCacheDatabase.getCacheCodeFromFilterable(this.mCacheListAdapter.getItem(position));
		final Intent intent = new Intent(this, CacheDetailActivity.class);
		intent.putExtra("cachecode", strCacheCode);
		intent.putExtra("isSearchResult", false);
		this.startActivity(intent);
	}

	/**
     * 
     */
	private void showAboutDialog()
	{
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		final LayoutInflater inflater = this.getLayoutInflater();
		final View layout = inflater.inflate(R.layout.aboutbox, null);

		dialog.setView(layout);
		dialog.setTitle(R.string.app_name);
		
		final TextView tvNameVersion = (TextView) layout.findViewById(R.id.AboutBoxNameVersion);
		try
		{
			final PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
			final String strSubTitleVersion = String.format("You Are The Search Engine\nVersion %s (Build %d)", pi.versionName, pi.versionCode);
			tvNameVersion.setText(strSubTitleVersion);
		}
		catch (Exception e)
		{
			mLogger.error("Can't find version name", e);
			tvNameVersion.setText("Version unknown");
		}
		final TextView tvCreditsGroundspeak = (TextView) layout.findViewById(R.id.AboutGroundspeak);
		Linkify.addLinks(tvCreditsGroundspeak, Pattern.compile("Groundspeak"), "http://www.groundspeak.com/?ref=");
		Linkify.addLinks(tvCreditsGroundspeak, Pattern.compile("Geocaching"), "http://www.geocaching.com/?ref=");
		
		// final TextView tvCreditsOsmdroid = (TextView) dialog.findViewById(R.id.AboutOsmdroid);
		final TextView tvCreditsOsmdroid = (TextView) layout.findViewById(R.id.AboutOsmdroid);
		Linkify.addLinks(tvCreditsOsmdroid, Pattern.compile("OpenStreetMap"), "http://wiki.openstreetmap.org/?ref=");
		Linkify.addLinks(tvCreditsOsmdroid, Pattern.compile("osmdroid"), "http://code.google.com/p/osmdroid/?ref=");
		
		// final TextView tvCreditsCloudMade = (TextView) dialog.findViewById(R.id.AboutCloudMade);
		final TextView tvCreditsCloudMade = (TextView) layout.findViewById(R.id.AboutCloudMade);
		Linkify.addLinks(tvCreditsCloudMade, Pattern.compile("CloudMade"), "http://cloudmade.com/?ref=");

		// final TextView tvCreditsDb4o = (TextView) dialog.findViewById(R.id.AboutDb4o);
		final TextView tvCreditsDb4o = (TextView) layout.findViewById(R.id.AboutDb4o);
		Linkify.addLinks(tvCreditsDb4o, Pattern.compile("db4objects"), "http://db4o.com/?ref=");

		dialog.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}
		});
		dialog.show();		
	}

	/**
     * 
     */
	private void sortCachesByName()
	{
		this.mCacheDatabase.setSortOrder(CacheDatabase.SORT_ORDER_NAME);
		this.mPreferences.setSortOrder(CacheDatabase.SORT_ORDER_NAME);
		this.mCacheListAdapter.updateListContent(this.mCacheDatabase.getFilterableList(), this.getListView().getTextFilter());
	}

	/**
     * 
     */
	private void sortCachesByDistance()
	{
		final LocationInfo locationInfo = this.getLastKnownLocation();
		final Coordinates coordinates = new Coordinates(locationInfo.latitude, locationInfo.longitude);
		final CoordinateFormat coordinateFormat = this.mPreferences.getCoordinateFormat();
		final String strMessage = String.format("%s [%s]", coordinates.toString(coordinateFormat), locationInfo.provider);
		Toast.makeText(this, strMessage, Toast.LENGTH_LONG).show();

		// Set database properties for sorting caches properly
		this.setGpsDatabaseProperties(locationInfo);
		this.mCacheDatabase.setSortOrder(CacheDatabase.SORT_ORDER_DISTANCE);
		this.mPreferences.setSortOrder(CacheDatabase.SORT_ORDER_DISTANCE);

		this.mCacheListAdapter.updateListContent(this.mCacheDatabase.getFilterableList(), this.getListView().getTextFilter());
	}

	/**
	 * 
	 * @param locationInfo
	 */
	private void setGpsDatabaseProperties(LocationInfo locationInfo)
	{
		this.mCacheDatabase.setMaxResults(this.mPreferences.getCacheLimit());
		this.mCacheDatabase.setReferenceCoordinates(locationInfo.latitude, locationInfo.longitude);

		// Set reference coordinates in cache list (necessary to calculate distance
		// / bearing)
		this.mCacheListAdapter.setReferenceCoordinates(locationInfo.latitude, locationInfo.longitude);
	}

	/**
	 * 
	 * @return
	 */
	private LocationInfo getLastKnownLocation()
	{
		LocationInfo locationInfo = new LocationInfo();
		Location locLastKnown = mLocationListener.getLastKnownLocation();
		
		if (locLastKnown != null)
		{
			if (locLastKnown.getProvider().equals(LocationManager.GPS_PROVIDER))
			{
				locationInfo.provider = "GPS";			
			}
			else if (locLastKnown.getProvider().equals(LocationManager.NETWORK_PROVIDER))
			{
				locationInfo.provider = "Network";				
			}

			locationInfo.latitude = locLastKnown.getLatitude();
			locationInfo.longitude = locLastKnown.getLongitude();
		}
		else
		{
			Coordinates homeCoordinates = this.mPreferences.getHomeCoordinates();
			// locationInfo.latitude = homeCoordinates.getLatitude().getD();
			// locationInfo.longitude = homeCoordinates.getLongitude().getD();
			locationInfo.latitude = homeCoordinates.getLatitude();
			locationInfo.longitude = homeCoordinates.getLongitude();
			locationInfo.provider = "Home";
		}

		return locationInfo;
	}
	
	/**
	 * 
	 */
	private void updateVotes()
	{
		final ArrayList<String> alCacheCodes = this.mCacheDatabase.getCacheCodes();
		// Get votes
		final GCVoteReader gcvr = new GCVoteReader();
		final HashMap<String, GCVote> gcVotes = gcvr.getVotes(alCacheCodes);
		// Add votes to database
		this.mCacheDatabase.addCacheVotes(gcVotes);
	}
	
	/**
	 * 
	 */
	private void updateVotesWithProgressDialog()
	{
		final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null)
		{
			mProgressDialog = ProgressDialog.show(this, "Updating Votes", "Please wait - this may take a while ...", true, false);
	
			Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					// Update votes
					updateVotes();
					
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							// Update cache list
							mCacheListAdapter.updateListContent(mCacheDatabase.getFilterableList(), getListView().getTextFilter());
							dismissProgressDialog();
						}
					});
				}
			});
			
			t.start();
		}
		else 
		{
			Toast.makeText(this, "Unable to update votes (no network connectivity)", Toast.LENGTH_LONG).show();
		}
	}
}
