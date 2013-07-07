package org.opengpx.lib.map;

import java.util.ArrayList;

import org.opengpx.R;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import org.opengpx.OsmPreferenceActivity;
import org.opengpx.Preferences;
import org.opengpx.lib.ResourceHelper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class OsmMapViewerActivity extends Activity 
{

	private static final int DEFAULT_ZOOM_LEVEL = 16;
	private static final int TILE_SIZE_PIXELS = 256;

	private MapView mOsmv;
	private MapController mOsmvController;
	private MyLocationOverlay mMyLocationOverlay = null;
	private MinimapOverlay mMiniMap = null;
	private ScaleBarOverlay mScaleBarOverlay;

	private int mintZoomLevel = DEFAULT_ZOOM_LEVEL;
	private ITileSource mTileSource;
	private Boolean mUseMetric;
	private GeoPoint mGeoPointCenter;
	private GeoPoint mGeoPointTarget = null;
	private ArrayList<MapOverlayItem> mMapOverlayItems;

	private SharedPreferences mSharedPreferences;
	private Preferences mPreferences;

	// Preferences names and default values	
	private static final String PREFS_KEY_OSM_RENDERER = "osmRenderer";

	// Some default values
	private static final String PREFS_DEFAULT_OSM_RENDERER = TileSourceFactory.MAPNIK.name();
	
	// private static final Logger mLogger = LoggerFactory.getLogger(ResourceHelper.class);

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        // Load preferences
        this.mPreferences = new Preferences(this);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        final String strRenderer = this.mSharedPreferences.getString(PREFS_KEY_OSM_RENDERER, PREFS_DEFAULT_OSM_RENDERER);
		this.mTileSource = this.getTileSourceByName(strRenderer);

		// Read bundle extras
        final Bundle bunExtras = this.getIntent().getExtras();

        String title;
        if (bunExtras.getString("title") != null)
        	title = "OpenGPX - " + bunExtras.getString("title");
        else
        	title = "OpenGPX - Map Viewer";
        setTitle (title);

        
        if (bunExtras.getInt("zoom_level") != 0)
        	this.mintZoomLevel = bunExtras.getInt("zoom_level");

        final MapOverlayItem mapOverlayItemCenter = (MapOverlayItem) bunExtras.get("map_center");
        this.mGeoPointCenter = new GeoPoint(mapOverlayItemCenter.getLatitudeE6(), mapOverlayItemCenter.getLongitudeE6());
        
        this.mMapOverlayItems = (ArrayList<MapOverlayItem>) bunExtras.get("map_items");

        this.mUseMetric = bunExtras.getBoolean("metric");
        
        if (bunExtras.get("target") != null)
        {
            final MapOverlayItem mapOverlayItemTarget = (MapOverlayItem) bunExtras.get("target");
        	this.mGeoPointTarget = new GeoPoint(mapOverlayItemTarget.getLatitudeE6(), mapOverlayItemTarget.getLongitudeE6());
        }

        this.initializeUI();
	}

	/**
	 * 
	 */
	/* @Override
	public void onConfigurationChanged(Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);

	    this.initializeUI();
	} */
	
	/**
	 * 
	 */
	private void initializeUI()
	{
        final RelativeLayout rl = new RelativeLayout(this);        

        this.mOsmv = new MapView(this, TILE_SIZE_PIXELS);
        
        this.mOsmv.setUseSafeCanvas(true);
        this.setHardwareAccelerationOff();
        
        this.mOsmvController = this.mOsmv.getController();
        this.mOsmv.setTileSource(this.mTileSource);
        rl.addView(this.mOsmv, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        // Set zoom level
        this.mOsmvController.setZoom(this.mintZoomLevel);
        // Enable multi touch
        this.mOsmv.setMultiTouchControls(true);

        // Set map center
        this.mOsmvController.setCenter(this.mGeoPointCenter);
        // this.mOsmvController.animateTo(gpCenter);

        // Add overlay items (caches and waypoints)
        this.addOverlayItems();
        
        int scaleBarTop = 10;
        if (this.mGeoPointTarget  != null)
        {
        	// Lock screen to Portrait in Line Navigation Module
    		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    		final OsmLineNavigationOverlay lno = new OsmLineNavigationOverlay(this, this.mOsmv, this.mGeoPointTarget);
    		this.mMyLocationOverlay = lno;
    		scaleBarTop += lno.getNavbarHeight();
        }
        else
        {
    		this.mMyLocationOverlay = new MyLocationOverlay(this, this.mOsmv);
        }

		this.addScaleBarOverlay(scaleBarTop, this.mUseMetric);
		this.mOsmv.getOverlays().add(this.mMyLocationOverlay);
		
		this.mMyLocationOverlay.runOnFirstFix(new Runnable() 
		{
            public void run() 
            {
            	mOsmvController.animateTo(mMyLocationOverlay.getMyLocation());
            }
        });

        // Add Zoom controls and minimap
        final boolean blnUseMinimap = this.mPreferences.getOsmUseMinimap();
        if (blnUseMinimap)
        {
            this.addZoomControls(rl, RelativeLayout.ALIGN_PARENT_LEFT);
            this.addMinimap(rl);
        }
        else
        {
            this.addZoomControls(rl, RelativeLayout.CENTER_IN_PARENT);
        }
		
		this.setContentView(rl);
	}
		
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setHardwareAccelerationOff()
    {
        // Turn off hardware acceleration here, or in manifest
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        	this.mOsmv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
	/**
	 * 
	 * @param name
	 * @return
	 */
	private ITileSource getTileSourceByName(String name)
	{
		ITileSource osmri;
		try 
		{
			osmri = TileSourceFactory.getTileSource(name);
		} 
		catch (IllegalArgumentException iae)
		{
			osmri = TileSourceFactory.getTileSources().get(0);
		}
		return osmri;
	}
	
	/**
	 * 
	 */
	@Override
	public void onResume()
	{
		super.onResume();

		this.mMyLocationOverlay.enableCompass();
		this.mMyLocationOverlay.enableMyLocation();		
		this.mMyLocationOverlay.enableFollowLocation();

		final String strCurrentRenderer = this.mOsmv.getTileProvider().getTileSource().name();
		final String strRendererPrefs = this.mSharedPreferences.getString(PREFS_KEY_OSM_RENDERER, PREFS_DEFAULT_OSM_RENDERER);
		if (!strCurrentRenderer.equals(strRendererPrefs))
		{
			final ITileSource tileSoure = this.getTileSourceByName(strRendererPrefs);
			this.mOsmv.setTileSource(tileSoure);
		
			if (this.mMiniMap != null)
			{
				this.mMiniMap.setTileSource(tileSoure);
			}
			
			Toast.makeText(this, "Map Renderer changed to: " + tileSoure.name(), Toast.LENGTH_SHORT).show();
		}

		// Set keep screen on property
		final boolean blnKeepScreenOn = this.mPreferences.getMapKeepScreenOn();
		this.mOsmv.setKeepScreenOn(blnKeepScreenOn);
		
		// Log.d("OsmMapViewerActivity", Integer.toString(this.getResources().getConfiguration().orientation));
	}
	
	/**
	 * 
	 */
	@Override
	public void onPause()
	{
		super.onPause();

		this.mMyLocationOverlay.disableMyLocation();
		this.mMyLocationOverlay.disableCompass();

		this.mOsmv.setKeepScreenOn(false);				
	}

	/**
	 * 
	 * @param rl
	 */
	private void addZoomControls(RelativeLayout rl, int alignment)
	{
		final OsmZoomControls zoomControls = new OsmZoomControls(this);

        final RelativeLayout.LayoutParams zoomControlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        zoomControlParams.addRule(alignment);
        zoomControlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        
        zoomControls.setOnZoomInClickListener(new OnClickListener()
        {
			public void onClick(View v) 
			{
				mOsmvController.zoomIn();
			}
        });

        zoomControls.setOnZoomOutClickListener(new OnClickListener()
        {
			public void onClick(View v) 
			{
				mOsmvController.zoomOut();
			}
        });
        
        rl.addView(zoomControls, zoomControlParams);
	}
	
	/**
	 * 
	 * @param rl
	 */
	private void addMinimap(RelativeLayout rl)
	{
		this.mMiniMap = new MinimapOverlay(this, this.mOsmv.getTileRequestCompleteHandler());
		this.mMiniMap.setTileSource(this.mOsmv.getTileProvider().getTileSource());
		this.mOsmv.getOverlays().add(this.mMiniMap);		
	}

	/**
	 * 
	 * @param top
	 */
	private void addScaleBarOverlay(int top, boolean metric)
    {
    	this.mScaleBarOverlay = new ScaleBarOverlay(this);
    	this.mOsmv.getOverlays().add(mScaleBarOverlay);
    	// Scale bar tries to draw as 1-inch, so to put it in the top center, set x offset to half screen width, minus half an inch.
    	this.mScaleBarOverlay.setScaleBarOffset(mScaleBarOverlay.screenWidth / 2.0f - mScaleBarOverlay.xdpi / 2.0f, top);
    	if (metric)
    		this.mScaleBarOverlay.setMetric();
    	else
    		this.mScaleBarOverlay.setImperial();
    }

	/**
	 * 
	 * @param alMapOverlayItems
	 */
	private void addOverlayItems()
	{
		final ResourceHelper resourceHelper = new ResourceHelper(this);
		final ResourceProxy resourceProxy = new DefaultResourceProxyImpl(this);
		
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		for (MapOverlayItem mapOverlayItem : this.mMapOverlayItems)
		{
			final GeoPoint geoPoint = new GeoPoint(mapOverlayItem.getLatitudeE6(), mapOverlayItem.getLongitudeE6());
			final Drawable drawable = mapOverlayItem.getDrawable(resourceHelper, false);
			// mLogger.debug("drawable size: width=" + drawable.getIntrinsicWidth() + " height=" + drawable.getIntrinsicHeight());
			final String strSnippet = mapOverlayItem.getSnippet();

			final OverlayItem overlayItem = new OverlayItem(strSnippet, strSnippet, geoPoint);
			overlayItem.setMarker(drawable);
			items.add(overlayItem);
		}		
		
		final ItemizedOverlay<OverlayItem> mOverlayItem =
			new ItemizedIconOverlay<OverlayItem>(items,
					new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() 
					{
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) 
                        {
                        	Toast.makeText( OsmMapViewerActivity.this, item.getSnippet(), Toast.LENGTH_LONG).show();
                            return true; // We 'handled' this event.
                        }

						public boolean onItemLongPress(final int index, final OverlayItem item) 
						{
							return false;
						}
					},
					resourceProxy);

        this.mOsmv.getOverlays().add(mOverlayItem);
	}
	
    /**
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.osm_preferences, menu);
        return true;
    }

    /**
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
    	switch (item.getItemId()) 
    	{
    		case R.id.OsmPreferences:
    			final Intent prefsActivity = new Intent(getBaseContext(), OsmPreferenceActivity.class);
    			startActivity(prefsActivity);
    			return true;
    	}
    	return false;
    }
}
