package info.fshi.datamule.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.os.Handler;
import android.os.Messenger;
import android.util.Log;

public class WifiCom {

	private final String TAG = "WifiCom";

	// constants
	public final static int WIFI_CLIENT_CONNECT_FAILED = 20404;
	public final static int WIFI_CLIENT_CONNECTED = 20401;
	public final static int WIFI_SERVER_CONNECTED = 20402;
	public final static int WIFI_DATA = 20403;
	public final static String WIFI_DATA_CONTENT = "wifi_data"; // data received from another device
	public static final String WIFI_DEVICE_MAC = "wifi_device_mac";
	
	public static final int WHAT_WIFI_DISCOVERY_SUCCESS = 10000;
	
	private int serverPort = 9999;	
	
	// all received messages are sent through this messenger to its parent
	Messenger mMessenger = null;

//	private ArrayList<ConnectedThread> connections = new ArrayList<ConnectedThread>();

	// current connection state, only one server thread and one client thread
	private ServerThread mServerThread = null;

	private StringBuffer sbLock = new StringBuffer("BTService");

	private Handler timeoutHandler = new Handler();

	public static WifiCom _obj = null;

	// time control params
	private long mTimeout = 5000;

	// server/client status
	private boolean serverRunning = false;
	/**
	 * Constructor. Prepares a new BT interface. 
	 */
	private WifiCom() {
		
	}

	/**
	 * singleton
	 * @return
	 */
	public static WifiCom getObject(){
		if(_obj == null){
			_obj = new WifiCom();
		}
		return _obj;
	}

	/**
	 * set wifi com timeout
	 * @param timeout
	 */
	public void setTimeout(long timeout){
		mTimeout = timeout;
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

	class ServerThread extends Thread{
		private final ServerSocket mServerSocket;
		
		public ServerThread(){
			ServerSocket tmp = null;
			try {
				// MY_UUID is the app's UUID string, also used by the client code
				tmp = new ServerSocket(serverPort);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mServerSocket = tmp;
		}
		
        @Override
		public void run() {
        	Socket socket = null;
			serverRunning = true;
			// Keep listening until exception occurs or a socket is returned
			while (true) {
				Log.d(TAG, "Wifi server waiting for incoming connections");
				try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
				// If a connection was accepted
				if (socket != null && socket.isConnected()) {
					// Do work to manage the connection (in a separate thread)
					Log.d(TAG, "Connected as a server");//manageConnectedSocket(socket);
					// start a new thread to handling data exchange
//					connected(socket, null, false);
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
	}
//	/**
//	 * Client thread to handle issued connection command
//	 * @author fshi
//	 *
//	 */
//	private class ClientThread extends Thread {
//		private final BluetoothSocket mClientSocket;
//		private final BluetoothDevice mBTDevice;
//		private boolean clientConnected = false;
//		private StringBuffer sb;
//
//		public ClientThread(BluetoothDevice device, StringBuffer sb) {
//			// Use a temporary object that is later assigned to mmSocket,
//			// because mmSocket is final
//			BluetoothSocket tmp = null;
//			mBTDevice = device;
//			this.sb=sb;
//
//			// Get a BluetoothSocket to connect with the given BluetoothDevice
//			try {
//				// MY_UUID is the app's UUID string, also used by the server code
//				tmp = mBTDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
//			} catch (IOException e) { }
//			mClientSocket = tmp;
//		}
//
//		public void run() {
//			// timestamp before connection
//			try {
//				// Connect the device through the socket. This will block
//				// until it succeeds or throws an exception
//				// stop the connection after 5 seconds
//				synchronized (sb){
//					timeoutHandler.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							Log.d(TAG, "post delay " + String.valueOf(clientConnected));
//							if(!clientConnected){
//								cancel();
//								if(mMessenger != null){
//									try {
//										Message msg=Message.obtain();
//										msg.what = BT_CLIENT_CONNECT_FAILED;
//										// send necessary info to the handler
//										Bundle b = new Bundle();
//										b.putString(BT_DEVICE_MAC, mBTDevice.getAddress());
//										msg.setData(b);
//										mMessenger.send(msg);
//									} catch (RemoteException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//								}
//							}
//						}
//					}, mTimeout);
//					mClientSocket.connect();
//					clientConnected = true;
//					Log.d(TAG, "Connected as a client");
//					// Do work to manage the connection (in a separate thread)
//					// start a new thread to handling data exchange
//					connected(mClientSocket, mClientSocket.getRemoteDevice(), true);
//				}
//			} catch (IOException connectException) {
//				// Unable to connect; close the socket and get out
//				try {
//					mClientSocket.close();
//				} catch (IOException closeException) { 
//					// TODO Auto-generated catch block
//					closeException.printStackTrace();
//				}
//			}
//
//			// Do work to manage the connection (in a separate thread)
//			return;
//		}
//
//		/* Call this from the main activity to shutdown the connection */
//		public void cancel() {
//			try {
//				Log.d(TAG, "client thread is stopped");
//				mClientSocket.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * Start the ConnectedThread to begin managing a wifi connection
//	 * @param socket
//	 * @param remote address
//	 * @param iAmClient
//	 */
//	private synchronized void connected(Socket socket, String rAddress, boolean iAmClient) {
//		ConnectedThread newConn = new ConnectedThread(socket);
//		newConn.start();
//		connections.add(newConn);
//		// Send the info of the connected device back to the UI Activity
//		Message msg=Message.obtain();
//		if(iAmClient){
//			msg.what = WIFI_CLIENT_CONNECTED;
//		}else{
//			msg.what = WIFI_SERVER_CONNECTED;
//		}
//		// send necessary info to the handler
//		Bundle b = new Bundle();
//		b.putString(BT_DEVICE_MAC, device.getAddress());
//		msg.setData(b);
//		try {
//			if(mMessenger != null)
//				mMessenger.send(msg);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	
}
