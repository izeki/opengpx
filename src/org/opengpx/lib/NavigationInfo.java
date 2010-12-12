package org.opengpx.lib;

import java.math.BigDecimal;

import org.opengpx.lib.UnitSystem;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class NavigationInfo 
{

	public Double distance;
	public Double bearing;
	public Direction direction;
	
	private UnitSystem mUnitSystem;

	/**
	 * 
	 * @param unitSystem
	 */
	public NavigationInfo(UnitSystem unitSystem)
	{
		this.mUnitSystem = unitSystem;
	}
	
	/**
	 * 
	 * @param d
	 * @param decimalPlaces
	 * @return
	 */
    private Double round(double d, int decimalPlaces)
    {
		BigDecimal bd = new BigDecimal(d);
	 	bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
 		return bd.doubleValue();
    }

    /**
     * 
     * @return
     */
	public String[] toStringArray()
	{
		String strDistance;
		String strBearing;
		String strDirection;
		
		String strLongDistance;
		String strShortDistance;
		
		if (this.mUnitSystem.equals(UnitSystem.Metric))
		{
			strLongDistance = "km";
			strShortDistance = "m";			
		}
		else
		{
			strLongDistance = "mi";
			strShortDistance = "ft";			
		}
		
        if (!Double.isNaN(distance))
        {
            if (distance >= 10)
            	strDistance = String.format("%.0f%s", this.round(distance, 0), strLongDistance);
            else if (distance >= 1)
            	strDistance = String.format("%.1f%s", this.round(distance, 1), strLongDistance);
            else
            {
            	if (this.mUnitSystem.equals(UnitSystem.Metric))
            		strDistance = String.format("%.0f%s", this.round((distance * 1000), 0), strShortDistance);
            	else
            		strDistance = String.format("%.0f%s", this.round((distance * 5280), 0), strShortDistance);
            }
        }
        else
        {
        	strDistance = String.format("---%s", strLongDistance);
        }

        if (!Double.isNaN(bearing))
        {
        	strBearing = String.format("%3d%s", this.round(bearing, 0).intValue(), "¡");
        	strDirection = direction.toString();
        }
        else
        {
        	strBearing = String.format("---%s", "¡");
        	strDirection = "--";
        }

        String[] arrResult = { strDistance, strBearing, strDirection };
		return arrResult;
	}
	
	/**
	 * 
	 */
	@Override
	public String toString()
	{
		String[] arrNavigationInfo = this.toStringArray();
		return String.format("%s (%s, %s)", arrNavigationInfo[0], arrNavigationInfo[1], arrNavigationInfo[2]);
	}
}
