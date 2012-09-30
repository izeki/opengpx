package org.opengpx;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.opengpx.lib.geocache.FieldNote;
import org.opengpx.lib.geocache.FieldNote.LogType;

import com.db4o.ObjectSet;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class FieldNoteHistoryAdapter extends BaseAdapter
{
	protected LayoutInflater mLayoutInflater;
	protected HashMap<LogType, Drawable> mhmIcons;

	private ArrayList<FieldNote> mData = new ArrayList<FieldNote>();
    private TreeSet<Integer> mSeparatorsSet = new TreeSet<Integer>();
    
    private java.text.DateFormat mDateFormat;
    private java.text.DateFormat mTimeFormat;
    private Format mWeekdayFormat = new SimpleDateFormat("EEEE");

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    /**
     * 
     * @param context
     */
	FieldNoteHistoryAdapter(Activity context, ObjectSet<FieldNote> items) 
	{
        // super(context, R.layout.fieldnotelistitem);

        this.mLayoutInflater = LayoutInflater.from(context);
        this.mhmIcons = new HashMap<LogType, Drawable>();
        
        // final FieldNoteList fieldNoteList = new FieldNoteList();
        // final Integer distinctiveDateCount = FieldNoteList.getDistinctiveDateCount(context, items);

        String currentDateString = "";
		this.mDateFormat = DateFormat.getDateFormat(context);
		this.mTimeFormat = DateFormat.getTimeFormat(context);

        for (final FieldNote fieldNote : items)
        {
        	// Get date of the fieldnote
    		final String dateString = this.mDateFormat.format(fieldNote.noteTime);
    		if (!dateString.equals(currentDateString))
    		{
    			// Add separator item
    			final FieldNote separatorFieldNote = new FieldNote();
    			separatorFieldNote.gcId = "---";
    			separatorFieldNote.gcName = "-----";
    			separatorFieldNote.logType = FieldNote.LogType.WRITE_NOTE;
    			separatorFieldNote.noteTime = fieldNote.noteTime;
    			separatorFieldNote.logText = dateString;

    			this.addSeparatorItem(separatorFieldNote);
    			currentDateString = dateString;
    		}
        	this.addItem(fieldNote);
        }
    }

	/**
	 * 
	 * @param item
	 */
	public void addItem(final FieldNote item) 
	{
        mData.add(item);
        notifyDataSetChanged();
    }
	
	/**
	 * 
	 * @param item
	 */
	public void addSeparatorItem(final FieldNote item) 
	{
        mData.add(item);
        // save separator position
        mSeparatorsSet.add(mData.size() - 1);
        notifyDataSetChanged();
    }
	
	/**
	 * 
	 * @param position
	 * @return
	 */
	 @Override
     public int getItemViewType(int position) 
	 {
         return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	 }

	 /**
	  * 
	  * @return
	  */
	 @Override
     public int getViewTypeCount() 
	 {
         return TYPE_MAX_COUNT;
     }

	 /**
	  * 
	  * @return
	  */
     public int getCount() 
     {
         return mData.size();
     }

     /**
      * 
      * @param position
      * @return
      */
     public FieldNote getItem(int position) 
     {
         return mData.get(position);
     }

     /**
      * 
      * @param position
      * @return
      */
     public long getItemId(int position) 
     {
         return position;
     }

     /**
      * 
      * @param position
      * @param convertView
      * @param parent
      * @return
      */
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		FieldNoteViewHolder viewHolder = null;
		
        int type = getItemViewType(position);
        
		if (convertView == null)
		{
			viewHolder = new FieldNoteViewHolder();
			
			if (type == TYPE_ITEM)
			{
				convertView = this.mLayoutInflater.inflate(R.layout.fieldnotelistitem, null);
	
				viewHolder.twoLineListItem = (TwoLineListItem) convertView.findViewById(R.id.FieldNoteItem);
				viewHolder.icon = (ImageView) convertView.findViewById(R.id.FieldNoteIcon);
			}
			else // type == TYPE_SEPARATOR
			{
				convertView = this.mLayoutInflater.inflate(R.layout.fieldnoteseparator, null);

				viewHolder.textView = (TextView) convertView.findViewById(R.id.FieldNoteSeparator);
			}
			convertView.setTag(viewHolder);
		}
		else 
		{
			viewHolder = (FieldNoteViewHolder) convertView.getTag();
		}

        final FieldNote fieldNote = this.getItem(position);
        if (fieldNote != null)
        {
			if (type == TYPE_ITEM)
			{
		        final TextView tvLine1 = viewHolder.twoLineListItem.getText1();
		        // tvLine1.setText(this.mTimeFormat.format(fieldNote.noteTime) + " " + fieldNote.gcId);
		        tvLine1.setText(this.mTimeFormat.format(fieldNote.noteTime) + " " + fieldNote.gcName);
		        final TextView tvLine2 = viewHolder.twoLineListItem.getText2();
		        // tvLine2.setText(fieldNote.gcName); // + " (" + fieldNote.gcId + ")");
		        tvLine2.setText(fieldNote.gcId + " (" + fieldNote.logType.toString() + ")");
		        
		        viewHolder.icon.setImageDrawable(this.getIcon(parent, fieldNote.logType));
			}
			else // type == TYPE_SEPARATOR
			{
		        final TextView separator = viewHolder.textView;
	        	// Get date of the fieldnote
	    		final String dateString = this.mWeekdayFormat.format(fieldNote.noteTime) + ", " + this.mDateFormat.format(fieldNote.noteTime);
		        
		        separator.setText(dateString);
			}			
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
		TextView textView;
	}
}
