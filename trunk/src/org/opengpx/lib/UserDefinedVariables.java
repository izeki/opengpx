package org.opengpx.lib;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import org.opengpx.lib.tools.FormulaValuePair;

import org.nfunk.jep.JEP;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class UserDefinedVariables 
{
	private String id = "";

	private boolean mblnIsdirty;
	private Hashtable<String, FormulaValuePair> mhtVariables;
	private ArrayList<String> mhtJEPStandardConstants; 

	/**
	 * 
	 */
	public UserDefinedVariables(String id)
	{
		this.id = id;
		this.mblnIsdirty = false;
    	this.mhtVariables = new Hashtable<String, FormulaValuePair>();
    	this.mhtJEPStandardConstants = new ArrayList<String>();
    	// Add JEP standard constants
    	this.mhtJEPStandardConstants.add("e");
    	this.mhtJEPStandardConstants.add("pi");
	}
		
	/**
	 * 
	 * @param key
	 * @param formula
	 */
    public void add(String key, String formula)
    {
    	FormulaValuePair fvp = new FormulaValuePair(formula);
    	this.mhtVariables.put(key, fvp);
    	this.mblnIsdirty = true;
    }

    /**
     * 
     * @param key
     * @param value
     */
    public void add(String key, double value)
    {
    	FormulaValuePair fvp = new FormulaValuePair(Double.toString(value), value);
    	this.mhtVariables.put(key, fvp);
    	this.mblnIsdirty = true;
    }

    /**
     * 
     * @param key
     * @return
     */
    public boolean containsKey(String key)
    {
    	return this.mhtVariables.containsKey(key);
    }

    /**
     * Return all keys (reverse sorted by key length).
     * @return
     */
    public TreeSet<String> keySet()
    {
    	return new TreeSet<String>(this.mhtVariables.keySet());
    }

    /**
     * 
     * @return
     */
    public int size()
    {
    	return this.mhtVariables.size();
    }
    
    /**
     * 
     * @param expression
     * @return
     */
    public Double parseExpression(String expression)
    {
		if (this.mblnIsdirty) 
			this.calculateAllVariables();

		final Set<String> keySet = this.mhtVariables.keySet();
		boolean blnAddStandardConstants = true;
    	for (String key : keySet)
    	{
    		if (this.mhtJEPStandardConstants.contains(key))
    			blnAddStandardConstants = false;
    	}

		JEP mathParser = new JEP();
		mathParser.addStandardFunctions();
		mathParser.addFunction("lcm", new org.opengpx.lib.tools.jep.LCM());
		mathParser.addFunction("gcd", new org.opengpx.lib.tools.jep.GCD());
		if (blnAddStandardConstants) mathParser.addStandardConstants();

		for (String strVariableName : keySet)
		{
			FormulaValuePair fvp = this.mhtVariables.get(strVariableName);
			if (fvp.isCalculated())
			{
				mathParser.addVariable(strVariableName, fvp.getValue());
			}
		}
		mathParser.parseExpression(expression);
    	return mathParser.getValue();
    }
    
    /**
     * 
     */
    private void calculateAllVariables()
    {
        // first make sure, that all items are invalid
		boolean blnAddStandardConstants = true;
    	for (String key : this.mhtVariables.keySet())
    	{
    		if (this.mhtJEPStandardConstants.contains(key))
    			blnAddStandardConstants = false;

			this.mhtVariables.get(key).setCalculated(false);
    		this.mhtVariables.get(key).setValue(Double.NaN);
    	}

    	int intMaxLoops = this.mhtVariables.size();
    	int intLoopNumber = 1;
    	boolean blnAllItemsCalculated = false;

    	TreeSet<String> tsSortedKeys = this.keySet();
		JEP mathParser = new JEP();
		mathParser.addStandardFunctions();
		if (blnAddStandardConstants) mathParser.addStandardConstants();

    	while ((!blnAllItemsCalculated) && (intLoopNumber <= intMaxLoops))
    	{
    		blnAllItemsCalculated = true;
        	for (String strVariableName : tsSortedKeys)
        	{
        		if (!this.mhtVariables.get(strVariableName).isCalculated())
        		{
        			String strFormula = this.mhtVariables.get(strVariableName).getFormula();
        			mathParser.parseExpression(strFormula);
        			Double dblValue = mathParser.getValue();
        			if (!mathParser.hasError())
        			{
        				this.mhtVariables.get(strVariableName).setValue(dblValue);
        				this.mhtVariables.get(strVariableName).setCalculated(true);
        				mathParser.addVariable(strVariableName, dblValue);
        			} 
        			else
        			{
        				blnAllItemsCalculated = false;
        			}
        		}
        	}
    		intLoopNumber++;
    	}
    	this.mblnIsdirty = false;
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public FormulaValuePair get(String key)
    {
    	if (this.mblnIsdirty) 
    		this.calculateAllVariables();
    	return this.mhtVariables.get(key);
    }

    /**
     * 
     * @return
     */
    public String getId()
    {
    	return this.id;
    }
    
	/**
	 * 
	 */
	@Override public String toString()
	{
		if (this.mblnIsdirty) 
			this.calculateAllVariables();
		StringBuilder sb = new StringBuilder();
		for (String key : this.keySet())
		{
			sb.append(String.format("%s: %s\n", key, this.mhtVariables.get(key)));
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		UserDefinedVariables udv = new UserDefinedVariables("test_udv_set");
	    udv.add("a", 3);
	    udv.add("b", "a+3");
	    udv.add("c", 7);
	    udv.add("N1", "35200");
	    udv.add("N2", "N1+a");
	    udv.add("N2b", "N1+A");
	    System.out.print(udv);
	    System.out.println(udv.keySet());
	    System.out.println(udv.get("a"));
	}

}
