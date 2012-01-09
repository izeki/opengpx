package org.opengpx.lib.geocache;

import java.util.Date;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class LogEntry
{

	public String id = "";
	public Date time = null;
	public String finder;
	public String text = "";
	public double latitude;
	public double longitude;

	private LogType mType = LogType.Unknown;

	/**
	 * 
	 */
	public LogEntry()
	{
	}
	
	/**
	 * 
	 * @param text
	 */
	public void parseTypeString(String text)
	{
		this.mType = LogType.parseString(text);
	}
		
	/**
	 * 
	 * @return
	 */
	public LogType getType()
	{
		return this.mType;
	}
	
	/**
	 * 
	 * @param logType
	 */
	public void setType(LogType logType)
	{
		this.mType = logType;
	}
	
	/**
	 * 
	 */
	@Override public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Type: %s\n", this.mType));
		sb.append(String.format("Finder: %s\n", this.finder));
		sb.append(String.format("Date: %s\n", this.time));
		return sb.toString();
	}
}
