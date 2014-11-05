package info.fshi.datamule.packet;

public class BasicPacket {
	// BT messge header
	// format:  type|data
	public static final String PACKET_TYPE = "type";
	public static final String PACKET_DATA = "data";
	
	// data type identifier
	public static final int PACKET_TYPE_TIMESTAMP_DATA = 100; // format 100|timestamp
	public static final int PACKET_TYPE_TIMESTAMP_ACK = 101; // format 101|
	
}
