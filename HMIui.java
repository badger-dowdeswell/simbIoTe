//
// HMI USER INTERFACE
// ==================
// Generic Human Machine Interface (HMI) and simulation framework for IEC 61499
// function block applications. The simulator provides a graphical user interface
// and interactive controls. It communicates with function block applications via
// a non-blocking TCP/IP server that supports multiple client connections.
//
// AUT University - 2019-2020.
//
// Revision History
// ================
// 18.12.2019 BRD Original version.
// 01.01.2020 BRD Extended the TCP interface to allow it to exchange
// 				  data between the 4DIAC function blocks and this HMI.
// 08.01.2020 BRD Implemented event handlers that allows the server to
//				  exchange data with the HMI UI.
//
package HVACsim;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.lang.Math.*;

public class HMIui extends JFrame{
	private static final long serialVersionUID = 1L;
	
	// The path to the directory where the graphics images can
	// be loaded from. <-RA_BRD can we load this dynamically
	//                          at run time?
	//
	String graphicsPath = System.getProperty("user.home") + "//Development/Java/HVACsim/src/HVACsim/graphics/";
	
	// Constants for the individual JLayeredPanel layers.
	private static int BASE_LAYER = 0;  // The lowest base level.
	private static int LAYER_1 = 1;		// Layer one, the next highest layer above
										// the BASE_LAYER.
	private static int DEPTH = 1;		// <-RA_BRD What does this do?
	
	// COMPONENT DEFINITIONS AND VALUES
	// ================================
	// The complete list distinct components that are needed for this
	// HMI. The components only need to be defined here if they have to 
	// interact with function block applications or other components when
	// the simulation is running. All other non-interactive components such
	// as labels or images can be created later in-line as needed.
	//
	public JLabel digitTemp1 = new JLabel();
	public JLabel digitTemp2 = new JLabel();
	public JLabel digitTemp3 = new JLabel();
	
	JButton cmdSetUp = new JButton("");
	JButton cmdSetDown = new JButton("");
	
	int Zone1temperature = 15;
	int Zone2temperature = 10;
	int Zone3temperature = 15;
	
	int Zone1setTemperature = 10;
	int Zone2setTemperature = 10;
	int Zone3setTemperature = 10;
	
	public JLabel digitSet1 = new JLabel();
	public JLabel digitSet2 = new JLabel();
	public JLabel digitSet3 = new JLabel();
	
	JLabel labelZone1 = new JLabel();
	
	//
	// HMI DEFINITION
	// ==============
	// Implements the inactive and interactive components within a Java Swing JFrame inside
	// the windows JFrame.
	//
	// title         The window title.
	//
	// windowTop     Vertical coordinate of the top left corner of the window relative to the 
	//				 desktop window.
	// 
	// windowLeft    Horizontal left coordinate of the window relative to the desktop window.
	// 		

	// windowWidth	 Default width of the window. This is a default that is overridden by the 
	//				 JPanel container after it has dynamically sized the components it contains.
	// 
	// windowHeight  Default height of the window. Also resized automatically by the JPanel.
	//
	public HMIui(String title, int windowTop, int windowLeft, int windowWidth, int windowHeight)  {
		// Create and layout the components using a Swing GridBagLayout. All components are laid out
		// on top of a JPanel called layoutPanel. This panel gets resized automatically as controls 
		// are laid out on it. Finally, the layout panel will be added to the JFrame that will get 
		// resized to wrap around the edges of the layoutPanel.
		// 
		JPanel layoutPanel = new JPanel(new GridBagLayout());
		layoutPanel.setBackground(Color.WHITE);
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLayeredPane layeredPane = new JLayeredPane();
		// These components are used as templates to create each component from before adding it 
		// to the layoutPanel or the layeredPanel. Interactive components that must retain their
		// own identify must be defined in the COMPONENT DEFINITIONS AND VALUES section above.
		//
		JLabel label = new JLabel();
		// Holds an image that can be displayed in a JLabel container.
		ImageIcon icon = new ImageIcon();
		
		//
		// INDIVIDUAL GRIDBAG SUB-PANELS WITHIN THE WINDOW
		// ===============================================
		// Define each of the sub-panels that contain groups of related components. Each
		// sub-panel is then added to the main layoutPanel in a specified GridBad layout position.
		//
		
		//
		// RIGHT LAYERED PANE 1,0  (NEW LEFT)
		// ======================
		// Displays a zone controller panel. 
		//
		layeredPane = new JLayeredPane();
		
		// Display the image of the controller using an JLabel.
		icon = new ImageIcon(graphicsPath + "testController.png");
		label = new JLabel();
		label.setIcon(icon);
		label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		label.setForeground(Color.WHITE);
		layeredPane.add(label, BASE_LAYER, DEPTH); 
		layeredPane.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); 
		
