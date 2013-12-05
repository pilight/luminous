package de.denisulmer.luminous.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class MainActivity extends ActionBarActivity
{
    private Timer mTimer;
    private Handler mHandler;
    private SharedPreferences mSharedPrefs;
    private SharedPreferences.Editor mEditor;
    private boolean mPauseTask = true;
    private MainActivity mMainActivity;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initiate core functions
        Core.Initiate(this);

        // Look for saved configuration
        String mHost = Core.getSavedHostname();
        int mPort = Core.getSavedPort();

        if (mHost != null &&  mPort > 0)
        {
            // Found configuration, try to connect
            Log.d(TAG, "Found configuration. Starting loop");
            Core.setLoopAutostart(true);
        }
        else
        {
            // No configuration found, show SetupActivity
            Log.d(TAG, "No settings found, opening SetupActivity");
            Core.startSetupActivity();
            finish();
        }
    }

    public void socketOnClick(View v)
    {
        try
        {
            Device device = (Device) v.getTag();
            String newState = device.getState().equals("off") ? "on" : "off";
            device.setState(newState);
            Core.sendLine("{\"message\":\"send\",\"code\":{\"location\": \""+device.getLocation()+"\",\"device\": \""+device.getName()+"\",\"state\": \""+newState+"\"}}");
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //here you can handle orientation change
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause called");
        Core.stopLoop();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume called");
        Core.startLoop();
    }
}
