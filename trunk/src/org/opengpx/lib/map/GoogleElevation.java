package org.opengpx.lib.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengpx.lib.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class GoogleElevation 
{
	private static String URL = "http://maps.google.com/maps/api/elevation";
	private static final Logger mLogger = LoggerFactory.getLogger(GoogleElevation.class);
	private Hashtable<Location, Double> mElevationMap;
	private Boolean mChanged;
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	private class Location
	{
		Double lat;
		Double lng;
		
		/**
		 * 
		 * @param latitude
		 * @param longitude
		 */
		public Location(Double latitude, Double longitude)
		{
			this.lat = latitude;
			this.lng = longitude;
		}
		
		/**
		 * 
		 * @param location
		 * @return
		 */
		public Boolean equals(Location location)
		{
			return (this.lng.equals(location.lng) && this.lat.equals(location.lat));
		}
		
		/**
		 * 
		 */
		public String toString()
		{
			return "Latitude: " + lat.toString() + " Longitude: " + lng.toString();
		}
	}

	/**
	 * 
	 */
	public GoogleElevation()
	{		
		this.mElevationMap = new Hashtable<Location, Double>();
		this.mChanged = false;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private URL getUrl(String url)
	{
		try 
		{
			return new URL(url);
		} 
		catch (MalformedURLException mue) 
		{
			mue.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private InputStream getInputStream(URL url)
	{
		try 
		{
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// conn.addRequestProperty("Accept-encoding", "gzip");
			int responseCode = conn.getResponseCode();
			mLogger.debug(Integer.toString(responseCode) + " - " + conn.getResponseMessage());
			if (responseCode == 200) // 200 = OK 
				return conn.getInputStream();
			else
				return null;
		} 
		catch (IOException ie)
		{
			return null;
		}
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void addSearchLocation(Double latitude, Double longitude)
	{
		final Location loc = new Location(latitude, longitude);
		this.mElevationMap.put(loc, Double.NaN);
		this.mChanged = true;
	}
	
	/**
	 * 
	 */
	private void updateElevationMap()
	{
		final StringBuilder urlList = new StringBuilder();
		for (Location loc : this.mElevationMap.keySet())
		{
			final String strLatitude = loc.lat.toString().replace(",", ".");
			final String strLongitude = loc.lng.toString().replace(",", ".");
			if (urlList.length() > 0) urlList.append("|");
			urlList.append(strLatitude).append(",").append(strLongitude);
		}
		
		final String strUrl = URL + "/json?locations=" + urlList.toString() + "&sensor=false";
		mLogger.debug("Url: " + strUrl);
		final URL url = this.getUrl(strUrl);		
		InputStream inputStream = null;
		if (url != null)
			inputStream = this.getInputStream(url);
		
		if (inputStream != null)
		{
			try 
			{
				final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")), 1024);
				final StringBuilder lvResult = new StringBuilder();
				String line;
	
				try 
				{
					while ((line = rd.readLine()) != null)
					{
						lvResult.append(line + "\n");
					}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
	
				final JSONObject obj = new JSONObject(lvResult.toString());
				
				final String status = obj.getString("status");
				if (status.equals("OK"))
				{
					final JSONArray results = obj.getJSONArray("results");
					
					for (int i = 0; i < results.length(); i++)
		            {
		                final JSONObject result = results.getJSONObject(i);
		                final Double elevation = result.getDouble("elevation");
		                final JSONObject jsonLocation = result.getJSONObject("location");
		                final Location location = new Location(jsonLocation.getDouble("lat"), jsonLocation.getDouble("lng"));
		                
		                for (Location loc : mElevationMap.keySet())
		                {
		                	if (loc.equals(location))
		                	{
		                		mElevationMap.put(loc, elevation);
		                	}
		                }
		                mLogger.debug(String.format("%s %s", location.toString(), elevation.toString()));
		            }
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}		
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Double getElevation(Double latitude, Double longitude)
	{
		Double elevation = Double.NaN;
		
		if (this.mChanged)
		{
			this.updateElevationMap();
			this.mChanged = false;
		}
		
		final Location location = new Location(latitude, longitude);
		for (Location loc : this.mElevationMap.keySet())
		{
			if (location.equals(loc))
				elevation = this.mElevationMap.get(loc);
		}

		return elevation;
	}
	
	/**
	 * 
	 * @param coords
	 * @return
	 */
	public Double getElevation(Coordinates coords)
	{
		return this.getElevation(coords.getLatitude().getD(), coords.getLongitude().getD());
	}
}