		label = new JLabel("Lounge temperature");
		label.setBounds(73, 60, 130, 20);
		label.setForeground(new Color(36, 203, 252));
		layeredPane.add(label, LAYER_1, DEPTH); 
		
		label = new JLabel("Set");
		label.setForeground(new Color(36, 203, 252));
		label.setBounds(248, 60, 120, 20);
		layeredPane.add(label, LAYER_1, DEPTH); 
	
		// Display the room temperature digits on layer one.
		digitTemp3 = new JLabel();
		icon = new ImageIcon(graphicsPath + "digitLarge_8.png");
		digitTemp3.setIcon(icon);
		digitTemp3.setBounds(64, 90, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(digitTemp3, LAYER_1, DEPTH); 
		
		digitTemp2 = new JLabel();
		digitTemp2.setIcon(icon);
		digitTemp2.setBounds(108, 90, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(digitTemp2, LAYER_1, DEPTH); 
		
		digitTemp1 = new JLabel();
		digitTemp1.setIcon(icon);
		digitTemp1.setBounds(152, 90, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(digitTemp1, LAYER_1, DEPTH); 
		
		// The room set temperature digits are made from the same images as the
		// room temperature ones but they are scaled dynamically.
		final int HORIZONTAL_SCALE = 25;  // 25% of the original size
		final int VERTICAL_SCALE = 45;    // 45% of the original size. RA_BRD can we scale using the 
		                                  // images Aspect ratio?
		
		// Create a digit image and reuse it for all three digits in the set LED display. Note the
		// way the image is scaled using getScaledInstance().
		ImageIcon imageIcon = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_8.png").getImage().getScaledInstance(HORIZONTAL_SCALE,
				                                          VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
		digitSet3 = new JLabel();
		digitSet3.setIcon(imageIcon);
		digitSet3.setBounds(205, 90, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		layeredPane.add(digitSet3, LAYER_1, DEPTH); showSetTemperature(Zone1setTemperature);
	
		digitSet2 = new JLabel();
		digitSet2.setIcon(imageIcon);
		digitSet2.setBounds(235, 90, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		layeredPane.add(digitSet2, LAYER_1, DEPTH); showSetTemperature(Zone1setTemperature);
		
		digitSet1 = new JLabel();
		digitSet1.setIcon(imageIcon);
		digitSet1.setBounds(265, 90, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		layeredPane.add(digitSet1, LAYER_1, DEPTH); 
		
		// Interactive set temperature switches implemented as JButton components.
		icon = new ImageIcon(graphicsPath + "cmdSetUp.png");
		cmdSetUp.setIcon(icon);		
		cmdSetUp.setFocusPainted(false);
		cmdSetUp.setBorder(BorderFactory.createEmptyBorder());
		cmdSetUp.setBounds(302, 75, icon.getIconWidth(), icon.getIconHeight());
	
		constraints.gridx = 1; 
		constraints.gridy = 1;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,1);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdSetUp, LAYER_1, DEPTH); //, 1);
		
		icon = new ImageIcon(graphicsPath + "cmdSetDown.png");
		cmdSetDown.setIcon(icon);		
		cmdSetDown.setFocusPainted(false);
		cmdSetDown.setBorder(BorderFactory.createEmptyBorder());
		cmdSetDown.setBounds(302, 110, icon.getIconWidth(), icon.getIconHeight());

		constraints.gridx = 0;   // 1
		constraints.gridy = 0;   // 1
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,1);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdSetDown, LAYER_1, DEPTH); //, 1);
	
		// Display the room selection buttons.
	//	JButton cmdZone1 = new JButton("");
	//	icon = new ImageIcon(graphicsPath + "cmdZone1.png");
	//	cmdZone1.setIcon(icon);		
	//	//cmdZone1.setBorder(BorderFactory.createEmptyBorder());
	//	cmdZone1.setFocusPainted(true);
	//	cmdZone1.setBounds(60, 180, icon.getIconWidth(), icon.getIconHeight());
	//	cmdZone1.addActionListener(new ActionListener() {
	//		@Override
	//		//
	//		// cmdZone1_Click()
//			// ================
//			public void actionPerformed(ActionEvent event) {
//				// TODO Auto-generated method stub
//				// <RA_BRD tidy up later - this saves a lot of time at the moment.
//				System.out.println("Clicked Zone 1.");
//				showRoomTemperature(Zone1temperature); 
//			}
//		});
//		
//		constraints.gridx = 1; 
//		constraints.gridy = 1;  
//		constraints.ipadx = 0;
//		constraints.ipady = 0;
//		constraints.insets = new Insets(10,10,10,10);
//		constraints.anchor = GridBagConstraints.LINE_END;
//		constraints.fill = GridBagConstraints.NONE;
//		layeredPane.add(cmdZone1, LAYER_1, DEPTH); //, 1);
//		
//		JButton cmdZone2 = new JButton("");
//		icon = new ImageIcon(graphicsPath + "cmdZone2.png");
//		cmdZone2.setIcon(icon);
//		//cmdZone1.setBorder(BorderFactory.createEmptyBorder());
//		cmdZone2.setFocusPainted(true);
//		cmdZone2.setBounds(160, 180, icon.getIconWidth(), icon.getIconHeight());
//		cmdZone2.addActionListener(new ActionListener() {
//			@Override
//			//
//			// cmdZone2_Click()
//			// ================
//			public void actionPerformed(ActionEvent event) {
//				// TODO Auto-generated method stub
//				// <RA_BRD tidy up later - this saves a lot of time at the moment.
//				System.out.println("Clicked Zone 2.");
//				showRoomTemperature(Zone2temperature); 
//			}
//		});
//		
//		constraints.gridx = 1;  
//		constraints.gridy = 1;  
//		constraints.ipadx = 0;
//		constraints.ipady = 0;
//		constraints.insets = new Insets(10,10,10,10);
//		constraints.anchor = GridBagConstraints.LINE_END;
//		constraints.fill = GridBagConstraints.NONE;
//		layeredPane.add(cmdZone2, LAYER_1, DEPTH); //, 1);
//		
//		JButton cmdZone3 = new JButton("");
//		icon = new ImageIcon(graphicsPath + "cmdZone3.png");
//		cmdZone3.setIcon(icon);
//		//cmdZone1.setBorder(BorderFactory.createEmptyBorder());
//		cmdZone3.setFocusPainted(true);
//		cmdZone3.setBounds(258, 180, icon.getIconWidth(), icon.getIconHeight());
//		cmdZone3.addActionListener(new ActionListener() {
//			@Override
//			//
//			// cmdZone3_Click()
//			// ================
//			public void actionPerformed(ActionEvent event) {
//				// TODO Auto-generated method stub
//				// <RA_BRD tidy up later - this saves a lot of time at the moment.
//				System.out.println("Clicked Zone 3.");
//				showRoomTemperature(Zone3temperature); 
//			}
//		});
//		
//		constraints.gridx = 1;  
//		constraints.gridy = 1;  
//		constraints.ipadx = 0;
//		constraints.ipady = 0;
//		constraints.insets = new Insets(10,10,10,10);
//		constraints.anchor = GridBagConstraints.LINE_END;
//		constraints.fill = GridBagConstraints.NONE;
//		layeredPane.add(cmdZone3, LAYER_1, DEPTH); //, 1);
		
		// Setup the constraints for the layeredPane to be applied when
		// it is added to the master or base layoutPanel.		
		constraints.gridx = 0;  
		constraints.gridy = 0;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(120,20,20,20);
		constraints.anchor = GridBagConstraints.FIRST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layoutPanel.add(layeredPane, constraints);
	
		//	
		// LEFT LAYERED PANE 0,0 NEW RIGHT
		// =====================
		layeredPane = new JLayeredPane();
		
		// Display the background image on the base layer using a JLabel.
		icon = new ImageIcon(graphicsPath + "smallRoomLayout4.png");   // was 3
		label = new JLabel();
		label.setIcon(icon);
		label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(label, BASE_LAYER, DEPTH); 
		layeredPane.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); 
		
		// Display static labels on layer one, just above the base layer.
		//JLabel labelZone1 = new JLabel();
		labelZone1.setText(Zone1temperature + "\u00B0");
		labelZone1.setBounds(670, 200, 100, 100);
		layeredPane.add(labelZone1, LAYER_1, DEPTH); 	
		
		JLabel labelZone2 = new JLabel();
		labelZone2.setText(Zone2temperature + "\u00B0");
		labelZone2.setBounds(610, 355, 100, 100);
		layeredPane.add(labelZone2, LAYER_1, DEPTH); //, 1);
		
	//	JLabel labelZone3 = new JLabel();
	//	labelZone3.setText(Zone3temperature + "\u00B0");
	//	labelZone3.setBounds(559, 325, 100, 100);
	//	layeredPane.add(labelZone3, LAYER_1, DEPTH); //, 1);	
	
		// Setup the constraints for the layeredPane to be applied when
		// it is added to the foundation layoutPanel.
		constraints.gridx = 1;  // 0
		constraints.gridy = 0;  // 0
		constraints.ipadx = 0;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,0);
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layoutPanel.add(layeredPane, constraints);
			
		
		//
		// BOTTOM LAYERED PANE 1,1
		// =======================
		// Command button
		JButton cmdButton = new JButton("OK");
		cmdButton.setBounds(100, 100, 50, 20);
		cmdButton.addActionListener(new ActionListener() {
			@Override
			//
			// cmdButton_Click()
			// =================
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				// <RA_BRD tidy up later - this saves a lot of time at the moment.
			//	temp = temp + 1;
			//	System.out.println("Temperature = " + temp);				
			//	showRoomTemperature(temp); 
				// System.exit(0);
			}
		});
		
		constraints.gridx = 1;   
		constraints.gridy = 1;  
		constraints.ipadx = 50;
		constraints.ipady = 10;
		constraints.insets = new Insets(10,10,10,10);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layoutPanel.add(cmdButton, constraints);
		
		//
		// WINDOW CREATION
		// ===============
		// Finally, set the main window properties and display it after
		// dynamically resizing the window to fit around the JPanel.
		//
		defineHMIEvents();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(title);
		
		// Add the layout JPanel background to the main JFrame.
		this.getContentPane().add(layoutPanel);
		this.setLocationRelativeTo(null);
		this.setLocation(windowLeft, windowTop);
		this.setResizable(true);
		
		showSetTemperature(Zone1setTemperature);
		showRoomTemperature(Zone1temperature);
