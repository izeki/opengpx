package org.opengpx.lib.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
	private Boolean mSensor = false;
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	public static class GoogleLocation
	{
		private Double mLatitude;
		private Double mLongitude;
		
		/**
		 * 
		 * @param latitude
		 * @param longitude
		 */
		public GoogleLocation(Double latitude, Double longitude)
		{
			this.mLatitude = latitude;
			this.mLongitude = longitude;
		}
		
		/**
		 * 
		 * @param location
		 * @return
		 */
		public Boolean equals(GoogleLocation location)
		{
			return (this.mLongitude.equals(location.mLongitude) && this.mLatitude.equals(location.mLatitude));
		}
		
		/**
		 * 
		 * @param latitude
		 * @param longitude
		 * @return
		 */
		public Boolean equals(Double latitude, Double longitude)
		{
			return (this.mLongitude.equals(longitude) && this.mLatitude.equals(latitude));			
		}
		
		/**
		 * 
		 */
		public String toString()
		{
			return "Latitude: " + mLatitude.toString() + " Longitude: " + mLongitude.toString();
		}
	}

	/**
	 * 
	 */
	public GoogleElevation(Boolean sensor)
	{
		this.mSensor = sensor;
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
			final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			final int responseCode = conn.getResponseCode();
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
	 */
	public Hashtable<GoogleLocation, Double> getElevation(ArrayList<GoogleLocation> locations)
	{
		final StringBuilder urlList = new StringBuilder();
		final Hashtable<GoogleLocation, Double> locationElevation = new Hashtable<GoogleLocation, Double>();

		for (GoogleLocation loc : locations)
		{
			final String strLatitude = loc.mLatitude.toString().replace(",", ".");
			final String strLongitude = loc.mLongitude.toString().replace(",", ".");
			if (urlList.length() > 0) urlList.append("|");
			urlList.append(strLatitude).append(",").append(strLongitude);
		}

		final String strUrl = URL + "/json?locations=" + urlList.toString() + "&sensor=" + this.mSensor.toString();
		mLogger.debug("Url: " + strUrl);
		final URL url = this.getUrl(strUrl);		
		InputStream inputStream = null;
		if (url != null) inputStream = this.getInputStream(url);
		
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
		                final GoogleLocation location = new GoogleLocation(jsonLocation.getDouble("lat"), jsonLocation.getDouble("lng"));
		                locationElevation.put(location, elevation);
		                mLogger.debug(String.format("%s %s", location.toString(), elevation.toString()));
		            }
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
		}
		
		return locationElevation;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	public Double getElevation(Double latitude, Double longitude)
	{
		final ArrayList<GoogleLocation> locations = new ArrayList<GoogleLocation>();
		locations.add(new GoogleLocation(latitude, longitude));
		final Hashtable<GoogleLocation, Double> result = this.getElevation(locations);
		if (result.size() == 1)
			return result.get(result.keys().nextElement());
		else
			return Double.NaN;
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
