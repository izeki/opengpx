package org.opengpx.lib.xml;

import java.util.HashMap;
import java.util.Stack;

import org.opengpx.lib.geocache.GCVote;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author preisl
 *
 *
 * Example output:
 * 
 * <votes userName='' currentVersion='2.0b' securityState='locked' loggedIn='false'>
 * <vote userName='' cacheId='425aa6bc-42fe-40b2-8a48-829e76b3ebc8' voteMedian='4' voteAvg='4.40000' voteCnt='5' voteUser='0' waypoint='GC1QMYJ' vote1='0' vote2='0' vote3='0' vote4='3' vote5='2' />
 * <vote userName='' cacheId='aae82360-a2fc-460f-b775-951b4a128a14' voteMedian='3' voteAvg='3.00000' voteCnt='11' voteUser='0' waypoint='GCXBZ0' vote1='0' vote2='1' vote3='9' vote4='1' vote5='0' />
 * <errorstring></errorstring>
 * </votes>
 */
public class GCVoteHandler extends DefaultHandler 
{

	private Stack<String> mElementStack = new Stack<String>();
	private StringBuilder builder = new StringBuilder();

	private HashMap<String, GCVote> mhmGCVotes = new HashMap<String, GCVote>();
	
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
    	
    	// System.out.println(localName);
    	
		this.mElementStack.push(localName);
		if (localName.equals("votes"))
		{
			// Ignore votes elements
		}
		else if (localName.equals("vote")) 
		{
			// Create a new vote
			final GCVote gcVote = new GCVote();
			/* for (int i = 0; i < attributes.getLength(); i++)
			{
				System.out.println(attributes.getLocalName(i));
				System.out.println(attributes.getValue(i));
			} */
			gcVote.userName = attributes.getValue("userName");
			gcVote.cacheId = attributes.getValue("cacheId");
			gcVote.voteMedian = Float.parseFloat(attributes.getValue("voteMedian"));
			gcVote.voteAverage = Float.parseFloat(attributes.getValue("voteAvg"));
			gcVote.voteCount = Integer.parseInt(attributes.getValue("voteCnt"));
			gcVote.voteUser = Integer.parseInt(attributes.getValue("voteUser"));
			gcVote.waypoint = attributes.getValue("waypoint");
			gcVote.vote1 = Integer.parseInt(attributes.getValue("vote1"));
			gcVote.vote2 = Integer.parseInt(attributes.getValue("vote2"));
			gcVote.vote3 = Integer.parseInt(attributes.getValue("vote3"));
			gcVote.vote4 = Integer.parseInt(attributes.getValue("vote4"));
			gcVote.vote5 = Integer.parseInt(attributes.getValue("vote5"));
			// System.out.println("RawVotes: " + attributes.getValue("rawVotes"));
			gcVote.setRawVotes(attributes.getValue("rawVotes"));
			// Add vote to list of votes
			this.mhmGCVotes.put(gcVote.waypoint, gcVote);
		} 
    }

    /** Gets be called on closing tags like: 
     * </tag> */ 
    @Override 
    public void endElement(String uri, String localName, String qName) throws SAXException 
    { 
    	super.endElement(uri, localName, qName);
    	
		final String strElementName = this.mElementStack.pop();
		// String strNodeValue = builder.toString().trim();

		if (this.mElementStack.size() > 0)
		{
			final String strParentNode = this.mElementStack.peek();
			if (strParentNode.equals("votes"))
			{
				if (strElementName.equals("vote"))
				{
					// Nothing to do here ...
				}
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
    
    /**
     * 
     * @return
     */
    public HashMap<String, GCVote> getVotes()
    {
    	return this.mhmGCVotes;
    }

}
