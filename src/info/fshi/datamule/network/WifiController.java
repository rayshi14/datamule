package info.fshi.datamule.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * register wifi broadcast receiver here and use a callback to send message to main activity
 * @author fshi
 *
 */
public class WifiController {
	private Messenger mMessenger;
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	Context mActivity;
	PeerListListener mPeerListListener;

	private static final String TAG = "WifiController";

	private final IntentFilter intentFilter = new IntentFilter();
	
	public WifiController(Context mContext, Handler handler, PeerListListener listener){
		// do initialization here
		mActivity = mContext;
		mMessenger = new Messenger(handler);
		mPeerListListener = listener;
		mManager = (WifiP2pManager) mActivity.getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mManager.initialize(mActivity, mActivity.getMainLooper(), null);
		mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel);

		// register intent

		//  Indicates a change in the Wi-Fi P2P status.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

		// Indicates a change in the list of available peers.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

		// Indicates the state of Wi-Fi P2P connectivity has changed.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

		// Indicates this device's details have changed.
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		mActivity.registerReceiver(mReceiver, intentFilter);
	}

	/**
	 * start a scan, isStart to check if it is to start or to cancel a scan
	 * @param isStart
	 */
	public void startWifiScan(){
		// start wifi manager and call the discoverpeers method
		mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
			@Override
			public void onSuccess() {
				Log.d(TAG, "discovery success");
				Message msg=Message.obtain();
				msg.what = WifiCom.WHAT_WIFI_DISCOVERY_SUCCESS;
				try {
					mMessenger.send(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final Handler discoveryHandler = new Handler();
				discoveryHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						stopWifiScan();
					}
				}, 10000);
			}

			@Override
			public void onFailure(int reasonCode) {
				Log.d(TAG, "discovery fail");
			}
		});
	}

	public void stopWifiScan(){
		Log.d(TAG, "scanning stopped");
		mManager.stopPeerDiscovery(mChannel, null);
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
	public void connectWifiServer(String mac){
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
				Log.d(TAG, "Wifi p2p is is enabled or disabled");
				// Determine if Wifi P2P mode is enabled or not, alert
				// the Activity.
				int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
				if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

				} else {

				}

			} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
				// Call WifiP2pManager.requestPeers() to get a list of current peers
				Log.d(TAG, "peer list change");
				mManager.requestPeers(mChannel, mPeerListListener);
			} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
				// Respond to new connection or disconnections
			} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				// Respond to this device's wifi state changing
			}
		}
	}
}
