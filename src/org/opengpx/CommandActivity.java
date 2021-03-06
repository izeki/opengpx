package org.opengpx;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.opengpx.Preferences;

import org.opengpx.lib.CacheDatabase;
import org.opengpx.lib.UserDefinedVariables;
import org.opengpx.lib.tools.Command;
import org.opengpx.lib.tools.CommandType;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Martin Preishuber
 *
 */
public class CommandActivity extends Activity 
{
	private Spinner mSpinnerCommandTypes;
	private TextView mCommandText;
	private TextView mResult;
	private String[] mCommandTypes;
	private CacheDatabase mCacheDatabase;
	private UserDefinedVariables mVariables;
	
	/**
	 * 
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.command);

        this.setTitle("OpenGPX - Toolbox");
        final Bundle bunExtras = this.getIntent().getExtras();
        final String strVariableId = (String) bunExtras.get("varid");

        final Preferences preferences = new Preferences(this);
    	this.setRequestedOrientation(preferences.getScreenOrientation());

        this.mCacheDatabase = CacheDatabase.getInstance();
	    this.mCommandText = (TextView) this.findViewById(R.id.CommandEntry);
	    this.mResult = (TextView) this.findViewById(R.id.CommandResult);
	    this.mVariables = this.mCacheDatabase.getVariables(strVariableId);

	    this.loadCommandTypes();
	    this.setButtonAction();
	}

    /**
     * 
     */
    @Override
    public void onPause()
    {
    	super.onPause();

    	if (this.mVariables.size() > 0)
    		this.mCacheDatabase.storeVariables(this.mVariables);
    }

	/**
	 * 
	 */
	private void loadCommandTypes()
	{
		int i = 0;   
		mCommandTypes = new String[CommandType.values().length];   
	    for (CommandType ct: CommandType.values()) 
	    {   
	    	mCommandTypes[i++] = Command.getCommandTypeName(ct);   
	    }

		mSpinnerCommandTypes = (Spinner) this.findViewById(R.id.CommandType);
		ArrayAdapter<String> aaCommandTypes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCommandTypes);
		aaCommandTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCommandTypes.setAdapter(aaCommandTypes);
	}

	/**
	 * 
	 */
	private void setButtonAction()
	{
		final Button btnCalcButton = (Button) findViewById(R.id.CalcButton);
		btnCalcButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                // Perform action on click
            	final CommandType commandType = CommandType.values()[mSpinnerCommandTypes.getSelectedItemPosition()];
            	final String strCommandText = mCommandText.getText().toString().trim();
            	if (strCommandText.length() == 0)
            	{
            		Toast enterText = Toast.makeText(mCommandText.getContext(), R.string.no_text_entered, Toast.LENGTH_LONG);
            		enterText.show();
            	} 
            	else 
            	{
            		
	            	final Command command = new Command(commandType, strCommandText, mVariables);
	            	String strResult = "";
	            	try
	            	{
	            		strResult = command.execute();
	            		strResult = strCommandText.concat("\n=\n").concat(strResult);
	            	} 
	            	catch (Exception e)
	            	{
						final Writer wrResult = new StringWriter();
						final PrintWriter pw = new PrintWriter(wrResult);
	            		e.printStackTrace(pw);
	            		strResult = wrResult.toString();
	            	}
	
	            	if (commandType == CommandType.Calculate)
	            	{
	            		if (mVariables.size() > 0)
	            		{
	                		// mCacheDatabase.storeVariables(mVariables);
	            			strResult += "\n\nVariables:\n\n".concat(mVariables.toString());
	            		}
	            	}
            	
	            	mResult.setText(strResult);
            	}
            }
        });
		
		// Set button action for clear button
		final Button btnClearButton = (Button) findViewById(R.id.ClearButton);
		btnClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) 
            {
                // Perform action on click
            	mCommandText.setText("");
            }
		});
		
		// Set button action for help button
		final Button btnHelpButton = (Button) findViewById(R.id.CommandHelpButton);
		btnHelpButton.setOnClickListener(new View.OnClickListener() 
		{
            public void onClick(View v) 
            {
            	CommandType commandType = CommandType.values()[mSpinnerCommandTypes.getSelectedItemPosition()];
            	
            	if (commandType == CommandType.Calculate)
            	{
            		final AlertDialog.Builder builder = new AlertDialog.Builder(mCommandText.getContext());
            		builder.setTitle(R.string.help_command_custom_functions);
            		builder.setMessage(mCommandText.getContext().getString(R.string.help_command_calculate));
            		builder.setCancelable(false);
            		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() 
            		{
            			public void onClick(DialogInterface dialog, int which) 
            			{
            				dialog.cancel();
            			} 
            		}); 
            		final AlertDialog alertDialog = builder.create();
            	    alertDialog.show();
            	} 
            	else 
            	{
	                // Perform action on click
	            	final Toast help = Toast.makeText(mCommandText.getContext(), R.string.no_help_for_command_type, Toast.LENGTH_LONG);
	            	help.show();
            	}
            }
		});
		
	}
}
