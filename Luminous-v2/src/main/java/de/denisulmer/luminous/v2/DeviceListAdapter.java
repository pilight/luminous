package de.denisulmer.luminous.v2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<Device> {

    private List<Device> mItems;
    private int mLayoutResourceId;
    private Context mContext;

    public DeviceListAdapter(Context context, int layoutResourceId, List<Device> items) {
        super(context, layoutResourceId, items);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DeviceHolder holder = null;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        row = inflater.inflate(mLayoutResourceId, parent, false);

        holder = new DeviceHolder();
        holder.device = mItems.get(position);
        holder.toggleButton = (ToggleButton) row.findViewById(R.id.device_toggle);
        holder.description = (TextView)row.findViewById(R.id.device_description);
        holder.toggleButton.setTag(holder.device);
        row.setTag(holder);
        setupItem(holder);
        return row;
    }

    private void setupItem(DeviceHolder holder) {
        holder.description.setText(holder.device.getDescription());

        if (holder.device.getState().equals("on"))
        {
            holder.toggleButton.setChecked(true);
        }
    }

    public static class DeviceHolder {
        Device device;
        TextView description;
        ToggleButton toggleButton;
    }
}