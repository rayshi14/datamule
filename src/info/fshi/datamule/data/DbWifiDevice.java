package info.fshi.datamule.data;

import info.fshi.datamule.utils.Constants;

public class DbWifiDevice extends BasicTable {
	//private variables
	String _name;
	String _mac;
	private short _rssi;
	private int _conn_state;

	public static final int COL_ID = 0;
	public static final int COL_NAME = 1;
	public static final int COL_MAC = 2;
	public static final int COL_RSSI = 3;
	public static final int COL_STATE = 4;
	// Empty constructor
	public DbWifiDevice(){

	}
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param mac
	 */
	public DbWifiDevice(long id, String name, String mac){
		super(id);
		this._name = name;
		this._mac = mac;
		this._rssi = 0;
		this._conn_state = Constants.STATE_CLIENT_UNCONNECTED;
	}

	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param mac
	 * @param rssi
	 */
	public DbWifiDevice(long id, String name, String mac, short rssi){
		super(id);
		this._name = name;
		this._mac = mac;
		this._rssi = rssi;
		this._conn_state = Constants.STATE_CLIENT_UNCONNECTED;
	}
	/**
	 * constructor
	 * @param id
	 * @param name
	 * @param mac
	 * @param rssi
	 * @param state
	 * @param retry
	 */
	public DbWifiDevice(long id, String name, String mac, short rssi, int state){
		super(id);
		this._name = name;
		this._mac = mac;
		this._rssi = rssi;
		this._conn_state = state;
	}

	// getting name
	public String getName(){
		return this._name;
	}

	public void setName(String name){
		this._name = name;
	}
	// getting mac
	public String getMac(){
		return this._mac;
	}

	public void setMac(String mac){
		this._mac = mac;
	}

	public short getRssi(){
		return this._rssi;
	}

	public void setRssi(short rssi){
		this._rssi = rssi;
	}
	public int getState(){
		return this._conn_state;
	}

	public void setState(int state){
		this._conn_state = state;
	}

}
