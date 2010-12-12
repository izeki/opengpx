package org.opengpx.lib.tools.jep;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class GCD extends PostfixMathCommand 
{
	/**
	 * 
	 */
	public GCD()
	{
		numberOfParameters = -1;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void run(Stack inStack) throws ParseException 
	{
		// check the stack
		checkStack(inStack);
	   
		// get the parameter from the stack
		long[] nums = new long[inStack.size()];
		int i = 0;
		while (!inStack.isEmpty())
		{
			Double d = (Double) inStack.pop();
			nums[i] = Math.round(d.doubleValue());
			i++;
		}
		Long lngGCD = org.opengpx.lib.tools.maths.GCD.calculate(nums);
		inStack.push(new Double(lngGCD));
	}
}
