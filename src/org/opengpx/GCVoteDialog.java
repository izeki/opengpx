package org.opengpx;

import java.util.TreeMap;

import org.opengpx.R;
import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.geocache.GCVote;
import org.opengpx.lib.xml.GCVoteReader;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class GCVoteDialog extends AlertDialog.Builder 
{

	private Context mContext;
	private GCVote mGCVote;
	private String mCacheCode;
	private View mLayout;
	
	/**
	 * 
	 * @param context
	 */
	public GCVoteDialog(Context context, GCVote gcVote, String cacheCode) 
	{
		super(context);
		
		this.mContext = context;
		this.mGCVote = gcVote;
		this.mCacheCode = cacheCode;
		
		this.initialize();
	}

	/**
	 * 
	 */
	private void initialize()
	{
		final LayoutInflater inflater = LayoutInflater.from(this.mContext);
		this.mLayout = inflater.inflate(R.layout.votes, null);

		this.setTitle(R.string.title_votes);
		this.setView(this.mLayout);
		
		this.updateVotes(this.mGCVote);

		// Update votes
		this.setPositiveButton(R.string.button_update, new OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				final ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm.getActiveNetworkInfo() != null)
				{
					final GCVoteReader gcVoteReader = new GCVoteReader();
					// Add votes to database
					CacheDatabase.getInstance().addCacheVotes(gcVoteReader.getVotes(mCacheCode));
					// Re-read votes from database
					updateVotes(CacheDatabase.getInstance().getVote(mCacheCode));
					
					Toast.makeText(mContext, "GCVotes updated", Toast.LENGTH_LONG).show();
				}
				else
				{
					Toast.makeText(mContext, "Unable to update votes - no network connection", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		// Set close button action
		this.setNegativeButton(R.string.button_close, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which) 
			{
				dialog.dismiss();
			}	
		});
	}
	
	/**
	 * 
	 * @param gcVote
	 */
	private void updateVotes(GCVote gcVote)
	{
		// Set votes
		final TreeMap<Float, Integer> rawVotes = gcVote.getRawVotes();
		for (float vote : rawVotes.keySet())
		{
			final int count = rawVotes.get(vote);
			final String voteTag = "Vote_" + Float.toString(vote).replace(".", "_").replace(",", "_");
			
			final TextView tvVotesText = (TextView) this.mLayout.findViewWithTag(voteTag);
			tvVotesText.setText(Integer.toString(count));
		}

		((TextView) this.mLayout.findViewById(R.id.VoteAverage)).setText(String.format("%.2f", gcVote.voteAverage).replace(",", "."));
		((TextView) this.mLayout.findViewById(R.id.VoteMedian)).setText(String.format("%.2f", gcVote.voteMedian).replace(",", "."));
		
		final TextView voteCount = ((TextView) this.mLayout.findViewById(R.id.VoteCount)); 
		voteCount.setText(Integer.toString(gcVote.voteCount));
		// Change color to green, if more than 10 votes have been supplied
		if (gcVote.voteCount >= 10)
		{
			voteCount.setTextColor(0xFF00CC66);
		}
	}
}
