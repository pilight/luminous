package de.denisulmer.luminous.v2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class LocationFragment extends Fragment
{
    private String mTitle;

    public static LocationFragment newInstance(String location)
    {
        LocationFragment locationFragment = new LocationFragment();
        locationFragment.setTitle(location);
        return locationFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.locationfragment_layout, container, false);
        ListView listView = (ListView) view.findViewById(R.id.locationfragment_listview);
        List<Device> devicesInLocation = Core.getDevicesForLocation(mTitle);
        listView.setAdapter(new DeviceListAdapter(Core.getMainActivity(), R.layout.listview_row_socket, devicesInLocation));
        return view;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String s)
    {
        this.mTitle = s;
    }
}