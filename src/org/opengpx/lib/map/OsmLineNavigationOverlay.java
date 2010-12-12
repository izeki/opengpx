package org.opengpx.lib.map;

import org.opengpx.R;

import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.andnav.osm.views.overlay.MyLocationOverlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.location.Location;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class OsmLineNavigationOverlay extends MyLocationOverlay 
{
	
	protected final Paint mLinePaint = new Paint();
	protected final Picture mNavLayout = new Picture();

	private final Context mContext;

	private final GeoPoint mTarget;
	private final Point mMapCoords = new Point();
	private final Point mTargetCoords = new Point();
	private final Matrix mNavigationMatrix = new Matrix();
	private final Paint mTextPaint = new Paint();

	private final int mTopAreaHeight;
	private float mScale = 1.0f;
	
	private final static int TOP_AREA_HEIGHT_DIP = 40;

	private final int mDisplayWidthInPixel;
	private final int mHalfDisplayWidth;
	private final float mTitlePosY;
	private final float mTextPosY;

	/**
	 * 
	 * @param ctx
	 * @param mapView
	 */
	public OsmLineNavigationOverlay(Context ctx, OpenStreetMapView mapView, GeoPoint target) 
	{
		super(ctx, mapView);
	
		this.mContext = ctx;
		// this.mMapView = mapView;
		this.mTarget = target;
		this.mScale = ctx.getResources().getDisplayMetrics().density;
		this.mDisplayWidthInPixel = ctx.getResources().getDisplayMetrics().widthPixels;
		this.mHalfDisplayWidth = this.mDisplayWidthInPixel / 2;
		
		this.mTopAreaHeight = (int) (TOP_AREA_HEIGHT_DIP * mScale);
		this.mTitlePosY = 13 * mScale;
		this.mTextPosY = 35 * mScale;
		
		this.definePaintStyles();
		this.createNavLayoutPicture();
		
		// Move compass down to have some space for the navigation layout above
		this.setCompassCenter(35.0f, 35.0f + TOP_AREA_HEIGHT_DIP);
	}

	/**
	 * 
	 */
    @Override
    public void onDraw(final Canvas c, final OpenStreetMapView osmv) 
    {
    	super.onDraw(c, osmv);

    	GeoPoint geoPointCurrent = null;

    	// Draw line to target
    	final Location location = super.getLastFix();
        if (location != null) 
        {
        	geoPointCurrent = new GeoPoint(location);
            final OpenStreetMapViewProjection pj = osmv.getProjection();
            // get point for current coordinates
            pj.toMapPixels(geoPointCurrent, this.mMapCoords);
            // get point for target point
            pj.toMapPixels(this.mTarget, this.mTargetCoords);
            
            // Draw line
            c.drawLine(mMapCoords.x, mMapCoords.y, this.mTargetCoords.x, this.mTargetCoords.y, this.mLinePaint);            
        }
        		
        // Draw navigation layout on top (draw it in front of the navigation line)
		this.mNavigationMatrix.set(c.getMatrix());
		this.mNavigationMatrix.setTranslate(0, 0);
		this.mNavigationMatrix.postTranslate(0, (c.getHeight() - mMapView.getHeight()));

		c.save();
		c.setMatrix(this.mNavigationMatrix);
		
		// Draw background image
		c.drawPicture(mNavLayout);

    	// Draw distance and bearing information
        String strDistance = "---m";
        String strBearing = "---¡";        
        if ((location != null) && (geoPointCurrent != null))
        {
            final float distanceInMeters = geoPointCurrent.distanceTo(mTarget);
			if (distanceInMeters > 1000)
				strDistance = String.format("%.2fkm", distanceInMeters / 1000);
			else
				strDistance = String.format("%.1fm", distanceInMeters);
			
            // replace by geoPointCurrent.bearingTo(mTarget) somewhen
			final double dblBearing = geoPointCurrent.bearingTo(mTarget);
            strBearing = String.format("%.0f¡", dblBearing);
        }
        
        float textWidth = mTextPaint.measureText(strDistance);
		c.drawText(strDistance, (mHalfDisplayWidth - textWidth) / 2, mTextPosY, mTextPaint);
        textWidth = mTextPaint.measureText(strBearing);
		c.drawText(strBearing, mHalfDisplayWidth + (mHalfDisplayWidth - textWidth) / 2, mTextPosY, mTextPaint);

    	c.restore();
    }
        
    /**
     * Define paint of navigation line
     */
    private void definePaintStyles()
    {
        mLinePaint.setAlpha(150);
        mLinePaint.setColor(Color.MAGENTA);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(5);
		mLinePaint.setStyle(Style.STROKE);    	
		
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setStrokeWidth(1.0f);
		mTextPaint.setTextSize(20 * mScale);
		mTextPaint.setAlpha(255);
    }
    
    /**
     * 
     */
    private void createNavLayoutPicture()
    {
    	// White background
		final Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.WHITE);
		backgroundPaint.setAntiAlias(true);
		backgroundPaint.setStyle(Style.FILL);
		backgroundPaint.setAlpha(200);

		// Draw black lines
		final Paint lineBorderPaint = new Paint();
		lineBorderPaint.setColor(Color.BLACK);
		lineBorderPaint.setAntiAlias(true);
		lineBorderPaint.setStyle(Style.STROKE);
		lineBorderPaint.setStrokeWidth(1.0f);
		lineBorderPaint.setAlpha(200);

		final Paint textPaint = new Paint();
		textPaint.setColor(Color.DKGRAY);
		textPaint.setAntiAlias(true);
		textPaint.setStyle(Style.FILL);
		textPaint.setStrokeWidth(1.0f);
		textPaint.setTextSize(10 * mScale);
		textPaint.setAlpha(180);
		
		final Canvas canvas = mNavLayout.beginRecording(mDisplayWidthInPixel, mTopAreaHeight);
		
		// Create a white rectangle which will contain distance and routing information
		canvas.drawRect(0, 0, mDisplayWidthInPixel, mTopAreaHeight, backgroundPaint);
		// Draw a blank line on bottom
		canvas.drawLine(0, mTopAreaHeight, mDisplayWidthInPixel, mTopAreaHeight, lineBorderPaint);
		// Draw a black line in the middle
		canvas.drawLine(mHalfDisplayWidth, 0, mHalfDisplayWidth, mTopAreaHeight, lineBorderPaint);
		
		// Draw "distance" text to background
		final String strDistance = mContext.getResources().getString(R.string.distance);
		float textWidth = textPaint.measureText(strDistance);
		canvas.drawText(strDistance, (mHalfDisplayWidth - textWidth) / 2, mTitlePosY, textPaint);

		// Draw "bearing" text to background
		final String strBearing = mContext.getResources().getString(R.string.bearing);
		textWidth = textPaint.measureText(strBearing);
		canvas.drawText(strBearing, mHalfDisplayWidth + (mHalfDisplayWidth - textWidth) / 2, mTitlePosY, textPaint);
		
		mNavLayout.endRecording();
    }
 
    /**
     * 
     * @return
     */
    public int getNavbarHeight()
    {
    	return this.mTopAreaHeight;
    }
}
