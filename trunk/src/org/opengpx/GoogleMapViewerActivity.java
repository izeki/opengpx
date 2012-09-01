package org.opengpx;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import org.opengpx.Preferences;
import org.opengpx.lib.ResourceHelper;
import org.opengpx.lib.map.MapOverlayItem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class GoogleMapViewerActivity extends MapActivity 
{
	private MapView mapView;
	private MyLocationOverlay mMyLocationOverlay;
	private List<Overlay> mapOverlays;
	private Drawable mdrawRedPin;
	private GoogleMapItemizedOverlay mGoogleMapsItemizedOverlay;
	private Preferences mPreferences;
	private ProgressDialog mProgressDialog;

	private static final Logger mLogger = LoggerFactory.getLogger(GoogleMapViewerActivity.class);

	private int mInitialZoomLevel = DEFAULT_ZOOM_LEVEL;
	private GeoPoint mMapCenter;
	
	private boolean mblnSatelliteView = false;
	private boolean mblnStreetView = false;
	private boolean mblnTrafficView = true;
	
	private static final int DEFAULT_ZOOM_LEVEL = 17;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.googlemapviewer);

        final Bundle bunExtras = this.getIntent().getExtras();
        this.mPreferences = new Preferences(this);
        
        @SuppressWarnings("unchecked")
        final ArrayList<MapOverlayItem> alMapOverlayItems = (ArrayList<MapOverlayItem>) bunExtras.get("map_items");
        final MapOverlayItem mapOverlayItemCenter = (MapOverlayItem) bunExtras.get("map_center");

        if (bunExtras.getString("title") != null)
        	setTitle ("OpenGPX - " + bunExtras.getString("title"));
        else
        	setTitle ("OpenGPX - Map Viewer");

		this.mapView = (MapView) this.findViewById(R.id.GoogleMapView);
		this.mapView.setBuiltInZoomControls(true);
		this.mapView.setClickable(true);
		this.mapView.setEnabled(true);
		
		// Set map view state
		this.mblnSatelliteView = this.mapView.isSatellite();
		this.mblnStreetView = this.mapView.isStreetView();
		this.mblnTrafficView = this.mapView.isTraffic();
		
		this.mapOverlays = mapView.getOverlays();
		this.mdrawRedPin = this.getResources().getDrawable(R.drawable.redpin);
		this.mGoogleMapsItemizedOverlay = new GoogleMapItemizedOverlay(this, mdrawRedPin);

        if (bunExtras.getInt("zoom_level") != 0) this.mInitialZoomLevel = bunExtras.getInt("zoom_level");
        this.mMapCenter = new GeoPoint(mapOverlayItemCenter.getLatitudeE6(), mapOverlayItemCenter.getLongitudeE6());
		
		this.addMyLocationOverlay();

		this.addMapOverlayItems(alMapOverlayItems);				
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
		
		// Set keep screen on property
		final boolean blnKeepScreenOn = this.mPreferences.getMapKeepScreenOn();
		this.mapView.setKeepScreenOn(blnKeepScreenOn);
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
		
		this.mapView.setKeepScreenOn(false);						
	}

	/**
	 * 
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}

    /**
     * 
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.googlemap, menu);
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
    		case R.id.GoogleMapToggleSatellite:
    			this.mblnSatelliteView = !this.mblnSatelliteView;
    			this.mapView.setSatellite(this.mblnSatelliteView);
    			return true;
    		case R.id.GoogleMapToggleStreet:
    			this.mblnStreetView = !this.mblnStreetView;
    			if (this.mblnStreetView)
    				this.mblnTrafficView = false;
    			this.mapView.setStreetView(this.mblnStreetView);
    			return true;
    		case R.id.GoogleMapToggleTraffic:
    			this.mblnTrafficView = !this.mblnTrafficView;
    			if (this.mblnTrafficView)
    				this.mblnStreetView = false;
    			this.mapView.setTraffic(this.mblnTrafficView);
    			return true;
    	}
    	return false;
    }
    
	/**
	 * 
	 */
	private void addMapOverlayItems(ArrayList<MapOverlayItem> mapOverlayItems)
	{
		final ResourceHelper resourceHelper = new ResourceHelper(this);
		final ArrayList<MapOverlayItem> mapOverlayItemsCopy = mapOverlayItems;
		
		mLogger.debug("Adding " + mapOverlayItems.size() + " mapoverlay items ...");

		mProgressDialog = ProgressDialog.show(this, "Adding map items", "Please wait - this may take a while ...", true, false);
		
		Thread t = new Thread() 
		{
			public void run() 
			{
				try 
				{
					for (MapOverlayItem mapOverlayItem : mapOverlayItemsCopy)
					{	
						final OverlayItem overlayItem = getOverlayItem(mapOverlayItem);
						final Drawable drawable = mapOverlayItem.getDrawable(resourceHelper);
						if (drawable != null) overlayItem.setMarker(drawable);
						mGoogleMapsItemizedOverlay.addOverlay(overlayItem);
					}
					mapOverlays.add(mGoogleMapsItemizedOverlay);
	 
					runOnUiThread(new Runnable() 
					{
						public void run() 
						{
							finalizeConstructor();
						}
					});
				} catch (Exception e) {}
			}
		};
		t.start();			
	}

	/**
	 * 
	 */
	private void finalizeConstructor()
	{
		mapView.getController().animateTo(this.mMapCenter);
		mapView.getController().setZoom(this.mInitialZoomLevel);
		
		try
		{
			mProgressDialog.dismiss();
		}
		catch (IllegalArgumentException iae)
		{
			// Ignore "View not attached to window" errors here
		}
		
	}
	
	/**
	 * 
	 */
	private void addMyLocationOverlay()
	{
		this.mMyLocationOverlay = new MyLocationOverlay(this, this.mapView);
		this.mapView.getOverlays().add(this.mMyLocationOverlay);
		
		this.mMyLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
            	mapView.getController().animateTo(mMyLocationOverlay.getMyLocation());
            }
        });
	}

	/**
	 * 
	 * @param mapOverlayItem
	 * @return
	 */
	private OverlayItem getOverlayItem(MapOverlayItem mapOverlayItem)
	{
		final GeoPoint geoPoint = new GeoPoint(mapOverlayItem.getLatitudeE6(), mapOverlayItem.getLongitudeE6());
		return new OverlayItem(geoPoint, mapOverlayItem.getTitle(), mapOverlayItem.getSnippet());
	}
	
}