package org.opengpx.tools;


import org.opengpx.lib.UserDefinedVariables;

public class Checksum implements GeocachingTool {

	private Integer mintErrorCode = 0;
	private String mstrErrorMessage = "";
	
	/**
	 * 
	 */
	public Integer getErrorCode() 
	{
		return this.mintErrorCode;
	}

	/**
	 * 
	 */
	public String getErrorMessage() 
	{
		return this.mstrErrorMessage;
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
		Integer intSum = 0;
	    for (int intAsciiCode : input.toCharArray())
	    {
	       	if ((intAsciiCode >= 48) && (intAsciiCode <= 57))
	       	{
	       		intSum += intAsciiCode - 48;
	       	} 
	       	else
	        {
	        	this.mintErrorCode = 1;
	        	this.mstrErrorMessage = "Invalid numeric character detected.";
	        }
	    }
	    return intSum.toString(); 
	}

	/**
	 * 
	 */
	public void setVariables(UserDefinedVariables variables) 
	{
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
	public String getExplanation() 
	{
		return "";
	}
}
