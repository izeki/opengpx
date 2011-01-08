package org.opengpx.lib.geocache;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FieldNote
{
	public String		gcId;
	public Date			noteTime;
	public LogType	logType;
	public String		logText;
	
	private static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public enum LogType
	{
		FOUND("Found it"),
		NOT_FOUND("Didn’t find it"),
		WRITE_NOTE("Write note"),
		ATTENDED("Attended"),
		WEBCAM_TAKEN("Webcam photo taken"),
		PRIVATE("Private note");

		public String	text;

		LogType(String text)
		{
			this.text = text;
		}
	}
	
	public static LogType getLogTypeFromString(String type)
	{
		for (LogType l : LogType.values())
		{
			if (l.text.equalsIgnoreCase(type)) return l;
		}
		
		return null;
	}
	
	public String getDateAsISOString()
	{
		final DateFormat iSODateFormat = new SimpleDateFormat (DATE_FORMAT_STRING);
		iSODateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return iSODateFormat.format(noteTime);
	}
}
