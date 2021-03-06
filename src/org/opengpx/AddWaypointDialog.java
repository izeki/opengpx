package org.opengpx;

import java.util.ArrayList;
import java.util.Date;

import org.opengpx.lib.CoordinateFormat;
import org.opengpx.lib.CoordinateType;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.Text;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.geocache.WaypointType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class AddWaypointDialog extends Dialog {
	
	private ReadyListener readyListener;
	private EditText wpName;
	private EditText wpLat;
	private EditText wpLong; 
	private Context mContext;
	private Coordinates mReferenceCoordinates;
	
	@SuppressWarnings("unused")
	private final Logger mLogger = LoggerFactory.getLogger(AddWaypointDialog.class);

	/**
	 * 
	 * @param context
	 * @param readyListener
	 * @param headerWaypoint
	 */
	public AddWaypointDialog(Context context, ReadyListener readyListener, Waypoint headerWaypoint) 
	{
		super(context);
		this.mContext = context;
		this.readyListener = readyListener;
		this.mReferenceCoordinates = new Coordinates(headerWaypoint.latitude, headerWaypoint.longitude);
		// this.mLogger.debug(this.mReferenceCoordinates.toString(CoordinateFormat.DM));
		
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

		wpLat.setText(Coordinates.convert(this.mReferenceCoordinates.getLatitude(), CoordinateType.Latitude, CoordinateFormat.DM));
		wpLong.setText(Coordinates.convert(this.mReferenceCoordinates.getLongitude(), CoordinateType.Longitude, CoordinateFormat.DM));
	}
	
	/**
	 * 
	 * @author Martin Preishuber
	 *
	 */
	private class CancelListener implements android.view.View.OnClickListener {

		public void onClick(View v) 
		{
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
			final String name = AddWaypointDialog.this.wpName.getText().toString();
			final String lat = AddWaypointDialog.this.wpLat.getText().toString();
			final String lng = AddWaypointDialog.this.wpLong.getText().toString();
			final Text input = new Text(lat + " " + lng);
			final ArrayList<Coordinates> coords = input.extractCoordinates();
			if(coords.size() == 1)
			{
				Waypoint wp = new Waypoint();
				wp.latitude = coords.get(0).getLatitude();
				wp.longitude = coords.get(0).getLongitude();
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
				Toast.makeText(mContext,R.string.failed_parsing_coords,Toast.LENGTH_LONG).show();
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
