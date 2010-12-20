package org.opengpx.lib;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengpx.lib.UnitSystem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Coordinates 
{
	private static final int earth_radius = 6371; // km
	
	public final static String coords_regexp = "([NnSs]) ?(\\d{1,2}).{0,1} ?(\\d{1,2}\\.\\d{1,4}).?\\s{0,25}([EeWw]) ?(\\d{1,3}).{0,1} ?(\\d{1,2}\\.\\d{1,4})"; 

	private Coordinate mcoordLatitude;
	private Coordinate mcoordLongitude;

	/**
	 * 
	 */
	public Coordinates()
	{
		this.mcoordLatitude = new Coordinate(CoordinateType.Latitude);
		this.mcoordLongitude = new Coordinate(CoordinateType.Longitude);
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public Coordinates(double latitude, double longitude)
	{
		this.mcoordLatitude = new Coordinate(CoordinateType.Latitude);
		this.mcoordLatitude.setD(latitude);
		this.mcoordLongitude = new Coordinate(CoordinateType.Longitude);
		this.mcoordLongitude.setD(longitude);
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setDMS(DMS latitude, DMS longitude)
	{
		this.mcoordLatitude.setDMS(latitude);
		this.mcoordLongitude.setDMS(longitude);
	}
	
	/**
	 * 
	 * @param latHemisphere
	 * @param latDegrees
	 * @param latMinutes
	 * @param latSeconds
	 * @param longHemisphere
	 * @param longDegrees
	 * @param longMinutes
	 * @param longSeconds
	 */
	public void setDMS(Hemisphere latHemisphere, int latDegrees, int latMinutes, double latSeconds, Hemisphere longHemisphere, int longDegrees, int longMinutes, double longSeconds)
	{
        this.mcoordLatitude.setDMS(latHemisphere, latDegrees, latMinutes, latSeconds);
        this.mcoordLongitude.setDMS(longHemisphere, longDegrees, longMinutes, longSeconds);
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void setDM(DM latitude, DM longitude)
	{
		this.mcoordLatitude.setDM(latitude);
		this.mcoordLongitude.setDM(longitude);
	}
	
	/**
	 * 
	 * @param latHemisphere
	 * @param latDegrees
	 * @param latMinutes
	 * @param longHemisphere
	 * @param longDegrees
	 * @param longMinutes
	 */
    public void setDM(Hemisphere latHemisphere, int latDegrees, double latMinutes, Hemisphere longHemisphere, int longDegrees, double longMinutes)
    {
        this.mcoordLatitude.setDM(latHemisphere, latDegrees, latMinutes);
        this.mcoordLongitude.setDM(longHemisphere, longDegrees, longMinutes);
    }

    /**
     * 
     * @param latitude
     * @param longitude
     */
    public void setD(double latitude, double longitude)
    {
        this.mcoordLatitude.setD(latitude);
        this.mcoordLongitude.setD(longitude);
    }

    /**
     * 
     * @param latitude
     * @param longitude
     */
    public void getDMS(DMS latitude, DMS longitude)
    {
    	latitude = this.mcoordLatitude.getDMS();
    	longitude = this.mcoordLongitude.getDMS();
    }

    /**
     * 
     * @param latitude
     * @param longitude
     */
    public void getDM(DM latitude, DM longitude)
    {
    	latitude = this.mcoordLatitude.getDM();
    	longitude = this.mcoordLongitude.getDM();
    }

    /**
     * 
     * @param latitude
     * @param longitude
     */
    /* public void getD(double latitude, double longitude)
    {
        latitude = this.mcoordLatitude.getD();
        longitude = this.mcoordLongitude.getD();
    } */

    /**
     * 
     */
    public Coordinate getLatitude()
    {
    	return this.mcoordLatitude;
    }
    
    /**
     * 
     * @param value
     */
    public void setLatitude(double value)
    {
    	this.mcoordLatitude.setD(value);
    }
    
    /**
     * 
     * @return
     */
    public Coordinate getLongitude()
    {
    	return this.mcoordLongitude;
    }

    /**
     * 
     * @param value
     */
    public void setLongitude(double value)
    {
    	this.mcoordLongitude.setD(value);
    }
    
    /**
     * 
     * @param text
     * @return
     */
    public boolean ParseFromText(String text)
    {
        boolean blnResult = false;
        final Pattern regex = Pattern.compile(coords_regexp, Pattern.DOTALL);
        final Matcher matcher = regex.matcher(text);
        if (matcher.find())
        {
        	final Hemisphere latHemisphere = Hemisphere.valueOf(matcher.group(1));
            final int latDegrees = Integer.parseInt(matcher.group(2));
            final double latMinutes = Double.parseDouble(matcher.group(3));
            final Hemisphere longHemisphere = Hemisphere.valueOf(matcher.group(4));
            final int longDegrees = Integer.parseInt(matcher.group(5));
            final double longMinutes = Double.parseDouble(matcher.group(6));
            this.setDM(latHemisphere, latDegrees, latMinutes, longHemisphere, longDegrees, longMinutes);
            blnResult = true;
        }
        return blnResult;
    }

    /**
     * 
     * @param coords
     * @return
     */
    public double DistanceTo(final Coordinates coords)
    {
    	return this.DistanceTo(coords, UnitSystem.Metric);
    }

    /**
     * 
     * @param coords
     * @param unitSystem
     * @return
     */
    public double DistanceTo(final Coordinates coords, UnitSystem unitSystem)
    {
        final double lat1 = Math.toRadians(this.mcoordLatitude.getD());
        final double long1 = Math.toRadians(this.mcoordLongitude.getD());
        final double lat2 = Math.toRadians(coords.getLatitude().getD());
        final double long2 = Math.toRadians(coords.getLongitude().getD());
        final double delta_lat = lat2 - lat1;
        final double delta_long = long2 - long1;
        
        final double a = Math.pow(Math.sin(delta_lat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(delta_long / 2), 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final double d = (earth_radius * c);
        
        if (unitSystem.equals(UnitSystem.Metric))
        	return d; // km
        else
        	return (d / 1.609344); // miles
    }

    /**
     * 
     * @param coords
     * @return
     */
    public double BearingTo(final Coordinates coords)
    {
        final double lat1 = Math.toRadians(this.mcoordLatitude.getD());
        final double long1 = Math.toRadians(this.mcoordLongitude.getD());
        final double lat2 = Math.toRadians(coords.getLatitude().getD());
        final double long2 = Math.toRadians(coords.getLongitude().getD());
        final double delta_long = long2 - long1;
        
        final double a = Math.sin(delta_long) * Math.cos(lat2);
        final double b = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(delta_long);
        final double bearing = Math.toDegrees(Math.atan2(a, b));
        final double bearing_normalized = (bearing + 360) % 360;
        
        return bearing_normalized;
    }

    /**
     * 
     * @param bearing
     * @return
     */
    public Direction DirectionForBearing(double bearing)
    {
        if ((bearing >= 337.5) || (bearing < 22.5))
            return Direction.N;
        else if ((bearing >= 22.5) && (bearing < 67.5))
            return Direction.NE;
        else if ((bearing >= 67.5) && (bearing < 112.5))
            return Direction.E;
        else if ((bearing >= 112.5) && (bearing < 157.5))
            return Direction.SE;
        else if ((bearing >= 157.5) && (bearing < 202.5))
            return Direction.S;
        else if ((bearing >= 202.5) && (bearing < 247.5))
            return Direction.SW;
        else if ((bearing > 247.5) && (bearing < 292.5))
            return Direction.W;
        else if ((bearing >= 292.5) && (bearing < 337.5))
            return Direction.NW;
        else
            return Direction.N;     // this is some default value if bearing = NaN
    }

    /**
     * 
     * @param coords
     * @return
     */
    public NavigationInfo NavigationInfoTo(Coordinates coords) 
    {
    	return this.NavigationInfoTo(coords, UnitSystem.Metric);
    }

    /**
     * 
     * @param coords
     * @param unitSystem
     * @return
     */
    public NavigationInfo NavigationInfoTo(Coordinates coords, UnitSystem unitSystem) 
    {
    	final NavigationInfo navigationInfo = new NavigationInfo(unitSystem);
    	navigationInfo.distance  = this.DistanceTo(coords, unitSystem);
    	navigationInfo.bearing = this.BearingTo(coords);
    	navigationInfo.direction = this.DirectionForBearing(navigationInfo.bearing);
        return navigationInfo;
    }

    /**
     * 
     * @param d
     * @param decimalPlaces
     * @return
     */
    private double round(double d, int decimalPlaces)
    {
		BigDecimal bd = new BigDecimal(d);
	 	bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
 		return bd.doubleValue();
    }
    
    /**
     * 
     * @param coords
     * @return
     */
    public boolean equals(Coordinates coords)
    {
    	final Double dblLatitude = this.round( this.mcoordLatitude.getD(), 5);
    	final Double dblLongitude = this.round( this.mcoordLongitude.getD(), 5);
    	
    	final Double dblLatitudeCompare = this.round(coords.getLatitude().getD(), 5);
    	final Double dblLongitudeCompare = this.round(coords.getLongitude().getD(), 5);
    	
    	final int intLatitudeEqual = dblLatitude.compareTo(dblLatitudeCompare);
    	final int intLongitudeEqual = dblLongitude.compareTo(dblLongitudeCompare);

    	// int intLatitudeEqual = ((Double) this.getLatitude().getD()).compareTo((Double) coords.getLatitude().getD());
    	// int intLongitudeEqual = ((Double) this.getLongitude().getD()).compareTo((Double) coords.getLongitude().getD());

    	return ((intLatitudeEqual == 0) && (intLongitudeEqual == 0));

        // return ((this.round(this.getLatitude().getD(), 5) == this.round(coords.getLatitude().getD(), 5)) &&
        // 		(this.round(this.getLongitude().getD(), 5) == this.round(coords.getLongitude().getD(), 5)));
    }

    /**
     * 
     * @param coordinateFormat
     * @return
     */
    public String toString(CoordinateFormat coordinateFormat)
    {
        return String.format("%s %s", this.mcoordLatitude.toString(coordinateFormat), this.mcoordLongitude.toString(coordinateFormat));
    }

    /**
     * 
     */
	@Override public String toString()
	{
		return this.toString(CoordinateFormat.D);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    Coordinates coords = new Coordinates();
	    coords.setD(46.61123, 13.89985);
	    System.out.println(coords);
	    System.out.println(coords.toString(CoordinateFormat.DMS));
	    // System.out.println(coords.DMS);
	    System.out.println(coords.getLongitude());

	    Coordinates coordsA = new Coordinates();
	    coordsA.setDM(Hemisphere.N, 46, 35.595, Hemisphere.E, 14, 16.394);
	    Coordinates coordsB = new Coordinates();
	    coordsB.setDM(Hemisphere.N, 46, 35.664, Hemisphere.E, 14, 16.057);
	    System.out.println(coordsA.DistanceTo(coordsB));
		System.out.println(coordsA.BearingTo(coordsB));
	    Coordinates coordsC = new Coordinates();
	    coordsC.setDM(Hemisphere.N, 46, 35.664, Hemisphere.E, 14, 16.057);
		System.out.println(coordsB.equals(coordsC));
		NavigationInfo navigationInfo = coordsA.NavigationInfoTo(coordsB);
		System.out.println(String.format("%.16f %.16f %s", navigationInfo.distance, navigationInfo.bearing, navigationInfo.direction));
		String[] arrNavigationInfo = navigationInfo.toStringArray();
		System.out.println(String.format("%s %s %s", arrNavigationInfo[0], arrNavigationInfo[1], arrNavigationInfo[2]));

	    Coordinates coordsT = new Coordinates();
	    System.out.println(coordsT.ParseFromText("N 46� 32.329 E 014� 30.535 "));
	    System.out.println(coordsT.toString(CoordinateFormat.DM));

	    Coordinates coordsNaN = new Coordinates();
	    coordsNaN.setLatitude(Double.NaN);
	    coordsNaN.setLongitude(Double.NaN);
	    System.out.println(coordsNaN.toString(CoordinateFormat.DM));
	}

}