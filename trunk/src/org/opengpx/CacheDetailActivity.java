package org.opengpx;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.opengpx.CacheAttributeDialog;
import org.opengpx.CompassProvider;
import org.opengpx.GCVoteDialog;
import org.opengpx.NavigationProvider;
import org.opengpx.Preferences;
import org.opengpx.WaypointDetailDialog;
import org.opengpx.lib.map.GoogleMapViewer;
import org.opengpx.lib.map.MapViewer;
import org.opengpx.lib.map.OsmMapViewer;

import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CacheIndexItem;
import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.geocache.FieldNote;
import org.opengpx.lib.geocache.GCVote;
import org.opengpx.lib.geocache.LogEntry;
import org.opengpx.lib.geocache.PersonalNote;
import org.opengpx.lib.NavigationInfo;
import org.opengpx.lib.Text;
import org.opengpx.lib.geocache.TravelBug;
import org.opengpx.lib.UserDefinedVariables;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.geocache.WaypointType;
import org.opengpx.lib.geocache.FieldNote.LogType;
import org.opengpx.lib.xml.GCVoteReader;
import org.opengpx.lib.tools.AndroidSystem;
import org.opengpx.lib.tools.HtmlCodeBuilder;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Martin Preishuber
 * 
 */
public class CacheDetailActivity extends TabActivity
{
	private TabHost					mTabHost;
	private Cache					mCache;
	private Preferences				mPreferences;
	private boolean					isSaved					= false;
	private ProgressDialog			progressDialog;
	private CacheDatabase			mCacheDatabase;
	private Resources				mResources;

	private static final int		MENU_INFO				= Menu.FIRST;
	private static final int		MENU_COMPASS			= MENU_INFO + 1;
	private static final int		MENU_MAP				= MENU_COMPASS + 1;
	private static final int		MENU_NAVIGATE			= MENU_MAP + 1;
	private static final int		MENU_GEO				= MENU_NAVIGATE + 1;
	private static final int		MENU_DELETE				= MENU_GEO + 1;

