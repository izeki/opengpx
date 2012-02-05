package org.opengpx.lib;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Coordinate
{

	private double mdblValue;
	private CoordinateType mCoordinateType;
	
	private final static String DEGREE_CHAR = "\u00B0";

	/**
	 * 
	 * @param type
	 */
	public Coordinate(CoordinateType type)
	{
		this.mdblValue = 0;
		this.mCoordinateType = type;
	}
	
	/**
	 * 
	 * @param type
	 * @param coordinate
	 */
	public Coordinate(CoordinateType type, double coordinate)
	{
		this.mdblValue = coordinate;
		this.mCoordinateType = type;
	}

	/**
	 * 
	 * @param value
	 */
	public void setD(double value)
	{
		this.mdblValue = value * 3600;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getD()
	{
		return this.mdblValue / 3600;
	}
	
	/**
	 * 
	 * @param dm
	 */
	public void setDM(DM dm)
	{
		this.setDM(dm.hemisphere, dm.degrees, dm.minutes);
	}
	
	/**
	 * 
	 * @param hemisphere
	 * @param degrees
	 * @param minutes
	 */
	public void setDM(Hemisphere hemisphere, int degrees, double minutes)
	{
        if ((hemisphere == Hemisphere.N) || (hemisphere == Hemisphere.S))
            this.mCoordinateType = CoordinateType.Latitude;
        else
            this.mCoordinateType = CoordinateType.Longitude;
        this.mdblValue = degrees * 3600 + minutes * 60;
        if ((hemisphere == Hemisphere.S) || (hemisphere == Hemisphere.W))
            this.mdblValue *= -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public DM getDM()
	{
		DM dm = new DM();
        if (this.mCoordinateType == CoordinateType.Latitude)
        	dm.hemisphere = (this.mdblValue < 0) ? Hemisphere.S : Hemisphere.N;
        else
        	dm.hemisphere = (this.mdblValue < 0) ? Hemisphere.W : Hemisphere.E;

        final double dblAbsValue = Math.abs(this.mdblValue);
        dm.degrees = (int) Math.floor(dblAbsValue / 3600);
        dm.minutes = (dblAbsValue - (dm.degrees * 3600)) / 60;
        return dm;
	}
	
	/**
	 * 
	 * @param dms
	 */
	public void setDMS(DMS dms)
	{
		this.setDMS(dms.hemisphere, dms.degrees, dms.minutes, dms.seconds);
	}
	
	/**
	 * 
	 * @param hemisphere
	 * @param degrees
	 * @param minutes
	 * @param seconds
	 */
	public void setDMS(Hemisphere hemisphere, int degrees, int minutes, double seconds)
	{	
        if ((hemisphere == Hemisphere.N) || (hemisphere == Hemisphere.S))
        	this.mCoordinateType = CoordinateType.Latitude;
        else
        	this.mCoordinateType = CoordinateType.Longitude;
        this.mdblValue = degrees * 3600 + minutes * 60 + seconds;
        if ((hemisphere == Hemisphere.S) || (hemisphere == Hemisphere.W))
        	this.mdblValue *= -1;
	}
	
	/**
	 * 
	 * @return
	 */
	public DMS getDMS()
	{
		DMS dms = new DMS();
	    if (this.mCoordinateType == CoordinateType.Latitude)
	    	dms.hemisphere = (this.mdblValue < 0) ? Hemisphere.S : Hemisphere.N;
        else
        	dms.hemisphere = (this.mdblValue < 0) ? Hemisphere.W : Hemisphere.E;

        final double dblAbsValue = Math.abs(this.mdblValue);            
        dms.degrees = (int) Math.floor(dblAbsValue / 3600);
        dms.minutes = (int) Math.floor((dblAbsValue - (dms.degrees * 3600)) / 60);
        dms.seconds = (dblAbsValue - (dms.degrees * 3600) - (dms.minutes * 60));
        return dms;
	}
	
	/**
	 * 
	 */
	@Override public String toString()
	{
		return this.toString(CoordinateFormat.D);
	}
	
	/**
	 * 
	 * @param coordinateFormat
	 * @return
	 */
	public String toString(CoordinateFormat coordinateFormat)
	{
        if ((Double.isNaN(this.mdblValue)) || (this.mdblValue == 0))
        {
        	return this.getNaNString(coordinateFormat);
        }
        else
        {
            if (coordinateFormat == CoordinateFormat.DM)
            {
            	// Make sure that we get coordinates with . (not , in german locale)
            	DecimalFormat decFormatMinutes = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            	decFormatMinutes.applyPattern("00.000");
            	
            	DM dm = this.getDM();
            	
                if ((dm.hemisphere == Hemisphere.E) || (dm.hemisphere == Hemisphere.W))
                {
                	return String.format("%s %03d%s %s\'", dm.hemisphere.toString(), dm.degrees, DEGREE_CHAR, decFormatMinutes.format(dm.minutes));
                	// return String.format("%s %03d%s %5.3f\'", dm.hemisphere.toString(), dm.degrees, DEGREE_CHAR, dm.minutes);
                }
                else
                {
                	return String.format("%s %02d%s %s\'", dm.hemisphere.toString(), dm.degrees, DEGREE_CHAR, decFormatMinutes.format(dm.minutes));
                	// return String.format("%s %02d%s %5.3f\'", dm.hemisphere.toString(), dm.degrees, DEGREE_CHAR, dm.minutes);
                }
            }
            else if (coordinateFormat == CoordinateFormat.DMS)
            {
            	DecimalFormat decFormatSeconds = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            	decFormatSeconds.applyPattern("00.0000");

            	DMS dms = this.getDMS();
                if ((dms.hemisphere == Hemisphere.E) || (dms.hemisphere == Hemisphere.W))
                {
                    return String.format("%s %03d%s %2d\' %s\"", dms.hemisphere, dms.degrees, DEGREE_CHAR, dms.minutes, decFormatSeconds.format(dms.seconds));
                    // return String.format("%s %03d%s %2d\' %2.4f\"", dms.hemisphere, dms.degrees, DEGREE_CHAR, dms.minutes, dms.seconds);
                }
                else
                {
                    return String.format("%s %02d%s %2d\' %s\"", dms.hemisphere, dms.degrees, DEGREE_CHAR, dms.minutes, decFormatSeconds.format(dms.seconds));
                    // return String.format("%s %02d%s %2d\' %2.4f\"", dms.hemisphere, dms.degrees, DEGREE_CHAR, dms.minutes, dms.seconds);
                }
            }
            else
            {
            	DecimalFormat decFormat = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            	decFormat.applyPattern("0.00000");
                return decFormat.format(this.getD());
                // return String.format("%.5f", this.getD());
            }
        }
	}

	/**
	 * 
	 * @param coordinateFormat
	 * @return
	 */
	private String getNaNString(CoordinateFormat coordinateFormat)
	{
		if (coordinateFormat == CoordinateFormat.DM)
			return String.format("- ---%s --.---'", DEGREE_CHAR);
		else if (coordinateFormat == CoordinateFormat.DMS)
			return String.format("- ---%s --' --.----\"", DEGREE_CHAR);
		else
			return "--.-----";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		Coordinate coord = new Coordinate(CoordinateType.Latitude);
		coord.setD(46.61123);
		System.out.println(coord.getD());
		System.out.println(coord);
		System.out.println((float) coord.getD());
		System.out.println(coord.toString(CoordinateFormat.DM));
		System.out.println(coord.toString(CoordinateFormat.DMS));
		System.out.println(coord.getDMS());
	    coord.setD(Double.NaN);
	    System.out.println(coord);
	    System.out.println(coord.toString(CoordinateFormat.DM));
	    System.out.println(coord.toString(CoordinateFormat.DMS));
	    System.out.println(coord.getDMS());
	}

}
