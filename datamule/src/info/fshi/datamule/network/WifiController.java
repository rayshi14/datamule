package info.fshi.datamule.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * register wifi broadcast receiver here and use a callback to send message to main activity
 * @author fshi
 *
 */
public class WifiController {

	public WifiController(){
		// do initialization here

	}

	/**
	 * start a scan, isStart to check if it is to start or to cancel a scan
	 * @param isStart
	 */
	public void startWifiScan(boolean isStart){
		// start wifi manager and call the discoverpeers method
		/*
		 * mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
    			@Override
			    public void onSuccess() {
			        ...
			    }
			
			    @Override
			    public void onFailure(int reasonCode) {
			        ...
			    }
			});
		 */

	}

	/**
	 * start WiFi server
	 */
	public void startWifiServer(){
		// start a sample wifi server instance to listen for incoming connections

	}

	/**
	 * connect to a device
	 * add necessary parameters here
	 */
	public void connectWifiServer(){
		// connect to a wifi server
		// call manager.connect method
		/*
		 * mManager.connect(mChannel, config, new ActionListener() {

		    @Override
		    public void onSuccess() {
		        //success logic
		    }

		    @Override
		    public void onFailure(int reason) {
		        //failure logic
		    }
		});
		 */


	}

	/**
	 * send sth to a WiFi device
	 */
	public void sendToWifiDevice(){

	}

	class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

		private WifiP2pManager mManager;
		private Channel mChannel;

		public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
			super();
			this.mManager = manager;
			this.mChannel = channel;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
				// Check to see if Wi-Fi is enabled and notify appropriate activity
			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				// Call WifiP2pManager.requestPeers() to get a list of current peers
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				// Respond to new connection or disconnections
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				// Respond to this device's wifi state changing
			}
		}
	}
}
