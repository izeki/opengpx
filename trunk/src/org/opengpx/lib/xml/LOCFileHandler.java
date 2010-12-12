package org.opengpx.lib.xml;

import java.util.Calendar;
import java.util.Stack;

import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.geocache.CacheType;
import org.opengpx.lib.geocache.Waypoint;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class LOCFileHandler extends DefaultHandler 
{

	private Stack<String> mElementStack = new Stack<String>();
	private StringBuilder builder = new StringBuilder();

	private Cache mCurrentCache = null;
	private Waypoint mCurrentWaypoint = null;
	
	private CacheDatabase mCacheDatabase = CacheDatabase.getInstance();


	/**
	 * 
	 */
	@Override 
    public void startDocument() throws SAXException 
    {
		super.startDocument();		
    }

	/**
	 * 
	 */
    @Override 
    public void endDocument() throws SAXException 
    {
    	super.endDocument();
    }

    /** Gets be called on opening tags like: 
     * <tag> 
     * Can provide attribute(s), when XML was like: 
     * <tag attribute="attributeValue">*/
    @Override 
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException 
    {
    	super.startElement(uri, localName, qName, attributes);
    	
    	String strElementName = localName;
		this.mElementStack.push(strElementName);
		if (strElementName.equals("loc"))
		{
			// Ignore loc tag
		} 
		else if (strElementName.equals("waypoint")) 
		{
			// Create a new waypoint
			this.mCurrentCache = new Cache();
			// set some cache properties
			this.mCurrentCache.setType(CacheType.Unknown);
			this.mCurrentCache.terrain = 0.0;
			this.mCurrentCache.difficulty = 0.0;
			this.mCurrentCache.country = "Unknown";
			this.mCurrentCache.state = "Unknown";
			this.mCurrentCache.placedBy = "Unknown";
			this.mCurrentCache.isArchived = false;
			this.mCurrentCache.isAvailable = true;
			// Add a new waypoint
			this.mCurrentWaypoint = new Waypoint();
			this.mCurrentWaypoint.time = Calendar.getInstance().getTime();
			this.mCurrentCache.addWaypoint(this.mCurrentWaypoint);
		}
		else if (strElementName.equals("name"))
		{
			this.mCurrentCache.code = attributes.getValue("id");
			this.mCurrentWaypoint.name = attributes.getValue("id");
		}
		else if (strElementName.equals("coord"))
		{
			this.mCurrentWaypoint.latitude = Double.parseDouble(attributes.getValue("lat"));
			this.mCurrentWaypoint.longitude = Double.parseDouble(attributes.getValue("lon"));
			// this.mCurrentWaypoint.getCoordinates().setD(dblLatitude, dblLongitude);
		}
    }
    
    
    /** Gets be called on closing tags like: 
     * </tag> */ 
    @Override 
    public void endElement(String uri, String localName, String qName) throws SAXException 
    { 
    	super.endElement(uri, localName, qName);
    	
		String strElementName = this.mElementStack.pop();
		String strNodeValue = builder.toString().trim();
		
		// if (strNodeValue.length() > 0)
		if (this.mElementStack.size() > 0)
		{
			String strParentNode = this.mElementStack.peek();
			if (strParentNode.equals("loc"))
			{
				if (strElementName.equals("waypoint"))
				{
					this.mCacheDatabase.addCache(this.mCurrentCache);
					this.mCurrentCache = null;
					this.mCurrentWaypoint = null;
				}
			}
			else if (strParentNode.equals("waypoint"))
			{
				if (strElementName.equals("name"))
					this.mCurrentCache.name = strNodeValue;
				else if (strElementName.equals("type"))
					this.mCurrentWaypoint.parseTypeString(strNodeValue);
			}
		}
		
		builder.setLength(0);
    }
    
    /** Gets be called on the following structure: 
     * <tag>characters</tag> */ 
    @Override 
    public void characters(char ch[], int start, int length) throws SAXException 
    {
    	super.characters(ch, start, length);
    	builder.append(ch, start, length);
    }

}
