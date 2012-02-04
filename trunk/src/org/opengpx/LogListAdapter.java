package org.opengpx;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.geocache.LogEntry;
import org.opengpx.lib.geocache.LogType;
import org.opengpx.tools.Rot13;

import android.app.Activity;
import android.content.res.Resources;
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
class LogListAdapter extends ArrayAdapter<LogEntry>
{
	final Activity mContext;
	final ArrayList<LogEntry> mLogEntries;
	LayoutInflater mLayoutInflater;
	HashMap<LogType, Drawable> mhmIcons = new HashMap<LogType, Drawable>();
	final CoordinateFormat mCoordinateFormat;
	final Resources mResources;

	/**
	 * 
	 * @param context
	 * @param logs
	 */
	public LogListAdapter(Activity context, ArrayList<LogEntry> logEntries, CoordinateFormat coordinateFormat, Resources resources) 
	{
         super(context, R.layout.loglistitem, logEntries);

         this.mContext = context;
         this.mLogEntries = logEntries;
         this.mLayoutInflater = this.mContext.getLayoutInflater();
         this.mCoordinateFormat = coordinateFormat;
         this.mResources = resources;
    }

	/**
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		LogListViewHolder logListViewHolder;
		if (convertView == null)
		{
			convertView = this.mLayoutInflater.inflate(R.layout.loglistitem, null);

			logListViewHolder = new LogListViewHolder();
			logListViewHolder.twoLineListItem = (TwoLineListItem) convertView.findViewById(R.id.LogItem);
			logListViewHolder.icon = (ImageView) convertView.findViewById(R.id.LogIcon);

			convertView.setTag(logListViewHolder);
		}
		else
		{
			logListViewHolder = (LogListViewHolder) convertView.getTag();
		}

        final LogEntry logEntry = this.mLogEntries.get(position);

        final TextView tvLine1 = logListViewHolder.twoLineListItem.getText1();
        final DateFormat df = DateFormat.getDateInstance();
		final String strLogDate = df.format(logEntry.time);
        final String strLogHeader = String.format("%s (%s)", logEntry.finder, strLogDate); 
        tvLine1.setText(strLogHeader);
        final TextView tvLine2 = logListViewHolder.twoLineListItem.getText2();
        String logText = logEntry.text;
        if (logEntry.isTextEncoded != null)
        {
            if (logEntry.isTextEncoded)
            {
            	final Rot13 rot13chiffre = new Rot13();
            	logText = rot13chiffre.process(logEntry.text);
            }
        }

        if ((!Double.isNaN(logEntry.latitude)) && (!Double.isNaN(logEntry.longitude)) && 
        	(logEntry.latitude != 0) && (logEntry.longitude != 0))
        {
        	final Coordinates logCoordinates = new Coordinates(logEntry.latitude, logEntry.longitude);
        	logText += "\n" + this.mResources.getString(R.string.waypoint) + ": " + logCoordinates.toString(this.mCoordinateFormat);
        }
    	tvLine2.setText(logText);

        logListViewHolder.icon.setImageDrawable(this.getIcon(parent, logEntry.getType()));            	

        return convertView;
    }
	
	/**
	 * 
	 * @param parent
	 * @param logType
	 * @return
	 */
	private Drawable getIcon(ViewGroup parent, LogType logType)
	{
		if (this.mhmIcons.containsKey(logType))
		{
			return this.mhmIcons.get(logType);
		}
		else
		{
			String strLogType = logType.toString();
	        String strLogIcon = String.format("log_%s.gif", strLogType.toLowerCase());
	        Drawable drawable = null;
	        try 
	        {
				drawable = Drawable.createFromStream(parent.getResources().getAssets().open(strLogIcon), strLogType);
				this.mhmIcons.put(logType, drawable);
			} 
	        catch (IOException e) 
	        {
				e.printStackTrace();
			}
	        return drawable;
		}
	}
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	static class LogListViewHolder
	{
		TwoLineListItem twoLineListItem;
		ImageView icon;
	}
}
