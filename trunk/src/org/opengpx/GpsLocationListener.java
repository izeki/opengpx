package org.opengpx;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * 
 * @author Martin Preishuber
 * 
 */
public class GpsLocationListener implements LocationListener
{
	// these values should be way enough for our purpose
	private static final long									GPS_UPDATE_TIME_MSEC	= 1000L;	// 1 sec.
	private static final float									GPS_MIN_DISTANCE		= 0f; // 100 meters

	private LocationManager										locationManager;
	private Location											gpsLocation;
	private Location											networkLocation;
	private boolean												isEnabled				= false;

	private static final Logger mLogger = LoggerFactory.getLogger(GpsLocationListener.class);

	public GpsLocationListener(LocationManager locationManager)
	{
		this.locationManager = locationManager;
		enableListener();
	}

	public void disableListener()
	{
		if (isEnabled)
		{
			locationManager.removeUpdates(this);
			isEnabled = false;
		}
	}

	public void enableListener()
	{
		if (!isEnabled)
		{
			for (String provider : locationManager.getAllProviders())
			{
				mLogger.debug("Requesting location updates (provider: " + provider + ")");
				locationManager.requestLocationUpdates(provider, GPS_UPDATE_TIME_MSEC, GPS_MIN_DISTANCE, this);
			}

			// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME_MSEC, GPS_MIN_DISTANCE, this);
			// locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_UPDATE_TIME_MSEC, GPS_MIN_DISTANCE, this);
			isEnabled = true;
		}
	}

	public boolean isEnabled()
	{
		return isEnabled;
	}

	public Location getLastKnownLocation()
	{
		GregorianCalendar nowUtc = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		// nowUtc.roll(Calendar.MINUTE, -5);
		nowUtc.add(Calendar.MINUTE, -5);
		long minLocationAge = nowUtc.getTimeInMillis();
		
		if (gpsLocation != null && gpsLocation.getTime() > minLocationAge)
		{
			return gpsLocation;
		}
		else if (networkLocation != null && networkLocation.getTime() > minLocationAge)
		{
			return networkLocation;
		}
		else
		{
			return null;
		}
	}

	/**
	 * 
	 */
	public void onLocationChanged(Location location)
	{
		if (location != null)
		{
			if (location.getProvider().equals(LocationManager.GPS_PROVIDER))
			{
				gpsLocation = location;
			}
			else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER))
			{
				networkLocation = location;
			}
		}
	}

	/**
	 * 
	 */
	public void onProviderDisabled(String provider)
	{
	}

	/**
	 * 
	 */
	public void onProviderEnabled(String provider)
	{
	}

	/**
	 * 
	 */
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

}
