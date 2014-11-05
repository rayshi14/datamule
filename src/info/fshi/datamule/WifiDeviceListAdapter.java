package info.fshi.datamule;

import info.fshi.datamule.network.BTCom;
import info.fshi.datamule.packet.BasicPacket;
import info.fshi.datamule.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WifiDeviceListAdapter extends ArrayAdapter<WifiDevice> {
	Context context; 
	int layoutResourceId;
	ArrayList<WifiDevice> deviceList = new ArrayList<WifiDevice>();

	public WifiDeviceListAdapter(Context context, int layoutResourceId, ArrayList<WifiDevice> deviceList) {

		super(context, layoutResourceId, deviceList);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.deviceList = deviceList;
	}

	public ArrayList<WifiDevice> getDeviceList(){
		return deviceList;
	}
	
	/**
	 * comparator to sort wifi device arraylist
	 * @author fshi
	 */
	public class WifiDeviceConnStateComparator implements Comparator<WifiDevice>
	{
		@Override
		public int compare(WifiDevice lhs, WifiDevice rhs) {
			// TODO Auto-generated method stub
			return lhs.getConnState() - rhs.getConnState();
		}
	}
	
	public void sortList(){
		Collections.sort(deviceList, new WifiDeviceConnStateComparator());
	}
	
	/**
	 * add a device to the adapter, sort the new list then
	 * @param wifiDevice
	 */
	public void addDevice(WifiDevice wifiDevice) {
		deviceList.add(wifiDevice);
		Collections.sort(deviceList, new WifiDeviceConnStateComparator());
	}

	public void setDeviceAction(String MAC, int action) {
		for (int i=0; i<deviceList.size(); i++){
			if(deviceList.get(i).getMAC().equalsIgnoreCase(MAC)){
				switch(action){
				case BTCom.BT_CLIENT_CONNECTED:
					deviceList.get(i).setConnState(Constants.STATE_CLIENT_CONNECTED);
					break;
				case BTCom.BT_CLIENT_CONNECT_FAILED:
					deviceList.get(i).setConnState(Constants.STATE_CLIENT_FAILED);
					break;
				case BasicPacket.PACKET_TYPE_TIMESTAMP_ACK:
					deviceList.get(i).setConnState(Constants.STATE_CLIENT_CONNECTED);
					break;
				default:
					break;
				}
			}
		}
		Collections.sort(deviceList, new WifiDeviceConnStateComparator());
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DeviceHolder holder = null;

		if(row == null)
		{
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new DeviceHolder();

			holder.deviceMac = (TextView)row.findViewById(R.id.device_mac);
			holder.deviceName = (TextView)row.findViewById(R.id.device_name);
			holder.deviceConnect = (Button) row.findViewById(R.id.device_connect);

			row.setTag(holder);
		}
		else
		{
			holder = (DeviceHolder)row.getTag();
		}

		WifiDevice device = deviceList.get(position);

		holder.deviceMac.setText(device.getMAC());
		if(device.getName() != null){
			holder.deviceName.setText(device.getName());
		}
		
		holder.deviceConnect.setEnabled(true);
		holder.deviceConnect.setTextColor(Color.WHITE);
		switch(device.getConnState()){
		case Constants.STATE_CLIENT_CONNECTED:
			holder.deviceConnect.setBackgroundResource(R.drawable.connected_button);
			holder.deviceConnect.setText(Constants.STATE_CONNECTED);
			break;
		case Constants.STATE_CLIENT_FAILED:
			holder.deviceConnect.setBackgroundResource(R.drawable.connect_failed_button);
			holder.deviceConnect.setText(Constants.STATE_CONNECTION_FAILED);
			break;
		case Constants.STATE_CLIENT_UNCONNECTED:
			holder.deviceConnect.setBackgroundResource(R.drawable.connect_button);
			holder.deviceConnect.setText(Constants.STATE_NOT_CONNECTED);
			break;
		case Constants.STATE_CLIENT_OUTDATED:
			holder.deviceConnect.setBackgroundResource(R.drawable.connect_outdated_button);
			holder.deviceConnect.setText(Constants.STATE_NOT_CONNECTED);
			holder.deviceConnect.setTextColor(Color.BLACK);
			holder.deviceConnect.setEnabled(false);
			break;
		default:
			break;

		}
		holder.deviceConnect.setOnClickListener(device.getOnClickListener());

		return row;
	}

	static class DeviceHolder
	{
		TextView deviceName;
		TextView deviceMac;
		Button deviceConnect;
	}
}
