package org.opengpx;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class ImageGridViewerActivity extends Activity 
{
	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.imagegridviewer);

        Bundle bunExtras = this.getIntent().getExtras();
        @SuppressWarnings("unchecked")
        ArrayList<String> arrFileNames = (ArrayList<String>) bunExtras.get("filenames");

	    this.setTitle("OpenGPX - Image Viewer");

	    GridView gridview = (GridView) findViewById(R.id.ImageGridView);
	    gridview.setAdapter(new ImageGridAdapter(this, arrFileNames));
	    gridview.setOnItemClickListener(new OnItemClickListener() 
	    {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				String strFileName = (String) parent.getItemAtPosition(position);
				Intent intent = new Intent(parent.getContext(), ImageViewerActivity.class);
    			intent.putExtra("filename", strFileName);
    			parent.getContext().startActivity(intent);
			}
	    });
	}
}
