package org.opengpx;

import org.andnav.osm.views.util.IOpenStreetMapRendererInfo;
import org.andnav.osm.views.util.OpenStreetMapRendererFactory;

import org.opengpx.R;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class OsmPreferenceActivity extends PreferenceActivity 
{
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.osm_preferences);
		
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
}
