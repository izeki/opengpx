package org.opengpx.console;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.CacheIndexItem;
import org.opengpx.lib.geocache.GCVote;
import org.opengpx.lib.xml.GCVoteReader;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class DatabaseManagementTool 
{

	private static final String DEFAULT_DATABASE_FILENAME = "database.db4o";
	private static final String VERSION = "0.6.0";

	private CacheDatabase mCacheDatabase;

	/**
	 * 
	 */
	public DatabaseManagementTool()
	{
		this.showProgramHeader();
	}
	
	/**
	 * 
	 * @param filename
	 */
	public void openDb(String filename)
	{
		this.mCacheDatabase = CacheDatabase.getInstance();
		this.mCacheDatabase.setDatabaseFolder(".");
		this.mCacheDatabase.openDatabase(filename);
	}
	
	/**
	 * 
	 */
	public void closeDb()
	{
		this.mCacheDatabase.close();		
	}

	/**
	 * 
	 */
	private void showProgramHeader()
	{
		System.out.println(String.format("\nOpenGPX Database Management Tool Ver. %s\n", VERSION));
	}
	
	/**
	 * 
	 */
	public void showUsage()
	{
		// org.classes.main.class.getProtectionDomain()		.getCodeSource().getLocation().toURI());
		File jarFile = new File(DatabaseManagementTool.class.getProtectionDomain().getCodeSource().getLocation().toString());
		
		StringBuilder sbUsage = new StringBuilder();
		sbUsage.append(String.format("Usage: %s -l|-i|-d|-a [directory]|-v|-s [code]\n\n", jarFile.getName()));
		sbUsage.append("Commands:\n\n");
		sbUsage.append("   -a      add all caches (gpx/loc) from a directory\n");
		sbUsage.append("   -d      delete all caches in database.\n");
		sbUsage.append("   -i      database information.\n");
		sbUsage.append("   -l      list all caches\n");
		sbUsage.append("   -s      show cache detail\n");		
		sbUsage.append("   -v      add votes (GCVote) for all caches\n");		
		System.out.println(sbUsage.toString());
	}

	/**
	 * 
	 */
	public void showDatabaseInfo()
	{
		System.out.println(this.mCacheDatabase.getInformation());
	}

	/**
	 * 
	 */
	public void addCaches(String strFileOrFolder)
	{
		this.mCacheDatabase.setBackupFolder("");
		File fileOrFolder = new File(strFileOrFolder);
		System.out.println(String.format("Importing files from %s ...", strFileOrFolder));
		if (fileOrFolder.isDirectory())
		{
			this.mCacheDatabase.addGpxFolder(strFileOrFolder);
			this.mCacheDatabase.readXmlFiles(true);
		}
		System.out.println("\nDone.");
	}

	/**
	 * 
	 */
	public void clearDatabase()
	{
		this.mCacheDatabase.clear();
		System.out.println("Done.");
	}

	/**
	 * 
	 */
	public void listCaches()
	{
		this.mCacheDatabase.readCacheIndex();
		for (String strCacheCode : this.mCacheDatabase.getCacheCodes())
		{
			CacheIndexItem cii = this.mCacheDatabase.getCacheIndexItem(strCacheCode);
			System.out.println(String.format("%s: %s (D/T/V: %.1f, %.1f, %.2f)", cii.code, cii.name, cii.difficulty, cii.terrain, cii.vote));
		}
		System.out.println("Done.");
	}

	/**
	 * 
	 */
	public void addGCVotes()
	{
		System.out.println(String.format("Adding cache codes (%d at a time with a pause of %d millisecs) - this may take a while.", GCVoteReader.DOWNLOAD_PACKET_SIZE, GCVoteReader.PACKET_SLEEP_TIME));
		// Get a list of all existing cache codes
		this.mCacheDatabase.readCacheIndex();
		ArrayList<String> alCacheCodes = this.mCacheDatabase.getCacheCodes();
		// Get votes
		GCVoteReader gcvr = new GCVoteReader();
		HashMap<String, GCVote> gcVotes = gcvr.getVotes(alCacheCodes);
		// Add votes to database
		this.mCacheDatabase.addCacheVotes(gcVotes);
		System.out.println("Done.");
	}
	
	/**
	 * 
	 * @param cacheCode
	 */
	public void showCache(String cacheCode)
	{
		this.mCacheDatabase.readCacheIndex();
		Cache cache = this.mCacheDatabase.getCache(cacheCode);
		System.out.println(cache);
		
		// Text txt = new Text();
		// txt.setPlainText(cache.longDescription);
		
		// ArrayList<Coordinates> alExtractedCoordinates = txt.ExtractCoordinates();
		// System.out.println(alExtractedCoordinates);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		DatabaseManagementTool odbmgt = new DatabaseManagementTool();
		
		if (args.length < 1)
		{
			odbmgt.showUsage();
		}
		else
		{
			odbmgt.openDb(DEFAULT_DATABASE_FILENAME);
			
			String strCommand = args[0].toLowerCase();
			
			if (strCommand.equals("-i"))
			{
				odbmgt.showDatabaseInfo();
			}
			else if (strCommand.equals("-a"))
			{
				if (args.length == 2)
				{
					odbmgt.addCaches(args[1]);
				}
				else
				{
					System.out.println("directory required.");
				}
			}
			else if (strCommand.equals("-d"))
			{
				odbmgt.clearDatabase();
			}
			else if (strCommand.equals("-l"))
			{
				odbmgt.listCaches();
			}
			else if (strCommand.equals("-v"))
			{
				odbmgt.addGCVotes();
			}
			else if (strCommand.equals("-s"))
			{
				if (args.length == 2)
				{
					odbmgt.showCache(args[1]);
				}
				else
				{
					System.out.println("cache code required.");
				}
			}
			
			odbmgt.closeDb();
			
		}
	}	
}