;	
		// Resize the window to fit the layout panel perfectly.
		this.getContentPane().setSize(new Dimension(layoutPanel.getSize())); 
		this.pack();
		this.setVisible(true);
	}
	
	//
	// HMI EVENTS FOR USER INTERACTIONS
	// ================================
	// Define all the events here that are triggered when interactive components respond to
	// the users.
	//
	void defineHMIEvents() {
		//
		// cmdSetUp_Click()
		// ================
		cmdSetUp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Zone1setTemperature++;
				//showSetTemperature(Zone1setTemperature);
			}
		});
		
		//
		// cmdSetDown_Click()
		// ==================
		cmdSetDown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Zone1setTemperature--;
				//showSetTemperature(Zone1setTemperature);
			}
		});
	}
	
	//
	// EXTERNAL EVENT HANDLER
	// ======================
	// The HMI receives requests for data and updates from the external systems 
	// it is connected to via the server session clients. This event handler
	// is customised to process the commands that have been defined for this
	// particular system.
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
		int setTemperature = 0;
		
		switch (command) {
		case "GZ1":
			setTemperature = Integer.parseInt(commandData);
			showRoomTemperature(setTemperature);
			showSetTemperature(Zone1setTemperature);
			responsePacket = "*GZ1|" + getSetTemperature() + "|&";   //Zone1temperature + "|&";
			break;
			
		case "GZ2":
			responsePacket = "*GZ2|" + Zone2temperature + "|&";
			break;
	
		default:
			System.out.print("Unrecognised command '" + command + "' with commandData '" + commandData + "'");
			break;
		}
		return responsePacket;
	}

	//
	// OTHER GENERAL-PURPOSE FUNCTIONS
	// ===============================
	// All other general-purpose functions are specified here.
	//
	// showRoomTemperature()
	// =====================
	// Displays a temperature on a three-digit LED panel
	//
	public void showRoomTemperature(int displayTemperature) {
		int absTemperature = Math.abs(displayTemperature);
		String temperature = "000" + Integer.toString(absTemperature);
		temperature = temperature.substring(temperature.length() - 3);
		
		Icon icon = new ImageIcon();
		Icon iconBlank = new ImageIcon(graphicsPath + "digitLarge_Blank.png");
		Icon iconMinus = new ImageIcon(graphicsPath + "digitLarge_Minus.png");
		
		// RA_BRD
		Zone1temperature = displayTemperature;
	//	labelZone1.setText(Zone1temperature + "\u00B0");
		
		icon = new ImageIcon(graphicsPath + "digitLarge_" + temperature.substring(2, 3) + ".png");
		digitTemp1.setIcon(icon);
		
		if (absTemperature < 10) {
			if (displayTemperature < 0) {
				digitTemp2.setIcon(iconMinus);
			} else {
				digitTemp2.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(graphicsPath + "digitLarge_" + temperature.substring(1, 2) + ".png");
			digitTemp2.setIcon(icon);
		}	
		
		if (absTemperature < 100) {
			if ((displayTemperature < 0) && (absTemperature > 9)) {
				digitTemp3.setIcon(iconMinus);
			} else {
				digitTemp3.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(graphicsPath + "digitLarge_" + temperature.substring(0, 1) + ".png");
			digitTemp3.setIcon(icon);
		}	
	}
	
	//
	// showSetTemperature()
	// ====================
	// Displays the preset temperature on the small three-digit LED panel
	//
	public void showSetTemperature(int displayTemperature) {
		// The room set temperature digits are made from the same images as the
		// room temperature ones but they are scaled dynamically.
		final int HORIZONTAL_SCALE = 25;
		final int VERTICAL_SCALE = 45;
		
		int absTemperature = Math.abs(displayTemperature);
		String temperature = "000" + Integer.toString(absTemperature);
		temperature = temperature.substring(temperature.length() - 3);
		
		ImageIcon icon = new ImageIcon();
		Icon iconBlank = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_Blank.png")
				                       .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
		Icon iconMinus = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_Minus.png")
				                       .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT));
		
		// <--RA_BRD Why are these images here? They are not used....
	//	ImageIcon imageBlank = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_Blank.png")
	//			                                           .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
	//	ImageIcon imageMinus = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_Minus.png")
	//			                                           .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
		
		icon = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_" + temperature.substring(2, 3) + ".png")
				             .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
		digitSet1.setIcon(icon);
		
		if (absTemperature < 10) {
			if (displayTemperature < 0) {
				digitSet2.setIcon(iconMinus);
			} else {
				digitSet2.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_" + temperature.substring(1, 2) + ".png")
					                           .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
			digitSet2.setIcon(icon);
		}	
		
		if (absTemperature < 100) {
			if ((displayTemperature < 0) && (absTemperature > 9)) {
				digitSet3.setIcon(iconMinus);
			} else {
				digitSet3.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(new ImageIcon(graphicsPath + "digitLarge_" + temperature.substring(0, 1) + ".png")
					                           .getImage().getScaledInstance(HORIZONTAL_SCALE, VERTICAL_SCALE, Image.SCALE_DEFAULT)); 
			digitSet3.setIcon(icon);
		}	
	}
	
	//
	// getSetTemperature()
	// ===================
	public int getSetTemperature() {
		return Zone1setTemperature;
	}
}
