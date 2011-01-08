package org.opengpx.lib.map;

import org.andnav.osm.ResourceProxy;
import org.andnav.osm.util.GeoPoint;
import org.andnav.osm.views.OpenStreetMapView;
import org.andnav.osm.views.OpenStreetMapView.OpenStreetMapViewProjection;
import org.andnav.osm.views.overlay.OpenStreetMapViewOverlay;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/** An itemized Overlay where the items have custom graphics */
public class OsmMapViewerCustomItemizedOverlay extends OpenStreetMapViewOverlay 
{
    
    // ===========================================================
    // Constants
    // ===========================================================

    private static final int mintTapWidth = 32;
    private static final int mintTapHeight = 32;

    // ===========================================================
    // Fields
    // ===========================================================

    protected final OnItemTapListener<Drawable> mOnItemTapListener;
    protected final Drawable mDrawable;
    protected final GeoPoint mGeoPoint;
    
    protected final int mintMarkerWidth;
    protected final int mintMarkerHeight;
    
    protected int mintTapExtraWidth = 0;
    protected int mintTapExtraHeight = 0;
    
    private Matrix mTempMatrix = new Matrix();

    // ===========================================================
    // Constructors
    // ===========================================================

    public OsmMapViewerCustomItemizedOverlay(GeoPoint geoPoint,
    		Drawable drawable,
            OnItemTapListener<Drawable> aOnItemTapListener, 
            ResourceProxy resourceProxy) 
    {
        super(resourceProxy);
        
        this.mDrawable = drawable;
        this.mGeoPoint = geoPoint;
        this.mOnItemTapListener = aOnItemTapListener;

        // Set marker width and height
        final Rect rectBounds = this.mDrawable.getBounds();
        if ((rectBounds.right - rectBounds.left) > 0)
        {
        	this.mintMarkerWidth = (rectBounds.right - rectBounds.left);
        	this.mintMarkerHeight = (rectBounds.bottom - rectBounds.top);
        } 
        else
        {
        	this.mintMarkerWidth = this.mDrawable.getIntrinsicWidth();
        	this.mintMarkerHeight = this.mDrawable.getIntrinsicHeight();    	
        }
        
        // Set extra tap width and height (to make tapping the marker easier)
        if (this.mintMarkerWidth < mintTapWidth)
        	this.mintTapExtraWidth = (mintTapWidth - this.mintMarkerWidth) / 2;
        if (this.mintMarkerHeight < mintTapHeight)
        	this.mintTapExtraHeight = (mintTapHeight - this.mintMarkerHeight) / 2;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods from SuperClass/Interfaces
    // ===========================================================

    @Override
    protected void onDrawFinished(Canvas c, OpenStreetMapView osmv) 
    {
        return;
    }

    @Override
    public void onDraw(final Canvas c, final OpenStreetMapView mapView) 
    {
        final OpenStreetMapViewProjection pj = mapView.getProjection();
        final Point curScreenCoords = new Point();
        pj.toMapPixels(this.mGeoPoint, curScreenCoords);

        mTempMatrix.set(c.getMatrix());
        mTempMatrix.postTranslate(-this.mintMarkerWidth / 2.0f - 0.5f, -this.mintMarkerHeight / 2.0f - 0.5f);
        // mTempMatrix.postScale(1/mtx[Matrix.MSCALE_X], 1/mtx[Matrix.MSCALE_Y]);
        mTempMatrix.postTranslate(curScreenCoords.x, curScreenCoords.y);

        c.save();
        c.setMatrix(mTempMatrix);
        this.mDrawable.draw(c);
        c.restore();
    }

    @Override
    public boolean onSingleTapUp(
    		final MotionEvent event, 
            final OpenStreetMapView mapView) 
    {
        final OpenStreetMapViewProjection pj = mapView.getProjection();
        final int eventX = (int) event.getX();
        final int eventY = (int) event.getY();

        final Rect curMarkerBounds = new Rect();

        final Point curScreenCoords = new Point();
        pj.toMapPixels(this.mGeoPoint, curScreenCoords);

        final int left = curScreenCoords.x - this.mintMarkerWidth / 2 - this.mintTapExtraWidth;
        final int right = left + this.mintMarkerWidth + this.mintTapExtraWidth;
        final int top = curScreenCoords.y - this.mintMarkerHeight / 2 - this.mintTapExtraHeight;
        final int bottom = curScreenCoords.y + this.mintMarkerHeight + this.mintTapExtraHeight;

        curMarkerBounds.set(left, top, right, bottom);

        final Point curScreenCoords2 = new Point();
        pj.fromMapPixels(eventX, eventY, curScreenCoords2);
        if (curMarkerBounds.contains(curScreenCoords2.x, curScreenCoords2.y)) 
        {
            if (onTap()) 
            {
                return true;
            }
        }
            
        return super.onSingleTapUp(event, mapView);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    protected boolean onTap() 
    {
        if (this.mOnItemTapListener != null)
            return this.mOnItemTapListener.onItemTap(this.mDrawable);
        else
            return false;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static interface OnItemTapListener<T>
    {
        public boolean onItemTap(final T aItem);
    }

}
