package org.opengpx;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class GoogleMapItemizedOverlay extends ItemizedOverlay<OverlayItem> 
{

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private GoogleMapViewerActivity mParent;

	/**
	 * 
	 * @param defaultMarker
	 */
	public GoogleMapItemizedOverlay(GoogleMapViewerActivity parent, Drawable defaultMarker) 
	{
		super(boundCenterBottom(defaultMarker));

		this.mParent = parent;
	}

	/**
	 * 
	 */
	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	/**
	 * 
	 */
	@Override
	public int size() 
	{
		return mOverlays.size();
	}

	/**
	 * 
	 * @param overlay
	 */
	public void addOverlay(OverlayItem overlay) 
	{
	    mOverlays.add(overlay);
	    populate();
	}
	
	/**
	 * 
	 * @param location
	 * @return
	 */
	public OverlayItem getOverlay(int location)
	{
		return mOverlays.get(location);
	}

	/**
	 * 
	 */
	@Override
	public boolean onTap(int i)
	{
		// final String strTitle = mOverlays.get(i).getTitle().trim();
		final String strSnippet = mOverlays.get(i).getSnippet().trim();
		
		// check, wether the title contains a waypoint ID
		// if ((strTitle.length()) <= 7 && !(strTitle.contains(" ")))
		//	Toast.makeText(this.mParent, String.format("%s (%s)", strSnippet, strTitle), Toast.LENGTH_SHORT).show();
		// else
		// 	Toast.makeText(this.mParent, strTitle, Toast.LENGTH_SHORT).show();
		Toast.makeText(this.mParent, strSnippet, Toast.LENGTH_SHORT).show();
		return true;
	}
}
