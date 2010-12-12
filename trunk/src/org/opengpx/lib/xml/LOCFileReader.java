package org.opengpx.lib.xml;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class LOCFileReader 
{
	/**
	 * 
	 */
	public LOCFileReader()
	{
	}
	
	/**
	 * 
	 * @param fileName
	 */
	public boolean read(String fileName)
	{
		File file = new File(fileName);
		if (file.exists())
		{
			try 
			{
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				saxParserFactory.setValidating(false);
				saxParserFactory.setNamespaceAware(true);
				SAXParser saxParser = saxParserFactory.newSAXParser();
				XMLReader xmlReader = saxParser.getXMLReader();
				LOCFileHandler locFileHandler = new LOCFileHandler();
				xmlReader.setContentHandler(locFileHandler);
				xmlReader.parse(new InputSource(new FileInputStream(file)));				
				
				return true;
			}
			catch (SAXParseException spe) 
			{
				spe.printStackTrace();
				return false;
			} 
			catch (SAXException se) 
			{
				se.printStackTrace();
				return false;
			} 
			catch (Throwable t)
			{
				t.printStackTrace();
				return false;
			}
		} 
		else
		{
			return false;			
		}
	}
}
