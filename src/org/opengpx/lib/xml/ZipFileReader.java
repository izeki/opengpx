package org.opengpx.lib.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ZipFileReader 
{
	
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
	public void read(String zipFileName)
	{
		try 
		{
			final GPXFileReader gpxFileReader = new GPXFileReader();
			final ZipFile zipFile = new ZipFile(zipFileName);
		
			for (Enumeration e = zipFile.entries(); e.hasMoreElements();) 
			{
				final ZipEntry zipEntry = (ZipEntry) e.nextElement();
				System.out.println("File name: " + zipEntry.getName() + "; size: " + zipEntry.getSize() + "; compressed size: " + zipEntry.getCompressedSize());
				final InputStream inputStream = zipFile.getInputStream(zipEntry);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

				// char[] buffer = new char[(int) zipEntry.getSize()];
				// final String bufferAsString = new String(buffer);
				// bufferAsString.getBytes();
				
				char[] buffer = new char[1024];
				String s = new String();
				while (inputStreamReader.read(buffer, 0, buffer.length) != -1) 
				{
					s += new String(buffer);
				}
				
				gpxFileReader.readByteArray(s.trim().getBytes());
			}
		} 
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
	}
}
