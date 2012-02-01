package org.opengpx;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.opengpx.lib.geocache.LogEntry;
import org.opengpx.lib.geocache.LogType;
import org.opengpx.tools.Rot13;

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
class LogListAdapter extends ArrayAdapter<LogEntry>
{
	Activity mContext;
	ArrayList<LogEntry> mLogEntries;
	LayoutInflater mLayoutInflater;
	HashMap<LogType, Drawable> mhmIcons = new HashMap<LogType, Drawable>();
	
	/**
	 * 
	 * @param context
	 * @param logs
	 */
	public LogListAdapter(Activity context, ArrayList<LogEntry> logEntries) 
	{
         super(context, R.layout.loglistitem, logEntries);

         this.mContext = context;
         this.mLogEntries = logEntries;
         this.mLayoutInflater = this.mContext.getLayoutInflater();
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
        if (logEntry.isTextEncoded != null)
        {
            if (logEntry.isTextEncoded)
            {
            	final Rot13 rot13chiffre = new Rot13();
            	tvLine2.setText(rot13chiffre.process(logEntry.text));
            }
            else
            {
            	tvLine2.setText(logEntry.text);
            }
        }
        else
        {
        	tvLine2.setText(logEntry.text);
        }

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
