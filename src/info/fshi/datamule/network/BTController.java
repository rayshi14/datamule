package info.fshi.datamule.network;

import info.fshi.datamule.utils.Constants;
import info.fshi.datamule.utils.SharedPreferencesUtil;

import org.json.JSONObject;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Messenger;
/**
 * controller to manage interface of bluetooth
 * @author fshi
 *
 */
public class BTController {

	private BTCom mBTHelper;
	private Messenger mMessenger;
	private Context mContext;

	public BTController(Context context, Handler btHandler){
		mContext = context;

		// init bt utility
		mBTHelper = BTCom.getObject();
		mMessenger = new Messenger(btHandler);
		mBTHelper.setCallback(mMessenger);
		mBTHelper.setDuration(SharedPreferencesUtil.loadSavedPreferences(mContext, Constants.SP_RADIO_SCAN_DURATION, Constants.DEFAULT_BT_SCAN_DURATION));
		mBTHelper.setTimeout(Constants.BT_CLIENT_TIMEOUT);
	}

	/**
	 * start or stop a scan service
	 * @param isStart
	 */
	public void startBTScan(boolean isStart){
		mBTHelper.startScan(isStart);
	}

	/**
	 * start bluetooth server thread
	 */
	public void startBTServer(){
		mBTHelper.startServer();
	}
	
	/**
	 * stop bt server thread
	 */
	public void stopBTServer(){
		mBTHelper.stopServer();
	}

	/**
	 * connect to a device
	 * @param btDevice
	 */
	public void connectBTServer(BluetoothDevice btDevice){
		mBTHelper.connect(btDevice);
	}

	/**
	 * send sth to a BT device
	 * @param String mac
	 * @param JSONObject data
	 */
	public void sendToBTDevice(String mac, JSONObject data){
		mBTHelper.send(mac, data);
	}

	/**
	 * stop connection
	 * @param mac
	 */
	public void stopConnection(String mac){
		mBTHelper.stopConnection(mac);
	}	
}
