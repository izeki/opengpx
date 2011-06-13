package org.opengpx.lib.xml;

import java.io.File;
import java.util.Date;

import org.opengpx.lib.geocache.Attribute;
import org.opengpx.lib.geocache.Cache;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.geocache.LogEntry;
import org.opengpx.lib.geocache.TravelBug;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.tools.ISODateTime;
import org.opengpx.lib.tools.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ximpleware.extended.AutoPilotHuge;
import com.ximpleware.extended.NavExceptionHuge;
import com.ximpleware.extended.VTDExceptionHuge;
import com.ximpleware.extended.VTDGenHuge;
import com.ximpleware.extended.VTDNavHuge;
import com.ximpleware.extended.XMLBuffer;

/**
 * 
 * @author Martin Preishuber
 * 
 */
public class GPXFileReader
{
	private String previousCacheCode;
	private boolean foundCacheRecord;
	private Cache	currentCache;
	private CacheDatabase cacheDatabase = CacheDatabase.getInstance();
	
	private static final Logger mLogger = LoggerFactory.getLogger(GPXFileReader.class);

	/**
	 * 
	 */
	public GPXFileReader()
	{
	}

	/**
	 * 
	 * @param fileName
	 * @throws Exception
	 */
	public Boolean read(String fileName)
	{
		boolean retVal = false;
	
		final File file = new File(fileName);
		long start = System.currentTimeMillis();

		if (file.exists())
		{
			try
			{
				final VTDGenHuge vgh = new VTDGenHuge();

				if (vgh.parseFile(fileName, true, VTDGenHuge.MEM_MAPPED))
				{
					mLogger.debug("Parsed in: " + Long.toString(System.currentTimeMillis() - start) + " msec");
					final VTDNavHuge vn = vgh.getNav();
					retVal = readData(vn);
					mLogger.debug("Completed in: " + Long.toString(System.currentTimeMillis() - start) + " msec");
				}
			}
			catch (Exception e)
			{
				mLogger.error(e.toString());
				e.printStackTrace();
				return false;
			}
		}
		return retVal;
	}

	/**
	 * 
	 * @param byteArray
	 * @return
	 */
	public Boolean readByteArray(byte[] byteArray)
	{
		boolean retVal = false;
	
		final long start = System.currentTimeMillis();

		try
		{
			final VTDGenHuge vgh = new VTDGenHuge();
			final XMLBuffer xmlBuffer = new XMLBuffer(byteArray);
			vgh.setDoc(xmlBuffer);
			vgh.parse(true);
			mLogger.debug("Parsed in: " + Long.toString(System.currentTimeMillis() - start) + " msec");
			final VTDNavHuge vn = vgh.getNav();
			retVal = readData(vn);
			mLogger.debug("Completed in: " + Long.toString(System.currentTimeMillis() - start) + " msec");
		}
		catch (Exception e)
		{
			mLogger.error(e.toString());
			e.printStackTrace();
			return false;
		}
		return retVal;
	}
	
