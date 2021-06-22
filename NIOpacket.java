//
// TCP SERVER PACKET
// =================
// (c) AUT University - 2019-2020
//
// Revision History
// ================
// 18.12.2019 BRD Original version based on the Fault Diagnostic Engine (FDE)
//				  version created on 05.07.2019	
// 08.01.2020 BRD Refactored to bring it up-to-date for use in the HMI simulator 
//				  system.
//
package HVACsim;

public class NIOpacket {
	private String command = "";
	String commandData = "";
				
	//
	// get command()
	// =============
	public String command() {
		return this.command;
	}
		
	//
	// set command()
	// =============
	public void command(String command) {
		this.command = command;
	}

	//
	// get commandData()
	// =================
	public String commandData() {
		return this.commandData;
	}
		
	//
	// set commandData()
	// =================
	public void commandData(String commandData) {
		this.commandData = commandData;
	}
}
