import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FileGui {

	static final int WIDTH = 960;
	static final int HEIGHT = 540;

	private Program program;

	private JFrame frame;
	private JPanel panel;
	private JLabel saveLabel;
	private JTextField saveTextField;
	private JButton fileButton;
	private JButton pngButton;
	private boolean reading = false;

	private static final int fontSize = 20;
	private static final Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);

	public void setUp() {
		if (reading) {
			frame = new JFrame("reading time.");
		} else {
			frame = new JFrame("saving time.");
		}

		panel = new JPanel();
		panel.setLayout(new GridLayout(4,1));

		// adds the 'stuff' to panel
		saveLabel = new JLabel ("Image Name: ");
		saveLabel.setFont(font);
		panel.add(saveLabel);

		saveTextField = new JTextField(7);
		saveTextField.setFont(font);
		panel.add(saveTextField);

		if (reading) {
			fileButton = new JButton("Read File");
			pngButton = new JButton("Read PNG");
		} else {
			fileButton = new JButton("Save as File");
			pngButton = new JButton("Save as PNG");
		}
		fileButton.setFont(font);
		panel.add(fileButton);
		
		pngButton.setFont(font);
		panel.add(pngButton);
		
		fileButton.addActionListener(new FileButtonListener());
		pngButton.addActionListener(new PNGButtonListener());
	}

	public FileGui(Program program, String type) {

		this.program = program;

		type = type.trim();

		if (type.equals("write")) {
			// no var... toby, please make enums or something, this is deplorable.
		} else if (type.equals("read")) {
			reading = true;
		}

		setUp();

	}

	public void activateFile() {
		String imageName = saveTextField.getText();
		
		if (reading) {
			program.readFile(imageName);
		} else {
			program.writeFile(imageName);
			program.getSelectedProject().setLastSavedByName(imageName);
		}

		frame.setVisible(false);
	}

	public void activatePNG() {
		String imageName = saveTextField.getText();

		if (reading) {
			program.readPNG(imageName);
		} else {
			program.createPNG(imageName);
		}

		frame.setVisible(false);
	}
	
	// listeners
	private class FileButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			activateFile();
			frame.dispose();

		}

	}
	
	private class PNGButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			activatePNG();
			frame.dispose();

		}

	}

	public void display() {
		// display
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocation(960-WIDTH/2, 540-HEIGHT/2);
		frame.getContentPane().add(panel);
		frame.setVisible(true);

	}

}