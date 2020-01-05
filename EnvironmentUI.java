//
// ENVIRONMENT USER INTERFACE
// ==========================
// Revision History
// ================
// 18.12.2019 BRD Original version.
// 01.01.2020 BRD Extended the TCP interface to allow it to exchange
// data between the 4DIAC function blocks and this HMI.
//
package HVACsim;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.lang.Math.*;

public class EnvironmentUI extends JFrame{
	private static final long serialVersionUID = 1L;
	private static int BASE_LAYER = 0;
	private static int LAYER_1 = 1;
	private static int DEPTH = 1;
	
	public JLabel digitTemp1 = new JLabel();
	public JLabel digitTemp2 = new JLabel();
	public JLabel digitTemp3 = new JLabel();
	
	public JLabel digitSet1 = new JLabel();
	public JLabel digitSet2 = new JLabel();
	
	String homeDirectory = System.getProperty("user.home");
	int temp = -15; // <RA_BRD
	
	int setTemperature = 34;

	public EnvironmentUI(String title, int windowTop, int windowLeft, int windowWidth, int windowHeight)  {
		// Create and layout the components using a GridBagLayout.
		// All components are laid out on top of a JPanel called
		// layoutPanel. This panel gets resized automatically as
		// controls are laid out on it. Finally, the layout panel
		// will be added to the JFrame that will get resized to
		// wrap around the edges of the layoutPanel.
		// 
		JPanel layoutPanel = new JPanel(new GridBagLayout());
		layoutPanel.setBackground(Color.WHITE);
		GridBagConstraints constraints = new GridBagConstraints();
		
		JLayeredPane layeredPane = new JLayeredPane();
		// These components are used as templates to create each
		// component from before adding it to the layoutPanel or
		// the layeredPanel
		JPanel panel;
		JLabel label = new JLabel();
		// Holds an image that can be displayed in a JLabel container.
		ImageIcon icon = new ImageIcon();
		
		// LEFT LAYERED PANE 0,0
		// =====================
		layeredPane = new JLayeredPane();
		
		// Display the background image on the base layer.
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/smallRoomLayout3.png");
		label = new JLabel();
		// Let the label display the image
		label.setIcon(icon);
		label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(label, BASE_LAYER, DEPTH); //, 1); // 2,1
		layeredPane.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); 
		
		// Display a label on layer one.
		JLabel labelZone1 = new JLabel();
		labelZone1.setText("100" + "\u00B0");
		labelZone1.setBounds(94, 63, 100, 100);
		layeredPane.add(labelZone1, LAYER_1, DEPTH); //, 1);	
		
		// Display a label on layer one.
		JLabel labelZone2 = new JLabel();
		labelZone2.setText("200" + "\u00B0");
		labelZone2.setBounds(620, 185, 100, 100);
		layeredPane.add(labelZone2, LAYER_1, DEPTH); //, 1);
		
		// Display a label on layer one.
		JLabel labelZone3 = new JLabel();
		labelZone3.setText("300" + "\u00B0");
		labelZone3.setBounds(559, 325, 100, 100);
		layeredPane.add(labelZone3, LAYER_1, DEPTH); //, 1);	
	
