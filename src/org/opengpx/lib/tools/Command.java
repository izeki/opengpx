package org.opengpx.lib.tools;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.opengpx.tools.Calculator;
import org.opengpx.tools.Checksum;
import org.opengpx.tools.GeocachingTool;
import org.opengpx.tools.RomanNumeral;
import org.opengpx.tools.Rot13;

import org.opengpx.lib.Text;
import org.opengpx.lib.UserDefinedVariables;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class Command 
{
	private CommandType mType;
	private String mText;
	private UserDefinedVariables mVariables;

	/**
	 * 
	 * @param type
	 * @param text
	 * @param variables
	 */
	public Command(CommandType type, String text, UserDefinedVariables variables)
	{
		this.mType = type;
		this.mText = text;
		this.mVariables = variables;
	}

	/**
	 * 
	 * @return
	 */
	public String execute()
	{
		GeocachingTool geocachingTool = null;
		
		switch (this.mType)
		{
		case Calculate:
			geocachingTool = new Calculator();
			if (geocachingTool.isSupportingVariables())
				geocachingTool.setVariables(this.mVariables);
			return geocachingTool.process(this.mText);
		case Rot13:
			geocachingTool = new Rot13();
			return geocachingTool.process(this.mText);
		case Text2Number:
            return this.TextToNumber();
		case RomanNumbers:
			geocachingTool = new RomanNumeral();
			return geocachingTool.process(this.mText);
		case CaesarEncrypt:
            final Text textCaesarEncrypt = new Text(ChiffreType.Caesar);
            textCaesarEncrypt.setPlainText(this.mText);
            return textCaesarEncrypt.getEncodedText();
		case CaesarDecrypt:
            final Text textCaesarDecrypt = new Text(ChiffreType.Caesar);
            textCaesarDecrypt.setEncodedText(this.mText);
            return textCaesarDecrypt.getPlainText();
		case MorseDecode:
			final MorseCode mcDecode = new MorseCode();
            return mcDecode.Decode(this.mText);
		case MorseEncode:
			final MorseCode mcEncode = new MorseCode();
            return mcEncode.Encode(this.mText);
		case Checksum:
			geocachingTool = new Checksum();
			return geocachingTool.process(this.mText);
		case PrimeFactorisation:
			final ArrayList<Integer> arrPrimeFactors = PrimeFactorization.calculate(Integer.parseInt(this.mText));
            return arrPrimeFactors.toString();
		case SumAscii:
            final Text textSumAscii = new Text();
            textSumAscii.setPlainText(this.mText);
            return textSumAscii.getAsciiValue().toString();
		case VanityNumbers:
            return this.getVanityNumbers();
		default:
            return this.mText;	
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private String TextToNumber()
	{
		final Text text2Number = new Text();
        text2Number.setPlainText(this.mText);
        
        StringBuilder text2Num = new StringBuilder(text2Number.getNumericalValue().toString());
        // String strText2Num = text2Number.getNumericalValue().toString();
        
        if (this.mText.length() > 0)
        {
        	final String firstSubString = this.mText.substring(0, 1);
        	text2Number.setPlainText(firstSubString);
        	// strText2Num += "\n\n" + strSubString + "=" + text2Number.getNumericalValue().toString();
        	text2Num.append("\n\n" + firstSubString + "=" + text2Number.getNumericalValue().toString());
	        for (int i = 1; i < this.mText.length(); i++)
	        {
	            final String nextSubString = this.mText.substring(i, i+1);
	            text2Number.setPlainText(nextSubString);
	            if (text2Number.getNumericalValue() > 0)
	            {
	            	// strText2Num += "," + strSubString + "=" + text2Number.getNumericalValue().toString();
	            	text2Num.append("," + nextSubString + "=" + text2Number.getNumericalValue().toString());
	            }
	        }
        }
        return text2Num.toString();
	}

    /**
     * 
     * @return
     */
    private String getVanityNumbers()
    {
    	final Hashtable<String, String> htVanityNumbers = new Hashtable<String, String>();
    	htVanityNumbers.put("ABC", "2");
    	htVanityNumbers.put("DEF", "3");
    	htVanityNumbers.put("GHI", "4");
    	htVanityNumbers.put("JKL", "5");
    	htVanityNumbers.put("MNO", "6");
    	htVanityNumbers.put("PQRS", "7");
    	htVanityNumbers.put("TUV", "8");
    	htVanityNumbers.put("WXYZ", "9");

    	StringBuilder result = new StringBuilder();
        // String strResult = "";
        for (char c : this.mText.toUpperCase().toCharArray())
        {
			for (Map.Entry<String, String> entry : htVanityNumbers.entrySet())
        	// for (String key : htVanityNumbers.keySet())
        	{
        		if (entry.getKey().contains(Character.toString(c)))
        			result.append(entry.getValue());
        			// strResult += htVanityNumbers.get(key);
        	}
        }
        
        return result.toString();
    }

    /**
     * 
     * @param commandType
     * @return
     */
    public static String getCommandTypeName(CommandType commandType)
    {
    	switch (commandType)
    	{
    	case Calculate : return "Calculate";
    	case Text2Number : return "Text2Number (A=1, ..., Z=26)";
    	case RomanNumbers : return "Roman numbers";
    	case CaesarEncrypt : return "Caesar (encrypt)";
    	case CaesarDecrypt : return "Caesar (decrypt)";
    	case Rot13 : return "Rot13";
    	case MorseEncode : return "Morse (encode)";
    	case MorseDecode : return "Morse (decode)";
    	case Checksum : return "Checksum";
    	case PrimeFactorisation : return "Prime factorisation";
    	case SumAscii : return "Sum of ASCII values";
    	case VanityNumbers : return "Vanity numbers";
    	default: return "Unknown";
    	}
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		UserDefinedVariables udv = new UserDefinedVariables("dummy_id");
		udv.add("a", 26);
		udv.add("b", 12);
		udv.add("c", 8);
		System.out.println(udv.get("e"));
		System.out.println(udv.parseExpression("e"));
		udv.add("e", 7);
		System.out.println(udv.get("e"));
		System.out.println(udv.parseExpression("e"));
	    Command cmd = new Command(CommandType.Calculate, "a*b+c-1", udv);
	    System.out.println("Calculate: " + cmd.execute());
	    cmd = new Command(CommandType.Calculate, "lcm(3528,3780)", udv);
	    System.out.println("Calculate LCM: " + cmd.execute());
	    cmd = new Command(CommandType.Calculate, "gcd(3528,3780)", udv);
	    System.out.println("Calculate GCD: " + cmd.execute());
	    cmd = new Command(CommandType.Calculate, "sin(pi)", udv);
	    System.out.println(cmd.execute());
	    cmd = new Command(CommandType.CaesarEncrypt, "Hello World", udv);
	    System.out.println( cmd.execute());
	    cmd = new Command(CommandType.CaesarDecrypt, "Khoor Zruog", udv);
	    System.out.println( cmd.execute());
	    cmd = new Command(CommandType.RomanNumbers, "MMIX", udv);
	    System.out.println( cmd.execute());
	    cmd = new Command(CommandType.Text2Number, "Niki Lauda", udv);
	    System.out.println("Text2Number: " + cmd.execute());
	    cmd = new Command(CommandType.PrimeFactorisation, "6937", udv);
	    System.out.println( cmd.execute());
	    cmd = new Command(CommandType.SumAscii, "ABC", udv);
	    System.out.println( cmd.execute());
	    cmd = new Command(CommandType.VanityNumbers, "AbCdEf", udv);
	    System.out.println( cmd.execute());
	    cmd = new Command(CommandType.Rot13, "Gbyyrf Rirag. Xnssrr, Xhpura. Haq fcgre xbzzra qvr Jefgy.", udv);
	    System.out.println( cmd.execute());
	}

}
