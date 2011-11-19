package org.opengpx.lib.xml;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

	private String mBaseOutputFolder;
	
	private static final Logger mLogger = LoggerFactory.getLogger(ZipFileReader.class);

	
	/**
	 * 
	 */
	public ZipFileReader(String baseOutputFolder)
	{
		this.mBaseOutputFolder = baseOutputFolder;
	}
	
	/**
	 * 
	 * @param zipFileName
	 */
	public Boolean read(String zipFileName)
	{
		try 
		{
			final ZipFile zipFile = new ZipFile(zipFileName);
		
			for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) 
			{
				final ZipEntry zipEntry = (ZipEntry) e.nextElement();
				mLogger.debug("File name: " + zipEntry.getName() + "; size: " + zipEntry.getSize() + "; compressed size: " + zipEntry.getCompressedSize());

				final String filename = this.getFilename(zipEntry.getName());

				if (filename.endsWith("gpx") || filename.endsWith("loc"))
				{
					final ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		            final InputStream in = zipFile.getInputStream(zipEntry); 

		            byte[] tempBuffer = new byte[1024];
		            int read;
		            while (-1 != (read = in.read(tempBuffer))) { 
		                out.write(tempBuffer, 0, read); 
		            } 
		            in.close();
		            
		            final byte[] buffer = out.toByteArray();
		            out.close();
					
					// final byte[] buffer = new byte[(int) zipEntry.getSize()];
					// final InputStream inputStream = zipFile.getInputStream(zipEntry);
					// inputStream.read(buffer, 0, buffer.length);
					// inputStream.close();

					// final InputStreamReader isr = new InputStreamReader(zipFile.getInputStream(zipEntry), "UTF-8");
					// final BufferedReader br = new BufferedReader(isr);

					if (filename.endsWith("gpx"))
					{
						final GPXFileReader gpxFileReader = new GPXFileReader();
						gpxFileReader.readByteArray(buffer);
					} 
					else if (filename.endsWith("loc"))
					{
						final LOCFileReader locFileReader = new LOCFileReader();
						locFileReader.readByteArray(buffer);
					}
				} 
				else if (filename.endsWith("txt"))
				{
					final InputStream inputStream = zipFile.getInputStream(zipEntry);
					final String targetFilename = String.format("%s%s%s", this.mBaseOutputFolder, File.separator, filename);
					this.saveInputStreamToFile(inputStream, targetFilename);
					inputStream.close();
				}
				else if (filename.endsWith("jpg") || filename.endsWith("png"))
				{
					final InputStream inputStream = zipFile.getInputStream(zipEntry);
					final String targetFilename = String.format("%s%s%s%s%s", this.mBaseOutputFolder, File.separator, "spoiler", File.separator, filename);
					this.saveInputStreamToFile(inputStream, targetFilename);					
					inputStream.close();
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
	
	/**
	 * 
	 * @param zipEntryName
	 * @return
	 */
	private String getFilename(String zipEntryName)
	{
		final File file = new File(zipEntryName);
		return file.getName().toLowerCase();
	}
	
	/**
	 * 
	 * @param inputStream
	 * @param targetFilename
	 * @throws IOException
	 */
	private void saveInputStreamToFile(final InputStream inputStream, final String targetFilename) throws IOException
	{
		mLogger.debug(String.format("Saving inputStream to file %s ...", targetFilename));
		
	    try
	    {
			final File outputFile = new File(targetFilename);
			final BufferedOutputStream outputStream  = new BufferedOutputStream(new FileOutputStream(outputFile), 2048);
			byte[] buffer = new byte[32 * 1024];
			int bytesRead = 0;
			while ((bytesRead = inputStream.read(buffer)) != -1)
			{
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
	    }
	    catch (Exception e)
	    {
	      throw new IOException(e.toString());
	    }
	}
}
