//
// NON-BLOCKING NETWORK SERVER
// ===========================
// Implements a threaded TCP/IP server that supports multiple, non-blocking
// connections. This allows the function block applications to connect
// and exchange information with the simulated environment controllers.
//
// (c) AUT University - 2019-2020
//
// Documentation
// =============
// Full documentation for this class is contained in the Fault Diagnosis System
// Software Architecture document. It is based on a version of the C++ class
// that was created for use in 4diac.
//
// A collection of session sockets are created while the server is interacting
// with the remote clients. All client sessions initiated by first connecting
// to the servers advertised host address and listener port. The server then
// manages a hand-over to a new session socket that manages that clients traffic
// for the duration of their session. 
//
// The server operates on its own thread since it needs to accept incoming 
// connections and buffer incoming data packets as soon as they arrive. To
// free up the simulator to do other things and only request packets when
// it is ready to, the server implements a First-In,First-Out (FIFO) packet
// queue. Each entry contains the data sent in one packet by the client. The 
// organisation of the fields in the packet is documented in
//
//
// Revision History
// ================
// 18.12.2019 BRD Original version based on the Fault Diagnostic Engine (FDE)
//				  version created on 05.07.2019	
// 02.01.2020 BRD Extending the server to manage direct interaction with the 
//                HMI. This implements an MVC-style model for exchanging data
//				  between the 4DIAC Service Interface Function Blocks and
//                the user interface.
// 08.01.2020 BRD Brought naming conventions in-line with the matching C++ 
//				  server and client components written for the FORTE function 
//				  block applications.
//
package HVACsim; // <-RA_BRD: Package needs to become generic so it can be moved into a production library.

import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import HVACsim.NIOstatus;
import HVACsim.NIOpacket;

public class NIOserver implements Runnable {
	//
	// Define the default input buffer size to read TCP
	// data into.
	final static int BUFFER_SIZE = 1024;

	// Data packet field separators. Please ensure
	// that any changes to these are also implemented
	// in the FORTE function blocks.
	final static String START_OF_PACKET = "*";
	final static String FIELD_SEPARATOR = "|";
	final static String END_OF_PACKET = "&";

	String hostName = "";
	int listenerPort = 0;
	HMIui ui;
	int serverStatus = NIOstatus.UNDEFINED;
	
	// FIFO queue for packets
	// ======================
	Queue<NIOpacket> FIFOqueue = new LinkedList<>();
	
