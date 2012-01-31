package org.opengpx.lib.geocache;

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

	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	public enum LogType
	{
		FOUND("Found it", "log_found_it.gif"),
		NOT_FOUND("Didn’t find it", "log_didnt_find_it.gif"),
		WRITE_NOTE("Write note", "log_write_note.gif"),
		ATTENDED("Attended", "log_attended.gif"),
		WEBCAM_TAKEN("Webcam photo taken", "log_webcam_photo_taken.gif"),
		PRIVATE("Private note", "log_write_note.gif");
	
		/**
		 * This map is a workardound for mIconFilename being null on Android
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
		private final String mIconFilename;
		
		/**
		 * 
		 * @param text
		 */
		private LogType(String text, String iconFilename)
		{
			mText = text;
			mIconFilename = iconFilename;
		}

		/**
		 * 
		 */
		public String toString()
		{
			return mText;
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
		fn.logType = LogType.WRITE_NOTE;
		
		System.out.println(fn);
		System.out.println(fn.logType.toString());
		System.out.println(fn.logType.getIconFilename());
		
		LogType l = FieldNote.getLogTypeFromString("write note");
		System.out.println(l.toString());
		System.out.println(l.getIconFilename());		
	}
}
