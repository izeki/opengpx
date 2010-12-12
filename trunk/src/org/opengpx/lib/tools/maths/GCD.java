package org.opengpx.lib.tools.maths;

/**
 * 
 * @author Martin Preishuber
 *
 * GCD (Greates Common Divisor) = GGT (grš§ter gemeinsamer Teiler)
 */
public class GCD {
	
	public static long calculate (long a, long b) 
	{
		while (b != 0) {
			long temp = a % b;
			a = b;
			b  = temp;
		}
		return a;
	}
	
	public static long calculate (long ... a) {
		if (a.length < 1)
			return -1 ;
		else if (a.length == 1)
			return a[0] ;
		else {
			long temp = a[0] ;
			for (int i = 1; i < a.length; i++) {
				temp = GCD.calculate (a[i], temp) ;
			}
			return temp;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		long a = 3528;
		long b = 3780;
		
	    System.out.println(GCD.calculate(a, b));
	    System.out.println(GCD.calculate(b, a));
	    
	    long c = 3840;
	    System.out.println(GCD.calculate(b, c));
	    System.out.println(GCD.calculate(a, b, c));
	}

}
