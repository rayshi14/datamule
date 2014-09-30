package info.fshi.datamule.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * a multi thread BT utility
 * @author fshi
 *
 */
public class BTCom {

	// tag
	private final String TAG = "BTCom";

	// constants
	private final String BTRecordName = "info.fshi.bluetooth";
	public final static int BT_CLIENT_CONNECT_FAILED = 10404;
	public final static int BT_CLIENT_CONNECTED = 10401;
	public final static int BT_SERVER_CONNECTED = 10402;
	public final static int BT_DATA = 10402;
	public final static String BT_DATA_CONTENT = "bt_data"; // data received from another device
	public final static String BT_DEVICE_MAC = "bt_device_mac"; // mac address of the communicating device


	// default UUID
	private static UUID MY_UUID = UUID.fromString("8113ac40-438f-11e1-b86c-0800200c9a66");

	private BluetoothAdapter mBluetoothAdapter;

	// server/client status
	private boolean serverRunning = false;

	// all received messages are sent through this messenger to its parent
	Messenger mMessenger = null;

	private ArrayList<ConnectedThread> connections = new ArrayList<ConnectedThread>();

	// current connection state, only one server thread and one client thread
	private ServerThread mServerThread = null;

	private StringBuffer sbLock = new StringBuffer("BTService");

	private Handler timeoutHandler = new Handler();

	public static BTCom _obj = null;

	// time control params
	private long mTimeout = 5000;
	private long mDuration = 15000;

	/**
	 * Constructor. Prepares a new BT interface. 
	 */
	private BTCom() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * singleton
	 * @return
	 */
	public static BTCom getObject(){
		if(_obj == null){
			_obj = new BTCom();
		}
		return _obj;
	}

	/**
	 * set bluetooth com timeout
	 * @param timeout
	 */
	public void setTimeout(long timeout){
		mTimeout = timeout;
	}

	public void setDuration(long duration){
		mDuration = duration;
	}

	public boolean setCallback(Messenger callback){
		if(mMessenger == null){
			mMessenger = callback;
			return true;
		}
		else{
			return false;
		}
	}

	// used to managed connections
	public synchronized void stopConnection(String MAC){
		for(ConnectedThread connection : connections){
			if(connection.getMac().equals(MAC)){
				connection.cancel();
				Log.d(TAG, "remove connection" + connection.getMac());
				connections.remove(connection);
				break;
			}
		}
		Log.d(TAG, String.valueOf(connections.size()));
	}

	// used to send data to a dest
	public synchronized boolean send(String MAC, JSONObject data){
		for(ConnectedThread connection : connections){
			if(connection.getMac().equals(MAC)){
				connection.writeObject(data);
				return true;
			}
		}
		return false;
	}

	/**
	 * Start listening
	 * @return
	 */
	public synchronized void startServer(){
		stopServer();
		mServerThread = new ServerThread();             
		mServerThread.start();
	}

	/**
	 * stop listening
	 * @return
	 */
	public void stopServer(){
		if(serverRunning){
			mServerThread.cancel();
		}
	}

	/**
	 * connect to a BT device
	 * @return
	 */
	public synchronized void connect(BluetoothDevice btDevice){
		// Start the thread to connect with the given device
		Log.d(TAG, "connect to :" + btDevice.getAddress());	
		ClientThread clientThread = new ClientThread(btDevice, sbLock);
		clientThread.start();
	}

	/**
	 * get the number of active connections
	 * @return
	 */
	private int getActiveConnectionsCount(){
		return connections.size();
	}

