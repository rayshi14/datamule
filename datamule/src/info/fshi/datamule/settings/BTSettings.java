package info.fshi.datamule.settings;

import info.fshi.datamule.R;
import info.fshi.datamule.utils.Constants;
import info.fshi.datamule.utils.SharedPreferencesUtil;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BTSettings {

	private int id;
	private Context mContext;

	public BTSettings(Context context, int i){
		mContext = context;
		id = i;
	}

	public void show(String title){
		switch(id){
		case Constants.BT_SCAN_DURATION_SETUP_ID:
			ScanDurationSetup dDialog = new ScanDurationSetup(mContext);
			dDialog.setTitle(title);
			dDialog.show();
			break;
		case Constants.BT_SCAN_INTERVAL_SETUP_ID:
			ScanIntervalSetup iDialog = new ScanIntervalSetup(mContext);
			iDialog.setTitle(title);
			iDialog.show();
			break;
		default:
			break;
		}
	}

	/**
	 * set scan duration for BT
	 * @author fshi
	 *
	 */
	public class ScanDurationSetup extends Dialog {

		Button okButton;
		Button cancelButton;

		private int selectedDurationId = 0;
		private int selectedDuration = 0;

		Context parentActivity;
		public class onRadioButtonClicked implements View.OnClickListener {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				// Is the button now checked?
				boolean checked = ((RadioButton) view).isChecked();

				// Check which radio button was clicked
				switch(view.getId()) {
				case R.id.radio_scan_duration_1:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_1;
						selectedDuration = 1000;
					}
					break;
				case R.id.radio_scan_duration_2:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_2;
						selectedDuration = 2000;
					}
					break;
				case R.id.radio_scan_duration_3:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_3;
						selectedDuration = 3000;
					}
					break;
				case R.id.radio_scan_duration_4:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_4;
						selectedDuration = 4000;
					}
					break;
				case R.id.radio_scan_duration_5:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_5;
						selectedDuration = 5000;
					}
					break;
				case R.id.radio_scan_duration_6:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_6;
						selectedDuration = 6000;
					}
					break;
				case R.id.radio_scan_duration_7:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_7;
						selectedDuration = 7000;
					}
					break;

				case R.id.radio_scan_duration_8:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_8;
						selectedDuration = 8000;
					}
					break;
				case R.id.radio_scan_duration_9:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_9;
						selectedDuration = 9000;
					}
					break;

				case R.id.radio_scan_duration_10:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_10;
						selectedDuration = 10000;
					}
					break;
				case R.id.radio_scan_duration_11:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_11;
						selectedDuration = 11000;
					}
					break;
				case R.id.radio_scan_duration_12:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_12;
						selectedDuration = 12000;
					}
					break;
				case R.id.radio_scan_duration_13:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_13;
						selectedDuration = 13000;
					}
					break;
				case R.id.radio_scan_duration_100:
					if (checked){
						selectedDurationId = R.id.radio_scan_duration_100;
						selectedDuration = 15000;
					}
					break;
				default:
					break;
				}
			}
		}

		public ScanDurationSetup(Context context) {
			super(context);
			parentActivity = context;
			// TODO Auto-generated constructor stub
			setContentView(R.layout.dialog_set_scanduration);
			okButton = (Button) findViewById(R.id.ok_button);
			okButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					// not click on anything but pressed confirmation
					if(selectedDurationId > 0){
						SharedPreferencesUtil.savePreferences(parentActivity, Constants.SP_RADIO_SCAN_DURATION_ID, selectedDurationId);
						SharedPreferencesUtil.savePreferences(parentActivity, Constants.SP_RADIO_SCAN_DURATION, selectedDuration);
					}
					ScanDurationSetup.this.dismiss();
				}
			});
			cancelButton = (Button) findViewById(R.id.cancel_button);
			cancelButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ScanDurationSetup.this.dismiss(); 
				}
			});

			// scan duration id refering to radio button id
			selectedDurationId = SharedPreferencesUtil.loadSavedPreferences(parentActivity, Constants.SP_RADIO_SCAN_DURATION_ID, Constants.DEFAULT_BT_SCAN_DURATION_ID);
			selectedDuration = SharedPreferencesUtil.loadSavedPreferences(parentActivity, Constants.SP_RADIO_SCAN_DURATION, Constants.DEFAULT_BT_SCAN_DURATION);
			RadioGroup radioGroup = (RadioGroup) findViewById(R.id.scan_duration_radiogroup);

			int count = radioGroup.getChildCount();
			for (int i=0;i<count;i++) {
				View o = radioGroup.getChildAt(i);
				if (o instanceof RadioButton) {
					o.setOnClickListener(new onRadioButtonClicked());
					if( o.getId() == selectedDurationId){
						((RadioButton) o).setChecked(true);
					}
				}
			}
		}
	}


	/**
	 * set scan interval for BT
	 * @author fshi
	 *
	 */
	public class ScanIntervalSetup extends Dialog {

		Button okButton;
		Button cancelButton;

		private int selectedIntervalId;
		private int selectedInterval;

		Context parentActivity;
		public class onRadioButtonClicked implements View.OnClickListener {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				// Is the button now checked?
				boolean checked = ((RadioButton) view).isChecked();

				// Check which radio button was clicked
				switch(view.getId()) {
				case R.id.radio_scan_interval_010:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_010;
						selectedInterval = 10*1000;
					}
					break;
				case R.id.radio_scan_interval_020:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_020;
						selectedInterval = 20*1000;
					}
					break;
				case R.id.radio_scan_interval_030:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_030;
						selectedInterval = 30*1000;
					}
					break;
				case R.id.radio_scan_interval_1:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_1;
						selectedInterval = 60*1000;
					}
					break;
				case R.id.radio_scan_interval_2:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_2;
						selectedInterval = 120*1000;
					}
					break;
				case R.id.radio_scan_interval_5:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_5;
						selectedInterval = 300*1000;
					}
					break;
				case R.id.radio_scan_interval_10:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_10;
						selectedInterval = 600*1000;
					}
					break;
				case R.id.radio_scan_interval_15:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_15;
						selectedInterval = 900*1000;
					}
					break;
				case R.id.radio_scan_interval_30:
					if (checked){
						selectedIntervalId = R.id.radio_scan_interval_30;
						selectedInterval = 1800*1000;
					}
					break;
				default:
					break;
				}
			}
		}

		public ScanIntervalSetup(Context context) {
			super(context);
			parentActivity = context;
			// TODO Auto-generated constructor stub
			setContentView(R.layout.dialog_set_scaninterval);
			okButton = (Button) findViewById(R.id.ok_button);
			okButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharedPreferencesUtil.savePreferences(parentActivity, Constants.SP_RADIO_SCAN_INTERVAL_ID, selectedIntervalId);
					SharedPreferencesUtil.savePreferences(parentActivity, Constants.SP_RADIO_SCAN_INTERVAL, selectedInterval);

					ScanIntervalSetup.this.dismiss();
				}
			});
			cancelButton = (Button) findViewById(R.id.cancel_button);
			cancelButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ScanIntervalSetup.this.dismiss(); 
				}
			});

			// scan interval id refering to radio button id
			selectedIntervalId = SharedPreferencesUtil.loadSavedPreferences(parentActivity, Constants.SP_RADIO_SCAN_INTERVAL_ID, Constants.DEFAULT_BT_SCAN_INTERVAL_ID);
			selectedInterval = SharedPreferencesUtil.loadSavedPreferences(parentActivity, Constants.SP_RADIO_SCAN_INTERVAL, Constants.DEFAULT_BT_SCAN_INTERVAL);
			RadioGroup radioGroup = (RadioGroup) findViewById(R.id.scan_interval_radiogroup);

			int count = radioGroup.getChildCount();
			for (int i=0;i<count;i++) {
				View o = radioGroup.getChildAt(i);
				if (o instanceof RadioButton) {
					o.setOnClickListener(new onRadioButtonClicked());
					if( o.getId() == selectedIntervalId){
						((RadioButton) o).setChecked(true);
					}
				}
			}
		}
	}
}


