package org.opengpx.lib.map;

import java.util.ArrayList;

import org.opengpx.MapOverlayItem;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

	private MapView mOsmv; //, mOsmvMinimap;
	private MapController mOsmvController;
	private MyLocationOverlay mMyLocationOverlay = null;
	private ScaleBarOverlay mScaleBarOverlay;

	private int mintZoomLevel = DEFAULT_ZOOM_LEVEL;

	private SharedPreferences mSharedPreferences;
	private Preferences mPreferences;

	// Preferences names and default values	
	private static final String PREFS_KEY_OSM_RENDERER = "osmRenderer";

	// Some default values
	private static final String PREFS_DEFAULT_OSM_RENDERER = TileSourceFactory.MAPNIK.name();
	
	private static final Logger mLogger = LoggerFactory.getLogger(ResourceHelper.class);

	/**
	 * 
	 */
	public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        
        final RelativeLayout rl = new RelativeLayout(this);        
        final Bundle bunExtras = this.getIntent().getExtras();
        
        // Load preferences
        this.mPreferences = new Preferences(this);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final String strRenderer = this.mSharedPreferences.getString(PREFS_KEY_OSM_RENDERER, PREFS_DEFAULT_OSM_RENDERER);

        this.mOsmv = new MapView(this, TILE_SIZE_PIXELS);
        this.mOsmvController = this.mOsmv.getController();
		final ITileSource tileSource = this.getTileSourceByName(strRenderer);
        this.mOsmv.setTileSource(tileSource);
        rl.addView(this.mOsmv, new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        if (bunExtras.getString("title") != null)
        	setTitle ("OpenGPX - " + bunExtras.getString("title"));
        else
        	setTitle ("OpenGPX - Map Viewer");
        if (bunExtras.getInt("zoom_level") != 0)
        	this.mintZoomLevel = bunExtras.getInt("zoom_level");

        // Set zoom level
        this.mOsmvController.setZoom(this.mintZoomLevel);
        // Enable multi touch
        this.mOsmv.setMultiTouchControls(true);

        final MapOverlayItem mapOverlayItemCenter = (MapOverlayItem) bunExtras.get("map_center");
        @SuppressWarnings("unchecked")
        final ArrayList<MapOverlayItem> alMapOverlayItems = (ArrayList<MapOverlayItem>) bunExtras.get("map_items");
        MapOverlayItem mapOverlayItemTarget = null;

        // Set map center
        final GeoPoint gpCenter = new GeoPoint(mapOverlayItemCenter.getLatitudeE6(), mapOverlayItemCenter.getLongitudeE6());
        this.mOsmvController.setCenter(gpCenter);
        // this.mOsmvController.animateTo(gpCenter);
                
        // Add overlay items (caches and waypoints)
        this.addOverlayItems(alMapOverlayItems);
        
        int scaleBarTop = 10;
        if (bunExtras.get("target") != null)
        {
        	mapOverlayItemTarget = (MapOverlayItem) bunExtras.get("target");
        	final GeoPoint gpTarget = new GeoPoint(mapOverlayItemTarget.getLatitudeE6(), mapOverlayItemTarget.getLongitudeE6());
    		final OsmLineNavigationOverlay lno = new OsmLineNavigationOverlay(this, this.mOsmv, gpTarget);
    		this.mMyLocationOverlay = lno;
    		scaleBarTop += lno.getNavbarHeight();
        }
        else
        {
    		this.mMyLocationOverlay = new MyLocationOverlay(this, this.mOsmv);
        }

		this.addScaleBarOverlay(scaleBarTop, bunExtras.getBoolean("metric"));

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

	/**
	 * 
	 * @param name
	 * @return
	 */
	private ITileSource getTileSourceByName(String name)
	{
		ITileSource osmri;
		try {
			osmri = TileSourceFactory.getTileSource(name);
		} catch (IllegalArgumentException iae)
		{
			osmri = TileSourceFactory.getTileSources().get(0);
			// Toast.makeText(this, "Using default renderer: " + osmri.name(), Toast.LENGTH_SHORT).show();
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
		final MinimapOverlay minimap = new MinimapOverlay(this, this.mOsmv.getTileRequestCompleteHandler());
		minimap.setTileSource(this.mOsmv.getTileProvider().getTileSource());
		this.mOsmv.getOverlays().add(minimap);		
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
	private void addOverlayItems(ArrayList<MapOverlayItem> alMapOverlayItems)
	{
		final ResourceHelper resourceHelper = new ResourceHelper(this);
		final ResourceProxy resourceProxy = new DefaultResourceProxyImpl(this);
		
		final ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
		for (MapOverlayItem mapOverlayItem : alMapOverlayItems)
		{
			final GeoPoint geoPoint = new GeoPoint(mapOverlayItem.getLatitudeE6(), mapOverlayItem.getLongitudeE6());
			final Drawable drawable = mapOverlayItem.getDrawable(resourceHelper, false);
			mLogger.debug("drawable size: width=" + drawable.getIntrinsicWidth() + " height=" + drawable.getIntrinsicHeight());
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
        MenuInflater inflater = getMenuInflater();
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
    			Intent prefsActivity = new Intent(getBaseContext(), OsmPreferenceActivity.class);
    			startActivity(prefsActivity);
    			return true;
    	}
    	return false;
    }
}
