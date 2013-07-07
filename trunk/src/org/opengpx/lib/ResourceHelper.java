package org.opengpx.lib;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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
	private String mPackageName = "org.opengpx"; // default value (should be valid)

	private static final Logger mLogger = LoggerFactory.getLogger(ResourceHelper.class);

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
		mLogger.debug("Display metrics density: " + Float.toString(this.mfltDensity));

		try 
		{
			final PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			mPackageName = packageInfo.packageName;
		} 
		catch (NameNotFoundException e) { }
	}

	/**
	 * 
	 * @param id
	 * @param width
	 * @param height
	 * @return
	 */
	public Drawable getDrawable(String id, int width, int height)
	{
		return this.getDrawable(id, width, height, true);
	}

	/**
	 * 
	 * @param mapOverlayItem
	 * @return
	 */
	public Drawable getDrawable(String id, int width, int height, boolean scale)
	{
		final String strDrawableId = id.toLowerCase();
		mLogger.debug("requested size: width=" + width + " height=" + height);
		int intRealWidth;
		int intRealHeight;
		if (scale)
		{
			intRealWidth = (int) (width * this.mfltDensity + 0.5f);
			intRealHeight = (int) (height * this.mfltDensity + 0.5f);
		} 
		else
		{
			intRealWidth = width;
			intRealHeight = height;			
		}
		mLogger.debug("real size: with=" + intRealWidth + " height=" + intRealHeight);
		final int res = this.mContext.getResources().getIdentifier(strDrawableId, "drawable", mPackageName);
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
	        catch (IOException e) 
	        { 
	        	mLogger.error("Unable to find resource file '" + strWpSymbolFileName + "'");
	        }
		}

		if (drawable != null)
		{
			if ((intRealWidth != drawable.getIntrinsicWidth()) || (intRealHeight != drawable.getIntrinsicHeight()))
			{
				// Rescale bitmap
				final Bitmap bitmapOrg = ((BitmapDrawable) drawable).getBitmap();
				final Bitmap bitmapNew = Bitmap.createScaledBitmap(bitmapOrg, intRealWidth, intRealHeight, true);
				drawable = new BitmapDrawable(this.mContext.getResources(), bitmapNew);
			}
			final Rect rect = new Rect(0, 0, intRealWidth, intRealHeight);
			drawable.setBounds(rect);
		}
		
		return drawable;
	}
	
}
