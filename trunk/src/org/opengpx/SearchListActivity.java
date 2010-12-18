package org.opengpx;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opengpx.Preferences;
import org.opengpx.lib.map.GoogleMapViewer;
import org.opengpx.lib.map.MapViewer;
import org.opengpx.lib.map.OsmMapViewer;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchListActivity extends ListActivity
{
	private CacheDatabase				cacheDatabase;
	private ProgressDialog			progressDialog;
	private SearchListAdapter		cacheListAdapter;
	private Preferences		mPreferences;
	private AlertDialog					alertDialog;

	private GpsLocationListener	locationListener	= null;

	private static final String titleBase 				= "OpenGPX - Online Search";
	// private static final int		MENU_DELETE				= Menu.FIRST;
	private static final int		MENU_SAVEDB				= Menu.FIRST;

	private long								loadingTime;

	private SearchBCaching			searchBCaching;
	private ScheduledExecutorService scheduler = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setTitle(titleBase);
		setContentView(R.layout.searchlist);

		this.locationListener = new GpsLocationListener((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		this.mPreferences = new Preferences(this);

		this.setRequestedOrientation(this.mPreferences.getScreenOrientation());

		this.cacheDatabase = CacheDatabase.getInstance();
		this.cacheDatabase.setSortOrder(this.mPreferences.getSortOrder());

		if (!cacheDatabase.isSearchDatabaseOpen()) 
			cacheDatabase.openSearchDatabase();

		this.registerForContextMenu(this.getListView());

		Toast.makeText(this, "Press MENU For Search Options", Toast.LENGTH_LONG).show();
		
		startUpdater();
		
		cacheDatabase.readSearchIndex();
		doFinalizeSearch();
		
//		try
//		{
//			searchBCaching s = new SearchBCaching(false);
//			s.parseJsonDetailData(new FileInputStream("/sdcard/test.json"));
//			doFinalizeSearch();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
		
	}

	protected void startUpdater()
	{
		if (scheduler == null)
		{
			scheduler = Executors.newScheduledThreadPool(1);
			
			Runnable accuracyUpdater = new Runnable()
			{
				public void run()
				{
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							Location loc = locationListener.getLastKnownLocation();
							
							if (loc != null)
							{
								setTitle(String.format(titleBase + "      Accuracy: %.0fM", loc.getAccuracy()));
							}
						}
					});
				}
			};
			
			scheduler.scheduleWithFixedDelay(accuracyUpdater, 0, 5, TimeUnit.SECONDS);
		}
	}
	
	protected void stopUpdater()
	{
		if (scheduler != null)
		{
			scheduler.shutdownNow();
			scheduler = null;			
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();

		locationListener.disableListener();
		stopUpdater();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		if (cacheDatabase.isSearchUpdated.compareAndSet(true, false))
		{
			doFinalizeSearch();
		}

		locationListener.enableListener();
		startUpdater();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		locationListener.disableListener();
		stopUpdater();
		
		cacheDatabase.closeSearchDatabase();
	}

	private void doFinalizeSearch()
	{
		// Clear text filter
		if (this.getListView().hasTextFilter()) this.getListView().clearTextFilter();

		// Create a new Data Adapter (for whatever reason it's not possible to
		// update the existing one)
		// this.cacheListAdapter = new SearchListAdapter(this);		
		this.cacheListAdapter = new SearchListAdapter(this, this.cacheDatabase.getSearchList());		
		// this.cacheListAdapter.updateListContent(this.cacheDatabase.getSearchList(), null);

		this.setListAdapter(this.cacheListAdapter);

		if (this.cacheDatabase.getSortOrder() == CacheDatabase.SORT_ORDER_DISTANCE)
		{
			// Set reference coordinates
			LocationInfo locationInfo = this.getLastKnownLocation();
			// Note: this function requires mCacheDatabase and mCacheListAdapter to be
			// initialized!
			this.setGpsDatabaseProperties(locationInfo);
		}

		try {
			dismissProgressDialog();
		} catch (IllegalArgumentException iae) {}

		TextView tvStats = (TextView) this.findViewById(R.id.CacheStats);
		tvStats.setText(String.format("Caches: %d (Time: %dms)", this.cacheDatabase.getSearchIndexSize(), loadingTime));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.searchlist, menu);
		return true;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final String cacheCode = this.cacheListAdapter.getItem(menuInfo.position);

		switch (item.getItemId())
		{
		// case MENU_DELETE:
			// this.deleteCache(strCacheCode);
		// 	return true;
		case MENU_SAVEDB:
			saveCacheToDB(cacheCode);
			return true;
		}
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.MenuSearchAdvanced:
			Intent intent = new Intent(this, AdvancedSearchActivity.class);
			this.startActivity(intent);
			return true;
		case R.id.MenuSearchByDistance:
			searchBCaching(null);
			return true;
		case R.id.MenuSearchByName:
			startSearchBCachingByName();
			return true;
		case R.id.MenuShowKeyboard:
			InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(this.getListView(), 0);
			return true;
		case R.id.MenuSearchSaveToDB:
			saveResultsToDB();
			return true;
		case R.id.MenuSearchClear:
			cacheDatabase.clearSearchCacheData();
			cacheDatabase.readSearchIndex();
			doFinalizeSearch();
			return true;
		case R.id.MenuCacheMap:
			MapProvider mapProvder = this.mPreferences.getMapProvider();
			if (mapProvder == MapProvider.Google)
				showCachesOnGoogleMap();
			else showCachesOnOSM();
			return true;
		case R.id.MenuSearchSortByDistance:
			this.sortCachesByDistance();
			return true;
		case R.id.MenuSearchSortByName:
			this.sortCachesByName();
			return true;
		}
		return false;
	}

	private void sortCachesByDistance()
	{
		LocationInfo locationInfo = this.getLastKnownLocation();
		final Coordinates coordinates = new Coordinates(locationInfo.latitude, locationInfo.longitude);
		final CoordinateFormat coordinateFormat = this.mPreferences.getCoordinateFormat();
		final String strMessage = String.format("%s [%s]", coordinates.toString(coordinateFormat), locationInfo.provider);
		Toast.makeText(this, strMessage, Toast.LENGTH_LONG).show();

		// Set database properties for sorting caches properly
		setGpsDatabaseProperties(locationInfo);
		cacheDatabase.setSortOrder(CacheDatabase.SORT_ORDER_DISTANCE);
		//this.saveSortOrder(CacheDatabase.SORT_ORDER_DISTANCE);

		cacheListAdapter.updateListContent(cacheDatabase.getSearchList(), this.getListView().getTextFilter());
	}
	
	private void sortCachesByName()
	{
		cacheDatabase.setSortOrder(CacheDatabase.SORT_ORDER_NAME);
		//this.saveSortOrder(CacheDatabase.SORT_ORDER_NAME);
		cacheListAdapter.updateListContent(cacheDatabase.getSearchList(), this.getListView().getTextFilter());
	}
	
	/**
     * 
     */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.setHeaderTitle("Cache Options");
		// menu.add(0, MENU_DELETE, Menu.NONE, "Delete cache");
		menu.add(0, MENU_SAVEDB, Menu.NONE, "Save Cache");
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		final String strCacheCode = cacheListAdapter.getItem(position);
		Intent intent = new Intent(this, CacheDetailActivity.class);
		intent.putExtra("cachecode", strCacheCode);
		intent.putExtra("isSearchResult", true);
		this.startActivity(intent);
	}

	private void saveCacheToDB(String cacheCode)
	{
		final String bcUsername = this.mPreferences.getBCachingUsername();
		final String bcPassword = this.mPreferences.getBCachingPassword();
		// final boolean bcTestSite = this.mPreferences.getUseBCachingTestSite();

		try
		{
			showSearchingDialog();
			
			// final SearchBCaching sbc = new SearchBCaching(bcTestSite);
			final SearchBCaching sbc = new SearchBCaching();
			sbc.setLoginInfo(bcUsername, bcPassword);
			sbc.doSingleDetailQuery(cacheCode);
			// searchBCaching.doSingleDetailQuery(cacheCode);
			dismissProgressDialog();

			showLoadingDialog();
			cacheDatabase.saveSearchCacheToDB(cacheCode);
			dismissProgressDialog();
			
			cacheDatabase.isUpdated.set(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			dismissProgressDialog();
			showBCachingErrorInUIThread();
		}
	}

	private void saveResultsToDB()
	{
		final List<String> list = cacheDatabase.getSearchCacheCodes();
		if (list != null && list.size() > 0)
		{
			try
			{
				showSearchingDialog();

				Thread t = new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							searchBCaching.doDetailQuery(list);

							runOnUiThread(new Runnable()
							{
								public void run()
								{
									dismissProgressDialog();
									showLoadingDialog();
								}
							});

							cacheDatabase.saveAllSearchCachesToDB();
							cacheDatabase.isUpdated.set(true);

							dismissProgressDialog();
						}
						catch (Exception e)
						{
							e.printStackTrace();
							dismissProgressDialog();
							showBCachingErrorInUIThread();
						}
					}
				});

				t.start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void setGpsDatabaseProperties(LocationInfo locationInfo)
	{
		this.cacheDatabase.setMaxResults(this.mPreferences.getCacheLimit());
		this.cacheDatabase.setReferenceCoordinates(locationInfo.latitude, locationInfo.longitude);

		// Set reference coordinates in cache list (necessary to calculate distance
		// / bearing)
		this.cacheListAdapter.setReferenceCoordinates(locationInfo.latitude, locationInfo.longitude);
	}

	private LocationInfo getLastKnownLocation()
	{
		LocationInfo locationInfo = new LocationInfo();
		Location locLastKnown = locationListener.getLastKnownLocation();

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
			locationInfo.latitude = homeCoordinates.getLatitude().getD();
			locationInfo.longitude = homeCoordinates.getLongitude().getD();
			locationInfo.provider = "Home";
		}

		return locationInfo;
	}

	private void dismissProgressDialog()
	{
		try
		{
			runOnUiThread(new Runnable()
			{
				public void run()
				{
					if (progressDialog != null) progressDialog.dismiss();
				}
			});
		}
		catch (IllegalArgumentException iae)
		{
			// Ignore "View not attached to window" errors here
		}
	}
	
	private void showBCachingErrorInUIThread()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				showBCachingError();
			}
		});		
	}

	private void showBCachingError()
	{
		// Show a dialog to let the user know something died....
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage("BCaching.com query failed.\n\nPlease check your login information.\n\nIf the error persists please email a log to the developers.");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				return;
			}
		});
		alertDialog.show();
	}

	/* private void showDBLoadError()
	{
		// Show a dialog to let the user know something died....
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Error");
		alertDialog.setMessage("Loading the GPX file into the database failed. If the error persists please email a log to the developers.");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				return;
			}
		});
		alertDialog.show();
	} */

	private void showLoadingDialog()
	{
		progressDialog = ProgressDialog.show(this, "Saving Cache Data To DB", "Please wait - this may take a while ...", true, false);
	}

	private void showSearchingDialog()
	{
		progressDialog = ProgressDialog.show(this, "Running Online Search", "Please wait - this may take a while ...", true, false);
	}

	private void startSearchBCachingByName()
	{
		alertDialog = new AlertDialog.Builder(this).setTitle("Enter Name or GCID").create();
		final EditText entry = new EditText(alertDialog.getContext());
		alertDialog.setView(entry);
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				searchBCaching(entry.getText().toString());
			}			
		});
		
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
			}
		});
		
		alertDialog.show();
	}

	private void searchBCaching(final String name)
	{
		final String bcUsername = this.mPreferences.getBCachingUsername();
		final String bcPassword = this.mPreferences.getBCachingPassword();
		final int bcMaxCaches = this.mPreferences.getBCachingMaxCaches();
		final int bcMaxDistance = this.mPreferences.getBCachingMaxDistance();
		// final boolean bcTestSite = this.mPreferences.getUseBCachingTestSite();
		
		final LocationInfo locationInfo = this.getLastKnownLocation();
		Coordinates coordinates = new Coordinates(locationInfo.latitude, locationInfo.longitude);
		CoordinateFormat coordinateFormat = this.mPreferences.getCoordinateFormat();
		String strMessage = String.format("%s [%s]", coordinates.toString(coordinateFormat), locationInfo.provider);
		Toast.makeText(this, strMessage, Toast.LENGTH_LONG).show();

		try
		{
			showSearchingDialog();

			Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					try
					{
						final long lngStartTime = Calendar.getInstance().getTimeInMillis();

						// searchBCaching = new SearchBCaching(bcTestSite);
						searchBCaching = new SearchBCaching();
						searchBCaching.setLoginInfo(bcUsername, bcPassword);
						searchBCaching.doFindQuery(locationInfo, bcMaxDistance, bcMaxCaches, name);

						loadingTime = Calendar.getInstance().getTimeInMillis() - lngStartTime;

						dismissProgressDialog();

						runOnUiThread(new Runnable()
						{
							public void run()
							{
								doFinalizeSearch();
							}
						});
					}
					catch (Exception e)
					{
						e.printStackTrace();
						dismissProgressDialog();
						showBCachingErrorInUIThread();
					}
				}
			});
			t.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/* private void saveDataFromStream(InputStream in, String fileName) throws IOException
	{
		String name = this.settings.getString(OpenGPX.PREFS_KEY_DATA_FOLDER, OpenGPX.PREFS_DEFAULT_DATA_FOLDER) + "/" + fileName;
		FileOutputStream out = new FileOutputStream(name);

		byte[] buf = new byte[512];
		int count = 0;
		while ((count = in.read(buf, 0, buf.length)) != -1)
		{
			out.write(buf, 0, count);
		}

		out.close();
		in.close();
	} */

	private void showCachesOnGoogleMap()
	{
		if (this.cacheListAdapter.getCount() > 0)
		// if (cacheDatabase.size() > 0)
		{
			final LocationInfo locationInfo = this.getLastKnownLocation();
	
			MapViewer mapViewer = new GoogleMapViewer(this);
			mapViewer.addCaches(cacheDatabase.getSearchCacheCodes());
			mapViewer.setCenter(locationInfo.latitude, locationInfo.longitude, "Home");
			mapViewer.startActivity();
		}
		else
		{
			Toast.makeText(this, "No caches available.", Toast.LENGTH_SHORT).show();
		}
	}

	/**
     * 
     */
	private void showCachesOnOSM()
	{
		if (this.cacheListAdapter.getCount() > 0)
		// if (cacheDatabase.size() > 0)
		{
			final LocationInfo locationInfo = this.getLastKnownLocation();

			MapViewer mapViewer = new OsmMapViewer(this);
			mapViewer.addCaches(cacheDatabase.getSearchCacheCodes());
			mapViewer.setCenter(locationInfo.latitude, locationInfo.longitude, "Home");
			mapViewer.startActivity();			
		}
		else
		{
			Toast.makeText(this, "No caches available.", Toast.LENGTH_SHORT).show();
		}
	}
}
