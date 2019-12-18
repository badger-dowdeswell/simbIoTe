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

public class HVACsim {
	public static String appVersion = "1.1";
	private static boolean silence = false;

	//
	// main()
	// ======
	public static void main(String[] args) {
		say("HVAC Simulator version " + appVersion + "\n");
	}
	
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
}
