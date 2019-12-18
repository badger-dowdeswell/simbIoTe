//
// TCP SERVER PACKET
// =================
// (c) AUT University - 2019-2020
//
// Revision History
// ================
// 18.12.2019 BRD Original version based on the Fault Diagnostic Engine (FDE)
//				  version created on 05.07.2019	
//
package HVACsim;

public class TCPserverPacket {
	private String SIFBinstanceID = "";
	String dataPacket = "";
				
	//
	// getSIFBinstanceID()
	// ===================
	public String getSIFBinstanceID() {
		return this.SIFBinstanceID;
	}
		
	//
	// setSIFBinstanceID()
	// ===================
	public void setSIFBinstanceID(String newSIFBinstanceID) {
		this.SIFBinstanceID = newSIFBinstanceID;
	}

	//
	// getdataPacket()
	// ===============
	public String getdataPacket() {
		return this.dataPacket;
	}
		
	//
	// setdataPacket()
	// ===============
	public void setdataPacket(String newdataPacket) {
		this.dataPacket = newdataPacket;
	}
}
