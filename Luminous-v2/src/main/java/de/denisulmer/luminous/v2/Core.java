package de.denisulmer.luminous.v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Core
{
    private static MainActivity mMainActivity;
    private static SetupActivity mSetupActivity;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mSharedPreferencesEditor;
    private static ViewPager mViewPager;
    private static Handler mHandler;
    private static Timer mTimer;
    private static Vector<Device> mDevices;

    private static String TAG = "Core";

    public static void Initiate(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
        mSharedPreferences = mMainActivity.getSharedPreferences("de.denisulmer.luminous.v2:general", mMainActivity.MODE_PRIVATE);
        Log.d(TAG, "Initiation of core successful");
    }

    public static void setSetupActivity(SetupActivity setupActivity)
    {
        mSetupActivity = setupActivity;
        Log.d(TAG, "Set new link for SetupActivity");
    }
    public static SetupActivity getSetupActivity()
    {
        Log.d(TAG, "Returned current link to SetupActivity");
        return mSetupActivity;
    }

    public static void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
        Log.d(TAG, "Set new link for MainActivity");
    }
    public static MainActivity getMainActivity()
    {
        Log.d(TAG, "Returned current link to MainActivity");
        return mMainActivity;
    }

    public static String getSavedHostname()
    {
        String s = mSharedPreferences.getString("Hostname", "");
        Log.d(TAG, "Reading hostname from shared preferences: " + s);
        return s;
    }
    public static void setSavedHostname(String s)
    {
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mSharedPreferencesEditor.putString("Hostname", s);
        mSharedPreferencesEditor.commit();
        Log.d(TAG, "Setting hostname in shared preferences to " + s);
    }

    public static int getSavedPort()
    {
        int i = mSharedPreferences.getInt("Port", 0);
        Log.d(TAG, "Reading port from shared preferences: " + i);
        return i;
    }
    public static void setSavedPort(int i)
    {
        mSharedPreferencesEditor = mSharedPreferences.edit();
        mSharedPreferencesEditor.putInt("Port", i);
        mSharedPreferencesEditor.commit();
        Log.d(TAG, "Setting port in shared preferences to " + i);
    }

    public static void setLoopAutostart(boolean b)
    {
        Log.d(TAG, "Setting loop-autostart to " + b);
        boolean mLoopAutostart = b;
    }

    public static void startLoop()
    {
        mTimer = new Timer();
        mHandler = new Handler();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        try {
                            BackgroundTask backgroundTask = new BackgroundTask();
                            backgroundTask.execute(getSavedHostname(), getSavedPort() + "");
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        int BGTASK_DELAY = 5000;
        mTimer.schedule(doAsynchronousTask, 0, BGTASK_DELAY);
        Log.d(TAG, "Started loop");
    }
    public static void stopLoop()
    {
        if (mTimer != null)
        {
            mTimer.cancel();
        }
        Log.d(TAG, "Stopped loop");
    }

    public static void startSetupActivity()
    {
        Intent setupIntent = new Intent(mMainActivity.getBaseContext(), SetupActivity.class);
        mMainActivity.startActivity(setupIntent);
    }
    public static void startMainActivity()
    {
        Intent mainIntent = new Intent(mSetupActivity.getBaseContext(), MainActivity.class);
        mSetupActivity.startActivity(mainIntent);
    }

    public static void refreshUI(String s)
    {
        Log.d(TAG, "Refreshing UI with new configuration");
        mDevices = parseConfig(s);
        Log.d(TAG, "Found " + mDevices.size() + " devices");
        List<String> locations = getLocations(mDevices);
        Log.d(TAG, "Found " + locations.size() + " locations");
        List<Fragment> fragments = getFragments(locations, mDevices);

        int currentItem = 0;
        if (mViewPager != null)
        {
            currentItem = mViewPager.getCurrentItem();
        }

        LocationPageAdapter mLocationPageAdapter = new LocationPageAdapter(mMainActivity.getSupportFragmentManager(), fragments);
        mViewPager = (ViewPager) mMainActivity.findViewById(R.id.mainactivity_viewpager);
        mViewPager.setAdapter(mLocationPageAdapter);
        mViewPager.setCurrentItem(currentItem);
        mLocationPageAdapter.notifyDataSetChanged();

    }

    public static List<Fragment> getFragments(List<String> locations, Vector<Device> devices)
    {
        List<Fragment> fragments = new ArrayList<Fragment>();
        for(String location : locations)
        {
            Fragment fragment = LocationFragment.newInstance(location);
            fragments.add(fragment);
        }
        return fragments;
    }

    public static List<Device> getDevicesForLocation(String location)
    {
        List<Device> locationDevices = new ArrayList<Device>();
        for( Device device : mDevices )
        {
            if (device.getLocation().equals(location))
            {
                locationDevices.add(device);
            }
        }
        return locationDevices;
    }

    public static List<String> getLocations(Vector<Device> devices)
    {
        Log.d(TAG, "Extracting locations from devices");
        List<String> locations = new ArrayList<String>();
        for (Device device : devices)
        {
            if (!locations.contains(device.getLocation()))
            {
                locations.add(device.getLocation());
            }
        }
        return locations;
    }

    public static void sendLine(final String s)
    {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try
                {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(getSavedHostname(), getSavedPort()), 5000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataOutputStream.writeBytes("{\"message\": \"client controller\"}\n");
                    dataOutputStream.writeBytes(s + "\n");
                    socket.close();
                    Log.d(TAG, "Sending to pilight: " + s);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    public static Vector<Device> parseConfig(String s)
    {
        Vector<Device> devices = new Vector<Device>();
        try
        {
            JSONObject jsonConfig = new JSONObject(s).getJSONObject("config");
            Iterator<String> configIterator = jsonConfig.keys();
            while (configIterator.hasNext())
            {
                String room = configIterator.next();
                try
                {
                    JSONObject jsonRoom = new JSONObject(jsonConfig.get(room).toString());
                    Iterator<String> roomIterator = jsonRoom.keys();

                    while (roomIterator.hasNext())
                    {
                        String socket = roomIterator.next();
                        try
                        {
                            Object obj = jsonRoom.get(socket);

                            // Name and order are unimportant
                            if (! ( socket.equals("name") || socket.equals("order") ) )
                            {
                                JSONObject jsonSocket = new JSONObject(jsonRoom.get(socket).toString());
                                Iterator<String> socketIterator = jsonSocket.keys();

                                // Temporary object
                                Device tempItem = new Device();
                                tempItem.setName(socket);

                                while (socketIterator.hasNext())
                                {
                                    String detail = socketIterator.next();
                                    try
                                    {
                                        Object value = jsonSocket.get(detail);

                                        if (detail.equals("name"))
                                        {
                                            tempItem.setDescription(value.toString());
                                            tempItem.setLocation(room.toString());
                                        }
                                        if (detail.equals("type"))
                                        {
                                            tempItem.setType(Integer.parseInt(value.toString()));
                                        }
                                        if (detail.equals("protocol"))
                                        {
                                            tempItem.setProtocol(value.toString());
                                        }
                                        if (detail.equals("id"))
                                        {
                                            JSONArray jsonArray = new JSONArray(value.toString());
                                            tempItem.setUnit(Integer.parseInt(((JSONObject) jsonArray.get(0)).get("unit").toString()));
                                            tempItem.setId(Integer.parseInt(((JSONObject) jsonArray.get(0)).get("id").toString()));
                                        }
                                        if (detail.equals("state"))
                                        {
                                            tempItem.setState(value.toString());
                                        }
                                    }
                                    catch(Exception e)
                                    {

                                    }
                                }

                                // Add new configuration item to vector list
                                devices.add(tempItem);
                            }
                        }
                        catch(Exception e)
                        {

                        }
                    }
                }
                catch (Exception e)
                {
                    // Something went wrong!
                }
            }
            Log.d(TAG, "Configuration parsed successfully");
            return devices;
        }
        catch(Exception e)
        {

        }
        return null;
    }
}
