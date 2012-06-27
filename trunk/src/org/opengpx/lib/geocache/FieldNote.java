package org.opengpx.lib.geocache;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class FieldNote
{
	public String gcId;
	public String gcName;
	public Date noteTime;
	public LogType logType;
	public String logText;

	private static final String DATE_FORMAT_STRING = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static char SINGLE_QUOTE = 39;

	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	public enum LogType
	{
		FOUND("Found it", "log_found_it.gif"),
		NOT_FOUND("Didn" + SINGLE_QUOTE + "t find it", "log_didnt_find_it.gif"),
		WRITE_NOTE("Write note", "log_write_note.gif"),
		ATTENDED("Attended", "log_attended.gif"),
		WEBCAM_TAKEN("Webcam photo taken", "log_webcam_photo_taken.gif"),
		PRIVATE("Private note", "log_write_note.gif");

		/**
		 * This map is a workaround for mIconFilename being null on Android
		 */
		private static final Map<LogType, String> logTypeFilenameMap = new HashMap<LogType, String>() 
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = -323641353621470338L;

			{
				put(LogType.FOUND, "log_found_it.gif");
				put(LogType.NOT_FOUND, "log_didnt_find_it.gif");
				put(LogType.WRITE_NOTE, "log_write_note.gif");
				put(LogType.ATTENDED, "log_attended.gif");
				put(LogType.WEBCAM_TAKEN, "log_webcam_photo_taken.gif");
				put(LogType.PRIVATE, "log_write_note.gif");
			}
		};
		
		private final String mText;
		@SuppressWarnings("unused")
		private final String mIconFilename; // This should be used, but doesn't work
		
		/**
		 * 
		 * @param text
		 */
		private LogType(final String text, final String iconFilename)
		{
			this.mText = text;
			this.mIconFilename = iconFilename;
		}

		/**
		 * 
		 */
		public String toString()
		{
			// FIXME: this is a workaround. logType.toString() returns an invalid
			// character for single quote in "Didn't find it"
			if (this == LogType.NOT_FOUND)
			{
				return "Didn't find it";
			}
			else
			{
				return this.mText;
			}
		}

		/**
		 * 
		 * @param encoding
		 * @return
		 * @throws UnsupportedEncodingException
		 */
		public byte[] getBytes(String encoding) throws UnsupportedEncodingException
		{
			return this.mText.getBytes(encoding);
		}
		
		/**
		 * 
		 * @return
		 */
		public String getIconFilename()
		{
			// Note: this.mIconFilename is null on Android
			// return this.mIconFilename;
			
			// This is the workaround
			return logTypeFilenameMap.get(this);
		}
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static LogType getLogTypeFromString(String type)
	{
		for (LogType lt : LogType.values())
		{
			if (lt.mText.equalsIgnoreCase(type)) return lt;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDateAsISOString()
	{
		final DateFormat iSODateFormat = new SimpleDateFormat (DATE_FORMAT_STRING);
		iSODateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return iSODateFormat.format(noteTime);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		final FieldNote fn = new FieldNote();
		fn.gcId = "GC123456";
		fn.gcName = "Test cache";
		fn.logType = LogType.NOT_FOUND;
		
		System.out.println(fn);
		System.out.println(fn.logType.toString());
		System.out.println(fn.logType.getIconFilename());
		
		LogType l = FieldNote.getLogTypeFromString("write note");
		System.out.println(l.toString());
		System.out.println(l.getIconFilename());		
	}
}
