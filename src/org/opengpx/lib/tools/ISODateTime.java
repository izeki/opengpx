package org.opengpx.lib.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ISODateTime {

	private static DateFormat mdfISO8601Local = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss");
	
	private static final Logger mLogger = LoggerFactory.getLogger(ISODateTime.class);

	/**
	 * 
	 * @param isoDateString
	 * @return
	 */
	public static Date parseString(String isoDateString)
	{
		Date dateTime = null;
		try 
		{
			dateTime = mdfISO8601Local.parse(isoDateString);
		} 
		catch (ParseException ex)
		{
			mLogger.error("Unable to parse ISO8601 date '" + isoDateString + "'");			
			ex.printStackTrace();
		}
		return dateTime;
	}	
}
