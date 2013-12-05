package de.denisulmer.luminous.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends ActionBarActivity
{
    EditText mEditTextHostname;
    EditText mEditTextPort;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setupactivity_layout);

        // Set link in core
        Core.setSetupActivity(this);

        // Set EditTexts
        mEditTextHostname = (EditText) findViewById(R.id.setupactivity_host);
        mEditTextPort = (EditText) findViewById(R.id.setupactivity_port);

        // Fill out form if there is saved data
        if (Core.getSavedHostname().length() > 0)
        {
            mEditTextHostname.setText((CharSequence) Core.getSavedHostname());
        }
        if (Core.getSavedPort() > 0)
        {
            mEditTextPort.setText((CharSequence) Integer.toString(Core.getSavedPort()));
        }
    }


    public void onButtonClick(View view)
    {
        Editable enteredHostname = mEditTextHostname.getText();
        Editable enteredPort = mEditTextPort.getText();

        // Check if fields are filled
        if (enteredHostname.length() == 0 || enteredPort.length() == 0)
        {
            // Toast about filling out all required fields
            Toast.makeText(getBaseContext(), getString(R.string.setupactivity_filloutfields), Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Save entered values for auto connect
            Core.setSavedHostname(enteredHostname.toString());
            Core.setSavedPort(Integer.parseInt(enteredPort.toString()));

            // Go back to the MainActivity
            Core.startMainActivity();
            finish();
        }
    }
}
