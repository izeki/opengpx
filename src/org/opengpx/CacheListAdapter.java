package org.opengpx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.opengpx.Preferences;
import org.opengpx.lib.UnitSystem;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CacheIndexItem;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.NavigationInfo;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class CacheListAdapter extends ArrayAdapter<String> implements Filterable
{
	protected CacheDatabase mCacheDatabase;
	protected LayoutInflater mLayoutInflater;
	protected HashMap<String, Drawable> mhmIcons;
	protected Coordinates mReferenceCoordinates;
	protected UnitSystem mUnitSystem;
	// protected ArrayList<String> mItems;

	/**
	 * 
	 * @param context
	 * @param cacheDatabase
	 */
	CacheListAdapter(Activity context, ArrayList<String> items) 
	{
        super(context, R.layout.cachelistitem);

        this.mLayoutInflater = LayoutInflater.from(context);
        this.mCacheDatabase = CacheDatabase.getInstance();
        this.mhmIcons = new HashMap<String, Drawable>();
        this.mUnitSystem = (new Preferences(context)).getUnitSystem();
        
        for (String cacheCode : items)
        	this.add(cacheCode);
        // this.mItems = items;
    }

	/**
	 * 
	 * @param cacheIndexItem
	 * @param textView
	 */
	public void drawCacheName(CacheIndexItem cacheIndexItem, TextView textView)
	{
        String strExtraNameTag = "";
        // Set text properties according to cache status
        if (cacheIndexItem.isArchived)
        {
        	textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        	textView.setTextColor(Color.RED);
        	strExtraNameTag = ", archived";		        
        }
        else if (!cacheIndexItem.isAvailable)
        {
        	textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        	textView.setTextColor(Color.WHITE);
        	strExtraNameTag = ", disabled";		        
        }
        else
        {
        	textView.setPaintFlags(textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        	textView.setTextColor(Color.WHITE);
        }
        textView.setText(String.format("%s (%s%s)", cacheIndexItem.name, cacheIndexItem.code, strExtraNameTag));
	}
	
	/**
	 * 
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		CacheListViewHolder cacheListViewHolder;
		if (convertView == null)
		{
			convertView = this.mLayoutInflater.inflate(R.layout.cachelistitem, null);

			cacheListViewHolder = new CacheListViewHolder();
			cacheListViewHolder.twoLineListItem = (TwoLineListItem) convertView.findViewById(R.id.CacheItem);
			cacheListViewHolder.icon = (ImageView) convertView.findViewById(R.id.CacheIcon);

			convertView.setTag(cacheListViewHolder);
		}
		else 
		{
			cacheListViewHolder = (CacheListViewHolder) convertView.getTag();
		}

        // final String strFilterableString = this.mItems.get(position);
        final String strFilterableString = this.getItem(position);
        final CacheIndexItem cacheIndexItem = this.mCacheDatabase.getCacheIndexItemForFilter(strFilterableString);

        if (cacheIndexItem != null)
        {
	        this.drawCacheName(cacheIndexItem, cacheListViewHolder.twoLineListItem.getText1());
	        
	        final TextView tvLine2 = cacheListViewHolder.twoLineListItem.getText2();
	        if (this.mCacheDatabase.getSortOrder() == CacheDatabase.SORT_ORDER_NAME)
	        {
	        	if (cacheIndexItem.vote > 0)
	        		tvLine2.setText(String.format("%s [D/T: %s/%s] [V:%.2f]", cacheIndexItem.container.toString().replace("_", " "), cacheIndexItem.difficulty, cacheIndexItem.terrain, cacheIndexItem.vote).replace(",", "."));
	        	else
	        		tvLine2.setText(String.format("%s [D/T: %s/%s]", cacheIndexItem.container.toString().replace("_", " "), cacheIndexItem.difficulty, cacheIndexItem.terrain));
	        }
	        else
	        {
	        	final Coordinates coordinates = new Coordinates(cacheIndexItem.latitude, cacheIndexItem.longitude);
	        	final NavigationInfo navInfo = this.mReferenceCoordinates.NavigationInfoTo(coordinates, this.mUnitSystem);
	        	if (cacheIndexItem.vote > 0)
		        	tvLine2.setText(String.format("Distance: %s [V:%.2f]", navInfo.toString(), cacheIndexItem.vote).replace(",", "."));
	        	else
	        		tvLine2.setText(String.format("Distance: %s", navInfo.toString()));
	        }
	        cacheListViewHolder.icon.setImageDrawable(this.getIcon(parent, cacheIndexItem.type));
        }
        
        return convertView;
    }

	/**
	 * 
	 * @param cacheCodes
	 */
	public void updateListContent(ArrayList<String> cacheCodes, CharSequence constraint)
	{
		this.clear();
		// this.mItems = cacheCodes;
		
		for (String strCacheCode : cacheCodes)
		{
			this.add(strCacheCode);
		}

		// This is necessary, otherwise updating of values doesn't work after text filtering
		this.getFilter().filter(constraint);
		super.notifyDataSetChanged();
	} 
	
	/* @Override
	public int getCount()
	{
		// return super.getCount();
		return this.mItems.size();
	} */

	/* @Override
	public void remove(String item)
	{
		this.mItems.remove(item);
		super.notifyDataSetChanged();
	} */
	
	/* @Override
	public String getItem(int position)
	{
		return this.mItems.get(position);
	} */

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setReferenceCoordinates(double latitude, double longitude)
	{
		this.mReferenceCoordinates = new Coordinates(latitude, longitude);
	}
	
	/**
	 * 
	 * @param parent
	 * @param strCacheType
	 * @return
	 */
	protected Drawable getIcon(ViewGroup parent, String strCacheType)
	{
		if (this.mhmIcons.containsKey(strCacheType))
		{
			return this.mhmIcons.get(strCacheType);
		}
		else
		{
			Drawable drawable = null;
			try 
			{
				drawable = Drawable.createFromStream(parent.getResources().getAssets().open(strCacheType.toLowerCase() + ".gif"), strCacheType);
				this.mhmIcons.put(strCacheType, drawable);
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
	static class CacheListViewHolder
	{
		TwoLineListItem twoLineListItem;
		ImageView icon;
	}
}
