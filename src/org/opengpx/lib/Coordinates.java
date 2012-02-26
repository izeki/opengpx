package org.opengpx.lib;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengpx.lib.UnitSystem;

import android.location.Location;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Coordinates
{
	// private static final int earth_radius = 6371; // km
	private static final double metric_to_miles_factor = 1.609344;
	
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
	 * @param coordinate
	 * @param coordinateType
	 * @param coordinateFormat
	 * @return
	 */
	public static String convert(double coordinate, CoordinateType coordinateType, CoordinateFormat coordinateFormat)
	{
		final Coordinate coord = new Coordinate(coordinateType);
		coord.setD(coordinate);
		return coord.toString(coordinateFormat);
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
    /* public void getDMS(DMS latitude, DMS longitude)
    {
    	latitude = this.mcoordLatitude.getDMS();
    	longitude = this.mcoordLongitude.getDMS();
    } */

    /**
     * 
     * @param latitude
     * @param longitude
     */
    /* public void getDM(DM latitude, DM longitude)
    {
    	latitude = this.mcoordLatitude.getDM();
    	longitude = this.mcoordLongitude.getDM();
    } */

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
    public double getLatitude()
    {
    	return this.mcoordLatitude.getD();
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
    public double getLongitude()
    {
    	return this.mcoordLongitude.getD();
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
    public boolean parseFromText(String text)
    {
        boolean blnResult = false;
        final Pattern regex = Pattern.compile(coords_regexp, Pattern.DOTALL);
        final Matcher matcher = regex.matcher(text.toUpperCase());
        if (matcher.find())
        {
        	// final Hemisphere latHemisphere = Hemisphere.valueOf(matcher.group(1));
            final int latDegrees = Integer.parseInt(matcher.group(2));
            final double latMinutes = Double.parseDouble(matcher.group(3));
            
            // final Hemisphere longHemisphere = Hemisphere.valueOf(matcher.group(4));
            final int longDegrees = Integer.parseInt(matcher.group(5));
            final double longMinutes = Double.parseDouble(matcher.group(6));
                        
            // this.setDM(latHemisphere, latDegrees, latMinutes, longHemisphere, longDegrees, longMinutes);
            
            // System.out.println(String.format("%.8f %.8f", this.getLatitude(), latitude));
            // System.out.println(String.format("%.8f %.8f", this.getLongitude(), longitude));

            double latitude = latDegrees + latMinutes / 60;
            if (matcher.group(1).equals("S")) latitude *= -1;
            double longitude = longDegrees + longMinutes / 60;
            if (matcher.group(4).equals("W")) longitude *= -1;
            
            this.mcoordLatitude.setD(latitude);
            this.mcoordLongitude.setD(longitude);
            
            blnResult = true;
        }
        return blnResult;
    }

    /**
     * 
     * @param coords
     * @return
     */
    public double getDistanceTo(final Coordinates coords)
    {
    	return this.getDistanceTo(coords, UnitSystem.Metric);
    }

    /**
     * 
     * @param coords
     * @param unitSystem
     * @return
     */
    public double getDistanceTo(final Coordinates coords, final UnitSystem unitSystem)
    {
    	/*
        final double lat1 = Math.toRadians(this.mcoordLatitude.getD());
        final double long1 = Math.toRadians(this.mcoordLongitude.getD());
        final double lat2 = Math.toRadians(coords.getLatitude());
        final double long2 = Math.toRadians(coords.getLongitude());
        final double delta_lat = lat2 - lat1;
        final double delta_long = long2 - long1;
        
        final double a = Math.pow(Math.sin(delta_lat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(delta_long / 2), 2);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final double distance = (earth_radius * c);
		*/

        float[] results = new float[1];
        Location.distanceBetween(this.getLatitude(), this.getLongitude(), coords.getLatitude(), coords.getLongitude(), results);
        final double distance = results[0] / 1000;

        // System.out.println("initial bearing: " + Double.toString(results[1]));
        // System.out.println("final bearing: " + Double.toString(results[2]));

        if (unitSystem.equals(UnitSystem.Metric))
        	return distance; // km
        else
        	return (distance / metric_to_miles_factor);
    }

    /**
     * 
     * @param coords
     * @return
     */
    public double getBearingTo(final Coordinates coords)
    {
    	/*
        final double lat1 = Math.toRadians(this.mcoordLatitude.getD());
        final double long1 = Math.toRadians(this.mcoordLongitude.getD());
        final double lat2 = Math.toRadians(coords.getLatitude());
        final double long2 = Math.toRadians(coords.getLongitude());
        final double delta_long = long2 - long1;
        
        final double a = Math.sin(delta_long) * Math.cos(lat2);
        final double b = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(delta_long);
        final double bearing = Math.toDegrees(Math.atan2(a, b));
        */

    	/*
        final Location start = new Location("opengpx");
        start.setLatitude(this.getLatitude());
        start.setLongitude(this.getLongitude());
        final Location end = new Location("opengpx");
        end.setLatitude(coords.getLatitude());
        end.setLongitude(coords.getLongitude());        
        final double bearing = start.bearingTo(end);
		*/

        float[] results = new float[2];
        Location.distanceBetween(this.getLatitude(), this.getLongitude(), coords.getLatitude(), coords.getLongitude(), results);
        final float bearing = results[1];
    	
        final double bearing_normalized = (bearing + 360) % 360;

        // System.out.println("bearing_normalized: " + Double.toString(bearing_normalized));
        // System.out.println("bearing: " + Double.toString(bearing));        
        
        // testLocation();
        
        return bearing_normalized;
    }

    /**
     * 
     * @param bearing
     * @return
     */
    public Direction getDirectionForBearing(double bearing)
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
    public NavigationInfo getNavigationInfoTo(Coordinates coords) 
    {
    	return this.getNavigationInfoTo(coords, UnitSystem.Metric);
    }

    /**
     * 
     * @param coords
     * @param unitSystem
     * @return
     */
    public NavigationInfo getNavigationInfoTo(Coordinates coords, UnitSystem unitSystem) 
    {
    	final NavigationInfo navigationInfo = new NavigationInfo(unitSystem);
    	navigationInfo.distance  = this.getDistanceTo(coords, unitSystem);
    	navigationInfo.bearing = this.getBearingTo(coords);
    	navigationInfo.direction = this.getDirectionForBearing(navigationInfo.bearing);
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
    	final Double dblLatitude = this.round(this.mcoordLatitude.getD(), 5);
    	final Double dblLongitude = this.round(this.mcoordLongitude.getD(), 5);
    	
    	final Double dblLatitudeCompare = this.round(coords.getLatitude(), 5);
    	final Double dblLongitudeCompare = this.round(coords.getLongitude(), 5);
    	
    	final int intLatitudeEqual = dblLatitude.compareTo(dblLatitudeCompare);
    	final int intLongitudeEqual = dblLongitude.compareTo(dblLongitudeCompare);

    	return ((intLatitudeEqual == 0) && (intLongitudeEqual == 0));
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
	@Override 
	public String toString()
	{
		return this.toString(CoordinateFormat.D);
	}

	/**
	 * 
	 */
	public static void testLocation()
	{
        Location l = new Location("opengpx");
        final Coordinates c1 = new Coordinates();
        
        c1.parseFromText("N 46¡ 35.596 E 014¡ 16.394");
        System.out.println("lat: " + Double.toString(c1.getLatitude()));
        System.out.println("long: " + Double.toString(c1.getLongitude()));
        l.setLatitude(c1.getLatitude());
        l.setLongitude(c1.getLongitude());
        System.out.println(Location.convert(l.getLatitude(), Location.FORMAT_DEGREES));
        System.out.println(Location.convert(l.getLongitude(), Location.FORMAT_DEGREES));
        System.out.println(Location.convert(l.getLatitude(), Location.FORMAT_MINUTES));
        System.out.println(Location.convert(l.getLongitude(), Location.FORMAT_MINUTES));
        System.out.println(Location.convert(l.getLatitude(), Location.FORMAT_SECONDS));
        System.out.println(Location.convert(l.getLongitude(), Location.FORMAT_SECONDS));
        
        c1.parseFromText("S 16¡ 26.313 W 039¡ 03.844");
        System.out.println("lat: " + Double.toString(c1.getLatitude()));
        System.out.println("long: " + Double.toString(c1.getLongitude()));
        l.setLatitude(c1.getLatitude());
        l.setLongitude(c1.getLongitude());
        System.out.println(Location.convert(l.getLatitude(), Location.FORMAT_DEGREES));
        System.out.println(Location.convert(l.getLongitude(), Location.FORMAT_DEGREES));
        System.out.println(Location.convert(l.getLatitude(), Location.FORMAT_MINUTES));
        System.out.println(Location.convert(l.getLongitude(), Location.FORMAT_MINUTES));
        System.out.println(Location.convert(l.getLatitude(), Location.FORMAT_SECONDS));
        System.out.println(Location.convert(l.getLongitude(), Location.FORMAT_SECONDS));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{

		final Coordinates coords = new Coordinates();
	    coords.setD(46.61123, 13.89985);
	    System.out.println(coords);
	    System.out.println(coords.toString(CoordinateFormat.DMS));
	    // System.out.println(coords.DMS);
	    System.out.println(coords.getLongitude());

	    final Coordinates coordsA = new Coordinates();
	    coordsA.setDM(Hemisphere.N, 46, 35.595, Hemisphere.E, 14, 16.394);
	    final Coordinates coordsB = new Coordinates();
	    coordsB.setDM(Hemisphere.N, 46, 35.664, Hemisphere.E, 14, 16.057);
	    System.out.println(coordsA.getDistanceTo(coordsB));
		System.out.println(coordsA.getBearingTo(coordsB));
	    final Coordinates coordsC = new Coordinates();
	    coordsC.setDM(Hemisphere.N, 46, 35.664, Hemisphere.E, 14, 16.057);
		System.out.println(coordsB.equals(coordsC));
		final NavigationInfo navigationInfo = coordsA.getNavigationInfoTo(coordsB);
		System.out.println(String.format("%.16f %.16f %s", navigationInfo.distance, navigationInfo.bearing, navigationInfo.direction));
		String[] arrNavigationInfo = navigationInfo.toStringArray();
		System.out.println(String.format("%s %s %s", arrNavigationInfo[0], arrNavigationInfo[1], arrNavigationInfo[2]));

	    final Coordinates coordsT = new Coordinates();
	    System.out.println(coordsT.parseFromText("N 46¡ 32.329 E 014¡ 30.535 "));
	    System.out.println(coordsT.toString(CoordinateFormat.DM));

	    final Coordinates coordsNaN = new Coordinates();
	    coordsNaN.setLatitude(Double.NaN);
	    coordsNaN.setLongitude(Double.NaN);
	    System.out.println(coordsNaN.toString(CoordinateFormat.DM));
	}

}
