//
// HVAC SIMULATOR
// ==============
// Simulates a room that is being heated and cooled by a HVAC
// Function Block application.
//
// (c) AUT University - 2019-2020
//
// Documentation
// =============
//
// Revision History
// ================
// 18.12.2019 BRD Original version
// 
package HVACsim;

import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

public class HVACsim {
	public static String appVersion = "1.1";
	private static boolean silence = false;

	//
	// main()
	// ======
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createGUI();
			}
		});
	}
	
	//
	// createGUI()
	// ===========	
	private static void createGUI() {
		EnvironmentUI ui;
		
		say("HVAC Simulator version " + appVersion + "\n");
		// windowTop, windowLeft, windowWidth, windowHeight
		ui = new EnvironmentUI("HVAC Simulator version " + appVersion, 0, 2000, 800, 500);
		startServer("127.0.0.1", 62501, ui);
	}	
	
	//
	// startServer()
	// =============
	private static void startServer(String hostName, int listenerPortNumber, EnvironmentUI ui) {	
		TCPserver server = new TCPserver("127.0.0.1", 62501, ui);
		new Thread(server).start();
	}
		
		
	//	TCPserverPacket packet = new TCPserverPacket();
	//	String dataPacket = "";	
	//	TCPserver server = new TCPserver("127.0.0.1", 62501);
	//	new Thread(server).start();
		
		
		
		// Monitor the network. Read the incoming SIFB data queues until they are empty 
//		do {
	//		while (server.getQueueSize() > 0) {
	//			packet = server.getPacket();
	//			System.err.println("Received_" + packet.getSIFBinstanceID() + " [" + packet.getdataPacket() + "] [" + server.getQueueSize() + "]");
	//		}
	//		delay((long) 1);
	//	} while(true);
	//}
	
	
	
	//
	// say()
	// =====
	// Output a console message for use during debugging. This
	// can be turned off by setting the private boolean variable 
	// silence false.
	//
	private static void say(String whatToSay){
		if(!silence) {
			System.err.println(whatToSay);
		}
	}	
	
	//
	// delay()
	// =======
	private static void delay(Long delayTime) {
		try {
			TimeUnit.SECONDS.sleep((long) delayTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}
