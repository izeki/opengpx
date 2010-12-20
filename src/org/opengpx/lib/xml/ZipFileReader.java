package org.opengpx.lib.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ZipFileReader 
{

	private static final Logger mLogger = LoggerFactory.getLogger(ZipFileReader.class);

	/**
	 * 
	 */
	public ZipFileReader()
	{	
	}
	
	/**
	 * 
	 * @param zipFileName
	 */
	@SuppressWarnings("unchecked")
	public Boolean read(String zipFileName)
	{
		try 
		{
			final ZipFile zipFile = new ZipFile(zipFileName);
		
			for (Enumeration e = zipFile.entries(); e.hasMoreElements();) 
			{
				final ZipEntry zipEntry = (ZipEntry) e.nextElement();
				mLogger.debug("File name: " + zipEntry.getName() + "; size: " + zipEntry.getSize() + "; compressed size: " + zipEntry.getCompressedSize());
				final InputStream inputStream = zipFile.getInputStream(zipEntry);

				final String zipEntryName = zipEntry.getName().toLowerCase();
				if (zipEntryName.endsWith("gpx") || zipEntryName.endsWith("loc"))
				{
					final byte[] buffer = new byte[(int) zipEntry.getSize()];
					inputStream.read(buffer, 0, buffer.length);
	
					if (zipEntry.getName().toLowerCase().endsWith("gpx"))
					{
						final GPXFileReader gpxFileReader = new GPXFileReader();
						gpxFileReader.readByteArray(buffer);
					} 
					else if (zipEntry.getName().toLowerCase().endsWith("loc"))
					{
						final LOCFileReader locFileReader = new LOCFileReader();
						locFileReader.readByteArray(buffer);
					}
				}
			}
			return true;
		} 
		catch (IOException ioex) 
		{
			ioex.printStackTrace();
			return false;
		}
	}
}
