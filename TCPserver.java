//
// TCP NETWORK SERVER
// ===================
// Implements a threaded TCP/IP server that supports multiple, non-blocking
// connections. This allows the function block applications to connect
// and exchange information with the simulated environment controllers.
//
// (c) AUT University - 2019-2020
//
// Documentation
// =============
// Full documentation for this class is contained in the Fault Diagnosis System
// Software Architecture document.
//
// This server operates on its own thread since it needs to accept incoming 
// connections and buffer incoming data packets as soon as they arrive. To
// free up the GORITE agent to do other things and only request packets when
// it is ready to, the server implements a First-In,First-Out (FIFO) packet
// queue. Each entry contains the data sent by the SIFB function block instance
// as well as an ID to show which SIFB sent the packet. 
//
// Revision History
// ================
// 18.12.2019 BRD Original version based on the Fault Diagnostic Engine (FDE)
//				  version created on 05.07.2019	
//
package HVACsim;

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import HVACsim.TCPserverExitCodes;
import HVACsim.TCPserverPacket;

public class TCPserver implements Runnable {
	//
	// Define the default input buffer size to read TCP
	// data into.
	final static int BUFFER_SIZE = 1024;

	// Data packet field separators. Please ensure
	// that any changes to these are also implemented
	// in the FORTE AGENT_SEND function block.
	final static String MESSAGE_START = "*";
	final static String FIELD_SEPARATOR = "|";

	String hostName = "";
	int listenerPortNumber = 0;
	int serverStatus = TCPserverExitCodes.UNDEFINED;
	
	// FIFO queue for packets
	// ======================
	Queue<TCPserverPacket> FIFOqueue = new LinkedList<>();
	
	//
	// Server()
	// ========
	// Provides the hostName and listener port number via the
	// class constructor. This class implements Runnable.
	//
	public TCPserver(String hostName, int listenerPortNumber) {
		this.hostName = hostName;
		this.listenerPortNumber = listenerPortNumber;
	}

	//
	// run()
	// =====
	// Starts the server on the designated thread using:
	//    new Thread(server).start();
	//
	// The status of the server can be checked after attempting
	// to start it by using:
	//
	// 	  status = server.serverStatus();
	//
	// This returns an integer status code from ExitCodes.
	//
	public void run() {
		System.err.println("Server started on thread" + "\n"); //RA_BRD
		try {
			startServer(hostName, listenerPortNumber);
		} catch (Exception e) {
			System.out.println("Server Exception caught on host " + hostName + " while trying to listen on port " + listenerPortNumber + ":" ); //RA_BRD
			System.out.println(e.getMessage()); //RA_BRD
			serverStatus = TCPserverExitCodes.EXIT_FAILURE;
			e.printStackTrace();
		}
	}

