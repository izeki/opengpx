package org.opengpx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.opengpx.lib.UnitSystem;

import org.opengpx.R;
import org.opengpx.lib.Coordinates;
import org.opengpx.lib.NavigationInfo;
import org.opengpx.lib.geocache.Waypoint;
import org.opengpx.lib.map.GoogleElevation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author Martin Preishuber
 *
 */
// public class WaypointDetailDialog extends Dialog
public class WaypointDetailDialog extends AlertDialog.Builder
{
	private Waypoint mWaypoint;
	private Context mContext;
	private ArrayList<Waypoint> mOtherWaypoints;
	private UnitSystem mUnitSystem;
	
	/**
	 * 
	 * @param context
	 * @param waypoint
	 * @param otherWaypoints
	 */
	public WaypointDetailDialog(Context context, Waypoint waypoint, ArrayList<Waypoint> otherWaypoints, UnitSystem unitSystem) 
	{
		super(context);
		
		this.mContext = context;
		this.mWaypoint = waypoint;
		this.mOtherWaypoints = otherWaypoints;
		this.mUnitSystem = unitSystem;
		
		this.initialize();
	}

	/**
	 * 
	 * @param waypoint
	 */
	private void initialize()
	{
		this.setTitle(this.mWaypoint.name);
		
		final LayoutInflater inflater = LayoutInflater.from(this.mContext);
		final View layout = inflater.inflate(R.layout.waypointdetail, null);

		this.setView(layout);
		// Show waypoint name
		final TextView tvWaypointName = (TextView) layout.findViewById(R.id.waypointName);
		tvWaypointName.setText(this.mWaypoint.description);
		// Show waypoint detail
		final TextView tvWaypointDetail = (TextView) layout.findViewById(R.id.waypointDetail);
		final StringBuilder sbWaypointDetail = new StringBuilder();
		// sbWaypointDetail.append(String.format("Name: %s", this.mWaypoint.name));
		// sbWaypointDetail.append(String.format("\nDescription: %s", this.mWaypoint.description));
		if (this.mWaypoint.comment.length() > 0)
			sbWaypointDetail.append(String.format("Comment: %s\n", this.mWaypoint.comment));			
		sbWaypointDetail.append(String.format("Type: %s", this.mWaypoint.getType().toString()));
		sbWaypointDetail.append(String.format("\nSymbol: %s", this.mWaypoint.symbol));
		if (this.mWaypoint.time != null) sbWaypointDetail.append(String.format("\nTime: %s", this.mWaypoint.time.toLocaleString()));
		
		GoogleElevation ge = new GoogleElevation();
		ge.getElevation(this.mWaypoint.latitude, this.mWaypoint.longitude);

		// Add address to waypoint details
		final ConnectivityManager cm = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        String strAddresses = "Unknown";
		if (cm.getActiveNetworkInfo() != null)
		{
			final Geocoder geocoder = new Geocoder(this.mContext, Locale.getDefault());
			try {
	            List<Address> addresses = geocoder.getFromLocation(this.mWaypoint.latitude, this.mWaypoint.longitude, 1);
	            if (addresses.size() > 0) 
	            {
		            final StringBuilder addressesText = new StringBuilder();
	                for (int i=0; i<addresses.get(0).getMaxAddressLineIndex(); i++)
	                {
	                	addressesText.append(addresses.get(0).getAddressLine(i)).append("\n");
	                	// strAddresses += addresses.get(0).getAddressLine(i) + "\n";
	                }
	                strAddresses = addressesText.toString();
	            }
	        }
	        catch (IOException e) 
	        {
	        	strAddresses = "Error retrieving adress.";
	        	// e.printStackTrace();
	        }
		}
        sbWaypointDetail.append(String.format("\nAddress: %s", strAddresses));
        
		tvWaypointDetail.setText(sbWaypointDetail.toString());
		// Set relative waypoint information
		final TextView tvRelativeInformation = (TextView) layout.findViewById(R.id.waypointRelInfo);
		StringBuilder sbRelativeInformation = new StringBuilder();
		if (this.mOtherWaypoints.size() > 1)
		{
			final Coordinates coordsFrom = new Coordinates(this.mWaypoint.latitude, this.mWaypoint.longitude);
			for (Waypoint wp : this.mOtherWaypoints)
			{
				if (!wp.name.equals(this.mWaypoint.name))
				{
					final Coordinates coordsTo = new Coordinates(wp.latitude, wp.longitude);
					final NavigationInfo ni = coordsFrom.getNavigationInfoTo(coordsTo, this.mUnitSystem);
					sbRelativeInformation.append(String.format("%s [%s]: %s\n", wp.description, wp.name, ni.toString()));
				}
			}
		}
		String strRelativeInformation = sbRelativeInformation.toString().trim();
		if (strRelativeInformation.length() == 0) strRelativeInformation = "No other waypoint available.";
		tvRelativeInformation.setText(strRelativeInformation);
		
		this.setNeutralButton(R.string.button_ok, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int arg1) 
			{
				dialog.dismiss();
			}
			
		});
	}

}
