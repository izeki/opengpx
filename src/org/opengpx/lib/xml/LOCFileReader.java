package org.opengpx.lib.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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
		final File file = new File(fileName);
		if (file.exists())
		{
			try 
			{
				return this.readInputStream(new FileInputStream(file));
			}
			catch (FileNotFoundException fnfe)
			{
				fnfe.printStackTrace();
				return false;
			}
		} 
		else
		{
			return false;			
		}
	}

	/**
	 * 
	 * @param byteArray
	 * @return
	 */
	public Boolean readByteArray(byte[] byteArray)
	{
		return this.readInputStream(new ByteArrayInputStream(byteArray));
	}

	/**
	 * 
	 * @param stream
	 * @return
	 */
	private boolean readInputStream(final InputStream stream)
	{
		try 
		{
			final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setValidating(false);
			saxParserFactory.setNamespaceAware(true);
			final SAXParser saxParser = saxParserFactory.newSAXParser();
			final XMLReader xmlReader = saxParser.getXMLReader();
			final LOCFileHandler locFileHandler = new LOCFileHandler();
			xmlReader.setContentHandler(locFileHandler);
			xmlReader.parse(new InputSource(stream));				

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
}
