package info.fshi.datamule.utils;

import info.fshi.datamule.R;


public abstract class Constants {
	public static final String STR_APPLICATION_NAME = "DataMule";
	public static final String TAG_APPLICATION = "DataMule";
	public static final String TAG_ACT_TEST = "ActivityTest";
	public static final String EXTRA_MESSENGER = "extra_messenger";
	
	// messenge bundle key
	public static final String MESSAGE_DATA_CONNECTION = "data_connection";
	public static final String MESSAGE_DATA_TIMESTAMP = "data_timestamp";
	public static final String MESSAGE_DATA_DEVICE_MAC = "data_device_mac";
	public static final String MESSAGE_DATA_DEVICE_NAME = "data_device_name";
	public static final String MESSAGE_ACK_DATA_TIMESTAMP = "ack_data_timestamp";
	public static final String MESSAGE_DATA = "message_data";
	
	// BT meta
	public static final int BT_CLIENT_TIMEOUT = 5000;
	public static final int BT_CONN_MAX_RETRY = 3;
	
	
	// BT activity intent extra name
	public static final String INTENT_EXTRA_DEVICE_MAC = "intent_extra_device_mac";
	
	// BT client and server state in listview, start from 200
	public static final int STATE_CLIENT_CONNECTED = 201;
	public static final int STATE_CLIENT_UNCONNECTED = 202;
	public static final int STATE_CLIENT_FAILED = 203;
	public static final int STATE_CLIENT_OUTDATED = 204;
	
	// String, upper case
	public static final String STATE_CONNECTED = "SUCCESS";
	public static final String STATE_CONNECTION_FAILED = "FAILED";
	public static final String STATE_NOT_CONNECTED = "CONNECT";
	
	public static final String LOG_TIMESTAMP_CONNECT = "log_timestamp_connect";
	public static final String LOG_TIMESTAMP_WRITE = "log_timestamp_write";
	public static final String LOG_TIMESTAMP_WRITE_FINISHED = "log_timestamp_write_finished";
	public static final String LOG_TIMESTAMP_ACK_RECEIVED = "log_timestamp_ack_received";
	
	// BT client and server status, start from 10
	public static final int SERVER_DISCONNECTED = 10;
	public static final int SERVER_CONNECTED = 11;
	public static final int CLIENT_DISCONNECTED = 12;
	public static final int CLIENT_CONNECTED = 13;
	
	// Shared preference keys
	public static final String SP_RADIO_SCAN_DURATION_ID = "sp_radio_bt_scan_duration_id";
	public static final String SP_RADIO_SCAN_DURATION = "sp_radio_bt_scan_duration";
	public static final String SP_RADIO_SCAN_INTERVAL_ID = "sp_radio_bt_scan_interval_id";
	public static final String SP_RADIO_SCAN_INTERVAL = "sp_radio_bt_scan_interval";
	public static final String SP_CHECKBOX_BT_AUTO_SCAN = "sp_checkbox_bt_auto_scan";
	// default values
	public static final int DEFAULT_BT_SCAN_INTERVAL = 300*1000;
	public static final int DEFAULT_BT_SCAN_DURATION = 10000;
	public static final int DEFAULT_BT_SCAN_INTERVAL_ID = R.id.radio_scan_interval_5;
	public static final int DEFAULT_BT_SCAN_DURATION_ID = R.id.radio_scan_duration_10;
	
	
	// bt settings ID
	public static final int BT_SCAN_DURATION_SETUP_ID = 400;
	public static final int BT_SCAN_INTERVAL_SETUP_ID = 401;

	
}
