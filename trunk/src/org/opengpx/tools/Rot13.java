package org.opengpx.tools;


import org.opengpx.lib.Text;
import org.opengpx.lib.UserDefinedVariables;
import org.opengpx.lib.tools.ChiffreType;

public class Rot13 implements GeocachingTool 
{

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
	public String process(String input) 
	{
		final Text textGroundspeakCode = new Text(ChiffreType.Rot13);
		textGroundspeakCode.setPlainText(input);
		return textGroundspeakCode.getEncodedText();
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
