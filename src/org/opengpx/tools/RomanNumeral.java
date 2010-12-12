package org.opengpx.tools;

import java.util.Hashtable;


import org.opengpx.lib.UserDefinedVariables;

/**
 * 
 * @author preisl
 *
 */
public class RomanNumeral implements GeocachingTool 
{

	private Hashtable<String, Integer> mhtRomanSubsitutionRules = new Hashtable<String, Integer>();
	private Hashtable<String, Integer> mhtRomanNumerals = new Hashtable<String, Integer>();

	/**
	 * 
	 */
	public RomanNumeral()
	{
		this.initializeSubstituionRules();
		this.initializeNumerals();
	}
	
	/**
	 * 
	 */
	private void initializeNumerals()
	{
    	// Note: V (with line above) = 5000 is missing here
		this.mhtRomanNumerals.put("I", 1);
		this.mhtRomanNumerals.put("V", 5);
		this.mhtRomanNumerals.put("X", 10);
		this.mhtRomanNumerals.put("L", 50);
		this.mhtRomanNumerals.put("C", 100);
		this.mhtRomanNumerals.put("D", 500);
		this.mhtRomanNumerals.put("M", 1000);
	}
	
	/**
	 * 
	 */
	private void initializeSubstituionRules()
	{
		this.mhtRomanSubsitutionRules.put("IV", 4);
		this.mhtRomanSubsitutionRules.put("IX", 9);
		this.mhtRomanSubsitutionRules.put("XL", 40);
		this.mhtRomanSubsitutionRules.put("XC", 90);
		this.mhtRomanSubsitutionRules.put("CD", 400);
		this.mhtRomanSubsitutionRules.put("CM", 900);
	}
	
	/**
	 * 
	 */
	public Integer getErrorCode() 
	{
		return 0;
	}

	/**
	 * 
	 */
	public String getErrorMessage() 
	{
		return "";
	}

	/**
	 * 
	 */
	public String getExplanation() 
	{
		return "";
	}

	/**
	 * 
	 */
	public String getHelp() 
	{
		return "";
	}

	/**
	 * 
	 */
	public Boolean isSupportingVariables() 
	{
		return false;
	}

	/**
	 * 
	 */
	public String process(String input) 
	{
    	String strRomanText = input.toUpperCase();
        Integer intSum = 0;
        
        // Replace special numbers
        for (String key : this.mhtRomanSubsitutionRules.keySet())
        {
        	if (strRomanText.contains(key))
        	{
        		intSum += this.mhtRomanSubsitutionRules.get(key);
        		strRomanText = strRomanText.replace(key, "");
        	}
        }
        
    	// System.out.println(strRomanText);
        for (char c : strRomanText.toCharArray())
        {
        	intSum += this.mhtRomanNumerals.get(Character.toString(c));
        }
      
        return intSum.toString();
	}

	/**
	 * 
	 */
	public void setVariables(UserDefinedVariables variables) 
	{
	}

}
