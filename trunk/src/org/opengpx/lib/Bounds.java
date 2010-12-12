package org.opengpx.lib;

/**
 * This class defines an area ("bounds") with min/max latitude and longitude.
 * @author Martin Preishuber
 *
 */
public class Bounds 
{
	private Coordinate mMinimumLatitude;
	private Coordinate mMinimumLongitude;
	private Coordinate mMaximumLatitude;
	private Coordinate mMaximumLongitude;
	
	/**
	 * 
	 */
	public Bounds()
	{
		this.mMinimumLatitude = new Coordinate(CoordinateType.Latitude);
		this.mMinimumLongitude = new Coordinate(CoordinateType.Longitude);
		this.mMaximumLatitude = new Coordinate(CoordinateType.Latitude);
		this.mMaximumLongitude = new Coordinate(CoordinateType.Longitude);
	}
	
	/**
	 * 
	 * @return
	 */
	public Coordinate getMinimumLatitude()
	{
		return this.mMinimumLatitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public Coordinate getMinimumLongitude()
	{
		return this.mMinimumLongitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public Coordinate getMaximumLatitude()
	{
		return this.mMaximumLatitude;
	}
	
	/**
	 * 
	 * @return
	 */
	public Coordinate getMaximumLongitude()
	{
		return this.mMaximumLongitude;
	}
	
	/**
	 * 
	 */
	@Override public String toString()
	{
		return String.format("%s %s %s %s", this.mMinimumLatitude, this.mMinimumLongitude, 
				this.mMaximumLatitude, this.mMaximumLongitude);
	}
}
