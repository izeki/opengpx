package org.opengpx.lib.tools.maths;


/**
 * 
 * @author Martin Preishuber
 *
 * LCM (least common multiple) = kgV (kleinstes gemeinsames Vielfaches)
 */
public class LCM {
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long calculate(long a, long b)
    {		
		return a * (b / GCD.calculate(a,b)); 
    }
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public static long calculate(long ... a) 
	{
		if (a.length < 1)
			return -1 ;
		else if (a.length == 1)
			return a[0] ;
		else {
			long temp = a[0] ;
			for (int i = 1; i < a.length; i++) {
				temp = LCM.calculate (a[i], temp) ;
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
		
	    System.out.println(LCM.calculate(a, b));
	    System.out.println(LCM.calculate(b, a));
	    
	    long c = 3840;
	    System.out.println(LCM.calculate(b, c));
	    System.out.println(LCM.calculate(a, b, c));
	}
}
