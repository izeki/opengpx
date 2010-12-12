package org.opengpx.lib.tools;

import java.util.ArrayList;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class PrimeNumbers {

	/**
	 * calculates all prime numbers between 2 and max. (simple Sieb des Eratosthenes implementation)
	 * @param max
	 * @return
	 */
	public static ArrayList<Integer> Generate(int max)
	{
		ArrayList<Integer> arrCandidates = new ArrayList<Integer>(max);
		for (Integer i = 2; i <= max; i ++)
			arrCandidates.add(i);
		
		int intIndex = 0;
		while (intIndex < arrCandidates.size())
		{
			int intNumber = arrCandidates.get(intIndex); 
			int intFactor = 2;
			Integer intKey = intNumber * intFactor;
			while (intKey <= max)
			{
				arrCandidates.remove(intKey);
				intFactor += 1;
				intKey = intNumber * intFactor;
			}
			intIndex += 1;
		}
		return arrCandidates;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	    int intTestNumber = 497;
	    System.out.println(PrimeNumbers.Generate(intTestNumber));
	}

}
