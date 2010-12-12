package org.opengpx.lib.tools;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class HtmlCodeBuilder 
{
	private String mstrBody = "";
	private String mstrBackgroundColor = "";
	private String mstrFontColor = "";

	/**
	 * 
	 */
	public HtmlCodeBuilder()
	{		
	}

	/**
	 * 
	 * @return
	 */
	public String getFullPage()
	{
		StringBuilder sbHtmlPage = new StringBuilder();
		sbHtmlPage.append("<html>\n");
		sbHtmlPage.append("<head></head>\n");
		sbHtmlPage.append("<body");
		if (mstrBackgroundColor.length() > 0)
			sbHtmlPage.append(String.format(" bgcolor=\"%s\"", mstrBackgroundColor));
		if (mstrFontColor.length() > 0)
			sbHtmlPage.append(String.format("text=\"%s\"", mstrFontColor));
		sbHtmlPage.append(">\n");
		sbHtmlPage.append(this.mstrBody);
		sbHtmlPage.append("</body>");
		return sbHtmlPage.toString();
	}
	
	/**
	 * 
	 * @param text
	 */
	public void setBody(String text)
	{
		if (mstrBackgroundColor.equals("#000000"))
		{
			// Fix black fonts, if background color is set to black
			text = text.replace("\"black\"", "\"white\"");
			text = text.replace("\"#000000\"", "\"#ffffff\"");
		}
		this.mstrBody = text;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBody()
	{
		return this.mstrBody;
	}
	
	/**
	 * 
	 * @param color
	 */
	public void setBackgroundColor(String color)
	{
		this.mstrBackgroundColor = color;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getBackgroundColor()
	{
		return this.mstrBackgroundColor;
	}
	
	/**
	 * 
	 * @param color
	 */
	public void setFontColor(String color)
	{
		this.mstrFontColor = color;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFontColor()
	{
		return this.mstrFontColor;
	}
}
