package info.fshi.datamule;

import info.fshi.datamule.network.WifiController;
import android.view.View;
import android.view.View.OnClickListener;

public class WifiConnectButtonOnClickListener implements OnClickListener {

	WifiDevice mWifiDevice;
	WifiController mWifiHelper;
	
	public WifiConnectButtonOnClickListener(WifiDevice wifiDevice, WifiController wifiController){
		this.mWifiDevice = wifiDevice;
		this.mWifiHelper = wifiController;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		mWifiHelper.connectWifiServer(mWifiDevice.getMAC());
		mWifiDevice.setConnectionStartTime(System.currentTimeMillis());
	}
}