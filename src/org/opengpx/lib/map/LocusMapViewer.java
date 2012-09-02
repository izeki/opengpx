package org.opengpx.lib.map;

import menion.android.locus.addon.publiclib.DisplayData;
// import menion.android.locus.addon.publiclib.LocusConst;
// import menion.android.locus.addon.publiclib.LocusIntents;
// import menion.android.locus.addon.publiclib.LocusUtils;
import menion.android.locus.addon.publiclib.geoData.Point;
// import menion.android.locus.addon.publiclib.geoData.PointGeocachingData;
import menion.android.locus.addon.publiclib.geoData.PointsData;
// import menion.android.locus.addon.publiclib.geoData.Track;
import menion.android.locus.addon.publiclib.utils.RequiredVersionMissingException;

import android.content.Context;
// import android.content.Intent;
import android.location.Location;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class LocusMapViewer extends MapViewerBase implements MapViewer 
{
	private static final String TAG = "";
	
	/**
	 * 
	 * @param context
	 */
	public LocusMapViewer(Context context) 
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
			try 
			{
				final PointsData pd = new PointsData("LocusMapStartActivity");
				
				for (final MapOverlayItem moi : this.mMapOverlayItems)
				{
					final Location loc = new Location(TAG);
					loc.setLatitude(moi.getLatitude());
					loc.setLongitude(moi.getLongitude());

					final Point pt = new Point(moi.getTitle(), loc);

					pd.addPoint(pt);
				}

				DisplayData.sendData(this.mContext, pd, true);
			} 
			catch (RequiredVersionMissingException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
