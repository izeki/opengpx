package org.opengpx;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class SpoilerImageFilter 
{
	private ArrayList<String> mFilenames;
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	class ImageFilter implements FilenameFilter 
	{
	    public boolean accept(File dir, String name) 
	    {
	    	final String strLowerCaseName = name.toLowerCase();
	    	final Boolean blnHeaderCorrect = (!name.startsWith("."));
	    	final Boolean blnExtensionCorrect = (strLowerCaseName.endsWith(".jpg") || (strLowerCaseName.endsWith(".png")));
	        return (blnExtensionCorrect && blnHeaderCorrect);
	    }
	}

	/**
	 * 
	 * @param cacheName
	 * @param cacheCode
	 */
	public SpoilerImageFilter(String path, String cacheName, String cacheCode)
	{
    	// Add cache name words and cachecode to keyword list
    	final ArrayList<String> alKeywords = new ArrayList<String>(Arrays.asList(cacheName.toLowerCase().split(" ")));
    	alKeywords.add(cacheCode.toLowerCase());

		final File filePath = new File(path);
		this.mFilenames = new ArrayList<String>();
		if (filePath.isDirectory())
		{
			final String[] arrFilenames = filePath.list(new ImageFilter());
			for (String strFilename : arrFilenames)
			{
				final String strLowerCaseFilename = strFilename.toLowerCase();
				for (String strKeyword: alKeywords)
				{
					if (strLowerCaseFilename.contains(strKeyword))
					{		
						final String strFullFilename = String.format("%s%s%s", path, File.separator, strFilename);
						if (!this.mFilenames.contains(strFullFilename))
						{
							this.mFilenames.add(strFullFilename);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getFilenames()
	{
		return this.mFilenames;
	}
}
