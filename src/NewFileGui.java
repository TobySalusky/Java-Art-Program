import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewFileGui {

    static final int WIDTH = 960;
    static final int HEIGHT = 540;

    private Program program;

    private JFrame frame;
    private JPanel panel;

    private JLabel dimensionsLabel;
    private JTextField dimensionsField;

    private JButton createButton;

    private boolean reading = false;

    private static final int fontSize = 20;
    private static final Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);

    public void setUp() {

        frame = new JFrame("file creation time.");

        panel = new JPanel();
        panel.setLayout(new GridLayout(3,1));

        // adds the 'stuff' to panel
        dimensionsLabel = new JLabel ("Dimensions \"x, y\": ");
        dimensionsLabel.setFont(font);
        panel.add(dimensionsLabel);

        dimensionsField = new JTextField(7);
        dimensionsField.setFont(font);
        panel.add(dimensionsField);

        createButton = new JButton("Create File");
        createButton.setFont(font);
        panel.add(createButton);
        createButton.addActionListener(new FileButtonListener());
    }

    public NewFileGui(Program program) {

        this.program = program;

        setUp();

    }

    public void activateFile() {

        String input = dimensionsField.getText();

        int xPixel = Integer.parseInt(input.substring(0, input.indexOf(",")));
        int yPixel = Integer.parseInt(input.substring(input.indexOf(", ") + 2));

        program.createProject(xPixel, yPixel);
    }

    // listeners
    private class FileButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            activateFile();
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
