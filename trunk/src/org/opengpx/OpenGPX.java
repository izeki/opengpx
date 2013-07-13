package org.opengpx;

import java.io.File;

import org.opengpx.Preferences;

import org.opengpx.lib.CacheDatabase;

// import com.nullwire.trace.ExceptionHandler;

import android.app.Application;
import android.widget.Toast;

/**
 * 
 * @author Martin Preishuber
 * 
 */
public class OpenGPX extends Application
{
	// private CacheDatabase mCacheDatabase;
	private boolean mblnDatabaseInitialized;
	private String mstrErrorMessage;
	private Preferences mPreferences;

	// private static final String	EXCEPTION_URL												= "http://flux.dnsdojo.org/opengpx/trace/";

	/**
	 * 
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();

		// Use some exception handler
		// ExceptionHandler.register(this, EXCEPTION_URL);

		this.initializeSettings();
		this.initializeDatabase();
	}

	/**
     * 
     */
	private void initializeSettings()
	{
		this.mPreferences = new Preferences(this);
		this.checkSettingsFolders();
	}

	/**
     * 
     */
	private void checkSettingsFolders()
	{
		// Create data folder
		final String strDataFolder = this.mPreferences.getDataFolder();
		final File fileDataPath = new File(strDataFolder);
		if (!fileDataPath.exists())
		{
			final Boolean blnDataPathCreated = fileDataPath.mkdirs();
			if (!blnDataPathCreated)
			{
				final String strMessage = String.format("Unable to create folder '%s'.", strDataFolder);
				Toast.makeText(this, strMessage, Toast.LENGTH_LONG).show();
			}
		}

		// Create spoiler path
		final String strSpoilerFolder = String.format("%s%sspoiler", strDataFolder, File.separator);
		final File fileSpoilerPath = new File(strSpoilerFolder);
		if (!fileSpoilerPath.exists())
		{
			final Boolean blnSpoilerPathCreated = fileSpoilerPath.mkdirs();
			if (!blnSpoilerPathCreated)
			{
				final String strMessage = String.format("Unable to create folder '%s'.", strSpoilerFolder);
				Toast.makeText(this, strMessage, Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
     * 
     */
	@Override
	public void onTerminate()
	{
		super.onTerminate();

		final CacheDatabase cacheDatabase = CacheDatabase.getInstance();
		if (cacheDatabase.isDatabaseOpen())
		{
			cacheDatabase.close();
			cacheDatabase.closeFieldNoteDatabase();
		}
	}

	/**
     * 
     */
	private void initializeDatabase()
	{
		final CacheDatabase cacheDatabase = CacheDatabase.getInstance();
		
		final String strDataPath = this.mPreferences.getDataFolder();
		// Set database properties
		cacheDatabase.setBaseFolder(strDataPath);
		cacheDatabase.setDatabaseFolder(String.format("%s%sdatabase", strDataPath, File.separator));
		cacheDatabase.setBackupFolder(String.format("%s%sbackup", strDataPath, File.separator));
		cacheDatabase.setSortOrder(CacheDatabase.SORT_ORDER_NAME);

		final String strDatabaseFilename = this.mPreferences.getDatabaseFilename();

		this.mblnDatabaseInitialized = cacheDatabase.openDatabase(strDatabaseFilename);
		if (!this.mblnDatabaseInitialized)
		{
			this.mstrErrorMessage = cacheDatabase.getErrorMessage();
		}
		
		if (!cacheDatabase.openFieldNoteDatabase())
		{
			this.mstrErrorMessage = cacheDatabase.getErrorMessage();			
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isDatabaseInitialized()
	{
		return this.mblnDatabaseInitialized;
	}

	/**
	 * 
	 * @return
	 */
	public String getErrorMessage()
	{
		return this.mstrErrorMessage;
	}
}
