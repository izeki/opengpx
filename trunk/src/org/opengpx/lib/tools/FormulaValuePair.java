package org.opengpx.lib.tools;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class FormulaValuePair
{
	private String mstrFormula;
	private Double mdblValue;
	private boolean mblnCalculated;

	/**
	 * 
	 * @param formula
	 */
	public FormulaValuePair(String formula)
	{
		this.mstrFormula = formula;
		this.mdblValue = Double.NaN;
	}

	/**
	 * 
	 * @param formula
	 * @param value
	 */
	public FormulaValuePair(String formula, Double value)
	{
		this.mstrFormula = formula;
		this.mdblValue = value;
	}

	/**
	 * @param mblnCalculated the mblnCalculated to set
	 */
	public void setCalculated(boolean calculated) 
	{
		this.mblnCalculated = calculated;
	}

	/**
	 * @return the mblnCalculated
	 */
	public boolean isCalculated() 
	{
		return this.mblnCalculated;
	}

	/**
	 * 
	 * @return
	 */
	public Double getValue() 
	{
		return this.mdblValue;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(Double value) 
	{
		this.mdblValue = value;
	}

	/**
	 * 
	 * @return
	 */
	public String getFormula()
	{
		return this.mstrFormula;
	}
	
	/**
	 * 
	 */
	@Override public String toString()
	{
		if (this.mstrFormula.equals(this.mdblValue.toString()))
			return this.mdblValue.toString();
		else
			return String.format("%s [%s]", this.mdblValue.toString(), this.mstrFormula);
	}
}
