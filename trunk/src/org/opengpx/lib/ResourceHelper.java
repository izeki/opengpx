package org.opengpx.lib;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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
	 * @param mapOverlayItem
	 * @return
	 */
	public Drawable getDrawable(String id, int width, int height)
	{
		final String strDrawableId = id.toLowerCase();
		final int intRealWidth = (int) (width * this.mfltDensity + 0.5f);
		final int intRealHeight = (int) (height * this.mfltDensity + 0.5f);
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
			final Rect rect = new Rect(0, 0, intRealWidth, intRealHeight);
			drawable.setBounds(rect);
		}
		
		return drawable;
	}
	
}
