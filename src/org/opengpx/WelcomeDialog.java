package org.opengpx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class WelcomeDialog extends AlertDialog.Builder
{

	private Context mContext;
	
	/**
	 * 
	 * @param context
	 */
	public WelcomeDialog(Context context) 
	{
		super(context);

		this.mContext = context;
		this.initialize();
	}

	/**
	 * 
	 */
	private void initialize()
	{		
		this.setTitle(R.string.welcome);

		final LayoutInflater inflater = LayoutInflater.from(this.mContext);
		final View layout = inflater.inflate(R.layout.welcomedialog, null);

		this.setView(layout);

		final TextView gpxFolder = (TextView) layout.findViewById(R.id.WelcomeGpxFolder);
		final Preferences preferences = new Preferences(this.mContext);
		gpxFolder.setText(preferences.getDataFolder());
		
		this.setNeutralButton(R.string.button_ok, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int arg1) 
			{
				dialog.dismiss();
			}			
		});
	}
}
