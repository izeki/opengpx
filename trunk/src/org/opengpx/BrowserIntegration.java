package org.opengpx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BrowserIntegration extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getData() != null) {
        	Log.d("Uri:", intent.getData().toString());
        }
	}
}
