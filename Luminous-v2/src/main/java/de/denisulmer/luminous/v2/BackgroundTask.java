package de.denisulmer.luminous.v2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BackgroundTask extends AsyncTask<String, Void, String>
{

    @Override
    protected String doInBackground(String... strings)
    {
        String host = strings[0];
        int port = Integer.parseInt(strings[1]);

        try
        {
            Socket socket = new Socket();
            int TIMEOUT = 5000;
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            dataOutputStream.writeBytes("{\"message\": \"client controller\"}\n");

            if (bufferedReader.readLine().contains("accept"))
            {
                dataOutputStream.writeBytes("{\"message\": \"request config\"}\n");
            }
            String config = bufferedReader.readLine();
            if (config.contains("config"))
            {
                socket.close();
                return config;
            }
        }
        catch (Exception e)
        {
            String TAG = "BackgroundTask";
            Log.d(TAG, "Error connecting to pilight-daemon: " + e.toString());
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Core.refreshUI(s);
    }
}
