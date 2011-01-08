package org.opengpx.lib.tools;

import java.util.Hashtable;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class MorseCode {

	private Hashtable<String, String> htMorseCodeCharacters = new Hashtable<String, String>();
	private Hashtable<String, String> htReverseLookupMap = new Hashtable<String, String>();
	
	/**
	 * 
	 */
	public MorseCode()
	{
		this.FillMorseCodeMap();
		this.FillReverseLookupMap();
	}

	/**
	 * 
	 */
	private void FillMorseCodeMap()
	{
		this.htMorseCodeCharacters.put("A", ".-");
		this.htMorseCodeCharacters.put("\304", ".-.-");
		this.htMorseCodeCharacters.put("\301", ".--.-");
		this.htMorseCodeCharacters.put("\305", ".--.-");
		this.htMorseCodeCharacters.put("B", "-...");
		this.htMorseCodeCharacters.put("C", "-.-.");
		this.htMorseCodeCharacters.put("\307", "----");
		this.htMorseCodeCharacters.put("D", "-..");
		this.htMorseCodeCharacters.put("E", ".");
		this.htMorseCodeCharacters.put("\311", "..-..");
		this.htMorseCodeCharacters.put("F", "..-.");
		this.htMorseCodeCharacters.put("G", "--.");
		this.htMorseCodeCharacters.put("H", "....");
		this.htMorseCodeCharacters.put("I", "..");
		this.htMorseCodeCharacters.put("J", ".---");
		this.htMorseCodeCharacters.put("K", "-.-");
		this.htMorseCodeCharacters.put("L", ".-..");
		this.htMorseCodeCharacters.put("M", "--");
		this.htMorseCodeCharacters.put("N", "-.");
		this.htMorseCodeCharacters.put("\321", "--.--");
		this.htMorseCodeCharacters.put("O", "---");
		this.htMorseCodeCharacters.put("\326", "---.");
		this.htMorseCodeCharacters.put("P", ".--.");
		this.htMorseCodeCharacters.put("Q", "--.-");
		this.htMorseCodeCharacters.put("R", ".-.");
		this.htMorseCodeCharacters.put("S", "...");
		this.htMorseCodeCharacters.put("T", "-");
		this.htMorseCodeCharacters.put("U", "..-");
		this.htMorseCodeCharacters.put("\334", "..--");
		this.htMorseCodeCharacters.put("V", "...-");
		this.htMorseCodeCharacters.put("W", ".--");
		this.htMorseCodeCharacters.put("X", "-..-");
		this.htMorseCodeCharacters.put("Y", "-.--");
		this.htMorseCodeCharacters.put("Z", "--..");
		this.htMorseCodeCharacters.put("1", ".----");
		this.htMorseCodeCharacters.put("2", "...--");
		this.htMorseCodeCharacters.put("3", "...--");
		this.htMorseCodeCharacters.put("4", "....-");
		this.htMorseCodeCharacters.put("5", ".....");
		this.htMorseCodeCharacters.put("6", "-....");
		this.htMorseCodeCharacters.put("7", "--...");
		this.htMorseCodeCharacters.put("8", "---..");
		this.htMorseCodeCharacters.put("9", "----.");
		this.htMorseCodeCharacters.put("0", "-----");
		this.htMorseCodeCharacters.put(",", "--..--");
		this.htMorseCodeCharacters.put(".", ".-.-.-");
		this.htMorseCodeCharacters.put("?", "..--..");
		this.htMorseCodeCharacters.put(";", "-.-.-");
		this.htMorseCodeCharacters.put(":", "---...");
		this.htMorseCodeCharacters.put("/", "-..-.");
		this.htMorseCodeCharacters.put("-", "-....-");
		this.htMorseCodeCharacters.put("\"", ".----.");
		this.htMorseCodeCharacters.put("(", "-.--.-");
		this.htMorseCodeCharacters.put(")", "-.--.-");
		this.htMorseCodeCharacters.put("[", "-.--.-");
		this.htMorseCodeCharacters.put("]", "-.--.-");
		this.htMorseCodeCharacters.put("{", "-.--.-");
		this.htMorseCodeCharacters.put("}", "-.--.-");
		this.htMorseCodeCharacters.put("_", "..--.-");
	}
	
	/**
	 * 
	 */
	private void FillReverseLookupMap()
	{
		for (String key : this.htMorseCodeCharacters.keySet())
			this.htReverseLookupMap.put(this.htMorseCodeCharacters.get(key), key);
	}
	
	/**
	 * 
	 * @param plainText
	 * @return
	 */
	public String Encode(String plainText)
	{
		final StringBuilder result = new StringBuilder();
		// String strResult = "";
        for (char c : plainText.toCharArray())
        {
        	final String strUpperCaseCharacter = Character.toString(c).toUpperCase(); 
        	if (this.htMorseCodeCharacters.containsKey(strUpperCaseCharacter))
        		result.append(this.htMorseCodeCharacters.get(strUpperCaseCharacter)).append(" ");
        		// strResult += this.htMorseCodeCharacters.get(strUpperCaseCharacter) + " ";
        	else
        		result.append(Character.toString(c));
        		// strResult += Character.toString(c);
        }
        
		return result.toString();
	}
	
	/**
	 * 
	 * @param morseCode
	 * @return
	 */
	public String Decode(String morseCode)
	{
		final String[] arrSplitted = morseCode.split(" ");
		
		final StringBuilder result = new StringBuilder();
		// String strResult = "";
		for (String strMorseChar : arrSplitted)
		{
			if (this.htReverseLookupMap.containsKey(strMorseChar))
			{
				result.append(this.htReverseLookupMap.get(strMorseChar).toLowerCase());
				// strResult += this.htReverseLookupMap.get(strMorseChar).toLowerCase();
			} 
			else
			{
				if (strMorseChar.equals("/"))
					result.append(" ");
					// strResult += " ";
			}
		}
		return result.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
	    String text = "hello world";
	    MorseCode mc = new MorseCode();
	    String enc = mc.Encode(text);
	    System.out.println(enc);
	    System.out.println(mc.Decode("-.. .-. .. ...- . / .. -. / -. . .- .-. / - .... . / ---. .- -- - -.-. / .- .-. . .-"));
	    System.out.println(mc.Decode("-.-. .- -.-. .... ."));
	    System.out.println(mc.Decode("-... .. --. / -.-. ..- -.-. ..- -- -... . .-. ... / --. .-.. .- ... / .-. --- --- - ... / --- ..-. / .- / - .-. . ."));
	    System.out.println(mc.Decode(".... .. -. -"));
	    System.out.println(mc.Decode("-.... / -- / .-.. . ..-. - / -... .-.. ..- . / ... .. --. -."));	    
	}

}
