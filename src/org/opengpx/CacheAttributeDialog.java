package org.opengpx;

import java.util.ArrayList;

import org.opengpx.R;
import org.opengpx.lib.geocache.Attribute;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class CacheAttributeDialog extends AlertDialog.Builder
{

	private Context mContext;
	private ArrayList<Attribute> mAttributes;
	
	/**
	 * 
	 * @param context
	 * @param attributes
	 */
	public CacheAttributeDialog(Context context, ArrayList<Attribute> attributes) 
	{
		super(context);
		
		this.mContext = context;
		this.mAttributes = attributes;
		
		this.initialize();
	}

	/**
	 * 
	 */
	private void initialize()
	{
		// dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		this.setTitle("Attributes");
		
		final LayoutInflater inflater = LayoutInflater.from(this.mContext);
		final View layout = inflater.inflate(R.layout.attributes, null);
		
		this.setView(layout);
		
		final LinearLayout llAttributes = (LinearLayout) layout.findViewById(R.id.LinearLayoutAttributes);
	
		String strAttributes = "";
		for (int i = 0; i < this.mAttributes.size(); i++)
		{
			final TextView tv = new TextView(this.mContext);
			tv.setTextSize(12);
	
			if (this.mAttributes.get(i).flag)
			{
				tv.setPaintFlags(tv.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
				tv.setTextColor(0xFF00CC66);
				strAttributes = this.mAttributes.get(i).name;
			}
			else
			{
				tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				tv.setTextColor(0xFFCC0066);
				strAttributes = this.mAttributes.get(i).name;
			}
			tv.setText(strAttributes);
			llAttributes.addView(tv);
		}
		// tvAttributesText.setText(Html.fromHtml(strAttributes));
	
		this.setNeutralButton(R.string.button_ok, new OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int arg1) 
			{
				dialog.dismiss();
			}
			
		});
	}
}