	//
	// startServer()
	// =============
	// Starts the server and makes connections available at the specified
	// named host address. All connections are initially accepted on the
	// specified listener port before being handed over to be managed by
	// to a session connection.
	//
	@SuppressWarnings("static-access")
	public int startServer(String hostName, int listenerPortNumber) throws Exception {
		int serverStatus = TCPserverExitCodes.EXIT_SUCCESS;
		int packetLength = 0;

		if (hostName.equals("")) {
			serverStatus = TCPserverExitCodes.INVALID_HOST_NAME;
		} else if (listenerPortNumber <= 0) {
			serverStatus = TCPserverExitCodes.INVALID_LISTENER_PORT;
		} else {
			// Resolve the host address.
			InetAddress host = InetAddress.getByName(hostName);

			// RA_BRD Will this have to move to a more appropriate place
			// later?
			Selector selector = Selector.open();

			// Open a non-blocking listener socket to accept incoming connections.
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(host, listenerPortNumber));
			serverSocketChannel.register(selector,  SelectionKey.OP_ACCEPT);

			// Ensure that after the channel has been opened, no channel
			// is currently accepted.
			SelectionKey key = null;
			serverStatus = TCPserverExitCodes.EXIT_SUCCESS;
			//System.err.println("Server started..");

			// this is the section that manages all the traffic
			while (true) {
				if (selector.select() > 0) {
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectedKeys.iterator();

					while(iterator.hasNext()) {
						key = (SelectionKey) iterator.next();
						iterator.remove();

						if (key.isAcceptable()) {
							// A client is trying to connect to this server.
							// Accept the incoming connection request on this
							// listening socket.
							SocketChannel sc = serverSocketChannel.accept();
							// Set this to non-blocking mode.
							sc.configureBlocking(false);
							// Set the socket to reading mode.
							sc.register(selector,  SelectionKey.OP_READ);
							System.err.println("Connection accepted on local address " + sc.getLocalAddress() + "\n");
						}

						if (key.isReadable()) {
							// This session socket was opened as a result of a
							// previous request for a connection on the server's
							// listener socket. It is therefore able to read data
							// sent to it. Try to read the data there into a
							// buffer.
							SocketChannel sc = (SocketChannel) key.channel();
							ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
							sc.read(byteBuffer);
							String dataPacket = new String (byteBuffer.array()).trim();
							packetLength = dataPacket.length();
							if (packetLength <= 0 ) {
								// A null packet was received indicating
								// that the server should close this
								// session socket.
								sc.close();
								System.err.println("Connection closed");
							} else {
								queuePacket(dataPacket);
							}
						}
					}
				}
				Thread.currentThread().yield();
			}
		}
		System.err.println("Jumped out of the server loop");
		return serverStatus;
	}

	//
	// queuePacket()
	// =============
	// Splits up the data packet received into separate messages from each
	// Service Interface Function Block agent and queues them into the LIFO
	// message buffer.
	//
	// Packet structure
	// ================
	// Example packet containing two data message packets together: *2|9|57.002834*1|6|-34.45
	//
	// 	 Start of packet character - currently character *
	// 	 SIFB instance ID - Integer.
	// 	 Field separator - currently character |
	//   Data field length - Integer
	//   Field separator - currently character |
	//   Data field - string
	//
	private void queuePacket(String dataPacket) {
		String SIFBinstanceID = "";
		int ptrStart = 0;
		int ptrEnd = 0;
		int fieldLen = 0;
		int packetLen = dataPacket.length();
		String message = "";

		System.err.println("[" + dataPacket + "] " + "[" + packetLen + "]\n");	//RA_BRD

		if (packetLen > 0) {
			ptrStart = -1;
			ptrEnd = 0;
			while (ptrStart < packetLen) {
				// Reinitialise for the next message.
				SIFBinstanceID = "";
				message = "";
				
				// Locate the start of the next message in the data packet
				ptrStart = dataPacket.indexOf(MESSAGE_START, ptrEnd);
				if (ptrStart >= 0) {
					// Found the start of a new message in the buffer. Locate the
					// end of the SIFB instance ID.
					ptrEnd = dataPacket.indexOf(FIELD_SEPARATOR, ptrStart + 1);
					if (ptrEnd > ptrStart) {
						SIFBinstanceID = dataPacket.substring(ptrStart + 1, ptrEnd);
					//	System.err.println("SIFBinstanceID [" + SIFBinstanceID + "]"); //RA_BRD
						ptrStart = ptrEnd + 1;
						// Extract the length of the message.
						ptrEnd = dataPacket.indexOf(FIELD_SEPARATOR, ptrStart + 1);
						if (ptrEnd > ptrStart) {
							fieldLen = Integer.valueOf(dataPacket.substring(ptrStart, ptrEnd));
						//	System.err.println("Field length " + fieldLen); //RA_BRD
							if (fieldLen > 0) {
								// Extract the message
								message = dataPacket.substring(ptrEnd + 1, ptrEnd + 1 + fieldLen);
							//	System.err.println("message [" + message + "]\n"); //RA_BRD
																
								TCPserverPacket packet = new TCPserverPacket();	
								packet.setSIFBinstanceID(SIFBinstanceID );
								packet.setdataPacket(message);
																
								FIFOqueue.add(packet);
								
								// Set the pointer to the beginning of where the next
								// message packet might be.
								ptrEnd = ptrEnd + 1 + fieldLen;
							} else {
								System.err.println("Bad field message pointer."); //RA_BRD
								break;
							}
						} else {
							System.err.println("Bad field length pointer.");  //RA_BRD
							break;
						}
					} else {
						System.err.println("Bad SIFB field pointer."); //RA_BRD
						break;
					}
				} else {
					break;
				}
			}
		}
	}

	//
	// getQueueSize()
	// ==============
	public int getQueueSize() {
		return FIFOqueue.size();
	}
	//
	// getPacket()
	// ===========
	public TCPserverPacket getPacket() {		
		TCPserverPacket packet = new TCPserverPacket();
		boolean found = false;
		String SIFBinstanceID = "";
		String dataPacket = "";
		
		if (FIFOqueue.size() > 0) {
			packet = FIFOqueue.poll();
			// System.err.println("getPacket()- " + packet.SIFBinstanceID + " [" + packet.dataPacket + "]");
			SIFBinstanceID = packet.getSIFBinstanceID();
			dataPacket = packet.dataPacket;
			found = true;
		}	
		return packet;
	}
	
	//
	// get hostName
	// ============
	public String hostName() {
		return this.hostName;
	}
	
	//
	// get listenerPortNumber
	// ======================
	public int listenerPortNumber() {
		return this.listenerPortNumber;
	}
}	

