package org.opengpx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.net.Uri;


/**
 * 
 * @author Martin Preishuber
 *
 */
public class FileSystemIntegration
{

	private static final FileSystemIntegration mFileSystemIntegration = new FileSystemIntegration();
	private static final Logger mLogger = LoggerFactory.getLogger(FileSystemIntegration.class);

	/**
	 * 
	 */
	private FileSystemIntegration() { }
	
	/**
	 * 
	 * @param intent
	 */
	public void handleIntent(final Intent intent, final Preferences preferences)
	{
		final Uri uri = intent.getData();
		if (uri != null)
		{
			final String type = intent.getType();
			if (type.equals("application/zip"))
			{
				// Handle zip file
				final File sourceFile = new File(uri.getPath());
				if (sourceFile.exists())
				{
					final File targetFolder = new File(preferences.getDataFolder());				
					final ArrayList<String> importFolders = new ArrayList<String>(Arrays.asList(preferences.getImportPathList()));
					importFolders.add(targetFolder.getPath());
					
					// check wether the file already is in one of the importfolders
					Boolean copyFile = true;
					for (String folder : importFolders)
					{
						if (folder.length() > 0)
							if (sourceFile.getPath().contains(folder)) copyFile = false;
					}
					
					if (copyFile && targetFolder.exists())
					{
						final File targetFile = new File(targetFolder.getPath() + System.getProperty("file.separator") + sourceFile.getName());
						try 
						{
							mLogger.debug("Copy zip file " + sourceFile.getPath() + " to " + targetFile.getPath());
							this.copyFile(sourceFile, targetFile);
						} 
						catch (IOException e) 
						{
							mLogger.error(e.toString());
						}
					}
				}
			}
		}
	}
	
	private void copyFile(File sourceFile, File destFile) throws IOException 
	{
		if(!destFile.exists()) 
		{
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try 
		{
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally 
		{
			if(source != null) 
			{
				source.close();
			}
			if(destination != null) 
			{
				destination.close();
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static FileSystemIntegration getInstance()
	{
		return mFileSystemIntegration;
	}
}