	// public static final String	ANDNAV2_VIEW_ACTION	= "org.andnav2.intent.ACTION_VIEW";
	// public static final String	ANDNAV2_NAV_ACTION	= "org.andnav2.intent.ACTION_NAV_TO";

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cachedetail);

		this.mResources = this.getResources();

		this.mCacheDatabase = CacheDatabase.getInstance();

		// Get cache from cache database
		final Bundle bunExtras = this.getIntent().getExtras();
		final String strCacheCode = (String) bunExtras.get("cachecode");
		this.isSaved = bunExtras.getBoolean("isCacheSaved");

		this.mPreferences = new Preferences(this);
		this.setRequestedOrientation(this.mPreferences.getScreenOrientation());

		if (bunExtras.getBoolean("isSearchResult"))
			this.mCache = CacheDatabase.getInstance().getCacheFromSearch(strCacheCode);
		else this.mCache = CacheDatabase.getInstance().getCache(strCacheCode);

		if (this.mCache != null)
		{
			this.setTitle("OpenGPX - " + this.mCache.name);

			this.mTabHost = getTabHost();

			this.mTabHost.addTab(mTabHost.newTabSpec("tabDescription").setIndicator(this.mResources.getString(R.string.cache_desc_description), this.mResources.getDrawable(android.R.drawable.ic_menu_info_details)).setContent(R.id.CacheDetailDescriptionScrollView));
			this.mTabHost.addTab(mTabHost.newTabSpec("tabWaypoints").setIndicator(this.mResources.getString(R.string.cache_desc_waypoints), this.mResources.getDrawable(android.R.drawable.ic_menu_myplaces)).setContent(R.id.WaypointList));
			this.mTabHost.addTab(mTabHost.newTabSpec("tabPersonalNotes").setIndicator(this.mResources.getString(R.string.cache_desc_personal_notes), this.mResources.getDrawable(android.R.drawable.ic_menu_edit)).setContent(R.id.CachePersonalNoteScrollView));
			this.mTabHost.addTab(mTabHost.newTabSpec("tabLogs").setIndicator(this.mResources.getString(R.string.cache_desc_logs), this.mResources.getDrawable(android.R.drawable.ic_menu_recent_history)).setContent(R.id.CacheDetailLogLayout));
			this.mTabHost.addTab(mTabHost.newTabSpec("tabLogVisit").setIndicator(this.mResources.getString(R.string.cache_desc_log_visit), this.mResources.getDrawable(android.R.drawable.ic_menu_agenda)).setContent(R.id.CacheDetailLogVisitScrollView));

			this.readCacheDescription();
			this.initializeWaypointList();
			this.initializePersonalNotes();
			this.initializeLogList();
			this.initializeLogVisit();

			this.mTabHost.setCurrentTab(0);
		}
		else
		{
			final String strText = String.format("Unable to load cache %s!", strCacheCode);
			Toast.makeText(this, strText, Toast.LENGTH_LONG).show();
			this.finish();
		}
	}

	protected FieldNote getFieldNote()
	{
		final EditText text = (EditText) findViewById(R.id.CacheLogVisitText);
		final Spinner type = (Spinner) findViewById(R.id.CacheLogVisitType);
		final String logType = (String) type.getSelectedItem();
		final LogType log = FieldNote.getLogTypeFromString(logType);

		if (log != null)
		{
			final FieldNote note = new FieldNote();
			note.gcId = mCache.code;
			note.gcName = mCache.name;
			note.logText = text.getText().toString();
			note.logType = log;
			note.noteTime = new Date();

			return note;
		}
		else
		{
			return null;
		}
	}

	protected void initializeLogVisit()
	{
		Button b = (Button) findViewById(R.id.CacheLogVisit);
		final Context context = this;

		b.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				showUploadingDialog();

				Thread t = new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							final FieldNote note = getFieldNote();

							final SearchBCaching searchBCaching = new SearchBCaching(mPreferences);
							final String result = searchBCaching.sendFieldNote(note);

							if (result != null)
							{
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
										view.setText("Error Uploading Field Note!");
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
		});

		b = (Button) findViewById(R.id.CacheLogVisitSave);
		b.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				FieldNote note = getFieldNote();
				
				if (note != null)
				{
					mCacheDatabase.addFieldNote(note);
					Toast.makeText(context, "Field Note Saved To Database.", Toast.LENGTH_LONG).show();
				}
				else
				{
					final AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("Save Result").create();
					final TextView view = new TextView(alertDialog.getContext());
					view.setText("Error Saving Field Note!");
					alertDialog.setView(view);
					alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
					{
						public void onClick(DialogInterface dialog, int whichButton)
						{
						}
					});

					alertDialog.show();					
				}
			}
		});
	}

	private void showUploadingDialog()
	{
		progressDialog = ProgressDialog.show(this, "Uploading Field Note", "Please wait - this may take a while ...", true, false);
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
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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

	
	private void showSearchingDialog()
	{
		progressDialog = ProgressDialog.show(this, "Running Online Search", "Please wait - this may take a while ...", true, false);
	}	

	public void showNewDetailView(boolean isCacheSaved)
	{
		final Intent intent = new Intent(this, CacheDetailActivity.class);
		intent.putExtra("cachecode", mCache.code);
		intent.putExtra("isSearchResult", true);
		intent.putExtra("isCacheSaved", isCacheSaved);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * 
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// Clear the existing menu
		menu.clear();
		MenuInflater inflater = getMenuInflater();

		switch (this.mTabHost.getCurrentTab())
		{
		case 0:
			inflater.inflate(R.menu.cachedetail, menu);
			return super.onPrepareOptionsMenu(menu);
		case 1:
			inflater.inflate(R.menu.waypoints, menu);
			return super.onPrepareOptionsMenu(menu);
		default:
			return false;
		}
	}

	/**
     * 
     */
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.MenuShowHint:
			this.showHint();
			return true;
		case R.id.MenuShowSpoiler:
			this.showSpoilerImages();
			return true;
		case R.id.MenuTools:
			Intent intCommand = new Intent(this, CommandActivity.class);
			intCommand.putExtra("varid", this.mCache.code);
			this.startActivity(intCommand);
			return true;
		case R.id.MenuShowInventory:
			this.showInventory();
			return true;
		case R.id.MenuCacheDetailMap:
			final MapProvider mapProvder = this.mPreferences.getMapProvider();
			final Waypoint wpHeader = this.mCache.getHeaderWaypoint();
			MapViewer mapViewer;
			if (mapProvder == MapProvider.Google)
				mapViewer = new GoogleMapViewer(this);
			else
				mapViewer = new OsmMapViewer(this);
			mapViewer.addWaypoints(this.mCache.getWaypoints());
			mapViewer.setCenter(wpHeader.latitude, wpHeader.longitude, wpHeader.description);
			mapViewer.setZoomLevel(16);
			mapViewer.setUnitSystem(this.mPreferences.getUnitSystem());
			mapViewer.startActivity();
			return true;
		case R.id.MenuVariables:
			this.showVariables();
			return true;
		case R.id.MenuVotes:
			this.showVotes();
			return true;
		case R.id.MenuAddWaypoint:
			this.addWaypoint();
			return true;
		case R.id.MenuShowAttributes:
			this.showAttributes();
			return true;
		}
		return false;
	}

	/**
     * 
     */
	private void addWaypoint()
	{
		final Waypoint wpHeader = this.mCache.getHeaderWaypoint();
		final AddWaypointDialog dialog = new AddWaypointDialog(this, new OnWaypointAddedListener(), wpHeader);
		dialog.show();
	}

	/**
	 * 
	 * @param strCacheCode
	 */
	private void readCacheDescription()
	{
		// Load some settings
		final Coordinates mHomeCoordinates = this.mPreferences.getHomeCoordinates();
		final CoordinateFormat coordinateFormat = this.mPreferences.getCoordinateFormat();
		final boolean blnUseWebView = this.mPreferences.getUseWebView();

		String strExtraNameTag = "";
		final TextView tvName = (TextView) this.findViewById(R.id.CacheDetailName);

        // Set text properties according to cache status
        if (this.mCache.isArchived)
        {
        	tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        	tvName.setTextColor(Color.RED);
        	strExtraNameTag = ", archived";
        }
        else if (!this.mCache.isAvailable)
        {
        	tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        	tvName.setTextColor(Color.WHITE);
        	strExtraNameTag = ", disabled";
        }
        else
        {
        	tvName.setPaintFlags(tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        	tvName.setTextColor(Color.WHITE);
        }
		final String strName = String.format("%s (%s%s)", this.mCache.name, this.mCache.code, strExtraNameTag);
		tvName.setText(strName);
		Linkify.addLinks(tvName, Pattern.compile("[GO]C\\d\\w+"), "http://coord.info/");

		final String strCacheType = this.mCache.getCacheType().toString();
		try 
		{
			final Drawable drawable = Drawable.createFromStream(this.mResources.getAssets().open(strCacheType.toLowerCase() + ".gif"), strCacheType);
			drawable.setBounds(0, 0, 32, 32);
			tvName.setCompoundDrawables(drawable, null, null, null);
		} 
		catch (IOException e1) { }
		
		String strCountryState;
		if (this.mCache.state.length() > 0)
			strCountryState = String.format("%s/%s", this.mCache.state, this.mCache.country);
		else 
			strCountryState = String.format("%s", this.mCache.country);
		
		final Waypoint wpCache = this.mCache.getHeaderWaypoint();
		final Coordinates coordsHeaderWaypoint = new Coordinates(wpCache.latitude, wpCache.longitude);
		String strLinkCountryCoordsPlaced;
		if (wpCache.elevation != Integer.MIN_VALUE)
			strLinkCountryCoordsPlaced = String.format("%s\n%s %sm", strCountryState, coordsHeaderWaypoint.toString(coordinateFormat), wpCache.elevation);
		else
			strLinkCountryCoordsPlaced = String.format("%s\n%s", strCountryState, coordsHeaderWaypoint.toString(coordinateFormat));
		
		// final String strLinkCountryCoordsPlaced = String.format("%s\n%s", strCountryState, coordsHeaderWaypoint.toString(coordinateFormat));
		((TextView) this.findViewById(R.id.CacheDetailLinkCountryCoords)).setText(strLinkCountryCoordsPlaced);

		final DateFormat df = DateFormat.getDateInstance();
		String strPlacedAt = df.format(this.mCache.getHeaderWaypoint().time);
		
		final TextView tvPlacedBy = ((TextView) this.findViewById(R.id.CacheDetailPlacedBy));
		final GregorianCalendar cal = new GregorianCalendar(1990, 1, 1);
		if (this.mCache.getHeaderWaypoint().time.before(cal.getTime())) strPlacedAt = "Unknown";
		final String strPlacedBy = String.format("%s: %s (%s)", this.mResources.getString(R.string.cache_desc_placed_by), this.mCache.placedBy, strPlacedAt);
		tvPlacedBy.setText(strPlacedBy);
		if (this.mCache.ownerId.length() > 0)
		{
			String strPatternPlacedBy = this.mCache.placedBy;
			strPatternPlacedBy = strPatternPlacedBy.replace("+", "\\+");
			strPatternPlacedBy = strPatternPlacedBy.replace("[", "\\[");
			strPatternPlacedBy = strPatternPlacedBy.replace("]", "\\]");

			final Pattern patOwner = Pattern.compile(strPatternPlacedBy);
			String strOwnerUrl;
			try 
			{
				// Owner URL for numeric owner IDs (gpx file version 1.0 and 1.0.1)
				Integer.parseInt(this.mCache.ownerId);
				strOwnerUrl = "http://www.geocaching.com/profile/?id=" + this.mCache.ownerId + "&name=";
			} 
			catch (Exception ex)
			{
				// Owner URL for text owner IDs (gpx file version 1.02)				
				strOwnerUrl = "http://coord.info/" + this.mCache.ownerId + "?name=";
			}

			// final String strOwnerUrl = "http://www.geocaching.com/profile/?id=" + this.mCache.ownerId + "&name=";
			Linkify.addLinks(tvPlacedBy, patOwner, strOwnerUrl);
		}
		
		final String cacheSize = this.mCache.getContainerType().toString().replace("_", " ");
		((TextView) this.findViewById(R.id.CacheDetailContainer)).setText("(" + cacheSize + ")");
		try
		{
			final String strIconFilename = this.mCache.getContainerType().getIconFilename();
			final Drawable sizeIcon = Drawable.createFromStream(this.mResources.getAssets().open(strIconFilename), cacheSize);
			sizeIcon.setBounds(0, 0, 45, 12);
			((TextView) this.findViewById(R.id.CacheDetailContainerLabel)).setCompoundDrawables(null, null, sizeIcon, null);
		}
		catch (IOException e2) { }
		
    	final DecimalFormat decFormat = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
    	decFormat.applyPattern("0.0");

		final String strDifficulty = decFormat.format(this.mCache.difficulty).replace(".", "_");
		final String strTerrain = decFormat.format(this.mCache.terrain).replace(".", "_");

		try 
		{
			final Drawable draDifficulty = Drawable.createFromStream(this.mResources.getAssets().open("stars" + strDifficulty + ".png"), strDifficulty);
			draDifficulty.setBounds(0, 0, 65, 14);
			((TextView) this.findViewById(R.id.CacheDetailDifficultyLabel)).setCompoundDrawables(null, null, draDifficulty, null);
			final Drawable draTerrain = Drawable.createFromStream(this.mResources.getAssets().open("stars" + strTerrain + ".png"), strTerrain);
			draTerrain.setBounds(0, 0, 65, 14);
			((TextView) this.findViewById(R.id.CacheDetailTerrainLabel)).setCompoundDrawables(null, null, draTerrain, null);
		} 
		catch (IOException e1) 
		{ }

		final NavigationInfo ni = mHomeCoordinates.getNavigationInfoTo(coordsHeaderWaypoint, this.mPreferences.getUnitSystem());
		final String[] arrNavInfo = ni.toStringArray();
		final String strDistance = String.format("%s: %s (%s %s)", this.mResources.getString(R.string.cache_desc_distance_home), arrNavInfo[0], arrNavInfo[1], arrNavInfo[2]);
		((TextView) this.findViewById(R.id.CacheDetailDistance)).setText(strDistance);

		final LinearLayout llCacheDescription = (LinearLayout) this.findViewById(R.id.CacheDescriptionLinearLayout);

		if (mCache.isBcachingSummary)
		{
			final Button b = new Button(this);
			b.setText("Download Details");
			b.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					try
					{
						showSearchingDialog();
						
						new Thread(new Runnable()
						{
							
							public void run()
							{
								final String bcUsername = mPreferences.getBCachingUsername();
								final String bcPassword = mPreferences.getBCachingPassword();
								// final boolean bcTestSite = mPreferences.getUseBCachingTestSite();

								try
								{
									// final SearchBCaching search = new SearchBCaching(bcTestSite);
									final SearchBCaching search = new SearchBCaching();
									search.setLoginInfo(bcUsername, bcPassword);
									search.doSingleDetailQuery(mCache.code);
									dismissProgressDialog();
									showNewDetailView(false);
								}
								catch (Exception e)
								{
									e.printStackTrace();
									dismissProgressDialog();
									showBCachingErrorInUIThread();
								}
							}
						}).start();						
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
			
			llCacheDescription.addView(b);
		}

		final Bundle bunExtras = this.getIntent().getExtras();
		if (bunExtras.getBoolean("isSearchResult") && !mCache.isBcachingSummary && !isSaved)
		{
			final Button b = new Button(this);
			b.setText("Save To Database");
			b.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					final CacheDatabase cacheDatabase = CacheDatabase.getInstance();
					final CacheIndexItem ciiSearchDb = cacheDatabase.getSearchCacheIndexItem(mCache.code);

					cacheDatabase.addCache(mCache, ciiSearchDb);
					cacheDatabase.isUpdated.set(true);
					showNewDetailView(true);
				}
			});
			llCacheDescription.addView(b);
		}

		if (this.mCache.shortDescription.trim().length() > 0)
		{
			final TextView tvShortDescHeader = new TextView(this);
			tvShortDescHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			tvShortDescHeader.setBackgroundColor(0xFFc8c8c8);
			tvShortDescHeader.setTextColor(0xFF000000);
			tvShortDescHeader.setTextSize(10);
			tvShortDescHeader.setText(this.mResources.getString(R.string.cache_desc_short_description));
			tvShortDescHeader.setGravity(Gravity.CENTER_HORIZONTAL);
			llCacheDescription.addView(tvShortDescHeader);

			if ((this.mCache.shortDescriptionIsHtml) && (blnUseWebView))
			{
				WebView wvShortDescription = new WebView(this);
				wvShortDescription.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
				wvShortDescription.getSettings().setJavaScriptEnabled(true);
				HtmlCodeBuilder hcb = new HtmlCodeBuilder();
				hcb.setBody(this.mCache.shortDescription);
				wvShortDescription.loadDataWithBaseURL(null, hcb.getFullPage(), "text/html", "utf-8", "about:blank");
				llCacheDescription.addView(wvShortDescription);
			}
			else
			{
				TextView tvShortDescription = new TextView(this);
				tvShortDescription.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
				tvShortDescription.setTypeface(null, Typeface.ITALIC);
				tvShortDescription.setAutoLinkMask(Linkify.WEB_URLS);
				tvShortDescription.setText(this.mCache.shortDescription);
				llCacheDescription.addView(tvShortDescription);
			}
		}

		final TextView tvLongDescHeader = new TextView(this);
		tvLongDescHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		tvLongDescHeader.setBackgroundColor(0xFFc8c8c8);
		tvLongDescHeader.setTextColor(0xFF000000);
		tvLongDescHeader.setTextSize(10);
		tvLongDescHeader.setText(this.mResources.getString(R.string.cache_desc_long_description));
		tvLongDescHeader.setGravity(Gravity.CENTER_HORIZONTAL);
		llCacheDescription.addView(tvLongDescHeader);

		if ((this.mCache.longDescriptionIsHtml) && (blnUseWebView))
		{
			final WebView wvLongDescription = new WebView(this);
			wvLongDescription.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			HtmlCodeBuilder hcb = new HtmlCodeBuilder();
			hcb.setBody(this.mCache.longDescription);
			wvLongDescription.loadDataWithBaseURL(null, hcb.getFullPage(), "text/html", "utf-8", "about:blank");
			llCacheDescription.addView(wvLongDescription);
		}
		else
		{
			final TextView tvLongDescription = new TextView(this);
			tvLongDescription.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
			tvLongDescription.setAutoLinkMask(Linkify.WEB_URLS);
			tvLongDescription.setText(this.mCache.longDescription);
			llCacheDescription.addView(tvLongDescription);
		}
	}

	/**
	 * 
	 */
	private void showSpoilerImages()
	{
		final String strDataPath = this.mPreferences.getDataFolder();
		final String strSpoilerPath = String.format("%s%sspoiler", strDataPath, File.separator);
		final SpoilerImageFilter sif = new SpoilerImageFilter(strSpoilerPath, this.mCache.name, this.mCache.code);
		final ArrayList<String> alSpoilerImageFiles = sif.getFilenames();
		if (alSpoilerImageFiles.size() > 0)
		{
			final Intent intImageViewer = new Intent(this, ImageGridViewerActivity.class);
			intImageViewer.putExtra("filenames", alSpoilerImageFiles);
			startActivity(intImageViewer);
		}
		else
		{
			Toast.makeText(this, "No image found.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 
	 */
	private void showHint()
	{
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		// String strHintText = mCache.getHint().getPlainText().trim();
		final String strHintText = mCache.hint.trim();
		if (strHintText.length() > 0)
		{
			alertDialog.setTitle("Hint");
			alertDialog.setMessage(strHintText);
		}
		else
		{
			alertDialog.setTitle("Information");
			alertDialog.setMessage("Sorry, no hint available.");
		}
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				return;
			}
		});
		alertDialog.show();
	}

	/**
	 * 
	 */
	private void showInventory()
	{
		// String strInventory = "";
		final StringBuilder travelBugs = new StringBuilder();
		if (this.mCache.getTravelBugs() != null)
		{
			for (TravelBug tb : this.mCache.getTravelBugs())
			{
				travelBugs.append(tb.toString()).append("\n");
				// strInventory += tb.toString() + "\n";
			}
			// Remove trailing newline
			travelBugs.delete(travelBugs.length() - 1, travelBugs.length());
			// strInventory = strInventory.substring(0, strInventory.length() - 1);
		}
		else
		{
			travelBugs.append("Sorry, there are no coins / travelbugs in this cache.");
			// strInventory = "Sorry, there are no coins / travelbugs in this cache.";
		}

		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Inventory");
		// alertDialog.setMessage(strInventory);
		alertDialog.setMessage(travelBugs);
		alertDialog.setButton("OK", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				return;
			}
		});
		alertDialog.show();
	}

	/**
	 * 
	 * @param dblDiff
	 * @return
	 */
	/* private int getColorForDifficulty(Double dblDiff)
	{
		if (dblDiff <= 2)
			return 0xFF41A317; // lime green
		else if ((dblDiff > 2) && (dblDiff <= 3.5))
			return 0xFFFF8040; // orange
		else return 0xFFFF0000; // red
	} */

	/**
	 * 
	 */
	private void initializeWaypointList()
	{
		final CoordinateFormat coordinateFormat = this.mPreferences.getCoordinateFormat();

		int intExtractedNum = 1;
		// Extract waypoints from description & logs
		final Text txtShortDescription = new Text();
		txtShortDescription.setPlainText(this.mCache.shortDescription);
		txtShortDescription.setIsHtml(this.mCache.shortDescriptionIsHtml);
		final Text txtLongDescription = new Text();
		txtLongDescription.setPlainText(this.mCache.longDescription);
		txtLongDescription.setIsHtml(this.mCache.longDescriptionIsHtml);

		final ArrayList<Coordinates> alExtractedCoordinates = txtShortDescription.extractCoordinates();
		alExtractedCoordinates.addAll(txtLongDescription.extractCoordinates());
		if (alExtractedCoordinates.size() > 0)
		{
			for (Coordinates coords : alExtractedCoordinates)
			{
				final Waypoint wp = new Waypoint();
				final String strName = String.format("Description #%d", intExtractedNum);
				wp.name = strName;
				wp.description = strName;
				wp.setType(WaypointType.Extracted);
				wp.symbol = "Extracted";
				// wp.setCoordinates(coords);
				wp.latitude = coords.getLatitude().getD();
				wp.longitude = coords.getLongitude().getD();
				this.mCache.addWaypoint(wp);
				intExtractedNum++;
			}
		}

		if (this.mCache.getLogEntries() != null)
		{
			for (LogEntry logEntry : this.mCache.getLogEntries())
			{
				if ((logEntry.latitude != 0) && (logEntry.longitude != 0))
				{
					final Waypoint wp = new Waypoint();
					final String strName = String.format("Log entry (%s)", logEntry.finder);
					wp.name = strName;
					wp.description = strName;
					wp.setType(WaypointType.Extracted);
					wp.symbol = "Extracted";
					wp.time = logEntry.time;
					wp.latitude = logEntry.latitude;
					wp.longitude = logEntry.longitude;
					this.mCache.addWaypoint(wp);
				}
				final Text textLogEntry = new Text(logEntry.text);
				for (Coordinates coords : textLogEntry.extractCoordinates())
				{
					final Waypoint wp = new Waypoint();
					final String strName = String.format("Log entry (%s)", logEntry.finder);
					wp.name = strName;
					wp.description = strName;
					wp.setType(WaypointType.Extracted);
					wp.symbol = "Extracted";
					wp.latitude = coords.getLatitude().getD();
					wp.longitude = coords.getLongitude().getD();
					this.mCache.addWaypoint(wp);
				}
			}
		}
		final ListView lvWaypointList = (ListView) this.findViewById(R.id.WaypointList);
		lvWaypointList.setAdapter(new WaypointListAdapter(this, this.mCache.getWaypoints(), this.mCache.code, this.mCache.getCacheType(), coordinateFormat));

		// This will make the listView create a ContextMenu when you long press it.
		lvWaypointList.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
		{
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
			{
				menu.setHeaderTitle("Waypoint Options");
				menu.add(0, MENU_INFO, Menu.NONE, "Information");
				menu.add(0, MENU_COMPASS, Menu.NONE, "Compass");
				// menu.add(0, MENU_RADAR, Menu.NONE, "Radar View");
				menu.add(0, MENU_MAP, Menu.NONE, "Map View");
				menu.add(0, MENU_NAVIGATE, Menu.NONE, "Navigate");
				menu.add(0, MENU_GEO, Menu.NONE, "Geo");
				menu.add(0, MENU_DELETE, Menu.NONE, "Delete");
			}
		});

		// Show radar view when selecting a waypoint
		lvWaypointList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				final Waypoint wp = mCache.getWaypoints().get(position);
				switch (mPreferences.getWaypointClickAction())
				{
				case InternalMap:
					showWaypointOnOsm(wp);
					break;
				case Compass:
					showWaypointOnCompass(wp);
					break;
				case Navigation:
					navigateToWaypoint(wp);
					break;
				}
			}
		});
	}

	/**
	 * 
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		super.onContextItemSelected(item);

		final AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		final Waypoint wp = this.mCache.getWaypoints().get(menuInfo.position);

		switch (item.getItemId())
		{
		case MENU_MAP:
			final MapProvider mapProvider = this.mPreferences.getMapProvider();
			MapViewer mapViewer;
			if (mapProvider == MapProvider.Google)
				mapViewer = new GoogleMapViewer(this);
			else
				mapViewer = new OsmMapViewer(this);
			mapViewer.setCenter(wp.latitude, wp.longitude, wp.description);
			mapViewer.addWaypoint(wp);
			mapViewer.setZoomLevel(17);
			mapViewer.startActivity();
			return true;
		case MENU_COMPASS:
			this.showWaypointOnCompass(wp);
			return true;
		case MENU_GEO:
			this.showWaypointOnGeo(wp);
			break;
		case MENU_NAVIGATE:
			this.navigateToWaypoint(wp);
			return true;
		case MENU_INFO:
			final WaypointDetailDialog dialog = new WaypointDetailDialog(this, wp, this.mCache.getWaypoints(), this.mPreferences.getUnitSystem());
			dialog.show();
			return true;
		case MENU_DELETE:
			if (wp.getType().equals(WaypointType.UserDefined))
				this.deleteWaypoint(menuInfo.position);
			else
				Toast.makeText(this, R.string.delete_udef_only, Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param wp
	 */
	private void deleteWaypoint(int position)
	{
		final Waypoint wp = this.mCache.getWaypoints().get(position);

		new AlertDialog.Builder(this).setTitle("Question").setMessage(String.format("Do you really want to delete the waypoint '%s'?", wp.name)).setIcon(
				android.R.drawable.ic_dialog_alert).setPositiveButton("Yes", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				mCache.getWaypoints().remove(wp);
				CacheDatabase db = CacheDatabase.getInstance();
				db.addCache(mCache);
				CacheDetailActivity.this.initializeWaypointList();
			}
		}).setNegativeButton("No", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
			}
		}).show();
	}

	/**
	 * 
	 * @param waypoint
	 */
	private void showWaypointOnRadar(Waypoint waypoint)
	{
		if (AndroidSystem.isIntentAvailable(this, "com.google.android.radar.SHOW_RADAR"))
		{
			final Intent intRadar = new Intent("com.google.android.radar.SHOW_RADAR");
			intRadar.putExtra("latitude", (float) waypoint.latitude);
			intRadar.putExtra("longitude", (float) waypoint.longitude);
			startActivity(intRadar);
		}
		else
		{
			Toast.makeText(this, "Please install 'Radar' by Mike Cleron first.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 
	 * @param waypoint
	 */
	private void showWaypointOnCompass(final Waypoint waypoint)
	{
		final CompassProvider compassProvider = mPreferences.getCompassProvider();
		if (compassProvider.equals(CompassProvider.CompassNavi))
			showWaypointOnCompassNavi(waypoint);
		else
			showWaypointOnRadar(waypoint);	
	}
	
	private void showWaypointOnGeo(final Waypoint waypoint)
	{
		final String destinationLatitude = ((Double) waypoint.latitude).toString().replace(",", ".");
		final String destinationLongitude = ((Double) waypoint.longitude).toString().replace(",", ".");
		
		final String actionView = "geo:" + destinationLatitude + "," + destinationLongitude;
        final Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionView));
		startActivity(geoIntent);

	}
	
	/**
	 * 
	 * @param waypoint
	 */
	private void navigateToWaypoint(final Waypoint waypoint)
	{
		final NavigationProvider navigationProvider = this.mPreferences.getNavigationProvider();
		if (navigationProvider.equals(NavigationProvider.Google))
			this.navigateToWaypointWithGoogleMaps(waypoint);
		// else
		//	this.navigateToWaypointWithAndNav2(waypoint);

	}
	
	/**
	 * 
	 * @param waypoint
	 */
	private void showWaypointOnOsm(final Waypoint waypoint)
	{
		final OsmMapViewer mapViewer = new OsmMapViewer(this);
		mapViewer.addWaypoint(waypoint);
		mapViewer.setTarget(waypoint.latitude, waypoint.longitude, waypoint.getSnippet());
		mapViewer.setCenter(waypoint.latitude, waypoint.longitude, waypoint.getSnippet());
		mapViewer.setUnitSystem(this.mPreferences.getUnitSystem());		
		mapViewer.startActivity();
	}
	
	/**
	 * 
	 * @param waypoint
	 */
	private void showWaypointOnCompassNavi(Waypoint waypoint)
	{
		if (AndroidSystem.isIntentAvailable(this, "mpr.compassNavi.SHOW_NAVI"))
		{
			final Intent intCompassNavi = new Intent("mpr.compassNavi.SHOW_NAVI");
			intCompassNavi.putExtra("latitude", waypoint.latitude);
			intCompassNavi.putExtra("longitude", waypoint.longitude);
			intCompassNavi.putExtra("name", waypoint.getSnippet());
			startActivity(intCompassNavi);
		}
		else
		{
			Toast.makeText(this, "Please install 'CompassNavi' first.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 
	 * @param waypoint
	 */
	/* private void navigateToWaypointWithAndNav2(Waypoint waypoint)
	{
		final Intent navTo = new Intent(ANDNAV2_NAV_ACTION);

		// Create a bundle that will transfer the routing-information
		final Bundle bundle = new Bundle();

		// Add navigation target
		String strLatitude = ((Double) waypoint.latitude).toString().replace(",", ".");
		String strLongitude = ((Double) waypoint.longitude).toString().replace(",", ".");
		String strTarget = String.format("%s,%s", strLatitude, strLongitude);
		bundle.putString("to", strTarget); // "Latitude,Longitude"

		// Add as many waypoints as you want here
		// final ArrayList<String> vias = new ArrayList<String>();
		// vias.add("50.119539,8.658031"); // "Latitude,Longitude"
		// b.putStringArrayList("via", vias);

		navTo.putExtras(bundle);
		sendBroadcast(navTo);
	} */

	/**
	 * 
	 * @param waypoint
	 */
	private void navigateToWaypointWithGoogleMaps(Waypoint waypoint)
	{
		final String destinationLatitude = ((Double) waypoint.latitude).toString().replace(",", ".");
		final String destinationLongitude = ((Double) waypoint.longitude).toString().replace(",", ".");
		
		final String vehicle = getResources().getString(R.string.vehicle);
		final String pedestrian = getResources().getString(R.string.pedestrian);
		
		final CharSequence[] items = { vehicle, pedestrian };

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.navigation_mode);
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
		    public void onClick(DialogInterface dialog, int item) 
		    {
		    	String actionView = "google.navigation:ll=" + destinationLatitude + "," + destinationLongitude;
		    	if (item == 1)
		    	{
		    		actionView = actionView + "&mode=w";
		    	}
		    	
		    	dialog.dismiss();
		    	
		        final Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(actionView));
				startActivity(navIntent);
		    }
		});
		
		final AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * 
	 */
	private void showVariables()
	{
		final UserDefinedVariables udv = CacheDatabase.getInstance().getVariables(this.mCache.code);
		if (udv.size() == 0)
		{
			Toast.makeText(this, "No variables defined.", Toast.LENGTH_LONG).show();
		}
		else
		{
			final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("Variables");
			final LayoutInflater inflater = this.getLayoutInflater();
			final View layout = inflater.inflate(R.layout.variables, null);

			dialog.setView(layout);
			final TextView tvVariablesText = (TextView) layout.findViewById(R.id.VariablesTextBox);
			tvVariablesText.setText(udv.toString());
			
			dialog.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	/**
	 * 
	 */
	private void showAttributes()
	{
		if (this.mCache.getAttributes() == null)
		{
			Toast.makeText(this, "No attributes available.", Toast.LENGTH_LONG).show();
		}
		else
		{
			final CacheAttributeDialog dialog = new CacheAttributeDialog(this, this.mCache.getAttributes());
			dialog.show();
		}
	}

	/**
	 * 
	 */
	private void showVotes()
	{
		GCVote votes = this.mCacheDatabase.getVote(this.mCache.code);
		if (votes == null)
		{
			// Update votes if network connection is available
			final ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm.getActiveNetworkInfo() != null)
			{
				final GCVoteReader gcVoteReader = new GCVoteReader();
				final HashMap<String, GCVote> gcVotes = gcVoteReader.getVotes(this.mCache.code);
				// Add votes to database
				this.mCacheDatabase.addCacheVotes(gcVotes);

				// Re-read votes from database
				votes = this.mCacheDatabase.getVote(this.mCache.code);
			}
		}

		if (votes == null)
		{
			Toast.makeText(this, "No votes found.", Toast.LENGTH_LONG).show();
		}
		else
		{
			final GCVoteDialog dialog = new GCVoteDialog(this, votes, this.mCache.code);
			dialog.show();
		}
	}

	/**
	 * 
	 */
	private void initializeLogList()
	{
		final HashMap<String, Integer> hmLogStats = new HashMap<String, Integer>();
		for (LogEntry logEntry : this.mCache.getLogEntries())
		{
			final String strLogType = logEntry.getType().toString();
			if (!hmLogStats.containsKey(strLogType)) hmLogStats.put(strLogType, 0);
			hmLogStats.put(strLogType, hmLogStats.get(strLogType) + 1);
		}

		final ListView lvLogList = (ListView) this.findViewById(R.id.LogList);
		lvLogList.setAdapter(new LogListAdapter(this, this.mCache.getLogEntries()));

		final TextView tvLogStats = (TextView) this.findViewById(R.id.CacheLogStats);
		final StringBuilder logStats = new StringBuilder();
		// String strLogStats = "";
		
		for (Map.Entry<String, Integer> entry : hmLogStats.entrySet())
		// for (String strLogType : hmLogStats.keySet())
		{
			logStats.append(String.format("%s: %d, ", entry.getKey().replace("_", " "), entry.getValue()));
			// logStats.append(String.format("%s: %d, ", strLogType.replace("_", " "), hmLogStats.get(strLogType)));
			// strLogStats += String.format("%s: %d, ", strLogType.replace("_", " "), hmLogStats.get(strLogType));
		}
		// if (strLogStats.length() > 0) strLogStats = strLogStats.substring(0, strLogStats.length() - 2);
		// tvLogStats.setText(strLogStats);
		if (logStats.length() > 0) logStats.delete(logStats.length() - 2, logStats.length());
		tvLogStats.setText(logStats);
	}

	/**
	 * 
	 */
	private void initializePersonalNotes()
	{
		final Context context = this;

		final EditText personalNoteText = (EditText) this.findViewById(R.id.PersonalNoteText);
		final PersonalNote note = this.mCacheDatabase.getPersonalNote(this.mCache.code);
		personalNoteText.setText(note.text);

		personalNoteText.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			public void onFocusChange(View v, boolean hasFocus) 
			{
				final String noteText = personalNoteText.getText().toString();
				if (!noteText.equals(note.text))
				{
					note.text = noteText;
					mCacheDatabase.storePersonalNote(note, true);
					Toast.makeText(context, mResources.getString(R.string.personal_notes_saved), Toast.LENGTH_LONG).show();
				}
			}
		});		
	}

	/**
	 * 
	 * @author Martin Preishuber
	 * 
	 */
	private class OnWaypointAddedListener implements org.opengpx.AddWaypointDialog.ReadyListener
	{
		public void ready(Waypoint wp)
		{
			final boolean added = CacheDetailActivity.this.mCache.addWaypoint(wp, true);
			if (!added)
			{
				final Toast failed = Toast.makeText(CacheDetailActivity.this.getApplicationContext(), R.string.wp_already_exists, Toast.LENGTH_LONG);
				failed.show();
			}
			else
			{
				final CacheDatabase db = CacheDatabase.getInstance();
				db.addCache(CacheDetailActivity.this.mCache);
				CacheDetailActivity.this.initializeWaypointList();
			}
		}
	}
}
