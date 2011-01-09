package org.opengpx.lib.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/*
* Simple utilities to return the stack trace of an
* exception as a String.
*/

public class StackTraceUtil 
{
	/**
	 * 
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(Throwable aThrowable) 
	{
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
}
