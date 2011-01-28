package org.opengpx.lib.map;

import org.opengpx.lib.UnitSystem;

import org.opengpx.MapOverlayItem;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class OsmMapViewer extends MapViewerBase implements MapViewer 
{
	// private static final String TAG = "OsmMapViewer";
	private MapOverlayItem moiTarget = null;
	
	public OsmMapViewer(Context context) 
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
			final Intent intOsmMap = new Intent(this.mContext, OsmMapViewerActivity.class);

			intOsmMap.putExtra("map_items", this.mMapOverlayItems);
			intOsmMap.putExtra("map_center", this.mOverlayItemCenter);
			intOsmMap.putExtra("title", this.mOverlayItemCenter.getTitle());
			intOsmMap.putExtra("zoom_level", this.mintZoomLevel);
			intOsmMap.putExtra("metric", (this.mUnitSystem == UnitSystem.Metric));
			if (moiTarget != null)
			{
				intOsmMap.putExtra("target", this.moiTarget);
			}
			
			this.mContext.startActivity(intOsmMap);
		}
		else
		{
			Toast.makeText(this.mContext, "No caches available.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @param name
	 */
	public void setTarget(double latitude, double longitude, String name)
	{
		this.moiTarget = new MapOverlayItem(latitude, longitude, "Target", name);
	}
}
