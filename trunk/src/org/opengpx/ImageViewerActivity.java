package org.opengpx;

import java.io.File;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ImageViewerActivity extends Activity 
{
	
	// private int mintRequestedOrientation;
	
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    
	    this.setContentView(R.layout.imageviewer);
	    
	    // this.mintRequestedOrientation = this.getRequestedOrientation();
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        final Bundle bunExtras = this.getIntent().getExtras();
        final String strFileName = bunExtras.getString("filename");
        
        // Set title to filename
        final File fileImage = new File(strFileName);
        this.setTitle("OpenGPX - " + fileImage.getName());
        
        final ImageView imageView = (ImageView) findViewById(R.id.ImageViewerImage);
        imageView.setImageDrawable(Drawable.createFromPath(strFileName));
	}
}