	/**
	 * 
	 * @param vn
	 * @return
	 * @throws VTDExceptionHuge
	 */
	private boolean readData(final VTDNavHuge vn) throws VTDExceptionHuge
	{
		final AutoPilotHuge apWpt = new AutoPilotHuge(vn);

		apWpt.selectXPath("/gpx/wpt");

		while (apWpt.evalXPath() != -1)
		{
			vn.push();

			foundCacheRecord = false;
			currentCache = new Cache();
			final Waypoint wpt = new Waypoint();
			currentCache.addWaypoint(wpt, false);

			wpt.latitude = vn.parseDouble(vn.getAttrVal("lat"));
			wpt.longitude = vn.parseDouble(vn.getAttrVal("lon"));
			
			vn.toElement(VTDNavHuge.FIRST_CHILD);
			
			do
			{
				final String currentElementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
				
				if (currentElementName.equalsIgnoreCase("time"))
				{
					final String data = vn.toNormalizedString(vn.getText());
					
					if (data.equals("null"))
						wpt.time = new Date();
					else
						wpt.time = ISODateTime.parseString(vn.toNormalizedString(vn.getText()));
				}
				else if (currentElementName.equalsIgnoreCase("name"))
				{
					wpt.name = vn.toNormalizedString(vn.getText());
					currentCache.code = wpt.name;
					previousCacheCode = currentCache.code;
				}
				else if (currentElementName.equalsIgnoreCase("desc"))
				{
					wpt.description = vn.toNormalizedString(vn.getText());
					
					// This is a workaround for waypoints, which have no description set (resp. the description is the name)
					// e.g. gpx files generated from GCtour (http://gctour.madd.in/)
					if (wpt.description == null || wpt.description.length() == 0)
					{
						wpt.description = wpt.name;					
						String tmp = wpt.description.toUpperCase().replace(" ", "");
						
						if (tmp.length() >= 2)
							tmp = tmp.substring(0, 2);
						else
							tmp = "ZZ";
						
						wpt.name = tmp + previousCacheCode.substring(2);
					}
				}
				else if (currentElementName.equalsIgnoreCase("sym"))
				{
					wpt.symbol = vn.toNormalizedString(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("type"))
				{
					wpt.parseTypeString(vn.toNormalizedString(vn.getText()));
				}
				else if (currentElementName.equalsIgnoreCase("ele"))
				{
					wpt.elevation = vn.parseInt(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("cmt"))
				{
					if (vn.getText() != -1)
						wpt.comment = vn.toNormalizedString(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("cache") || currentElementName.equalsIgnoreCase("geocache"))
				{
					this.readCache(vn);
				}
				// opencaching.de puts the cache into a extensions node
				else if (currentElementName.equalsIgnoreCase("extensions"))
				{
					vn.push();
					vn.toElement(VTDNavHuge.FIRST_CHILD);
					
					final String elementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
					
					if (elementName.equalsIgnoreCase("cache"))
					{
						mLogger.debug("Found opencaching.de cache node.");
						this.readCache(vn);
					}

					vn.pop();
				}
			} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));

			if (foundCacheRecord)
			{
				mLogger.debug("Adding cache: " + currentCache.code);
				cacheDatabase.addCache(currentCache, false);
			}
			else
			{
				mLogger.debug("Orphaned waypoint: " + wpt.name);
				cacheDatabase.addOrphanedWaypoint(wpt);
			}

			vn.pop();
		}

		cacheDatabase.commit();
		return true;
	}

	/**
	 * 
	 * @param vn
	 */
	private void readCache(final VTDNavHuge vn)
	{
		try
		{
			foundCacheRecord = true;
			
			int idx = -1;
			
			// currentCache.id = vn.parseInt(vn.getAttrVal("id"));
			// Note: some generated gpx files don't contain cache IDs ...
			if ((idx = vn.getAttrVal("id")) != -1)
			{
				final String strId = vn.toNormalizedString(vn.getAttrVal("id"));
				try 
				{
					currentCache.id = Integer.parseInt(strId);
		        } 
				catch (NumberFormatException ex) { }						
			}
			
			if ((idx = vn.getAttrVal("archived")) != -1)
			{
				currentCache.isArchived = Boolean.parseBoolean(vn.toNormalizedString(idx));
			}
			
			if ((idx = vn.getAttrVal("available")) != -1)
			{
				currentCache.isAvailable = Boolean.parseBoolean(vn.toNormalizedString(idx));
			}
			
			if ((idx = vn.getAttrVal("status")) != -1)
			{
				currentCache.parseOpencachingStatus(vn.toNormalizedString(idx));
			}
	
			vn.toElement(VTDNavHuge.FIRST_CHILD);
	
			do
			{
				final String currentElementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
				
				if (currentElementName.equalsIgnoreCase("name"))
				{
					currentCache.name = vn.toNormalizedString(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("placed_by"))
				{
					currentCache.placedBy = vn.toNormalizedString(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("owner"))
				{
					currentCache.owner = vn.toNormalizedString(vn.getText());
					if (vn.getAttrVal("id") != -1) 
					{
						currentCache.ownerId = vn.parseInt(vn.getAttrVal("id"));
					}
				}
				else if (currentElementName.equalsIgnoreCase("type"))
				{
					currentCache.parseCacheTypeString(vn.toNormalizedString(vn.getText()));
				}
				else if (currentElementName.equalsIgnoreCase("container"))
				{
					currentCache.parseContainerTypeString(vn.toNormalizedString(vn.getText()));
				}
				else if (currentElementName.equalsIgnoreCase("difficulty"))
				{
					final String data = vn.toNormalizedString(vn.getText());
					
					if (data.equals("null")) 
						currentCache.difficulty = 1d;
					else							
						currentCache.difficulty = vn.parseDouble(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("terrain"))
				{
					final String data = vn.toNormalizedString(vn.getText());
					
					if (data.equals("null")) 
						currentCache.terrain = 1d;
					else							
						currentCache.terrain = vn.parseDouble(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("country"))
				{
					currentCache.country = vn.toNormalizedString(vn.getText());
				}
				else if (currentElementName.equalsIgnoreCase("state"))
				{
					final int i = vn.getText();
					if (i != -1)
						currentCache.state = vn.toNormalizedString(i);
				}
				else if (currentElementName.equalsIgnoreCase("short_description") || currentElementName.equalsIgnoreCase("summary"))
				{
					if (vn.getAttrVal("html") != -1) currentCache.shortDescriptionIsHtml = true;
					final int i = vn.getText();
					if (i != -1) currentCache.shortDescription = currentCache.shortDescription.concat(vn.toNormalizedString(i));
				}
				else if (currentElementName.equalsIgnoreCase("long_description") || currentElementName.equalsIgnoreCase("description"))
				{
					if (vn.getAttrVal("html") != -1) currentCache.longDescriptionIsHtml = true;
					final int i = vn.getText();
					if (i != -1) currentCache.longDescription = currentCache.longDescription.concat(vn.toNormalizedString(i));
				}
				else if ((currentElementName.equalsIgnoreCase("encoded_hints") || currentElementName.equalsIgnoreCase("hints")) && vn.getText() != -1)
				{
					currentCache.hint = currentCache.hint.concat(vn.toNormalizedString(vn.getText()));
				}
				else if (currentElementName.equalsIgnoreCase("logs"))
				{
					this.readLogs(vn);
				}
				else if (currentElementName.equalsIgnoreCase("attributes"))
				{
					this.readAttributes(vn);
				}
				else if (currentElementName.equalsIgnoreCase("travelbugs"))
				{
					this.readTravelbugs(vn);
				}
			} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));
			
		}
		catch (NavExceptionHuge e) 
		{
			mLogger.error("Error reading cache information.");
			e.printStackTrace();
		}		
	}
	
	/**
	 * 
	 * @param vn
	 */
	private void readAttributes(final VTDNavHuge vn)
	{
		try
		{
			vn.push();
			vn.toElement(VTDNavHuge.FIRST_CHILD);
			
			do
			{
				final String elementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
				
				if (elementName.equalsIgnoreCase("attribute"))
				{
					final Attribute attr = new Attribute();
					
					attr.id = vn.parseInt(vn.getAttrVal("id"));
					attr.flag = (vn.toNormalizedString(vn.getAttrVal("inc")).equals("1"));
					attr.name = vn.toNormalizedString(vn.getText());

					currentCache.addAttribute(attr);
				}
			} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));

			vn.pop();
		}
		catch (NavExceptionHuge e) 
		{
			mLogger.error("Error reading attribute information.");
			e.printStackTrace();
		}		
	}
	
	/**
	 * 
	 * @param vn
	 */
	private void readLogs(final VTDNavHuge vn)
	{
		try 
		{
			vn.push();
			vn.toElement(VTDNavHuge.FIRST_CHILD);
			
			do
			{
				final String elementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
				
				if (elementName.equalsIgnoreCase("log"))
				{
					vn.push();
					
					final LogEntry logEntry = new LogEntry();
					currentCache.addLogEntry(logEntry);
	
					vn.toElement(VTDNavHuge.FIRST_CHILD);
					
					do
					{
						final String subElementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
						// Log.d(TAG, currentElementName);
						if (subElementName.equalsIgnoreCase("time") || subElementName.equalsIgnoreCase("date"))
						{
							final String dateString = vn.toNormalizedString(vn.getText());
							logEntry.time = ISODateTime.parseString(dateString);
							if (logEntry.time == null) logEntry.time = LocalDateTime.parseString(dateString);
								
						}
						else if (subElementName.equalsIgnoreCase("type"))
						{
							logEntry.parseTypeString(vn.toNormalizedString(vn.getText()));
						}
						else if (subElementName.equalsIgnoreCase("finder") || subElementName.equalsIgnoreCase("geocacher"))
						{
							logEntry.finder = vn.toNormalizedString(vn.getText());
						}
						else if (subElementName.equalsIgnoreCase("text") && vn.getText() != -1)
						{
							logEntry.text = vn.toNormalizedString(vn.getText());
						}
						else if (subElementName.equalsIgnoreCase("log_wpt"))
						{
							logEntry.latitude = vn.parseDouble(vn.getAttrVal("lat"));
							logEntry.longitude = vn.parseDouble(vn.getAttrVal("lon"));
						}													
					} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));
					
					vn.pop();
				}
			} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));
	
			vn.pop();
		} 
		catch (NavExceptionHuge e) 
		{
			mLogger.error("Error reading log information.");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param vn
	 */
	private void readTravelbugs(final VTDNavHuge vn)
	{
		try 
		{
			vn.push();
			vn.toElement(VTDNavHuge.FIRST_CHILD);

			do
			{
				final String elementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
				
				if (elementName.equalsIgnoreCase("travelbug"))
				{
					final TravelBug bug = new TravelBug();
					currentCache.addTravelBug(bug);

					bug.reference = vn.toNormalizedString(vn.getAttrVal("ref"));

					vn.push();
					vn.toElement(VTDNavHuge.FIRST_CHILD);
					
					do
					{
						final String subElementName = vn.toNormalizedString(vn.getCurrentIndex()).replace("groundspeak:", "");
						if (subElementName.equalsIgnoreCase("name"))
						{
							bug.name = vn.toNormalizedString(vn.getText());											
						}
					} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));
					
					vn.pop();
				}
			} while (vn.toElement(VTDNavHuge.NEXT_SIBLING));
	
			vn.pop();
		} 
		catch (NavExceptionHuge e) 
		{
			mLogger.error("Error reading travelbug information.");
			e.printStackTrace();
		}		
	}
}