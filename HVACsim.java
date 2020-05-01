//
// HVAC SIMULATOR
// ==============
// Simulates a room that is being heated and cooled by a heating, ventilation and
// air conditioning system (HVAC). The simulator provides an model of a physical
// environment that an IEC 61499 Function Block application can interact with.
//
// (c) AUT University - 2019-2020
//
// Documentation
// =============
// The application provides both a Human Machine Interaction (HMI) to model the
// rooms in a building as well as a IP server. This server allows function block 
// applications to connect to the simulator as IP clients. A packet exchange 
// protocol allows function block applications to exchange information with the
// simulation and interact with it.
//
// Revision History
// ================
// 18.12.2019 BRD Original version
// 19.01.2020 BRD Linked in new non-blocking network libraries.
// 13.01.2020 BRD Crafted the new Environment section of the simulation.
// 
package HVACsim;

import javax.swing.SwingUtilities;

public class HVACsim {
	public static String appVersion = "1.1";
	private static boolean isSilent = true;
	
	//
	// main()
	// ======
	public static void main(String[] args) {
		say("HVAC Simulator version " + appVersion + "\n");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createSimulation();
			}
		});
	}
	
	//
	// createSimulation()
	// ==================	
	private static void createSimulation() {
		say("HVAC Simulator version " + appVersion + "\n");
		//
		// RA_BRD parameters are windowTop, windowLeft, windowWidth, windowHeight. 
		//        Are these in the right (i.e. standard) order for the documented
		//        AWT libraries? A lot of the functions seem to switch them around
		//        inconsistently. Also, fix up magic numbers and since the HMI 
		//		  automatically resized the window to fit the contents, do we need
		//        the window height and width parameters?
		HMIui ui = new HMIui("simbIoTe HVAC Room Simulator", 0, 2000, 800, 500); //0, 2000, 800, 500
		
		Environment envr = new Environment(ui);
		new Thread(envr).start();
		
		// RA_BRD parameterise these properly from a configuration file.
		startServer("127.0.0.1", 62501, envr);
	}	
	
	//
	// startServer()
	// =============
	// Creates and starts the non-blocking TCP/IP server.
	//
	private static void startServer(String hostName, int listenerPort, Environment envr) {
		NIOserver server = new NIOserver(hostName, listenerPort, envr);
		new Thread(server).start();
		// RA_BRD make sure the server starts properly and returns back a status.
	}

	//
	// say()
	// =====
	// Output a console message for use during debugging. This
	// can be turned off by setting the private boolean variable 
	// isSilent true.
	//
	private static void say(String whatToSay){
		if(!isSilent) {
			System.out.println(whatToSay);
		}
	}	
}
