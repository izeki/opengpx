package org.opengpx;

import java.io.IOException;
import java.util.ArrayList;

import org.opengpx.lib.geocache.CacheType;
import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.geocache.Waypoint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class WaypointListAdapter extends ArrayAdapter<Waypoint>
{
	final Activity mContext;
	final ArrayList<Waypoint> mWaypoints;
	final String mCacheCode;
	final CoordinateFormat mCoordinateFormat;
	final CacheType mCacheType;
	final LayoutInflater mLayoutInflater;

	/**
	 * 
	 * @param context
	 * @param waypoints
	 */
	WaypointListAdapter(Activity context, ArrayList<Waypoint> waypoints, final String cacheCode, final CacheType cacheType, final CoordinateFormat coordinateFormat) 
	{
		super(context, R.layout.waypointitem, waypoints);  

        this.mContext = context;
        this.mWaypoints = waypoints;
        this.mCacheCode = cacheCode;
        this.mCacheType = cacheType;
        this.mCoordinateFormat = coordinateFormat;

        this.mLayoutInflater = this.mContext.getLayoutInflater();
    }

	/**
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		WaypointListViewHolder waypointListViewHolder;
		if (convertView == null)
		{
			convertView = this.mLayoutInflater.inflate(R.layout.waypointitem, null);

			waypointListViewHolder = new WaypointListViewHolder();
			waypointListViewHolder.twoLineListItem = (TwoLineListItem) convertView.findViewById(R.id.WaypointItem);
			waypointListViewHolder.icon = (ImageView) convertView.findViewById(R.id.WaypointIcon);

			convertView.setTag(waypointListViewHolder);		
		}
		else 
		{
			waypointListViewHolder = (WaypointListViewHolder) convertView.getTag();
		}

        final Waypoint wp = this.mWaypoints.get(position);
        final TextView tvLine1 = waypointListViewHolder.twoLineListItem.getText1();
        tvLine1.setText(wp.getSnippet()); 
        final TextView tvLine2 = waypointListViewHolder.twoLineListItem.getText2();
        final Coordinates coords = new Coordinates(wp.latitude, wp.longitude);
        if (wp.elevation != Integer.MIN_VALUE)
            tvLine2.setText(String.format("%s %sm", coords.toString(this.mCoordinateFormat), wp.elevation));
        else
        	tvLine2.setText(coords.toString(this.mCoordinateFormat));
        
        final String strWpSymbolFileName = String.format("waypoint_%s.jpg", wp.getType().toString().toLowerCase());
        try 
        {
        	waypointListViewHolder.icon.setImageDrawable(Drawable.createFromStream(parent.getResources().getAssets().open(strWpSymbolFileName), wp.getType().toString()));            	
        } 
        catch (IOException e) { }

        return convertView;
    }
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	static class WaypointListViewHolder
	{
		TwoLineListItem twoLineListItem;
		ImageView icon;
	}

}
