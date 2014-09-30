package info.fshi.datamule;

import info.fshi.datamule.data.DbBTComLog;
import info.fshi.datamule.data.DbBTDevice;
import info.fshi.datamule.data.DbBTScanLog;
import info.fshi.datamule.data.DbHelper;
import info.fshi.datamule.network.BTCom;
import info.fshi.datamule.network.BTController;
import info.fshi.datamule.network.BTScanningAlarm;
import info.fshi.datamule.packet.BasicPacket;
import info.fshi.datamule.settings.BTSettings;
import info.fshi.datamule.utils.Constants;
import info.fshi.datamule.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


public class DataMule extends Activity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static Context mContext;

	/**
	 * List of request code
	 */
	private final int REQUEST_BT_ENABLE = 1;
	private final int REQUEST_BT_DISCOVERABLE = 11;

	private int RESULT_BT_DISCOVERABLE_DURATION = 300;

	private static ArrayList<BTDevice> deviceList = new ArrayList<BTDevice>();
	private static BTDeviceListAdapter deviceListAdapter;

	private BluetoothAdapter mBluetoothAdapter = null;

	ArrayList<String> devicesFound = new ArrayList<String>();

	private static BTController mBTController;

	// database helper
	DbHelper dbHelper = null;

	private long scanStartTimestamp = System.currentTimeMillis() - 100000;

	// bool to indicate if scanning
	boolean mScanning = false;

	public static ArrayList<String> goodDevices = new ArrayList<String>(Arrays.asList("BC:EE:7B:B0:7E:5A","F8:D0:BD:95:7E:28","D8:50:E6:34:15:4D"));


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_data_mule);

		mContext = this;

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			finish();
			return;
		}


		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
		}
		else{			
			// start bluetooth utils
			initBluetoothUtil();
		}

		dbHelper = new DbHelper(mContext);

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(BTFoundReceiver, filter);

		// update shared preference
		SharedPreferencesUtil.savePreferences(mContext, Constants.SP_RADIO_SCAN_DURATION, Constants.DEFAULT_BT_SCAN_DURATION);
		SharedPreferencesUtil.savePreferences(mContext, Constants.SP_RADIO_SCAN_DURATION_ID, Constants.DEFAULT_BT_SCAN_DURATION_ID);
		SharedPreferencesUtil.savePreferences(mContext, Constants.SP_RADIO_SCAN_INTERVAL, Constants.DEFAULT_BT_SCAN_INTERVAL);
		SharedPreferencesUtil.savePreferences(mContext, Constants.SP_RADIO_SCAN_INTERVAL_ID, Constants.DEFAULT_BT_SCAN_INTERVAL_ID);
		SharedPreferencesUtil.savePreferences(mContext, Constants.SP_CHECKBOX_BT_AUTO_SCAN, false);


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		//inflate the device list
		List<DbBTDevice> dbDeviceList = dbHelper.getAllBTDevices();
		for(DbBTDevice dbDevice : dbDeviceList){
			BluetoothDevice tmpBTDevice = mBluetoothAdapter.getRemoteDevice(dbDevice.getMac());
			BTDevice tmpDevice = new BTDevice(tmpBTDevice);
			tmpDevice.setConnState(Constants.STATE_CLIENT_OUTDATED);
			tmpDevice.setRssi(dbDevice.getRssi());
			deviceList.add(tmpDevice);
		}

		// adapters
		deviceListAdapter = new BTDeviceListAdapter(mContext, R.layout.bt_deivce, deviceList);

	}

	private void initBluetoothUtil(){
		BTServiceHandler handler = new BTServiceHandler();
		mBTController = new BTController(mContext, handler);
		mBTController.startBTServer();
		// clean up
		BTScanningAlarm.stopScanning(mContext);
	}
	
	@Override
	protected void onDestroy() {
		Log.d(Constants.TAG_APPLICATION, "onDestroy()");
		BTScanningAlarm.stopScanning(mContext);
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d(Constants.TAG_APPLICATION, "onStop()");
		super.onStop();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.d(Constants.TAG_APPLICATION, "onPause()");
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.d(Constants.TAG_APPLICATION, "onRestart()");
		super.onRestart();
	}

	@Override
	protected void onResume() {
		Log.d(Constants.TAG_APPLICATION, "onResume()");
		super.onResume();
	}


	// Create a BroadcastReceiver for actions
	BroadcastReceiver BTFoundReceiver = new BTServiceBroadcastReceiver();

	class BTServiceBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Log.d(Constants.TAG_APPLICATION, "get a device : " + String.valueOf(device.getAddress()));
				/*
				 * -30dBm = Awesome
				 * -60dBm = Good
				 * -80dBm = OK
				 * -90dBm = Bad
				 */
				short rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
				int deviceIndex = -1;
				for(int i=0; i < deviceList.size(); i++){
					if (deviceList.get(i).getMAC().equals(device.getAddress())){
						deviceIndex = i;
					}
				}
				DbBTDevice myBTDevice = new DbBTDevice();
				if (deviceIndex < 0){ // device not exist
					BTDevice btDevice = new BTDevice(device);
					btDevice.setRssi(rssi);
					btDevice.setConnState(Constants.STATE_CLIENT_UNCONNECTED);
					BTConnectButtonOnClickListener onClickListener = new BTConnectButtonOnClickListener(btDevice, mBTController);
					btDevice.setOnClickListener(onClickListener);
					deviceIndex = deviceList.size();
					deviceList.add(btDevice);
					myBTDevice.setMac(btDevice.getMAC());
					myBTDevice.setName(btDevice.getName());
					myBTDevice.setRssi(btDevice.getRssi());
					myBTDevice.setState(btDevice.getConnState());
					myBTDevice.setCounter(btDevice.getRetryCounter());
				}
				else{ // device already found
					BTDevice btDevice = deviceList.get(deviceIndex);
					btDevice.setRssi(rssi);
					btDevice.setConnState(Constants.STATE_CLIENT_UNCONNECTED);
					BTConnectButtonOnClickListener onClickListener = new BTConnectButtonOnClickListener(btDevice, mBTController);
					btDevice.setOnClickListener(onClickListener);
					myBTDevice.setMac(btDevice.getMAC());
					myBTDevice.setName(btDevice.getName());
					myBTDevice.setRssi(btDevice.getRssi());
					myBTDevice.setState(Constants.STATE_CLIENT_OUTDATED);
					myBTDevice.setCounter(btDevice.getRetryCounter());
				}
				dbHelper.insertBTDevice(myBTDevice);
				if(!devicesFound.contains(myBTDevice.getMac())){
					devicesFound.add(myBTDevice.getMac());
					DbBTScanLog dbScanLog = new DbBTScanLog();
					dbScanLog.setTimestamp(System.currentTimeMillis());
					dbScanLog.setDeviceId(dbHelper.getBTDevice(myBTDevice.getMac()).getId());
					dbScanLog.setRssi(rssi);
					dbScanLog.setScanDuration(SharedPreferencesUtil.loadSavedPreferences(context, Constants.SP_RADIO_SCAN_DURATION, Constants.DEFAULT_BT_SCAN_DURATION));
					dbScanLog.setBaselineTimestamp(scanStartTimestamp);
					dbHelper.insertBTScanLog(dbScanLog);
				}
				deviceListAdapter.sortList();
				deviceListAdapter.notifyDataSetChanged();
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				mScanning = true;
				Log.d(Constants.TAG_APPLICATION, "Discovery process has been started: " + String.valueOf(System.currentTimeMillis()));
				if(System.currentTimeMillis() - scanStartTimestamp > SharedPreferencesUtil.loadSavedPreferences(context, Constants.SP_RADIO_SCAN_DURATION, Constants.DEFAULT_BT_SCAN_DURATION)){
					//a new scan has been started
					Log.d(Constants.TAG_APPLICATION, "Timestamp updated: " + String.valueOf(System.currentTimeMillis()));
					for (BTDevice device : deviceList){
						device.setConnState(Constants.STATE_CLIENT_OUTDATED);
						deviceListAdapter.notifyDataSetChanged();
					}
					devicesFound = new ArrayList<String>();
					scanStartTimestamp = System.currentTimeMillis();
				}
				invalidateOptionsMenu();
			}
			else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				if(mScanning){
					mScanning = false;
					invalidateOptionsMenu();

					Log.d(Constants.TAG_APPLICATION, "Discovery process has been stopped: " + String.valueOf(System.currentTimeMillis()));

					for(BTDevice device : deviceList){
						if(goodDevices.contains(device.getMAC())){
							Log.d(Constants.TAG_ACT_TEST, "good list contains " + device.getMAC());
							device.resetRetryCounter();
							/**
							 * make decisions on whether to choose bluetooth to communicate OR to scan using WiFi-Direct
							 */
							//mBTService.connect(device.getRawDevice());
							device.setConnectionStartTime(System.currentTimeMillis());
						}
					}
				}
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		switch (requestCode){
		case REQUEST_BT_ENABLE:
			if (resultCode == RESULT_OK) {
				Log.d(Constants.TAG_APPLICATION, "Bluetooth is enabled by the user.");
				// start bluetooth utils
				initBluetoothUtil();
				Intent discoverableIntent = new
						Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, RESULT_BT_DISCOVERABLE_DURATION);
				startActivityForResult(discoverableIntent, REQUEST_BT_DISCOVERABLE);
			}
			else{
				Log.d(Constants.TAG_APPLICATION, "Bluetooth is not enabled by the user.");
			}
			break;
		case REQUEST_BT_DISCOVERABLE:
			if (resultCode == RESULT_CANCELED){
				Log.d(Constants.TAG_APPLICATION, "Bluetooth is not discoverable.");
			}
			else{
				Log.d(Constants.TAG_APPLICATION, "Bluetooth is discoverable by 300 seconds.");
			}
			break;
		default:
			break;
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.data_mule, menu);
		if (!mScanning) {
			menu.findItem(R.id.action_stop).setVisible(false);
			menu.findItem(R.id.action_scan).setVisible(true);
			menu.findItem(R.id.menu_refresh).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else {
			menu.findItem(R.id.action_stop).setVisible(true);
			menu.findItem(R.id.action_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.actionbar_indeterminate_progress);
		}
		menu.findItem(R.id.bt_set_autoscan_start).setChecked(SharedPreferencesUtil.loadSavedPreferences(mContext, Constants.SP_CHECKBOX_BT_AUTO_SCAN, false));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		BTSettings btSetting;
		int id = item.getItemId();
		switch (id){
		case R.id.action_scan:
			Log.d(Constants.TAG_APPLICATION, "Click scan button");
			mBTController.startBTScan(true);
			break;
		case R.id.action_stop:
			Log.d(Constants.TAG_APPLICATION, "Click stop button");
			mBTController.startBTScan(false);
			break;
		case R.id.bt_set_scaninterval:
			Log.d(Constants.TAG_APPLICATION, "Set scan interval");
			btSetting = new BTSettings(mContext, Constants.BT_SCAN_INTERVAL_SETUP_ID);
			btSetting.show("SET SCAN INTERVAL");
			break;
		case R.id.bt_set_scantime:
			Log.d(Constants.TAG_APPLICATION, "Set scan time");
			btSetting = new BTSettings(mContext, Constants.BT_SCAN_DURATION_SETUP_ID);
			btSetting.show("SET SCAN DURATION");
			break;
		case R.id.bt_set_autoscan_start:
			if(item.isChecked()){ //autoscan will be stopped
				SharedPreferencesUtil.savePreferences(mContext, Constants.SP_CHECKBOX_BT_AUTO_SCAN, false);
				item.setChecked(false);
				stopAutoScanTask();
			}
			else{
				SharedPreferencesUtil.savePreferences(mContext, Constants.SP_CHECKBOX_BT_AUTO_SCAN, true);
				item.setChecked(true);
				startAutoScanTask();
			}
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = null;
			switch(getArguments().getInt(ARG_SECTION_NUMBER)){
			case 1:
				rootView = inflater.inflate(R.layout.fragment_data_mule, container, false);
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fragment_device_list, container, false);
				ListView lvList = (ListView) rootView.findViewById(R.id.device_list);
				lvList.setAdapter(deviceListAdapter);
				lvList.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(mContext, BTDeviceScanHistory.class);
						intent.putExtra(Constants.INTENT_EXTRA_DEVICE_MAC, deviceList.get(position).getMAC());
						startActivity(intent);
					}
				});

				break;
			case 3:
				rootView = inflater.inflate(R.layout.fragment_data_mule, container, false);
				break;
			default:
				break;
			}
			return rootView;
		}
	}

	@SuppressLint("HandlerLeak") private class BTServiceHandler extends Handler {

		private final String TAG = "BTServiceHandler";

		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			String MAC = b.getString(BTCom.BT_DEVICE_MAC);
			switch(msg.what){
			case BTCom.BT_DATA:
				JSONObject json;
				int type;
				try {
					json = new JSONObject(b.getString(BTCom.BT_DATA_CONTENT));
					type = json.getInt(BasicPacket.PACKET_TYPE);
					switch(type){
					case BasicPacket.PACKET_TYPE_TIMESTAMP_DATA:
						Log.d(Constants.TAG_ACT_TEST, "receive data");
						Toast.makeText(mContext, "received data:" + json.getString(BasicPacket.PACKET_DATA),Toast.LENGTH_SHORT).show();
						JSONObject ack = new JSONObject();
						try {
							ack.put(BasicPacket.PACKET_TYPE, BasicPacket.PACKET_TYPE_TIMESTAMP_ACK);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Log.e(TAG, "json error");
							e.printStackTrace();
						}
						mBTController.sendToBTDevice(MAC, ack);
						break;
					case BasicPacket.PACKET_TYPE_TIMESTAMP_ACK:
						Log.d(Constants.TAG_ACT_TEST, "receive ack");
						long timeAckReceived = System.currentTimeMillis();
						Toast.makeText(mContext, "received ack",Toast.LENGTH_SHORT).show();
						mBTController.stopConnection(MAC);
						// update deviceList adapter
						deviceListAdapter.setDeviceAction(MAC, BasicPacket.PACKET_TYPE_TIMESTAMP_ACK);
						deviceListAdapter.notifyDataSetChanged();
						// update log adapter
						String name = null;
						long timeConnectionStart = timeAckReceived + 1;
						for(BTDevice device : deviceListAdapter.getDeviceList()){
							if (device.getMAC().equals(MAC)){
								name = device.getName();
								timeConnectionStart = device.getConnectionStartTime();
								break;
							}
						}
						if(name != null){
							DbBTComLog comLog = new DbBTComLog();
							DbBTDevice myDevice = dbHelper.getBTDevice(MAC);
							comLog.setTimestamp(timeAckReceived);
							comLog.setDeviceId(myDevice.getId());
							comLog.setRssi(myDevice.getRssi());
							comLog.setDelay(timeAckReceived - timeConnectionStart);
							dbHelper.insertBTComLog(comLog);
						}
						break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case BTCom.BT_CLIENT_CONNECTED:
				Log.d(TAG, "client connected");
				if (!DataMule.goodDevices.contains(MAC)) DataMule.goodDevices.add(MAC);
				// update main UI (current listview)
				deviceListAdapter.setDeviceAction(MAC, BTCom.BT_CLIENT_CONNECTED);
				deviceListAdapter.notifyDataSetChanged();
				Toast.makeText(mContext, "Client connected",Toast.LENGTH_SHORT).show();
				JSONObject dataTimestamp = new JSONObject();
				try {
					dataTimestamp.put(BasicPacket.PACKET_TYPE, BasicPacket.PACKET_TYPE_TIMESTAMP_DATA);
					dataTimestamp.put(BasicPacket.PACKET_DATA, String.valueOf(System.currentTimeMillis()));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mBTController.sendToBTDevice(MAC, dataTimestamp);
				break;
			case BTCom.BT_CLIENT_CONNECT_FAILED:
				Log.d(Constants.TAG_ACT_TEST, "client failed");
				if(DataMule.goodDevices.contains(MAC)){
					DataMule.goodDevices.remove(MAC);
				}
				deviceListAdapter.setDeviceAction(MAC, BTCom.BT_CLIENT_CONNECT_FAILED);
				deviceListAdapter.notifyDataSetChanged();
				Toast.makeText(mContext, "Connection failed, retry",Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * start automatic bluetooth scan, scanning alarm
	 */
	private void startAutoScanTask() {
		BTScanningAlarm scanningAlarm = new BTScanningAlarm(mContext, mBTController);
	}

	private void stopAutoScanTask() {
		BTScanningAlarm.stopScanning(mContext);
	}


}