	//
	// Server()
	// ========
	// Provides the hostName and listener port number via the
	// class constructor. This class implements Runnable so that
	// it can operate on its own thread.
	//
	public NIOserver(String hostName, int listenerPort, HMIui ui) {
		this.hostName = hostName;
		this.listenerPort = listenerPort;
		this.ui = ui;
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
	// This returns an integer status code from NIOstatusCodes.
	//
	public void run() {
		System.err.println("Server started on thread" + "\n"); //RA_BRD
		try {
			startServer(hostName, listenerPort);
		} catch (Exception e) {
			System.out.println("Server Exception caught on host " + hostName + " while trying to listen on port " + listenerPort + ":" ); //RA_BRD
			System.out.println(e.getMessage()); //RA_BRD
			serverStatus = NIOstatus.EXIT_FAILURE;
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
	// hostName				Fully-qualified network URL for this server
	//						instance that non-blocking clients can connect
	//						to. Either a name such as "localhost" or an
	//						IP address such as "127.0.0.1".
	//
	// listenerPort			The socket port that the server will listen on
	//						for incoming connections. This is a handover
	//						type socket. Once a client connects, the server
	//						will hand over the connection to an individual
	//						session socket that will manage the traffic for
	//						that client for the duration of the session.
	//
	// returns				One of the NIOstatus status codes.
	//
	@SuppressWarnings("static-access")
	public int startServer(String hostName, int listenerPort) throws Exception {
		int serverStatus = NIOstatus.EXIT_SUCCESS;
		NIOpacket packet = new NIOpacket();
		int packetLength = 0;
		String replyPacket = "";
		int lastSetTemperature = 0;
		int setTemperature = 0;
		String responsePacket = "";
		String command = "";	
		
		// RA_BRD Part of unit test to be moved into a proper test regime.
		// queuePacket("junk");
		// RA_BRD

		if (hostName.equals("")) {
			serverStatus = NIOstatus.INVALID_HOST_NAME;
		} else if (listenerPort <= 0) {
			serverStatus = NIOstatus.INVALID_LISTENER_PORT;
		} else {
			// Resolve the host address.
			InetAddress host = InetAddress.getByName(hostName);
		
			Selector selector = Selector.open();

			// Open a non-blocking listener socket to accept incoming connections.
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			serverSocketChannel.bind(new InetSocketAddress(host, listenerPort));
			serverSocketChannel.register(selector,  SelectionKey.OP_ACCEPT);

			// Ensure that after the channel has been opened, no channel is 
			// marked as "accepted".
			SelectionKey key = null;
			serverStatus = NIOstatus.EXIT_SUCCESS;
			//System.err.println("Server started.."); // RA_BRD

			// This section manages all the traffic across multiple client
			// connections.
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
							// Set the socket to read and write mode.
							sc.register(selector,  SelectionKey.OP_READ | SelectionKey.OP_WRITE);
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
								//System.out.println("[" + dataPacket + "]");
								
								// RA_BRD Interesting way of uniquely identifying this socket amongst the connections.
								// Perhaps we could run separate FIFO queues for each socket based on this identifier?
								System.out.println("socket identifier " + key.hashCode());								
								queuePacket(dataPacket);
								
								while (getQueueSize() > 0) {
									packet = getPacket();
									System.out.println(packet.command() + " " + packet.commandData());
									responsePacket = ui.externalEventHandler(packet.command(), packet.commandData()); 
									if (responsePacket.length() > 0) {
										System.out.println("Response packet [" + responsePacket + "]");
										if (key.isWritable()) {											
											ByteBuffer byteBuffer2 = ByteBuffer.wrap(responsePacket.getBytes());
											sc.write(byteBuffer2);
											byteBuffer2.clear();
											replyPacket = "";
										}
									}
								}
							}
								
							//	responsePacket = ui.externalEventHandler(String command, String commandData); 
								
						//		int temp = (int) Float.parseFloat(dataPacket);
						//		ui.showRoomTemperature(temp);
								
						//		setTemperature = ui.getSetTemperature();
						//		if (setTemperature != lastSetTemperature) {
						//			lastSetTemperature = setTemperature;
						//			replyPacket = "Set temperature changed " + setTemperature;
						//		}	
							
							//	if (responsePacket != "") {
							//		if (key.isWritable()) {
							//			System.out.println("--> [" + responsePacket + "]");
							//			ByteBuffer byteBuffer2 = ByteBuffer.wrap(responsePacket.getBytes());
							//			sc.write(byteBuffer2);
							//			byteBuffer2.clear();
							//			replyPacket = "";
							//		}
							//	}
							//}	
							//			// <RA_BRD
							//			temp = ui.getSetTemperature();
							//			if (lastSetTemperature != temp) {
							//				lastSetTemperature = temp;
							//				replyPacket = "set temperature changed [" + lastSetTemperature + "]\n";
							//				byteBuffer2 = ByteBuffer.wrap(replyPacket.getBytes());
							//				sc.write(byteBuffer2);
							//				byteBuffer2.clear();
							//				replyPacket = "";
							//			}
							//			// <RA_BRD
							//		}
							
							//}
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
	// Service Interface Function Block agent and queues them into the FIFO
	// message buffer. Since this is a non-blocking server, partial, incomplete
	// packets may be read in at times. The packet structure allows incomplete
	// packets to be buffered. The function only queues complete packets.
	//
	// Packet structure
	// ================
	// This example packet containing two data message packets together: *2|9|57.002834*1|6|-34.45
	//
	// 	 Start of packet character - currently character *
	//   Command string. Typically two or three character strings such as RS or Z1
	// 	 Field separator - currently character |
	//   Command data. May be blank if not needed.
	//   Field separator
	//   End of packet character - currently &
	//
	private void queuePacket(String dataPacket) {
		
		// <-RA_BRD convert this into a proper unit test later.
//		dataPacket = "erty" + START_OF_PACKET + END_OF_PACKET 
//					 + START_OF_PACKET + "RS" + FIELD_SEPARATOR + "here_be_data" + FIELD_SEPARATOR + END_OF_PACKET 
//					 + END_OF_PACKET 
//					 + END_OF_PACKET
//					 + "junk" + START_OF_PACKET + "VS" + FIELD_SEPARATOR + "here_be_data more" + FIELD_SEPARATOR + END_OF_PACKET
//				     + "BC" + FIELD_SEPARATOR + FIELD_SEPARATOR +  FIELD_SEPARATOR + END_OF_PACKET
//				     + START_OF_PACKET + FIELD_SEPARATOR + "here_be_bad data" + FIELD_SEPARATOR + END_OF_PACKET 
//				     + START_OF_PACKET + "LP" + FIELD_SEPARATOR + "here_be_the_last_good_data" + FIELD_SEPARATOR + END_OF_PACKET
//				     + "junk" + START_OF_PACKET + "GZ1" + FIELD_SEPARATOR + " " + FIELD_SEPARATOR + END_OF_PACKET 
//					 + START_OF_PACKET + "LAST LONG SPACED-OUT COMMAND" + FIELD_SEPARATOR + "REALLY THE LAST PACKET" + FIELD_SEPARATOR + END_OF_PACKET 
//					 + "badddd junk";
		// JUnit test for data packets.
				
		int ptrStart = 0;
		int ptrEnd = 0;
		int ptrFieldStart = 0;
		int ptrFieldEnd = 0;
		int packetLen = dataPacket.length();
		
		String message = "";
		String command = "";
		String commandData = "";

		System.out.println(dataPacket + "  [" + packetLen + "]\n");	// <--RA_BRD

		if (packetLen > 0) {
			ptrStart = -1;
			ptrEnd = 0;
			while (ptrStart < packetLen) {
				// Reinitialise for the next message.
				message = "";
				// Locate the start of the next message in the data packet
				ptrStart = dataPacket.indexOf(START_OF_PACKET, ptrEnd);
				if (ptrStart >= 0) {
					// Found the start of a new packet in the buffer. Locate
					// the end of the packet.
					ptrEnd = dataPacket.indexOf(END_OF_PACKET, ptrStart);
					if (ptrEnd == -1) {
						// There is no end of packet marker so either we have an incomplete buffer 
						// or lots of junk. At present, assume junk.. RA_BRD need to buffer this better.
						break;
					} else {
						message = dataPacket.substring(ptrStart, ptrEnd + 1);
						
						System.out.println("[" + message + "]");
						command = "";
						commandData = "";
						ptrFieldStart = 1;
						ptrFieldEnd = message.indexOf(FIELD_SEPARATOR);
						if (ptrFieldEnd > 0) {
							command = message.substring(ptrFieldStart, ptrFieldEnd);
							if (command.length() > 0) {
								ptrFieldStart = ptrFieldEnd;
								ptrFieldEnd = message.indexOf(FIELD_SEPARATOR, ptrFieldStart + 1);
								if (ptrFieldEnd > 0) {
									commandData = message.substring(ptrFieldStart + 1, ptrFieldEnd);
								
									System.out.println("-- message [" + command + "] [" + commandData + "]");
									NIOpacket packet = new NIOpacket();	
									packet.command(command);
									packet.commandData(commandData);
									FIFOqueue.add(packet);
								}	
							} else {
								System.err.println("Missing command field in current packet."); // <-RA_BRD
							}
						}	
						ptrStart = ptrEnd;
					}	
				} else {
					// There are no more messages in the buffer.
					break;	
				}	
			}
		}
		
		// RA_BRD convert this to a proper Unit Test later.
//		System.out.println("Queue size = " + getQueueSize());
//		NIOpacket packet = new NIOpacket();
//		while (getQueueSize() > 0) {
//			packet = getPacket();
//			System.out.println(packet.command() + " " + packet.commandData());
//		}
//		System.out.println("");
		// RA_BRD
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
	public NIOpacket getPacket() {		
		NIOpacket packet = new NIOpacket();
		boolean found = false;
		String SIFBinstanceID = "";
		String dataPacket = "";
		
		if (FIFOqueue.size() > 0) {
			packet = FIFOqueue.poll();
			// System.err.println("getPacket()- " + packet.SIFBinstanceID + " [" + packet.dataPacket + "]");
		//	SIFBinstanceID = packet.getSIFBinstanceID();
		//	dataPacket = packet.dataPacket;
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
	// get listenerPort
	// ======================
	public int listenerPort() {
		return this.listenerPort;
	}
}	
