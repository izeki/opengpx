package org.opengpx;

import java.io.IOException;
import java.util.HashMap;

import org.opengpx.lib.geocache.FieldNote;
import org.opengpx.lib.geocache.FieldNote.LogType;
import org.opengpx.lib.geocache.helpers.FieldNoteList;

import com.db4o.ObjectSet;

import android.app.Activity;
import android.graphics.drawable.Drawable;
// import android.text.format.DateFormat;
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
public class FieldNoteHistoryAdapter extends ArrayAdapter<FieldNote>
{
	protected LayoutInflater mLayoutInflater;
	protected HashMap<LogType, Drawable> mhmIcons;

	/**
	 * 
	 * @param context
	 * @param items
	 */
	FieldNoteHistoryAdapter(Activity context, ObjectSet<FieldNote> items) 
	{
        super(context, R.layout.fieldnotelistitem);

        this.mLayoutInflater = LayoutInflater.from(context);
        this.mhmIcons = new HashMap<LogType, Drawable>();
        
        // final FieldNoteList fieldNoteList = new FieldNoteList();
        final Integer distinctiveDateCount = FieldNoteList.getDistinctiveDateCount(context, items);

        for (FieldNote fieldNote : items)
        {
        	this.add(fieldNote);
        }
    }

	/**
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		FieldNoteViewHolder fieldNoteViewHolder;
		if (convertView == null)
		{
			convertView = this.mLayoutInflater.inflate(R.layout.fieldnotelistitem, null);

			fieldNoteViewHolder = new FieldNoteViewHolder();
			fieldNoteViewHolder.twoLineListItem = (TwoLineListItem) convertView.findViewById(R.id.FieldNoteItem);
			fieldNoteViewHolder.icon = (ImageView) convertView.findViewById(R.id.FieldNoteIcon);

			convertView.setTag(fieldNoteViewHolder);
		}
		else 
		{
			fieldNoteViewHolder = (FieldNoteViewHolder) convertView.getTag();
		}

        final FieldNote fieldNote = this.getItem(position);
        if (fieldNote != null)
        {	        
	        final TextView tvLine1 = fieldNoteViewHolder.twoLineListItem.getText1();
	        tvLine1.setText(fieldNote.gcName + " (" + fieldNote.gcId + ")");
	        final TextView tvLine2 = fieldNoteViewHolder.twoLineListItem.getText2();
	        tvLine2.setText(fieldNote.noteTime.toString());
	        
			// java.text.DateFormat dateFormat = DateFormat.getDateFormat(this.getContext());
			// java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this.getContext());
	        // tvLine2.setText(dateFormat.format(fieldNote.noteTime) + " " + timeFormat.format(fieldNote.noteTime));
	        
        	fieldNoteViewHolder.icon.setImageDrawable(this.getIcon(parent, fieldNote.logType));
        }

        return convertView;
    }
			
	/**
	 * 
	 * @param parent
	 * @param strCacheType
	 * @return
	 */
	protected Drawable getIcon(ViewGroup parent, LogType logType)
	{
		if (this.mhmIcons.containsKey(logType))
		{
			return this.mhmIcons.get(logType);
		}
		else
		{
			Drawable drawable = null;
	        try 
	        {
	        	drawable = Drawable.createFromStream(parent.getResources().getAssets().open(logType.getIconFilename()), logType.name());
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
	static class FieldNoteViewHolder
	{
		TwoLineListItem twoLineListItem;
		ImageView icon;
	}
}
