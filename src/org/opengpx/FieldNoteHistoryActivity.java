package org.opengpx;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.geocache.FieldNote;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import com.db4o.ObjectSet;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class FieldNoteHistoryActivity extends ListActivity
{

	private FieldNoteHistoryAdapter mFieldNoteHistoryAdapter;
	
	// private static final Logger mLogger = LoggerFactory.getLogger(FieldNoteHistoryActivity.class);

	/**
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.field_notes));
		setContentView(R.layout.fieldnotehistory);

		final ObjectSet<FieldNote> fieldNotes = CacheDatabase.getInstance().getFieldNotes(true);
		this.mFieldNoteHistoryAdapter = new FieldNoteHistoryAdapter(this, fieldNotes);
		this.setListAdapter(this.mFieldNoteHistoryAdapter);	
	}
	
	/**
	 * Show cache associated with the current fieldnote
	 */
	public void onListItemClick(ListView parent, View v, int position, long id)
	{
		final FieldNote fieldNote = this.mFieldNoteHistoryAdapter.getItem(position);
		
		final Intent intent = new Intent(this, CacheDetailActivity.class);
		intent.putExtra("cachecode", fieldNote.gcId);
		intent.putExtra("isSearchResult", false);
		this.startActivity(intent);
	}

}
