package info.fshi.datamule;

import info.fshi.datamule.network.BTController;
import android.view.View;
import android.view.View.OnClickListener;

public class BTConnectButtonOnClickListener implements OnClickListener {

	BTDevice btDevice;
	BTController btHelper;
	
	public BTConnectButtonOnClickListener(BTDevice btDevice, BTController controller){
		this.btDevice = btDevice;
		this.btHelper = controller;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		btHelper.connectBTServer(btDevice.getRawDevice());
		btDevice.setConnectionStartTime(System.currentTimeMillis());
	}
}