		// Setup the constraints for the layeredPane to be applied when
		// it is added to the master or base layoutPanel.
		constraints.gridx = 0;  
		constraints.ipadx = 0;
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,0);
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layoutPanel.add(layeredPane, constraints);
	
		// RIGHT LAYERED PANE 1,0
		// ======================
		// Displays the controller HMI 
		//
		layeredPane = new JLayeredPane();
		
		// Display the image of the controller
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/testController.png");
		label = new JLabel();
		// Let the label display the image
		label.setIcon(icon);
		label.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		label.setForeground(Color.WHITE);
		layeredPane.add(label, BASE_LAYER, DEPTH); 
		layeredPane.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); 
		
		label = new JLabel("Room temperature");
		label.setBounds(73, 60, 120, 20);
		label.setForeground(new Color(36, 203, 252));
		layeredPane.add(label, LAYER_1, DEPTH); 
		
		label = new JLabel("Set");
		label.setForeground(new Color(36, 203, 252));
		label.setBounds(248, 60, 120, 20);
		layeredPane.add(label, LAYER_1, DEPTH); 
	
		// Display the room temperature on layer one.
		digitTemp3 = new JLabel();
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_8.png");
		digitTemp3.setIcon(icon);
		digitTemp3.setBounds(64, 90, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(digitTemp3, LAYER_1, DEPTH); 
		
		digitTemp2 = new JLabel();
		digitTemp2.setIcon(icon);
		digitTemp2.setBounds(108, 90, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(digitTemp2, LAYER_1, DEPTH); //, 1);
		
		digitTemp1 = new JLabel();
		digitTemp1.setIcon(icon);
		digitTemp1.setBounds(152, 90, icon.getIconWidth(), icon.getIconHeight());
		layeredPane.add(digitTemp1, LAYER_1, DEPTH); //, 1);
				
		digitSet2 = new JLabel();
		ImageIcon imageIcon = new ImageIcon(new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_8.png").getImage().getScaledInstance(25, 45, Image.SCALE_DEFAULT)); 
		digitSet2.setIcon(imageIcon);
		digitSet2.setBounds(230, 90, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		layeredPane.add(digitSet2, LAYER_1, DEPTH); 
		
		digitSet1 = new JLabel();
		imageIcon = new ImageIcon(new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_8.png").getImage().getScaledInstance(25, 45, Image.SCALE_DEFAULT)); 
		digitSet1.setIcon(imageIcon);
		digitSet1.setBounds(260, 90, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		layeredPane.add(digitSet1, LAYER_1, DEPTH); 
		
		JButton cmdSetUp = new JButton("");
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/cmdSetUp.png");
		cmdSetUp.setIcon(icon);		
		cmdSetUp.setFocusPainted(false);
		cmdSetUp.setBorder(BorderFactory.createEmptyBorder());
		cmdSetUp.setBounds(290, 75, icon.getIconWidth(), icon.getIconHeight());
		cmdSetUp.addActionListener(new ActionListener() {
			@Override
			//
			// cmdSetUp_Click()
			// ================
			public void actionPerformed(ActionEvent event) {
				setTemperature++;
				showSetTemperature(setTemperature);
			}
		});
		
		constraints.gridx = 1; 
		constraints.gridy = 1;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,1);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdSetUp, LAYER_1, DEPTH); //, 1);
		
		JButton cmdSetDown = new JButton("");
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/cmdSetDown.png");
		cmdSetDown.setIcon(icon);		
		cmdSetDown.setFocusPainted(false);
		cmdSetDown.setBorder(BorderFactory.createEmptyBorder());
		cmdSetDown.setBounds(290, 110, icon.getIconWidth(), icon.getIconHeight());
		cmdSetDown.addActionListener(new ActionListener() {
			@Override
			//
			// cmdSetDown_Click()
			// ==================
			public void actionPerformed(ActionEvent event) {
				setTemperature--;
				showSetTemperature(setTemperature);
			}
		});
		
		constraints.gridx = 1; 
		constraints.gridy = 1;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,1);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdSetDown, LAYER_1, DEPTH); //, 1);
	
		// Display the room selection buttons.
		JButton cmdZone1 = new JButton("");
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/cmdZone1.png");
		cmdZone1.setIcon(icon);		
		//cmdZone1.setBorder(BorderFactory.createEmptyBorder());
		cmdZone1.setFocusPainted(true);
		cmdZone1.setBounds(60, 180, icon.getIconWidth(), icon.getIconHeight());
		cmdZone1.addActionListener(new ActionListener() {
			@Override
			//
			// cmdZone1_Click()
			// ================
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				// <RA_BRD tidy up later - this saves a lot of time at the moment.
				System.out.println("Clicked Zone 1.");
				showRoomTemperature(821); 
			}
		});
		
		constraints.gridx = 1; 
		constraints.gridy = 1;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(10,10,10,10);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdZone1, LAYER_1, DEPTH); //, 1);
		
		JButton cmdZone2 = new JButton("");
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/cmdZone2.png");
		cmdZone2.setIcon(icon);
		//cmdZone1.setBorder(BorderFactory.createEmptyBorder());
		cmdZone2.setFocusPainted(true);
		cmdZone2.setBounds(160, 180, icon.getIconWidth(), icon.getIconHeight());
		cmdZone2.addActionListener(new ActionListener() {
			@Override
			//
			// cmdZone2_Click()
			// ================
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				// <RA_BRD tidy up later - this saves a lot of time at the moment.
				System.out.println("Clicked Zone 2.");
				showRoomTemperature(5); 
			}
		});
		
		constraints.gridx = 1;  
		constraints.gridy = 1;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(10,10,10,10);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdZone2, LAYER_1, DEPTH); //, 1);
		
		JButton cmdZone3 = new JButton("");
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/cmdZone3.png");
		cmdZone3.setIcon(icon);
		//cmdZone1.setBorder(BorderFactory.createEmptyBorder());
		cmdZone3.setFocusPainted(true);
		cmdZone3.setBounds(258, 180, icon.getIconWidth(), icon.getIconHeight());
		cmdZone3.addActionListener(new ActionListener() {
			@Override
			//
			// cmdZone3_Click()
			// ================
			public void actionPerformed(ActionEvent event) {
				// TODO Auto-generated method stub
				// <RA_BRD tidy up later - this saves a lot of time at the moment.
				System.out.println("Clicked Zone 3.");
				showRoomTemperature(-95); 
			}
		});
		
		constraints.gridx = 1;  
		constraints.gridy = 1;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(10,10,10,10);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layeredPane.add(cmdZone3, LAYER_1, DEPTH); //, 1);
		
		// Setup the constraints for the layeredPane to be applied when
		// it is added to the master or base layoutPanel.		
		constraints.gridx = 1;  
		constraints.gridy = 0;  
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(120,20,20,20);
		constraints.anchor = GridBagConstraints.FIRST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		layoutPanel.add(layeredPane, constraints);
	
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
				temp = temp + 1;
				System.out.println("Temperature = " + temp);				
				showRoomTemperature(temp); 
				// System.exit(0);
			}
		});
		
		constraints.gridx = 1; //2;  
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
		// Finally, set the main window properties, including its size, and add 
		// the layout panel to the window before displaying it.
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(title);
		
		// Add the layout JPanel background to the main JFrame.
		this.getContentPane().add(layoutPanel);
		this.setLocationRelativeTo(null);
		this.setLocation(windowLeft, windowTop);
		this.setResizable(true);
	
		// Resize the window to fit the layout panel perfectly.
		this.getContentPane().setSize(new Dimension(layoutPanel.getSize())); 
		this.pack();
		this.setVisible(true);
	}

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
		Icon iconBlank = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_Blank.png");
		Icon iconMinus = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_Minus.png");
		
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_" + temperature.substring(2, 3) + ".png");
		digitTemp1.setIcon(icon);
		
		if (absTemperature < 10) {
			if (displayTemperature < 0) {
				digitTemp2.setIcon(iconMinus);
			} else {
				digitTemp2.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_" + temperature.substring(1, 2) + ".png");
			digitTemp2.setIcon(icon);
		}	
		
		if (absTemperature < 100) {
			if ((displayTemperature < 0) && (absTemperature > 9)) {
				digitTemp3.setIcon(iconMinus);
			} else {
				digitTemp3.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_" + temperature.substring(0, 1) + ".png");
			digitTemp3.setIcon(icon);
		}	
	}
	
	//
	// showSetTemperature()
	// ====================
	// Displays the preset temperature on the small three-digit LED panel
	//
	public void showSetTemperature(int displayTemperature) {
		int absTemperature = Math.abs(displayTemperature);
		String temperature = "000" + Integer.toString(absTemperature);
		temperature = temperature.substring(temperature.length() - 3);
		
		ImageIcon icon = new ImageIcon();
		Icon iconBlank = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_Blank.png");
		Icon iconMinus = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_Minus.png");
		
		ImageIcon imageBlank = new ImageIcon(new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_Blank.png").getImage().getScaledInstance(25, 45, Image.SCALE_DEFAULT)); 
		ImageIcon imageMinus = new ImageIcon(new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_Minus.png").getImage().getScaledInstance(25, 45, Image.SCALE_DEFAULT)); 
		
		icon = new ImageIcon(new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_" + temperature.substring(2, 3) + ".png").getImage().getScaledInstance(25, 45, Image.SCALE_DEFAULT)); 
		digitSet1.setIcon(icon);
		
		if (absTemperature < 10) {
			if (displayTemperature < 0) {
				digitSet2.setIcon(iconMinus);
			} else {
				digitSet2.setIcon(iconBlank);
			}
		} else {
			icon = new ImageIcon(new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/digitLarge_" + temperature.substring(1, 2) + ".png").getImage().getScaledInstance(25, 45, Image.SCALE_DEFAULT)); 
			digitSet2.setIcon(icon);
		}	
	}
	
	//
	// getSetTemperature()
	// ===================
	public int getSetTemperature() {
		return setTemperature;
	}
}
