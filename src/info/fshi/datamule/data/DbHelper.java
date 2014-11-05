package info.fshi.datamule.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "datamule";

	// Database table names;
	// bluetooth
	public static final String TABLE_BT_DEVICES = "btdevice";
	public static final String TABLE_BT_COMMUNICATION_LOGS = "com";
	public static final String TABLE_BT_SCAN_LOGS = "scan";
	public static final String TABLE_WIFI_DEVICES = "wifidevice";

	public static final String COL_ROW_ID = BaseColumns._ID;

	private static final int DATABASE_VERSION = 2;

	// table bt devices
	private static final String COL_BT_DEVICE_NAME = "col_bt_device_name";
	private static final String COL_BT_DEVICE_MAC = "col_bt_device_mac";
	private static final String COL_BT_DEVICE_RSSI = "col_bt_device_rssi";
	private static final String COL_BT_DEVICE_STATE = "col_bt_device_state";
	private static final String COL_BT_DEVICE_COUNTER = "col_bt_device_counter";

	// table comm logs for wifi
	private static final String COL_BT_COM_TIMESTAMP = "col_bt_com_timestamp";
	private static final String COL_BT_COM_RSSI = "col_bt_com_rssi";
	private static final String COL_BT_COM_FK_DEVICE = "col_bt_com_fk_device";
	private static final String COL_BT_COM_DELAY = "col_bt_com_delay";

	// table scan log for bluetooth
	private static final String COL_BT_SCAN_TIMESTAMP = "col_bt_scan_timestamp";
	private static final String COL_BT_SCAN_RSSI = "col_bt_scan_rssi";
	private static final String COL_BT_SCAN_FK_DEVICE = "col_bt_scan_fk_device";
	private static final String COL_BT_SCAN_DURATION = "col_bt_scan_duration";
	private static final String COL_BT_SCAN_BASELINE = "col_bt_scan_baseline";

	// table wifi devices
	private static final String COL_WIFI_DEVICE_NAME = "col_wif_device_name";
	private static final String COL_WIFI_DEVICE_MAC = "col_wif_device_mac";
	private static final String COL_WIFI_DEVICE_RSSI = "col_wif_device_rssi";
	private static final String COL_WIFI_DEVICE_STATE = "col_wif_device_state";
	
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_BT_DEVICE_TABLE = "CREATE TABLE " + TABLE_BT_DEVICES + "("
				+ COL_ROW_ID + " INTEGER PRIMARY KEY," + COL_BT_DEVICE_NAME + " TEXT,"
				+ COL_BT_DEVICE_MAC + " TEXT," + COL_BT_DEVICE_RSSI + " INTEGER," + COL_BT_DEVICE_STATE + " INTEGER," + COL_BT_DEVICE_COUNTER + " INTEGER" + ")";

		String CREATE_BT_COM_TABLE = "CREATE TABLE " + TABLE_BT_COMMUNICATION_LOGS + "("
				+ COL_ROW_ID + " INTEGER PRIMARY KEY," + COL_BT_COM_TIMESTAMP + " INTEGER,"
				+ COL_BT_COM_RSSI + " INTEGER," + COL_BT_COM_FK_DEVICE + " INTEGER," + COL_BT_COM_DELAY + " INTEGER" + ")";
		String CREATE_BT_SCAN_TABLE = "CREATE TABLE " + TABLE_BT_SCAN_LOGS + "("
				+ COL_ROW_ID + " INTEGER PRIMARY KEY," + COL_BT_SCAN_TIMESTAMP + " INTEGER,"
				+ COL_BT_SCAN_RSSI + " INTEGER," + COL_BT_SCAN_FK_DEVICE + " INTEGER," + COL_BT_SCAN_DURATION + " INTEGER," + COL_BT_SCAN_BASELINE + " INTEGER" + ")";
		String CREATE_WIFI_DEVICE_TABLE = "CREATE TABLE " + TABLE_WIFI_DEVICES + "("
				+ COL_ROW_ID + " INTEGER PRIMARY KEY," + COL_WIFI_DEVICE_NAME + " TEXT,"
				+ COL_WIFI_DEVICE_MAC + " TEXT," + COL_WIFI_DEVICE_RSSI + " INTEGER," + COL_WIFI_DEVICE_STATE + " INTEGER" + ")";

		
		db.execSQL(CREATE_BT_DEVICE_TABLE);
		db.execSQL(CREATE_BT_COM_TABLE);
		db.execSQL(CREATE_BT_SCAN_TABLE);
		db.execSQL(CREATE_WIFI_DEVICE_TABLE);
		// TODO WiFi table creation
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BT_DEVICES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BT_COMMUNICATION_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BT_SCAN_LOGS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_WIFI_DEVICES);
		// Create tables again
		onCreate(db);
		// TODO wifi table upgrade
	}

	/**
	 * Adding new device
	 * @param device
	 */
	public void insertBTDevice(DbBTDevice device) {
		SQLiteDatabase db = this.getWritableDatabase();

		DbBTDevice btDevice = getBTDevice(device.getMac());
		if (btDevice != null){
			updateBTDevice(device);
		}
		else{
			ContentValues values = new ContentValues();
			values.put(COL_BT_DEVICE_NAME, device.getName());
			values.put(COL_BT_DEVICE_MAC, device.getMac());
			values.put(COL_BT_DEVICE_RSSI, device.getRssi());
			values.put(COL_BT_DEVICE_COUNTER, device.getCounter());
			values.put(COL_BT_DEVICE_STATE, device.getState());
			// Inserting Row
			db.insert(TABLE_BT_DEVICES, null, values);
		}
		db.close(); // Closing database connection
	}

	/**
	 * Adding new com log
	 * @param log
	 */
	public void insertBTComLog(DbBTComLog log) {
		SQLiteDatabase db = this.getWritableDatabase();

		DbBTComLog tmpLog = getBTComLogByTimestamp(log.getTimestamp());
		if(tmpLog!=null){
			updateBTComLog(log);
		}else{
			ContentValues values = new ContentValues();
			values.put(COL_BT_COM_TIMESTAMP, log.getTimestamp());
			values.put(COL_BT_COM_RSSI, log.getRssi());
			values.put(COL_BT_COM_FK_DEVICE, log.getDeviceId());
			values.put(COL_BT_COM_DELAY, log.getDelay());
			// Inserting Row
			db.insert(TABLE_BT_COMMUNICATION_LOGS, null, values);
		}
		db.close(); // Closing database connection
	}

	/**
	 * insert new log
	 * @param log
	 */
	public void insertBTScanLog(DbBTScanLog log) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(COL_BT_SCAN_TIMESTAMP, log.getTimestamp());
		values.put(COL_BT_SCAN_RSSI, log.getRssi());
		values.put(COL_BT_SCAN_FK_DEVICE, log.getDeviceId());
		values.put(COL_BT_SCAN_DURATION, log.getScanDuration());
		values.put(COL_BT_SCAN_BASELINE, log.getBaselineTimestamp());
		// Inserting Row
		db.insert(TABLE_BT_SCAN_LOGS, null, values);

		db.close(); // Closing database connection
	}

	/**
	 * Adding new device
	 * @param device
	 */
	public void insertWifiDevice(DbWifiDevice device) {
		SQLiteDatabase db = this.getWritableDatabase();

		DbWifiDevice wifiDevice = getWifiDevice(device.getMac());
		if (wifiDevice != null){
			updateWifiDevice(device);
		}
		else{
			ContentValues values = new ContentValues();
			values.put(COL_WIFI_DEVICE_NAME, device.getName());
			values.put(COL_WIFI_DEVICE_MAC, device.getMac());
			values.put(COL_WIFI_DEVICE_RSSI, device.getRssi());
			values.put(COL_WIFI_DEVICE_STATE, device.getState());
			// Inserting Row
			db.insert(TABLE_WIFI_DEVICES, null, values);
		}
		db.close(); // Closing database connection
	}

	/**
	 * Getting single device by id
	 * @param id
	 * @return
	 */
	public DbBTDevice getBTDevice(long id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BT_DEVICES, new String[] { COL_ROW_ID,
				COL_BT_DEVICE_NAME, COL_BT_DEVICE_MAC, COL_BT_DEVICE_RSSI, COL_BT_DEVICE_STATE, COL_BT_DEVICE_COUNTER }, COL_ROW_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();

			DbBTDevice device = new DbBTDevice(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getString(cursor.getColumnIndex(COL_BT_DEVICE_NAME)), cursor.getString(cursor.getColumnIndex(COL_BT_DEVICE_MAC)), 
					cursor.getShort(cursor.getColumnIndex(COL_BT_DEVICE_RSSI)), cursor.getInt(cursor.getColumnIndex(COL_BT_DEVICE_STATE)), 
					cursor.getInt(cursor.getColumnIndex(COL_BT_DEVICE_COUNTER)));
			return device;
		}
		else{
			return null;
		}
	}
		
	/**
	 * Get single device by mac addr
	 * @param mac
	 * @return
	 */
	public DbBTDevice getBTDevice(String mac) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BT_DEVICES, new String[] { COL_ROW_ID,
				COL_BT_DEVICE_NAME, COL_BT_DEVICE_MAC, COL_BT_DEVICE_RSSI, COL_BT_DEVICE_STATE, COL_BT_DEVICE_COUNTER }, COL_BT_DEVICE_MAC + "=?",
				new String[] { mac }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();
			DbBTDevice device = new DbBTDevice(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getString(cursor.getColumnIndex(COL_BT_DEVICE_NAME)), cursor.getString(cursor.getColumnIndex(COL_BT_DEVICE_MAC)), 
					cursor.getShort(cursor.getColumnIndex(COL_BT_DEVICE_RSSI)), cursor.getInt(cursor.getColumnIndex(COL_BT_DEVICE_STATE)), 
					cursor.getInt(cursor.getColumnIndex(COL_BT_DEVICE_COUNTER)));
			return device;
		}
		else{
			return null;
		}
	}

	/**
	 * Getting single device by id
	 * @param id
	 * @return
	 */
	public DbWifiDevice getWifiDevice(long id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_WIFI_DEVICES, new String[] { COL_ROW_ID,
				COL_WIFI_DEVICE_NAME, COL_WIFI_DEVICE_MAC, COL_WIFI_DEVICE_RSSI, COL_WIFI_DEVICE_STATE }, COL_ROW_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();

			DbWifiDevice device = new DbWifiDevice(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getString(cursor.getColumnIndex(COL_WIFI_DEVICE_NAME)), cursor.getString(cursor.getColumnIndex(COL_WIFI_DEVICE_MAC)), 
					cursor.getShort(cursor.getColumnIndex(COL_WIFI_DEVICE_RSSI)), cursor.getInt(cursor.getColumnIndex(COL_WIFI_DEVICE_STATE)));
			return device;
		}
		else{
			return null;
		}
	}
		
	/**
	 * Get single device by mac addr
	 * @param mac
	 * @return
	 */
	public DbWifiDevice getWifiDevice(String mac) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_WIFI_DEVICES, new String[] { COL_ROW_ID,
				COL_WIFI_DEVICE_NAME, COL_WIFI_DEVICE_MAC, COL_WIFI_DEVICE_RSSI, COL_WIFI_DEVICE_STATE }, COL_WIFI_DEVICE_MAC + "=?",
				new String[] { mac }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();
			DbWifiDevice device = new DbWifiDevice(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getString(cursor.getColumnIndex(COL_WIFI_DEVICE_NAME)), cursor.getString(cursor.getColumnIndex(COL_WIFI_DEVICE_MAC)), 
					cursor.getShort(cursor.getColumnIndex(COL_WIFI_DEVICE_RSSI)), cursor.getInt(cursor.getColumnIndex(COL_WIFI_DEVICE_STATE)));
			return device;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Getting single bt com log by id
	 * @param id
	 * @return
	 */
	public DbBTComLog getBTComLogById(long id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BT_COMMUNICATION_LOGS, new String[] { COL_ROW_ID,
				COL_BT_COM_TIMESTAMP, COL_BT_COM_RSSI, COL_BT_COM_FK_DEVICE, COL_BT_COM_DELAY }, COL_ROW_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();

			DbBTComLog log = new DbBTComLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getLong(cursor.getColumnIndex(COL_BT_COM_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_COM_RSSI)), 
					cursor.getInt(cursor.getColumnIndex(COL_BT_COM_FK_DEVICE)), cursor.getLong(cursor.getColumnIndex(COL_BT_COM_DELAY)));
			// return log
			return log;
		}
		else{
			return null;
		}
	}
	/**
	 * Getting single bt com log by timestamp
	 * @param timestamp
	 * @return
	 */
	public DbBTComLog getBTComLogByTimestamp(long timestamp) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BT_COMMUNICATION_LOGS, new String[] { COL_ROW_ID,
				COL_BT_COM_TIMESTAMP, COL_BT_COM_RSSI, COL_BT_COM_FK_DEVICE, COL_BT_COM_DELAY }, COL_BT_COM_TIMESTAMP + "=?",
				new String[] { String.valueOf(timestamp) }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();

			DbBTComLog log = new DbBTComLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getLong(cursor.getColumnIndex(COL_BT_COM_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_COM_RSSI)), 
					cursor.getInt(cursor.getColumnIndex(COL_BT_COM_FK_DEVICE)), cursor.getLong(cursor.getColumnIndex(COL_BT_COM_DELAY)));
			// return log
			return log;
		}
		else{
			return null;
		}
	}

	/**
	 * Getting single bt scan log by id
	 * @param id
	 * @return
	 */
	public DbBTScanLog getBTScanLogById(long id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BT_SCAN_LOGS, new String[] { COL_ROW_ID,
				COL_BT_SCAN_TIMESTAMP, COL_BT_SCAN_RSSI, COL_BT_SCAN_FK_DEVICE, COL_BT_SCAN_DURATION, COL_BT_SCAN_BASELINE }, COL_ROW_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor.getCount() > 0 && cursor != null){
			cursor.moveToFirst();

			DbBTScanLog log = new DbBTScanLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
					cursor.getLong(cursor.getColumnIndex(COL_BT_SCAN_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_SCAN_RSSI)), 
					cursor.getInt(cursor.getColumnIndex(COL_BT_SCAN_FK_DEVICE)), cursor.getInt(cursor.getColumnIndex(COL_BT_SCAN_DURATION)),
					cursor.getLong(cursor.getColumnIndex(COL_BT_SCAN_BASELINE)));
			// return log
			return log;
		}
		else{
			return null;
		}
	}

	/**
	 * Getting All bt devices
	 * @return
	 */
	public List<DbBTDevice> getAllBTDevices() {
		List<DbBTDevice> deviceList = new ArrayList<DbBTDevice>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_BT_DEVICES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.getCount() > 0 && cursor != null){
			if (cursor.moveToFirst()) {
				do {
					DbBTDevice device = new DbBTDevice(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
							cursor.getString(cursor.getColumnIndex(COL_BT_DEVICE_NAME)), cursor.getString(cursor.getColumnIndex(COL_BT_DEVICE_MAC)), 
							cursor.getShort(cursor.getColumnIndex(COL_BT_DEVICE_RSSI)), cursor.getInt(cursor.getColumnIndex(COL_BT_DEVICE_STATE)), 
							cursor.getInt(cursor.getColumnIndex(COL_BT_DEVICE_COUNTER)));
					// Adding contact to list
					deviceList.add(device);
				} while (cursor.moveToNext());
			}
		}

		// return device list
		return deviceList;
	}
	

	/**
	 * Getting All wifi devices
	 * @return
	 */
	public List<DbWifiDevice> getAllWifiDevices() {
		List<DbWifiDevice> deviceList = new ArrayList<DbWifiDevice>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_WIFI_DEVICES;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.getCount() > 0 && cursor != null){
			if (cursor.moveToFirst()) {
				do {
					DbWifiDevice device = new DbWifiDevice(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
							cursor.getString(cursor.getColumnIndex(COL_WIFI_DEVICE_NAME)), cursor.getString(cursor.getColumnIndex(COL_WIFI_DEVICE_MAC)), 
							cursor.getShort(cursor.getColumnIndex(COL_WIFI_DEVICE_RSSI)), cursor.getInt(cursor.getColumnIndex(COL_WIFI_DEVICE_STATE)));
					// Adding contact to list
					deviceList.add(device);
				} while (cursor.moveToNext());
			}
		}

		// return device list
		return deviceList;
	}

	
	/**
	 * Getting All bt com logs
	 * @return
	 */
	public List<DbBTComLog> getAllBTComLogs() {
		List<DbBTComLog> logList = new ArrayList<DbBTComLog>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_BT_COMMUNICATION_LOGS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.getCount() > 0 && cursor != null){
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					DbBTComLog log = new DbBTComLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
							cursor.getLong(cursor.getColumnIndex(COL_BT_COM_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_COM_RSSI)), 
							cursor.getInt(cursor.getColumnIndex(COL_BT_COM_FK_DEVICE)), cursor.getLong(cursor.getColumnIndex(COL_BT_COM_DELAY)));
					// Adding contact to list
					logList.add(log);
				} while (cursor.moveToNext());
			}
		}

		// return log list
		return logList;
	}
	/**
	 * Getting All bt scan logs of a bt device
	 * @param id
	 * @return
	 */
	public List<DbBTComLog> getAllBTComLogsByDeviceId(long id) {
		List<DbBTComLog> logList = new ArrayList<DbBTComLog>();

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_BT_COMMUNICATION_LOGS, new String[] { COL_ROW_ID,
				COL_BT_COM_TIMESTAMP, COL_BT_COM_RSSI, COL_BT_COM_FK_DEVICE, COL_BT_COM_DELAY }, COL_BT_COM_FK_DEVICE + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if(cursor.getCount() > 0 && cursor != null){
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					DbBTComLog log = new DbBTComLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
							cursor.getLong(cursor.getColumnIndex(COL_BT_COM_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_COM_RSSI)), 
							cursor.getInt(cursor.getColumnIndex(COL_BT_COM_FK_DEVICE)), cursor.getLong(cursor.getColumnIndex(COL_BT_COM_DELAY)));
					// Adding contact to list
					logList.add(log);
				} while (cursor.moveToNext());
			}
		}

		// return log list
		return logList;
	}
	/**
	 * Getting All bt scan logs
	 * @return
	 */
	public List<DbBTScanLog> getAllBTScanLogs() {
		List<DbBTScanLog> logList = new ArrayList<DbBTScanLog>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_BT_SCAN_LOGS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if(cursor.getCount() > 0 && cursor != null){
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					DbBTScanLog log = new DbBTScanLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
							cursor.getLong(cursor.getColumnIndex(COL_BT_SCAN_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_SCAN_RSSI)), 
							cursor.getInt(cursor.getColumnIndex(COL_BT_SCAN_FK_DEVICE)), cursor.getInt(cursor.getColumnIndex(COL_BT_SCAN_DURATION)),
							cursor.getLong(cursor.getColumnIndex(COL_BT_SCAN_BASELINE)));
					// Adding contact to list
					logList.add(log);
				} while (cursor.moveToNext());
			}
		}

		// return log list
		return logList;
	}
	/**
	 * Getting All scan logs of a bt device
	 * @param id
	 * @return
	 */
	public List<DbBTScanLog> getAllBTScanLogsByDeviceId(long id) {
		List<DbBTScanLog> logList = new ArrayList<DbBTScanLog>();
		// Select All Query

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_BT_SCAN_LOGS, new String[] { COL_ROW_ID,
				COL_BT_SCAN_TIMESTAMP, COL_BT_SCAN_RSSI, COL_BT_SCAN_FK_DEVICE, COL_BT_SCAN_DURATION, COL_BT_SCAN_BASELINE }, COL_BT_SCAN_FK_DEVICE + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if(cursor.getCount() > 0 && cursor != null){
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					DbBTScanLog log = new DbBTScanLog(cursor.getLong(cursor.getColumnIndex(COL_ROW_ID)),
							cursor.getLong(cursor.getColumnIndex(COL_BT_SCAN_TIMESTAMP)), cursor.getShort(cursor.getColumnIndex(COL_BT_SCAN_RSSI)), 
							cursor.getInt(cursor.getColumnIndex(COL_BT_SCAN_FK_DEVICE)), cursor.getInt(cursor.getColumnIndex(COL_BT_SCAN_DURATION)),
							cursor.getLong(cursor.getColumnIndex(COL_BT_SCAN_BASELINE)));
					// Adding contact to list
					logList.add(log);
				} while (cursor.moveToNext());
			}
		}

		// return log list
		return logList;
	}

	/**
	 * Getting bt devices Count
	 * @return
	 */
	public int getBTDevicesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BT_DEVICES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	/**
	 * Getting wifi devices Count
	 * @return
	 */
	public int getWifiDevicesCount() {
		String countQuery = "SELECT  * FROM " + TABLE_WIFI_DEVICES;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	
	/**
	 * Getting bt com logs Count
	 * @return
	 */
	public int getBTComLogsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BT_COMMUNICATION_LOGS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}

	/**
	 * Getting bt scan logs Count
	 * @return
	 */
	public int getBTScanLogsCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BT_SCAN_LOGS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		// return count
		return count;
	}
	
	/**
	 * Updating single device
	 * @param device
	 * @return
	 */
	public int updateBTDevice(DbBTDevice device) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(COL_BT_DEVICE_NAME, device.getName());
		values.put(COL_BT_DEVICE_MAC, device.getMac());
		values.put(COL_BT_DEVICE_RSSI, device.getRssi());
		values.put(COL_BT_DEVICE_STATE, device.getState());
		values.put(COL_BT_DEVICE_COUNTER, device.getCounter());

		// updating row
		return db.update(TABLE_BT_DEVICES, values, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(device.getId()) });
	}
	/**
	 * Updating single device
	 * @param device
	 * @return
	 */
	public int updateWifiDevice(DbWifiDevice device) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(COL_WIFI_DEVICE_NAME, device.getName());
		values.put(COL_WIFI_DEVICE_MAC, device.getMac());
		values.put(COL_WIFI_DEVICE_RSSI, device.getRssi());
		values.put(COL_WIFI_DEVICE_STATE, device.getState());

		// updating row
		return db.update(TABLE_WIFI_DEVICES, values, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(device.getId()) });
	}
	/**
	 * Updating single bt com log
	 * @param log
	 * @return
	 */
	public int updateBTComLog(DbBTComLog log) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(COL_BT_COM_TIMESTAMP, log.getTimestamp());
		values.put(COL_BT_COM_RSSI, log.getRssi());
		values.put(COL_BT_COM_FK_DEVICE, log.getDeviceId());
		values.put(COL_BT_COM_DELAY, log.getDelay());

		// updating row
		return db.update(TABLE_BT_COMMUNICATION_LOGS, values, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(log.getId()) });
	}
	/**
	 * Updating single bt scan log
	 * @param log
	 * @return
	 */
	public int updateBTScanLog(DbBTScanLog log) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(COL_BT_SCAN_TIMESTAMP, log.getTimestamp());
		values.put(COL_BT_SCAN_RSSI, log.getRssi());
		values.put(COL_BT_SCAN_FK_DEVICE, log.getDeviceId());
		values.put(COL_BT_SCAN_BASELINE, log.getBaselineTimestamp());
		// updating row
		return db.update(TABLE_BT_SCAN_LOGS, values, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(log.getId()) });
	}
	/**
	 * Deleting single bt device
	 * @param device
	 */
	public void deleteBTDevice(DbBTDevice device) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BT_DEVICES, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(device.getId()) });
		db.close();
	}
	/**
	 * Deleting single wifi device
	 * @param device
	 */
	public void deleteWifiDevice(DbWifiDevice device) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_WIFI_DEVICES, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(device.getId()) });
		db.close();
	}

	/**
	 * Deleting single bt com log
	 * @param log
	 */
	public void deleteBTComLog(DbBTComLog log) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BT_COMMUNICATION_LOGS, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(log.getId()) });
		db.close();
	}
	/**
	 * Deleting single bt scan log
	 * @param log
	 */
	public void deleteBTScanLog(DbBTScanLog log) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BT_SCAN_LOGS, COL_ROW_ID + " = ?",
				new String[] { String.valueOf(log.getId()) });
		db.close();
	}
	
}
