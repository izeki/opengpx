package org.opengpx.lib;

import java.io.IOException;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ResourceHelper 
{

	private Context mContext;
	private float mfltDensity;

	// private static final String TAG = "com.opengpx.lib.ResourceHelper";

	/**
	 * 
	 * @param context
	 */
	public ResourceHelper(Context context)
	{
		this.mContext = context;
		
		// This could be solved somewhat better, see
		// http://developer.android.com/guide/practices/screens_support.html
		this.mfltDensity = this.mContext.getResources().getDisplayMetrics().density;
		// Log.d(TAG, "Display metrics density: " + Float.toString(this.mfltDensity));
	}
	
	/**
	 * 
	 * @param mapOverlayItem
	 * @return
	 */
	/* public Drawable getDrawable(MapOverlayItem mapOverlayItem, int width, int height)
	{
		final String strDrawableId = mapOverlayItem.getDrawableId().toLowerCase();
		final int intRealWidth = (int) (width * this.mfltDensity + 0.5f);
		final int intRealHeight = (int) (height * this.mfltDensity + 0.5f);
		final int res = this.mContext.getResources().getIdentifier(strDrawableId, "drawable", "mpr.openGPX");
		Drawable drawable = null;
		if (res > 0)
		{
			// Log.d(TAG, "Drawable found: " + strDrawableId);
			drawable = this.mContext.getResources().getDrawable(res);
		} 
		else
		{
	        final String strWpSymbolFileName = String.format("waypoint_%s.jpg", strDrawableId);
	        try 
	        {
	        	drawable = Drawable.createFromStream(this.mContext.getResources().getAssets().open(strWpSymbolFileName), strDrawableId);
				// Log.d(TAG, "Drawable found (waypoint): " + strWpSymbolFileName);
	        } 
	        catch (IOException e) { }
		}

		if (drawable != null)
		{
			// Matrix matrix = new Matrix();
	        // resize the bit map
	        // matrix.postScale(intRealWidth, intRealHeight);
	        
			// String strImageWidth = Integer.toString(drawable.getIntrinsicWidth());
			// String strImageHeight = Integer.toString(drawable.getIntrinsicHeight());
			// Log.d(TAG, "Original image size (intrinsic): " + strImageWidth + "x" + strImageHeight);
			// Rect bounds = drawable.getBounds();
			// strImageWidth = Integer.toString(bounds.right);
			// strImageHeight = Integer.toString(bounds.bottom);
			// Log.d(TAG, "Original image size (bounds): " + strImageWidth + "x" + strImageHeight);
			// Log.d(TAG, "Scale image size: " + Integer.toString(intRealWidth) + "x" + Integer.toString(intRealHeight));
			// img.setBounds(0, 0, img.getIntrinsicWidth(), img.getIntrinsicHeight());
			Rect rect = new Rect(0, 0, intRealWidth, intRealHeight);
			// drawable.setBounds(0, 0 + intRealHeight, intRealWidth, 0 + intRealHeight);
			drawable.setBounds(rect);
			
			// strImageWidth = Integer.toString(drawable.getIntrinsicWidth());
			// strImageHeight = Integer.toString(drawable.getIntrinsicHeight());
			// Log.d(TAG, "New image size (intrinsic): " + strImageWidth + "x" + strImageHeight);
			// Rect r2 = drawable.getBounds();
			// strImageWidth = Integer.toString(r2.right);
			// strImageHeight = Integer.toString(r2.bottom);
			// Log.d(TAG, "New image size (bounds): " + strImageWidth + "x" + strImageHeight);
		}
		
		return drawable;
	} */

	/**
	 * 
	 * @param mapOverlayItem
	 * @return
	 */
	public Drawable getDrawable(String id, int width, int height)
	{
		final String strDrawableId = id.toLowerCase();
		final int intRealWidth = (int) (width * this.mfltDensity + 0.5f);
		final int intRealHeight = (int) (height * this.mfltDensity + 0.5f);
		final int res = this.mContext.getResources().getIdentifier(strDrawableId, "drawable", "mpr.openGPX");
		Drawable drawable = null;
		if (res > 0)
		{
			drawable = this.mContext.getResources().getDrawable(res);
		} 
		else
		{
	        final String strWpSymbolFileName = String.format("waypoint_%s.jpg", strDrawableId);
	        try 
	        {
	        	drawable = Drawable.createFromStream(this.mContext.getResources().getAssets().open(strWpSymbolFileName), strDrawableId);
	        } 
	        catch (IOException e) { }
		}

		if (drawable != null)
		{
			Rect rect = new Rect(0, 0, intRealWidth, intRealHeight);
			drawable.setBounds(rect);
		}
		
		return drawable;
	}
	
}