	/**
	 * start scan service
	 * @param scanStart
	 */
	public void startScan(boolean scanStart) {
		if (scanStart){ // if command is to start scanning
			if (mBluetoothAdapter.isDiscovering()){ // if scan is already started, stop the current scanning
				mBluetoothAdapter.cancelDiscovery();
			}

			if(getActiveConnectionsCount() == 0){
				mBluetoothAdapter.startDiscovery();

				// Cancel the discovery process after SCAN_INTERVAL
				final Handler discoveryHandler = new Handler();
				discoveryHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mBluetoothAdapter.isDiscovering()){
							mBluetoothAdapter.cancelDiscovery();
						}
					}
				}, mDuration);
			}
		}
		else{ // if command is to stop scanning
			mBluetoothAdapter.cancelDiscovery();
		}
	}

	/**
	 * BT Server thread
	 * @author fshi
	 *
	 */
	private class ServerThread extends Thread {
		private final BluetoothServerSocket mServerSocket;

		public ServerThread() {
			// Use a temporary object that is later assigned to mmServerSocket,
			// because mmServerSocket is final
			BluetoothServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client code
				tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(BTRecordName, MY_UUID);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mServerSocket = tmp;
		}

		public void run() {
			BluetoothSocket socket = null;
			serverRunning = true;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				Log.d(TAG, "BT server waiting for incoming connections");
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				// If a connection was accepted
				if (socket != null && socket.isConnected()) {
					if(mBluetoothAdapter.isDiscovering()){
						mBluetoothAdapter.cancelDiscovery();
					}
					// Do work to manage the connection (in a separate thread)
					Log.d(TAG, "Connected as a server");//manageConnectedSocket(socket);
					// start a new thread to handling data exchange
					connected(socket, socket.getRemoteDevice(), false);
				}
			}
			try {
				mServerSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			serverRunning = false;
			return;
		}

		/** Will cancel the listening socket, and cause the thread to finish */
		public void cancel() {
			try {
				Log.d(TAG, "server thread is stopped");
				mServerSocket.close();
				serverRunning = false;
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}


	/**
	 * Client thread to handle issued connection command
	 * @author fshi
	 *
	 */
	private class ClientThread extends Thread {
		private final BluetoothSocket mClientSocket;
		private final BluetoothDevice mBTDevice;
		private boolean clientConnected = false;
		private StringBuffer sb;

		public ClientThread(BluetoothDevice device, StringBuffer sb) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mBTDevice = device;
			this.sb=sb;

			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server code
				tmp = mBTDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
			} catch (IOException e) { }
			mClientSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			if(mBluetoothAdapter.isDiscovering()){
				mBluetoothAdapter.cancelDiscovery();
			}
			// timestamp before connection
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				// stop the connection after 5 seconds
				synchronized (sb){
					timeoutHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Log.d(TAG, "post delay " + String.valueOf(clientConnected));
							if(!clientConnected){
								cancel();
								if(mMessenger != null){
									try {
										Message msg=Message.obtain();
										msg.what = BT_CLIENT_CONNECT_FAILED;
										// send necessary info to the handler
										Bundle b = new Bundle();
										b.putString(BT_DEVICE_MAC, mBTDevice.getAddress());
										msg.setData(b);
										mMessenger.send(msg);
									} catch (RemoteException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
					}, mTimeout);
					mClientSocket.connect();
					clientConnected = true;
					Log.d(TAG, "Connected as a client");
					// Do work to manage the connection (in a separate thread)
					// start a new thread to handling data exchange
					connected(mClientSocket, mClientSocket.getRemoteDevice(), true);
				}
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mClientSocket.close();
				} catch (IOException closeException) { 
					// TODO Auto-generated catch block
					closeException.printStackTrace();
				}
			}

			// Do work to manage the connection (in a separate thread)
			return;
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				Log.d(TAG, "client thread is stopped");
				mClientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * @param socket  The BluetoothSocket on which the connection was made
	 * @param device  The BluetoothDevice that has been connected
	 */
	private synchronized void connected(BluetoothSocket socket, BluetoothDevice device, boolean iAmClient) {
		ConnectedThread newConn = new ConnectedThread(socket);
		newConn.start();
		connections.add(newConn);
		// Send the info of the connected device back to the UI Activity
		Message msg=Message.obtain();
		if(iAmClient){
			msg.what = BT_CLIENT_CONNECTED;
		}else{
			msg.what = BT_SERVER_CONNECTED;
		}
		// send necessary info to the handler
		Bundle b = new Bundle();
		b.putString(BT_DEVICE_MAC, device.getAddress());
		msg.setData(b);
		try {
			if(mMessenger != null)
				mMessenger.send(msg);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Connected Thread for handling established connections
	 * @author fshi
	 *
	 */
	private class ConnectedThread extends Thread {
		private final BluetoothSocket mConnectedSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mConnectedSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = mConnectedSocket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) { }

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public String getMac(){
			return mConnectedSocket.getRemoteDevice().getAddress();
		}

		public void run() {
			Object buffer;

			// Keep listening to the InputStream until an exception occurs
			while (true) {
				try {
					// Read from the InputStream
					ObjectInputStream in = new ObjectInputStream(mmInStream);
					buffer = in.readObject();
					// Send the obtained bytes to the UI activity
					if(mMessenger != null)
					{
						try {
							// Send the obtained bytes to the UI Activity
							Message msg=Message.obtain();
							Bundle b = new Bundle();
							b.putString(BT_DATA_CONTENT, buffer.toString());
							b.putString(BT_DEVICE_MAC, this.getMac());
							msg.what = BT_DATA;
							msg.setData(b);
							mMessenger.send(msg);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					stopConnection(getMac());
					break;
					// stop the connected thread
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					stopConnection(getMac());
					break;
				}
			}
		}

		public void writeObject(JSONObject json) {
			try {
				Log.d(TAG, String.valueOf(mmOutStream));
				ObjectOutputStream out = new ObjectOutputStream(mmOutStream);
				out.writeObject(json.toString());
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "output stream error");
				e.printStackTrace();
				stopConnection(getMac());
			}
		}

		/* Call this from the main activity to shutdown the connection */
		public void cancel() {
			try {
				Log.d(TAG, "connected thread " + getMac() + " is stopped");
				mmInStream.close();
				mmOutStream.close();
				mConnectedSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
