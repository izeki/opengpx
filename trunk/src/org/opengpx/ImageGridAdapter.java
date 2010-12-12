package org.opengpx;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ImageGridAdapter extends BaseAdapter 
{

	private Context mContext;
    private ArrayList<String> mFileNames;  

    /**
     * 
     * @param c
     */
    public ImageGridAdapter(Activity context, ArrayList<String> filenames) 
    {	
        this.mContext = context;
        this.mFileNames = filenames;
    }

    /**
     * 
     */
    public int getCount() 
    {
        return mFileNames.size();
    }

    /**
     * 
     */
    public Object getItem(int position) 
    {
    	return this.mFileNames.get(position);
    }

    /**
     * 
     */
    public long getItemId(int position) 
    {
    	// FIXME
        return 0;
    }

    /**
     * 
     */
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        ImageView imageView;
        if (convertView == null)
        {
    		final float scale = parent.getResources().getDisplayMetrics().density;
    		final int intImgWidthHeight = (int) (92.0 * scale + 0.5f);
        	
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(intImgWidthHeight, intImgWidthHeight));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(2, 2, 2, 2);
            
            convertView = imageView;
        }
        else 
        {
            imageView = (ImageView) convertView;
        }

        Drawable draImage = Drawable.createFromPath(this.mFileNames.get(position));
        imageView.setImageDrawable(draImage);

        return convertView;
    }
}
