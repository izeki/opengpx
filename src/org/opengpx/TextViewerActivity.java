package org.opengpx;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.opengpx.lib.tools.StackTraceUtil;

import android.app.Activity;
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
        
        Bundle bunExtras = this.getIntent().getExtras();
        String strFilename = (String) bunExtras.get("filename");
        // File file = new File(strFilename);
        
        this.setTitle("OpenGPX - " + strFilename);
        
        TextView tvContent = (TextView) this.findViewById(R.id.TextViewerContent);
        tvContent.setText(this.loadFileContent(strFilename));
        // TextView tvFooter = (TextView) this.findViewById(R.id.TextViewerFooter);
        // tvFooter.setText(strFilename);
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
    		FileInputStream fis;
			try {
				fis = new FileInputStream(strFilename);
	    		final DataInputStream dis = new DataInputStream(fis);
	    		String strData = "";
	    		while (dis.available() != 0)
	    		{
	    			strData = strData.concat(dis.readLine()).concat("\n");
	    		}
	    		dis.close();
	    		fis.close();
	    		return strData;
			} 
			catch (IOException e) 
			{
				return StackTraceUtil.getStackTrace(e);
			}
    	} 
    	else 
    	{
    		return "File not found.";
    	}
    }
}
