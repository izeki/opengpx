package org.opengpx.tools;


import org.opengpx.lib.UserDefinedVariables;

/**
 * 
 * @author preisl
 *
 */
public class Calculator implements GeocachingTool 
{

	private Integer mintErrorCode = 0;
	private String mstrErrorMessage = "";
	private UserDefinedVariables mVariables = null;
	
	/**
	 * 
	 */
	public Integer getErrorCode() 
	{
		return mintErrorCode;
	}

	/**
	 * 
	 */
	public String getErrorMessage() 
	{
		return mstrErrorMessage;
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
		return true;
	}

	/**
	 * 
	 */
	public String process(String input) 
	{
		Double dblNumericResult = Double.NaN;
		if (this.mVariables == null)
		{
			this.mVariables = new UserDefinedVariables("dummy_id");
		}
		
		if (input.length() > 0)
        {
        	if (input.contains("="))
        	{
        		String[] arrSplitted = input.split("=");
        		String strVariableName = arrSplitted[0];
        		String strExpression = arrSplitted[1];
        		this.mVariables.add(strVariableName, strExpression);
        		dblNumericResult = this.mVariables.get(strVariableName).getValue();
        	} 
        	else 
        	{
        		Double dblValue = this.mVariables.parseExpression(input);
        		if (dblValue.isNaN())
        		{
        			// Check wether a variable name has been queried
        			if (this.mVariables.containsKey(input))
        				dblValue = this.mVariables.get(input).getValue();
        		}
        		dblNumericResult = dblValue;
        	}
        }
		else
        {
        	this.mintErrorCode = 1;
        	this.mstrErrorMessage = "No valid input given.";
        }
		
        return dblNumericResult.toString();
	}

	/**
	 * 
	 */
	public void setVariables(UserDefinedVariables variables) 
	{
		this.mVariables = variables;
	}

}
