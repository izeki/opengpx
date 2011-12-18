package org.opengpx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.opengpx.lib.tools.StackTraceUtil;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class TextViewerActivity extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.textviewer);
        
    	// this.setRequestedOrientation(this.mSettings.getInt(OpenGPX.PREFS_KEY_SCREEN_ORIENTATION, OpenGPX.PREFS_DEFAULT_SCREEN_ORIENTATION));
        
        final Bundle bunExtras = this.getIntent().getExtras();
        final String strFilename = (String) bunExtras.get("filename");
        
        this.setTitle("OpenGPX - " + strFilename);
        
        final TextView tvContent = (TextView) this.findViewById(R.id.TextViewerContent);
        tvContent.setText(this.loadFileContent(strFilename));
    }
   
    /**
     * 
     * @param strFilename
     * @return
     */
    private String loadFileContent(String strFilename)
    {
    	final File file = new File(strFilename);
    	if (file.exists())
    	{
			try {
				
				final FileInputStream fis = new FileInputStream(strFilename);
				final InputStreamReader isr = new InputStreamReader(fis, "UTF8");
				final BufferedReader in = new BufferedReader(isr);
				
				final StringBuilder text = new StringBuilder();
				String strLine = "";
	    		
	    		while ((strLine = in.readLine()) != null) 
	    		{
	    			text.append(strLine.concat(System.getProperty("line.separator")));
	    		}
	    		
	    		in.close();
	    		isr.close();
	    		fis.close();
	    		
	    		return text.toString();
			} 
			catch (IOException e) 
			{
				return StackTraceUtil.getStackTrace(e);
			}
    	} 
    	else 
    	{
    		final Resources res = this.getResources();
    		return res.getString(R.string.file_not_found);
    	}
    }
}
