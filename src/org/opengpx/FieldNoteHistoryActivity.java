package org.opengpx;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.geocache.FieldNote;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import com.db4o.ObjectSet;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class FieldNoteHistoryActivity extends ListActivity
{

	private FieldNoteHistoryAdapter mFieldNoteHistoryAdapter;
	
	// private static final Logger mLogger = LoggerFactory.getLogger(FieldNoteHistoryActivity.class);

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setTitle(R.string.app_title);
		setContentView(R.layout.fieldnotehistory);

		final ObjectSet<FieldNote> fieldNotes = CacheDatabase.getInstance().getFieldNotes(true);
		this.mFieldNoteHistoryAdapter = new FieldNoteHistoryAdapter(this, fieldNotes); 
		this.setListAdapter(this.mFieldNoteHistoryAdapter);	
	}
}
