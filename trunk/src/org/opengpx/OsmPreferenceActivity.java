package org.opengpx;

import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

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
		CharSequence[] csRenderers = new CharSequence[TileSourceFactory.getTileSources().size()];
		int i = 0;
		for(ITileSource renderer : TileSourceFactory.getTileSources()) 
		{
			csRenderers[i++] = renderer.name();
		}
		ListPreference listPref = ((ListPreference) findPreference("osmRenderer"));
		listPref.setEntries(csRenderers);
        listPref.setEntryValues(csRenderers);
	}
}
