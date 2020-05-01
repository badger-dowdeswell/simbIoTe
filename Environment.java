//
// SIMULATION ERNVIRONMENT
// =======================
// This class implements a model of the physical environment of the simulator that
// is visualised by the HMIui component.
//
// AUT University - 2019-2020.
//
// Revision History
// ================
// 13.01.2020 BRD Original version.
// 21.01.2020 BRD Migrated the external event handler into here based on feedback
//                from the emsoft team.
//
package HVACsim;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.text.DecimalFormat;

public class Environment implements Runnable {
	//
	// Define environment characteristics
	// ==================================
	// Define module-level data to represent characteristics of the
	// environment that can vary as the simulation progresses. The
	// HMIui requests these from the Environment when the model
	// view needs to be refreshed.
	//
	// In this example, note that the temperature of each zone in the
	// building is modeled here, but the zone temperature is not. That 
	// is appropriate since each zone temperature is a characteristic
	// of a different part of the building. The zone set temperature
	// is something: it is a managed property of each zone controller,
	// set by the user not the environment.
	//  
	//int Zone1temperature = 18;
	//int Zone2temperature = 18;
	//int Zone3temperature = 18;
	
	float Zone1temperature = (float) 14.3;
	float savedZone1temperature = Zone1temperature;
	float Zone2temperature = (float) 18.7;
	float Zone3temperature = (float) 19.18;
	
	
	long elapsedTime = System.currentTimeMillis() / 1000;
	boolean temperatureIncrease = false;
	
	// Internal environment control
	// ============================
	private static boolean isSilent = false;
	private HMIui ui;
	
	public Environment(HMIui ui) {
		this.ui = ui;
	}
	
	//
	// runEnvironment()
	// ================
	// This function manages all changes in the environment using custom 
	// rules and timers.
	// 
	// Note that it regularly releases its timeslice to ensure other threads
	// do not get delayed as it executes its main loop.
	//
	private void runEnvironment() {
		final int MAX_TEMP = 50;
		final int MIN_TEMP = -50;
		int newTemperature = 0;
		long newElapsedTime = 0;
		int cycleCount = 0;
		
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		//System.out.println(df.format(decimalNumber));
		
		
		while (true) {
			newElapsedTime = System.currentTimeMillis();
			// RA_BRD BEWARE ! - this won't wrap around properly at midnight, Cinderella....
			if ((newElapsedTime - elapsedTime) >= 2000) {
				elapsedTime = newElapsedTime;
				cycleCount++;
				
				if (cycleCount > 10) {
					int min = 1;
					int max = 10;
					int value = 0;
					Random r = new Random();
					value = r.nextInt((max - min) + 1) + min;
					if (value > 9) {
						// Generate an outlier temperature spike
						Zone1temperature = Zone1temperature + (float) 10.0;
						ui.labelZone3.setText(Zone1temperature + "\u00B0");
						say("Zone 1 temperature " + Zone1temperature);
					} else {	
						// Calculate a new random temperature
						Zone1temperature = savedZone1temperature; 
						newTemperature = (int) (Math.random() * ((MAX_TEMP - MIN_TEMP) + 1 )) + MIN_TEMP;
						Zone1temperature = Zone1temperature + ((float) newTemperature / 100);
						Zone1temperature = Float.valueOf(df.format(Zone1temperature));
						ui.labelZone3.setText(Zone1temperature + "\u00B0");
						say("Zone 1 temperature " + Zone1temperature);
						savedZone1temperature = Zone1temperature;
					}		
				} else {	
					// Calculate a new random temperature
					Zone1temperature = savedZone1temperature; 
					newTemperature = (int) (Math.random() * ((MAX_TEMP - MIN_TEMP) + 1 )) + MIN_TEMP;
					Zone1temperature = Zone1temperature + ((float) newTemperature / 100);
					Zone1temperature = Float.valueOf(df.format(Zone1temperature));
					ui.labelZone3.setText(Zone1temperature + "\u00B0");
					say("Zone 1 temperature " + Zone1temperature);
					savedZone1temperature = Zone1temperature;
				}
			}	
		
			Thread.currentThread();
			Thread.yield();
		}
	}
	
	//
	// EXTERNAL EVENT HANDLER
	// ======================
	// The Environment receives requests for data and updates from the external systems 
	// it is connected to via the server session clients. This event handler
	// is customised to process the commands that have been defined for this
	// particular system. The environment holds a reference to the HMI so that
	// it can update the simulation view.
	//
	// command		Command received from the client. These must have been 
	//              predefined for this particular simulation. E
	//
	// commandData  Data that has been supplied with the command. May be blank
	//              if not needed for that particular command.
	//
	// returns      A response packet, usually a data value, appropriate to
	//              the command received. Will be blank if no command response
	//              is required.
	//
	public String externalEventHandler(String command, String commandData) {
		String responsePacket = "";
		int pointPosn = 0;
		int temperature = 0;
		float fahrenheitTemperature = 0;
		
		switch (command) {
		case "GZ1":
			// Requesting the current temperature in zone 1. Note that the sensor being simulated
			// outputs temperatures in Fahrenheit. 
			fahrenheitTemperature = (Zone1temperature * (float) 1.8) + 32 ;
			responsePacket = "*GZ1|" + fahrenheitTemperature + "|&";   
			break;
			
		case "GZ2":
	//		responsePacket = "*GZ2|" + Zone2temperature + "|&";
			break;
			
		case "SW1":
			// Read the Zone 1 set temperature up and down buttons 
			if (ui.cmdUpClicked()) {
				responsePacket = "T";
			} else {
				responsePacket = "F";
			}
			
			if (ui.cmdDownClicked()) {
				responsePacket = responsePacket + "T";
			} else {
				responsePacket = responsePacket + "F";
			}
			responsePacket = "SW1|" + responsePacket + "|&";
			break;
		
		case "DZ1":
			// Display the new temperature in zone 1
			pointPosn = commandData.indexOf(".");
			if (pointPosn > 0) {
				temperature = Integer.parseInt(commandData.substring(0, pointPosn));
				ui.showRoomTemperature(1, temperature); 
			}
	 		break;
	
		case "DZ2":
			// Display the new temperature in zone 2
			//packet = "*DZ" + to_string(ZONE()) + "|" + to_string(TEMP()) +"|&";
			//client.sendPacket(packet);
 			break;
		
	    
		case "DS1":
			// Display the new set temperature for zone 1
			pointPosn = commandData.indexOf(".");
			if (pointPosn > 0) {
				temperature = Integer.parseInt(commandData.substring(0, pointPosn));
				ui.showSetTemperature(1, temperature); 
			}
			break;
		
		case "DS2":
			// Display the new set temperature for zone 2
			//packet = "*DS" + to_string(ZONE()) + "|" + to_string(SET_TEMP()) +"|&";
			//client.sendPacket(packet);
			break;
		
		default:
			say("Unrecognised command '" + command + "' with commandData '" + commandData + "'");
			break;
		}
		return responsePacket;
	}

	
	// get Zone1temperature()
	// ======================
	public float Zone1temperature() {
		return this.Zone1temperature;
	}
	
	//
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
	@Override
	public void run() {
		try {
			runEnvironment();
			say("Environment started on thread" + "\n"); 
		} catch (Exception e) {
			say("Environment could not be created. " + e.getMessage());
		}
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
	
	//
	// delay()
	// =======
	// Delays the execution of the program.
	//
	// delayTime Delay specified in seconds.
	//
	private static void delay(int delayTime) {
		try {
			TimeUnit.SECONDS.sleep((long) delayTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}	
	}
}
