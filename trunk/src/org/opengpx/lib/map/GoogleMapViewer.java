package org.opengpx.lib.map;

import org.opengpx.GoogleMapViewerActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class GoogleMapViewer extends MapViewerBase implements MapViewer 
{

	// private static final String TAG = "GoogleMapViewer";

	public GoogleMapViewer(Context context) 
	{
		super(context);
	}
	
	/**
	 * 
	 */
	public void startActivity()
	{
		if (this.mMapOverlayItems.size() > 0)
		{
			Intent intGoogleMap = new Intent(this.mContext, GoogleMapViewerActivity.class);

			intGoogleMap.putExtra("map_items", this.mMapOverlayItems);
			intGoogleMap.putExtra("map_center", this.mOverlayItemCenter);
			intGoogleMap.putExtra("title", this.mOverlayItemCenter.getTitle());
			intGoogleMap.putExtra("zoom_level", this.mintZoomLevel);
			
			this.mContext.startActivity(intGoogleMap);
		}
		else
		{
			Toast.makeText(this.mContext, "No caches available.", Toast.LENGTH_SHORT).show();
		}
	}
}
