package edu.jespinoza;

import edu.jespinoza.service.OracleTestConnectionService;
import edu.jespinoza.service.impl.OracleTestConnectionServiceImpl;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;

/**
 * Created by Julio Espinoza on 12/06/2017.
 */
public class App implements ActionListener {
    OracleTestConnectionService service = OracleTestConnectionServiceImpl.getInstance();
    private static final String CONECTAR = "Conectar";
    private JComboBox<String> comboBoxDriver;
    private JTextField textFieldUrl, textFieldUser;
    private JPasswordField passwordField;
    private JTextArea resultText;

    final static String LOOKANDFEEL = "Metal";
    final static String THEME = "Ocean";

    private Component createComponents() {
        JPanel dataPanel = new JPanel(new SpringLayout());
        Border blackline = BorderFactory.createLineBorder(Color.black);
        TitledBorder titleBD = BorderFactory.createTitledBorder(blackline, "BASE DE DATOS");
        titleBD.setTitleJustification(TitledBorder.CENTER);
        dataPanel.setBorder(titleBD);

        JLabel labelDriver = new JLabel("Driver");
        dataPanel.add(labelDriver);
        comboBoxDriver = new JComboBox<>();
        labelDriver.setLabelFor(comboBoxDriver);
        comboBoxDriver.addItem("oracle.jdbc.driver.OracleDriver");
        dataPanel.add(comboBoxDriver);

        JLabel labelURL = new JLabel("URL:");
        dataPanel.add(labelURL);
        textFieldUrl = new JTextField(20);
        labelURL.setLabelFor(textFieldUrl);
        dataPanel.add(textFieldUrl);

        JLabel labelUsuario = new JLabel("Usuario:");
        dataPanel.add(labelUsuario);
        textFieldUser = new JTextField(20);
        labelUsuario.setLabelFor(textFieldUser);
        dataPanel.add(textFieldUser);

        JLabel labelPassword = new JLabel("Password");
        dataPanel.add(labelPassword);
        passwordField = new JPasswordField(20);
        labelPassword.setLabelFor(passwordField);
        dataPanel.add(passwordField);
        GuiUtilities.makeCompactGrid(dataPanel, 4, 2, 6, 6, 6, 6);

        resultText = new JTextArea(5, 20);
        resultText.setEditable(false);
        resultText.setFont(new Font("Serif", Font.ITALIC, 14));
        resultText.setLineWrap(true);
        resultText.setWrapStyleWord(true);

        JScrollPane textScroller = new JScrollPane(resultText);
        textScroller.setPreferredSize(new Dimension(600, 175));
        textScroller.setMinimumSize(new Dimension(100, 100));
        textScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel textPanel = new JPanel();
        TitledBorder titleRESULT = BorderFactory.createTitledBorder(blackline, "RESULTADO DE LA CONEXION");
        titleRESULT.setTitleJustification(TitledBorder.CENTER);
        textPanel.setBorder(titleRESULT);
        textPanel.add(textScroller);

        JButton button = new JButton(CONECTAR);
        button.setActionCommand(CONECTAR);
        button.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(button);

        JPanel pane = new JPanel(new BorderLayout());
        pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        pane.add(BorderLayout.NORTH, dataPanel);
        pane.add(BorderLayout.CENTER, textPanel);
        pane.add(BorderLayout.SOUTH, buttonPanel);

        return pane;
    }

    public void actionPerformed(ActionEvent event) {
        if (CONECTAR.equals(event.getActionCommand())) {
            resultText.selectAll();
            resultText.replaceRange("", 0, resultText.getSelectionEnd());
            String driver = comboBoxDriver.getSelectedItem().toString();
            if (driver == null || driver.isEmpty()) {
                resultText.append("Es necesario escoger un Driver.\n");
                return;
            }
            String url = textFieldUrl.getText();
            if (url == null || url.isEmpty()) {
                resultText.append("Es necesario el URL de la Base de Datos.\n");
                return;
            }
            String user = textFieldUser.getText();
            if (user == null || user.isEmpty()) {
                resultText.append("Es necesario el USUARIO de la Base de Datos.\n");
                return;
            }
            String password = String.valueOf(passwordField.getPassword());
            if (password.isEmpty()) {
                resultText.append("Es necesario el PASSWORD de la Base de Datos.\n");
                return;
            }
            try {
                Collection<String> c = service.checkConnection(driver, url, user, password);
                for (String s : c) {
                    resultText.append(s);
                    resultText.append("\n");
                }
            } catch (Exception e) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 1024);
                     PrintStream printWriter = new PrintStream(baos)) {
                    e.printStackTrace(printWriter);
                    resultText.append("Error NO Esperado\n");
                    resultText.append(baos.toString());
                } catch (IOException ioe) {
                    resultText.append("IOException\n");
                }
            }
        }
    }

    private static void initLookAndFeel() {
        String lookAndFeel;
        if (LOOKANDFEEL.equals("Metal")) {
            lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        } else if (LOOKANDFEEL.equals("System")) {
            lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        } else if (LOOKANDFEEL.equals("Motif")) {
            lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        } else if (LOOKANDFEEL.equals("GTK")) {
            lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
        } else {
            System.err.println("Unexpected value of LOOKANDFEEL specified: " +
                    LOOKANDFEEL);
            lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
        }

        try {
            UIManager.setLookAndFeel(lookAndFeel);
            if (LOOKANDFEEL.equals("Metal")) {
                if (THEME.equals("DefaultMetal")) {
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                } else if (THEME.equals("Ocean")) {
                    MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                } else {
                    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                }
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the GUI and show it. For thread safety
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //  Set the look and feel.
        initLookAndFeel();

        //  Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //  Create and set up the window.
        JFrame frame = new JFrame("Test Oracle Jdbc Por Ing. Julio Espinoza");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        App app = new App();
        Component contents = app.createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        //  Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //  Schedule a job for the event-dispatching thread:
        //  creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
