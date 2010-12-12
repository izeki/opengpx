package org.opengpx.lib.xml;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.opengpx.lib.geocache.GCVote;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class GCVoteReader 
{
	private static final String BASE_URL_GET_VOTE = "http://dosensuche.de/GCVote/getVotes.php";
	
	public static final int DOWNLOAD_PACKET_SIZE = 20;
	public static final int PACKET_SLEEP_TIME = 1000; // milliseconds

	private String mstrUsername = "";
	private String mstrPassword = "";
	
	/**
	 * 
	 */
	public GCVoteReader()
	{
	}
	
	/**
	 * 
	 * @param username
	 */
	public GCVoteReader(String username)
	{
		this.mstrUsername = username;
	}
	
	/**
	 * 
	 * @param username
	 * @param password
	 */
	public GCVoteReader(String username, String password)
	{
		this.mstrUsername = username;
		this.mstrPassword = password;
	}
	
	/**
	 * 
	 * @param waypoint
	 */
	public HashMap<String, GCVote> getVotes(String waypoint)
	{
		final ArrayList<String> alWaypoints = new ArrayList<String>();
		alWaypoints.add(waypoint);
		return this.getVotes(alWaypoints);
	}
	
	/**
	 * 
	 * @param s
	 * @param delimiter
	 * @return
	 */
	private String join(AbstractCollection<String> s, String delimiter) 
	{
	    if (s.isEmpty()) return "";
	    Iterator<String> iter = s.iterator();
	    StringBuffer buffer = new StringBuffer(iter.next());
	    while (iter.hasNext()) buffer.append(delimiter).append(iter.next());
	    return buffer.toString();
	}
	
	/**
	 * 
	 * @param waypoints
	 */
	public HashMap<String, GCVote> getVotes(ArrayList<String> waypoints)
	{	
		final HashMap<String, GCVote> votes = new HashMap<String, GCVote>();

		final ArrayList<String> alWaypointSet = new ArrayList<String>();
		boolean blnSleepBeforeDownload = false;
		for (int i = 0; i < waypoints.size(); i++)
		{
			alWaypointSet.add(waypoints.get(i));
			if (alWaypointSet.size() == DOWNLOAD_PACKET_SIZE)
			{
				// System.out.println(String.format("Getting votes for waypoints %s", this.join(alWaypointSet, ",")));
				votes.putAll(this.getVotes(alWaypointSet, blnSleepBeforeDownload));
				alWaypointSet.clear();

				blnSleepBeforeDownload = true;
			}
		}
		if (alWaypointSet.size() > 0)
		{
			// System.out.println(String.format("Getting votes for waypoints %s", this.join(alWaypointSet, ",")));
			votes.putAll(this.getVotes(alWaypointSet, blnSleepBeforeDownload));
		}

		return votes;
	}
	
	/**
	 * 
	 * @param waypoints
	 * @param blnSleepBeforeDownload
	 * @return
	 */
	private HashMap<String, GCVote> getVotes(ArrayList<String> waypoints, boolean blnSleepBeforeDownload)
	{
		
		if (blnSleepBeforeDownload)
		{
			try 
			{
				Thread.sleep(PACKET_SLEEP_TIME);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}

		final String strWaypoints = this.join(waypoints, ",");

		try 
		{
			String strParameters = URLEncoder.encode("waypoints", "UTF-8") + "=" + URLEncoder.encode(strWaypoints, "UTF-8");
	        // Add more parameters
			if (this.mstrUsername.length() > 0)
			{
		        strParameters += "&" + URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(this.mstrUsername, "UTF-8");
		        if (this.mstrPassword.length() > 0)
		        {
			        strParameters += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(this.mstrPassword, "UTF-8");
		        }
			}

	        final URL url = new URL(BASE_URL_GET_VOTE);
	        URLConnection conn = url.openConnection();
	        conn.setDoOutput(true);
	        final OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
	        osw.write(strParameters);
	        osw.flush();

	        // Parse XML response
			final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			saxParserFactory.setNamespaceAware(true);
			final SAXParser saxParser = saxParserFactory.newSAXParser();
			final XMLReader xmlReader = saxParser.getXMLReader();
			final GCVoteHandler gcVoteHandler = new GCVoteHandler();
			xmlReader.setContentHandler(gcVoteHandler);
			xmlReader.parse(new InputSource(new InputStreamReader(conn.getInputStream())));
	        
			return gcVoteHandler.getVotes();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}		
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		GCVoteReader gcvr = new GCVoteReader();
		HashMap<String, GCVote> gcVotes = gcvr.getVotes("GC1QMYJ");
		System.out.println("Votes: " + gcVotes.size());
		System.out.println(gcVotes.get("GC1QMYJ"));
	}

}
