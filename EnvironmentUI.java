//
// ENVIRONMENT USER INTERFACE
// ==========================
//
// Revision History
// ================
// 18.12.2019 BRD Original version.
//
package HVACsim;
import java.awt.*;
import javax.swing.*;

public class EnvironmentUI extends JFrame{
	private static final long serialVersionUID = 1L;

	public EnvironmentUI(String title, int windowTop, int windowLeft, int windowWidth, int windowHeight)  {
		String homeDirectory = System.getProperty("user.home");
		
		// Create and layout the components using a GridBagLayout.
		// All components are laid out on top of a JPanel called
		// layoutPanel.
		JPanel layoutPanel = new JPanel(new GridBagLayout());
		layoutPanel.setBackground(Color.WHITE);
		GridBagConstraints constraints = new GridBagConstraints();
		
		// These components are used as templates to create each
		// component from before adding it to the layoutPanel.
		JPanel panel;
		ImageIcon icon = new ImageIcon();
		JLabel label = new JLabel();
		JButton cmdButton = new JButton();
				
		// Panel 1
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		constraints.gridx = 0;  // column of upper left corner of component
		constraints.gridy = 0;  // row of upper left corner of component
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,0);
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/smallRoomLayout.PNG");
		label = new JLabel();
		// Let the label display the image
		label.setIcon(icon);
		
		panel.add(label);
		layoutPanel.add(panel, constraints);
		
		// Panel 2
		panel = new JPanel();
		panel.setBackground(Color.WHITE);
		constraints.gridx = 1;  // column of upper left corner of component
		constraints.gridy = 0;  // row of upper left corner of component
		constraints.ipadx = 100;
		constraints.ipady = 0;
		constraints.insets = new Insets(0,0,0,0);
		constraints.anchor = GridBagConstraints.FIRST_LINE_END;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		
		icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/smallController.PNG");
		label = new JLabel();
		// Let the label display the image
		label.setIcon(icon);
		panel.add(label);
		layoutPanel.add(panel, constraints);
		
		// Command button
		cmdButton = new JButton();
		cmdButton.setText("OK");
		constraints.gridx = 1;  // column of upper left corner of component
		constraints.gridy = 1;  // row of upper left corner of component
		constraints.ipadx = 50;
		constraints.ipady = 10;
		constraints.insets = new Insets(10,10,10,10);
		constraints.anchor = GridBagConstraints.LINE_END;
		constraints.fill = GridBagConstraints.NONE;
		layoutPanel.add(cmdButton, constraints);
		
		// Set the main window properties and add the layout panel
		// to the window before displaying it.
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(title);
		// Add the layout JPanel background to the main JFrame.
		getContentPane().add(layoutPanel);
		// Resize the window to fit the layout panel perfectly.
		getContentPane().setSize(new Dimension(layoutPanel.getSize())); 
		pack();
		setLocationRelativeTo(null);
		setLocation(windowTop, windowLeft);
		this.setResizable(false);
		this.setVisible(true);
	}
}



		//#######################################################################################3
	//	GridBagLayout gridBag = new GridBagLayout();
		
	//	contentPane.setLayout(gridBag);
		
	//	if(shouldFill) {
	//		// Set a layout that has the natural height of the window and
	//		// the maximum window width
	//		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
	//		
	//	}
		

		// Create the main window as a JFrame.
		//getContentPane().setLayout(null);
		//this.setDefaultLookAndFeelDecorated(true);
		//this.setBackground(Color.WHITE);
		//this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//this.setTitle(title);
		//this.getContentPane().setPreferredSize(new Dimension(windowWidth, windowHeight));
		//this.pack();
		//this.setLocationRelativeTo(null);
		//this.setLocation(windowTop, windowLeft);
		
		
		
//		JButton button;
//		
//		button = new JButton("Button One");
//		if (shouldWeightX) {
//			gridBagConstraints.weightx = 0.5;
//		}
//		gridBagConstraints.ipady = 50;
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 0;
//		gridBag.setConstraints(button, gridBagConstraints);
//		contentPane.add(button);
//		
//		button = new JButton("Button Two");
//		gridBagConstraints.ipadx = 10;
//		gridBagConstraints.ipady = 20;
//		gridBagConstraints.gridx = 1;
//		gridBagConstraints.gridy = 0;
//		gridBag.setConstraints(button, gridBagConstraints);
//		contentPane.add(button);
//		
//		button = new JButton("Button Three");
//		gridBagConstraints.ipadx = 0;
//		gridBagConstraints.ipady = 0;
//		gridBagConstraints.gridx = 2;
//		gridBagConstraints.gridy = 0;
//		gridBag.setConstraints(button, gridBagConstraints);
//		contentPane.add(button);
//		
//		button = new JButton("Button Four");
//		gridBagConstraints.ipady = 40;
//		gridBagConstraints.weightx = 0.0;
//		gridBagConstraints.gridwidth = 3;
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 1;
//		gridBag.setConstraints(button, gridBagConstraints);
//		contentPane.add(button);
//		
//		button = new JButton("Button Five");
//		gridBagConstraints.ipadx = 0;
//		gridBagConstraints.ipady = 0;
//		gridBagConstraints.weightx = 0.0;
//		gridBagConstraints.weighty = 1.0;
//		gridBagConstraints.anchor = GridBagConstraints.SOUTH;	
//		gridBagConstraints.insets = new Insets(windowWidth,500,10,10);
//		gridBagConstraints.gridx = 0;
//		gridBagConstraints.gridy = 2;	
//		gridBagConstraints.gridwidth = 2;
//		gridBag.setConstraints(button, gridBagConstraints);
//		contentPane.add(button);
		
	
		
		// #############################################################################################
		// Insert a JPanel called background that fills
		// the window. All components sit on-top of and
		// inside this panel. First calculate the available
		// window area, which is less than the JFrame size
		//
		// The JPanel to be used as a palate to put the components on. It will
		// be sized using the specified window width and window height. To allow
		// room for the title bar, resize the JFrame to fit the JPanel in.
		
		// Lay out the components manually.
		//this.setLayout(null);

		//this.getContentPane().setPreferredSize(new Dimension(windowWidth, windowHeight));
		//this.pack();
		//this.setLocationRelativeTo(null);
		//this.setLocation(windowTop, windowLeft);
//		this.setVisible(true);
		
		//JPanel background = new JPanel();
		//background.setLocation(0, 0);
		//background.setSize(windowWidth, windowHeight);
		///background.setBackground(Color.YELLOW);
		// Add the JPanel background to the main JFrame.
		//this.getContentPane().add(background);
		
		// Display the background image of the building.
	//	ImageIcon icon = new ImageIcon(homeDirectory + "//Development/Java/HVACsim/src/HVACsim/graphics/smallRoomLayout.PNG");
	//	JLabel label = new JLabel();
		// Let the label display the image
	//	background.add(label);
	//	label.setBackground(Color.GREEN);
		
	//	label.setIcon(icon);
	//	label.setHorizontalAlignment(0);
	//	label.setLocation(0, 0);
	//	label.setSize(100, 50);
				
		// Add this JLabel to the JPanel called background.
		
		
		
		// Display the window.
		//this.setVisible(true);
//	}

//}
