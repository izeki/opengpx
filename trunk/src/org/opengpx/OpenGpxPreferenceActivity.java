package org.opengpx;

import org.andnav.osm.views.util.IOpenStreetMapRendererInfo;
import org.andnav.osm.views.util.OpenStreetMapRendererFactory;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
import org.opengpx.GpsLocationListener;
import org.opengpx.R;
import org.opengpx.lib.Coordinates;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class OpenGpxPreferenceActivity extends PreferenceActivity 
{
	
	private GpsLocationListener	mGpsLocationListener;
	private Preferences mPreferences;

	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
		
		this.mGpsLocationListener = new GpsLocationListener((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		this.mPreferences = new Preferences(this);

		((Preference) findPreference(Preferences.PREFS_KEY_SET_HOME_COORDINATES)).setOnPreferenceClickListener(new OnPreferenceClickListener() 
		{ 
			public boolean onPreferenceClick(Preference preference) 
			{
				final Location location = mGpsLocationListener.getLastKnownLocation();
				if (location != null)
				{
					final Coordinates coordinates = new Coordinates(location.getLatitude(), location.getLongitude());
					mPreferences.setHomeCoordinates(coordinates);
					
					Toast.makeText(getBaseContext(), "Home coordinates set to: " + coordinates.toString() + " [" + location.getProvider() + "]", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(getBaseContext(), "Unable to set home coordinates", Toast.LENGTH_LONG).show();
				}
				return true;
			}
		});

		// Set OSM renderer list
		CharSequence[] csRenderers = new CharSequence[OpenStreetMapRendererFactory.getRenderers().length];
		int i = 0;
		for(IOpenStreetMapRendererInfo renderer : OpenStreetMapRendererFactory.getRenderers()) 
		{
			csRenderers[i++] = renderer.name();
		}
		ListPreference listPref = ((ListPreference) findPreference("osmRenderer"));
		listPref.setEntries(csRenderers);
        listPref.setEntryValues(csRenderers);
	}	
	
	@Override
	public void onPause()
	{
		super.onPause();

		mGpsLocationListener.disableListener();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		mGpsLocationListener.enableListener();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		mGpsLocationListener.disableListener();
	}
}
