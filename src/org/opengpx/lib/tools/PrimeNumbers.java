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
	public static ArrayList<Integer> generate(int max)
	{
		final ArrayList<Integer> arrCandidates = new ArrayList<Integer>(max);
		for (Integer i = 2; i <= max; i ++)
			arrCandidates.add(i);
		
		int intIndex = 0;
		while (intIndex < arrCandidates.size())
		{
			final int intNumber = arrCandidates.get(intIndex); 
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
	public static void main(String[] args) 
	{
	    final int intTestNumber = 100000;
	    final ArrayList<Integer> primes = PrimeNumbers.generate(intTestNumber);
	    
	    System.out.println(primes.size());
	    System.out.println(primes.get(3611)); // This one is step XX of DropQuest2011 ;-)
	}

}
