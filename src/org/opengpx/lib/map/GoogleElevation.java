package org.opengpx.lib.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opengpx.lib.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleElevation 
{
	private static String URL = "http://maps.google.com/maps/api/elevation";

	private static final Logger mLogger = LoggerFactory.getLogger(GoogleElevation.class);

	public GoogleElevation()
	{
		
	}
	
	public int getElevation(Coordinates coordinates)
	{
		return 0;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	private URL getUrl(String url)
	{
		try {
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
			return conn.getInputStream();
		} 
		catch (IOException ie)
		{
			return null;
		}
	}
	
	public int getElevation(Double latitude, Double longitude)
	{
		final String strUrl = URL + "/json?locations=40.714728,-73.998672&sensor=false";
		mLogger.debug("Url: " + strUrl);

		final URL url = this.getUrl(strUrl);
		final InputStream inputStream = this.getInputStream(url);
		
		try {
			
			// conn.setReadTimeout(mTimeout);
			// conn.setConnectTimeout(mTimeout);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")), 1024);
			StringBuilder lvResult = new StringBuilder();
			String line;

			try {
				while ((line = rd.readLine()) != null)
				{
					lvResult.append(line + "\n");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final JSONObject obj = new JSONObject(lvResult.toString());
			final JSONArray data = obj.getJSONArray("data");
			mLogger.debug(data.toString());
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
		}

		return 0;
	}
}
