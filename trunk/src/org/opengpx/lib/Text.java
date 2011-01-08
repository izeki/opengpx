package org.opengpx.lib;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opengpx.lib.tools.ChiffreType;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Text
{
	private String mstrPlainText = "";
	private ChiffreType mChiffreType = ChiffreType.Groundspeak;
	private boolean mblnIsHtml = false;

	/**
	 * 
	 */
	public Text()
	{
	}
	
	/**
	 * 
	 * @param text
	 */
	public Text(String text)
	{
		this.mstrPlainText = text;
	}
	
	/**
	 * 
	 * @param chiffreType
	 */
	public Text(ChiffreType chiffreType)
	{
		this.mChiffreType = chiffreType;
	}

	/**
	 * 
	 * @param text
	 */
	public void setPlainText(String text)
	{
		this.mstrPlainText = "";
		this.addPlainText(text);
	}

	/**
	 * 
	 * @param text
	 */
	public void addPlainText(String text)
	{
		// Remove Groundspeak tags (smileys ignored)
		text = text.replace("[b]", "");
		text = text.replace("[/b]", "");
		text = text.replace("[u]", "");
		text = text.replace("[/b]", "");
		
		this.mstrPlainText = this.mstrPlainText.concat(text);
	}
	/**
	 * 
	 * @return
	 */
	public String getPlainText()
	{
	    return this.mstrPlainText;
	}

	/**
	 * 
	 * @param text
	 */
    public void setEncodedText(String text)
    {
        if (this.mChiffreType == ChiffreType.Groundspeak)
        	this.mstrPlainText = this.ConvertGroundspeak(text);
        else if (this.mChiffreType == ChiffreType.Caesar)
        	this.mstrPlainText = this.ConvertCaesar(text, -3);
    }

    /**
     * 
     * @return
     */
    public String getEncodedText()
    {
        if (this.mChiffreType == ChiffreType.Groundspeak)
            return this.ConvertGroundspeak(this.mstrPlainText);
        else if (this.mChiffreType == ChiffreType.Caesar)
            return this.ConvertCaesar(this.mstrPlainText, 3);
        else
        	return ""; // dummy return code
    }
    
    /**
     * 
     * @param text
     * @return
     */
    private String ConvertGroundspeak(String text)
    {
    	final StringBuilder result = new StringBuilder();
        // String strResult = "";
        for (int intAsciiCode : text.toCharArray())
        {
            if (((intAsciiCode >= 65) && (intAsciiCode <= 77)) || ((intAsciiCode >= 97) && (intAsciiCode <= 109)))
            	result.append((char)(intAsciiCode + 13));
            	// strResult += (char)(intAsciiCode + 13);
            else if (((intAsciiCode >= 78) && (intAsciiCode <= 90)) || ((intAsciiCode >= 110) && (intAsciiCode <= 122)))
            	result.append((char)(intAsciiCode - 13));
                // strResult += (char)(intAsciiCode - 13);
            else
            	result.append((char)intAsciiCode);
                // strResult += (char)intAsciiCode;
        }
        return result.toString();
    }
    
    /**
     * 
     * @param text
     * @param shift
     * @return
     */
    private String ConvertCaesar(String text, int shift)
    {
    	final StringBuilder result = new StringBuilder();
        // String strResult = "";
        int intShiftToZero = 0;
        
        for (int intAsciiCode : text.toCharArray())
        {
        	if ((intAsciiCode >= 97) && (intAsciiCode <= 122)) // lower case characters
        		intShiftToZero = 97;
        	else if ((intAsciiCode >= 65) && (intAsciiCode <= 90)) // upper case characters
        		intShiftToZero = 65;
        	else
        		intShiftToZero = 0;
        
        	if (intShiftToZero > 0)
        	{
                // shift ascii code to 0, 1, ... 25 and add the shift
        		intAsciiCode = intAsciiCode - intShiftToZero + shift;
                // wrap codes >= 26
        		intAsciiCode = (intAsciiCode % 26) + intShiftToZero;
        	}
        	// Convert back to character
        	result.append((char)intAsciiCode);
            // strResult += (char)intAsciiCode;
        }
        return result.toString();
    }

    /**
     * 
     * @return
     */
    public ChiffreType getChiffreType()
    {
        return this.mChiffreType;
    }
    
    /**
     * 
     * @return
     */
    public Integer getNumericalValue()
    {
        int intValue = 0;
        for (int intAsciiCode : this.mstrPlainText.toUpperCase().toCharArray())
        {
        	if ((intAsciiCode >= 65) && (intAsciiCode <= 91)) // upper case characters
        		intValue += intAsciiCode - 64;
        }
        return intValue;
    }
    
    /**
     * 
     * @return
     */
    public Integer getAsciiValue()
    {
        int intValue = 0;
        for (int intAsciiCode : this.mstrPlainText.toCharArray())
        	intValue += intAsciiCode;
        return intValue;
    }

    /**
     * 
     * @return
     */
    public boolean getIsHtml()
    {
        return this.mblnIsHtml;
    }

    /**
     * 
     * @param value
     */
    public void setIsHtml(boolean value)
    {
    	this.mblnIsHtml = value;
    }

    /**
     * Extracts geographical coordinates from the text by using regular expressions.
     * @return
     */
    public ArrayList<Coordinates> extractCoordinates()
    {
    	String strText = this.mstrPlainText.toUpperCase();
        // remove some HTML tags
    	strText = strText.replace("<BR>", "");
    	strText = strText.replace("&DEG;", "");
    	
    	final ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();
    	
        final Pattern regex = Pattern.compile(Coordinates.coords_regexp, Pattern.DOTALL);
        final Matcher matcher = regex.matcher(strText);
        while (matcher.find())
        {
        	final Coordinates coords = new Coordinates();
        	coords.parseFromText(matcher.group(0));
        	coordinates.add(coords);
        }
    	
        return coordinates;
    }

    /**
     * 
     * @return
     */
    public boolean containsHtmlTag()
    {
        final String[] arrTempTags = { "&gt;", "&lt;", "&nbsp;", "&amp;", "&deg;", "&quot;", "&apos;" };
        final String strLCaseText = this.mstrPlainText.toLowerCase();
        boolean blnTagFound = false;
        for (String strTag : arrTempTags)
        {
        	blnTagFound = blnTagFound || strLCaseText.contains(strTag);
        }
        return blnTagFound;
    }

    /**
     * 
     */
    @Override public String toString()
    {
    	return this.mstrPlainText;
    }
    
    /**
     * 
     * @param args
     */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	    Text text = new Text();
	    // System.out.println(text.get_chiffre_type());
	    text.setPlainText("abcdefghijklmnopqrstuvwxyz");
	    System.out.println(text.getPlainText());
	    String enc = text.getEncodedText();
	    System.out.println(enc);
	    text.setPlainText(enc);
	    System.out.println(text.getEncodedText());

	    Text caesar = new Text(ChiffreType.Caesar);
	    caesar.setPlainText("Hello World");
	    System.out.println(caesar.getEncodedText());
	    caesar.setEncodedText("Khoor Zruog");
	    System.out.println(caesar.getPlainText());

	    Text html_text = new Text();
	    html_text.setPlainText("hello world in html");
	    System.out.println(html_text);
	    System.out.println(html_text.getIsHtml());
	    
	    Text coord_text = new Text();
	    coord_text.setPlainText("Meter vor dem Beginn des Weges, ist ein ausgeschilderter Schotterparkplatz für ein paar Fahrzeuge. (Koordinaten N46° 45.093 E014° 16.200). DIE HEADERKOORDINATEN SIND FALSCH! DER GLETSCHERTOPF IST LIEGT N 46 38.044 E 14 14.079! PEILUNG DANN ERST VON DORT");
	    ArrayList<Coordinates> coordinates = coord_text.extractCoordinates();
	    System.out.println(coordinates);
	    for (Coordinates coord : coordinates)
	        System.out.println(coord);
	}

}
