package org.opengpx.tools;

import org.opengpx.lib.UserDefinedVariables;

public interface GeocachingTool {

	// Input, calculation and output handling
	public String process(String input);
	public String getExplanation();		// additional information for the result
	
	// Variable handling
	public Boolean isSupportingVariables();
	public void setVariables(UserDefinedVariables variables);
	
	// Error handling. 
	public Integer getErrorCode(); // Should be zero in case of no error
	public String getErrorMessage(); // Should be empty string in case of no error

	// Help
	public String getHelp();
}
