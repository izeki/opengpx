package org.opengpx.lib.tools;

import java.util.ArrayList;


/**
 * 
 * @author Martin Preishuber
 *
 */
public class PrimeFactorization {

	/**
	 * computes the prime factorisation of a given number.
	 * @param number
	 * @return
	 */
	public static ArrayList<Integer> calculate(Integer number)
	{
		final ArrayList<Integer> arrPrimeNumbers = PrimeNumbers.generate(number);
		int intPrimeNumberIndex = 0;
		final ArrayList<Integer> arrFactors = new ArrayList<Integer>();
	    while (number > 1)
	    {
	    	final Integer intPrimeNumber = arrPrimeNumbers.get(intPrimeNumberIndex);
	    	int intRemainder = (number % intPrimeNumber);
	        while (intRemainder == 0)
	        {
	        	arrFactors.add(intPrimeNumber);
	        	number = number / intPrimeNumber;
	            intRemainder = (number % intPrimeNumber);
	        }
	        intPrimeNumberIndex += 1;
	    }
	    
	    return arrFactors;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		int intTestNumber = 17499;
	    System.out.println(PrimeFactorization.calculate(intTestNumber));
	}

}
