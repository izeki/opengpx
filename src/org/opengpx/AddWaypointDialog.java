package org.opengpx;

import java.util.ArrayList;
import java.util.Date;

import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.Text;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.geocache.WaypointType;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddWaypointDialog extends Dialog {
	
	private ReadyListener readyListener;
	private EditText wpName;
	private EditText wpLat;
	private EditText wpLong; 
	private Context mContext;
	private Coordinates mReferenceCoordinates;
	
	// public AddWaypointDialog(Context context, ReadyListener readyListener, SharedPreferences mSettings) 
	public AddWaypointDialog(Context context, ReadyListener readyListener, Waypoint headerWaypoint) 
	{
		super(context);
		this.mContext = context;
		this.readyListener = readyListener;
		this.mReferenceCoordinates = new Coordinates(headerWaypoint.latitude, headerWaypoint.longitude);
	}

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addwaypointdialog);
		setTitle(R.string.dialog_add_waypoint);
		
		final Button cancelButton = (Button) findViewById(R.id.waypointcancelbutton);
		cancelButton.setOnClickListener(new CancelListener());
		
		final Button okButton = (Button) findViewById(R.id.waypointsavebutton);
		okButton.setOnClickListener(new OKListener());

		wpName = (EditText)findViewById(R.id.EditWaypointName);
		wpLat = (EditText)findViewById(R.id.EditLatitude);
		wpLong = (EditText)findViewById(R.id.EditLongtitude);

		wpLat.setText(this.mReferenceCoordinates.getLatitude().toString(CoordinateFormat.DM));
		wpLong.setText(this.mReferenceCoordinates.getLongitude().toString(CoordinateFormat.DM));
	}
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	private class CancelListener implements android.view.View.OnClickListener {

		public void onClick(View v) {
			AddWaypointDialog.this.dismiss();		
		}
	}
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	private class OKListener implements android.view.View.OnClickListener {

		public void onClick(View v) 
		{
			String name = AddWaypointDialog.this.wpName.getText().toString();
			String lat = AddWaypointDialog.this.wpLat.getText().toString();
			String lng = AddWaypointDialog.this.wpLong.getText().toString();
			Text input = new Text(lat + " " + lng);
			ArrayList<Coordinates> coords = input.ExtractCoordinates();
			if(coords.size() == 1)
			{
				Waypoint wp = new Waypoint();
				wp.latitude = coords.get(0).getLatitude().getD();
				wp.longitude = coords.get(0).getLongitude().getD();
				wp.name = name;
				wp.description = name;
				wp.symbol = "User";
				wp.time = new Date();
				wp.setType(WaypointType.UserDefined);
				AddWaypointDialog.this.readyListener.ready(wp);
				AddWaypointDialog.this.dismiss();
			}
			else
			{
				Toast failed = Toast.makeText(mContext,R.string.failed_parsing_coords,Toast.LENGTH_LONG);
				failed.show();
			}
		}
	}
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	public interface ReadyListener 
	{
		public void ready(Waypoint wp);			
	}

}
