package org.opengpx.lib;

import java.util.ArrayList;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ImportResult 
{
	public int successful = 0;
	public int failed = 0;
	public ArrayList<String> filesFailed = new ArrayList<String>();
}
