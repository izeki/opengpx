package org.opengpx;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        tvContent.setText(this.LoadFileContent(strFilename));
        // TextView tvFooter = (TextView) this.findViewById(R.id.TextViewerFooter);
        // tvFooter.setText(strFilename);
    }
   
    /**
     * 
     * @param strFilename
     * @return
     */
    private String LoadFileContent(String strFilename)
    {
    	File file = new File(strFilename);
    	if (file.exists())
    	{
    		FileInputStream fis;
			try {
				fis = new FileInputStream(strFilename);
	    		DataInputStream dis = new DataInputStream(fis);
	    		String strData = "";
	    		while (dis.available() != 0)
	    		{
	    			strData = strData.concat(dis.readLine()).concat("\n");
	    		}
	    		dis.close();
	    		fis.close();
	    		return strData;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				return e.getStackTrace().toString();
			}
    	} 
    	else 
    	{
    		return "File not found.";
    	}
    }
}
