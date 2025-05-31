import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {
    public static int margin = 10;
    public static String tryb = "txt";
    public static int parts = 2;
    public static String nazw_plik;
    public static String nazw_plik_output;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        Font boldFont = new Font("Arial", Font.BOLD, 14);

        // Input File
        JLabel inputLabel = new JLabel("Ścieżka pliku wejściowego:");
        inputLabel.setFont(boldFont);
        JTextField inputField = new JTextField();
        JButton browseInput = new JButton("Przeglądaj...");
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(browseInput, BorderLayout.EAST);

        // Mode
        JLabel modeLabel = new JLabel("Tryb:");
        String[] trybOptions = {"txt", "bin", "csrrg"};
        JComboBox<String> trybBox = new JComboBox<>(trybOptions);

        // Output File
        JLabel outputLabel = new JLabel("Ścieżka pliku wyjściowego:");
        outputLabel.setFont(boldFont);
        JTextField outputField = new JTextField();
        JButton browseOutput = new JButton("Przeglądaj...");
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.add(outputField, BorderLayout.CENTER);
        outputPanel.add(browseOutput, BorderLayout.EAST);

        // Parts
        JLabel partsLabel = new JLabel("Ilość partów:");
        JTextField partsField = new JTextField("2");
        partsField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        // Create arrow buttons for parts
        JButton partsDown = new JButton("←");
        JButton partsUp = new JButton("→");

        partsDown.addActionListener(e -> {
            try {
                int value = Integer.parseInt(partsField.getText());
                if (value > 1) {
                    partsField.setText(String.valueOf(value - 1));
                }
            } catch (NumberFormatException ex) {
                partsField.setText("2");
            }
        });

        partsUp.addActionListener(e -> {
            try {
                int value = Integer.parseInt(partsField.getText());
                partsField.setText(String.valueOf(value + 1));
            } catch (NumberFormatException ex) {
                partsField.setText("2");
            }
        });

        JPanel partsPanel = new JPanel(new BorderLayout(5, 5));
        partsPanel.add(partsField, BorderLayout.CENTER);

        JPanel partsButtonPanel = new JPanel(new GridLayout(1, 2, 2, 0));
        partsButtonPanel.add(partsDown);
        partsButtonPanel.add(partsUp);
        partsPanel.add(partsButtonPanel, BorderLayout.EAST);

        // Margin
        JLabel marginLabel = new JLabel("Margines:");
        JTextField marginField = new JTextField("10");
        marginField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        // Create arrow buttons for margin
        JButton marginDown = new JButton("←");
        JButton marginUp = new JButton("→");

        marginDown.addActionListener(e -> {
            try {
                int value = Integer.parseInt(marginField.getText());
                if (value > 0) {
                    marginField.setText(String.valueOf(value - 1));
                }
            } catch (NumberFormatException ex) {
                marginField.setText("10");
            }
        });

        marginUp.addActionListener(e -> {
            try {
                int value = Integer.parseInt(marginField.getText());
                marginField.setText(String.valueOf(value + 1));
            } catch (NumberFormatException ex) {
                marginField.setText("10");
            }
        });

        JPanel marginPanel = new JPanel(new BorderLayout(5, 5));
        marginPanel.add(marginField, BorderLayout.CENTER);

        JPanel marginButtonPanel = new JPanel(new GridLayout(1, 2, 2, 0));
        marginButtonPanel.add(marginDown);
        marginButtonPanel.add(marginUp);
        marginPanel.add(marginButtonPanel, BorderLayout.EAST);

        // Buttons
        JButton submitButton = new JButton("Podziel graf");
        JButton helpButton = new JButton("Pomoc");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(helpButton);

        mainPanel.add(inputLabel);
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(outputLabel);
        mainPanel.add(outputPanel);
        mainPanel.add(Box.createVerticalStrut(100));
        mainPanel.add(modeLabel);
        mainPanel.add(trybBox);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(partsLabel);
        mainPanel.add(partsPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(marginLabel);
        mainPanel.add(marginPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);

        frame.add(mainPanel);
        frame.setVisible(true);

        // Browse input
        browseInput.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                inputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Browse output
        browseOutput.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                outputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Submit
        submitButton.addActionListener(e -> {
            try {
                tryb = (String) trybBox.getSelectedItem();
                nazw_plik = inputField.getText();
                nazw_plik_output = outputField.getText();
                parts = partsField.getText().isEmpty() ? 2 : Integer.parseInt(partsField.getText());
                margin = marginField.getText().isEmpty() ? 10 : Integer.parseInt(marginField.getText());

                if (tryb.equals("txt") || tryb.equals("bin")) {
                    Map<Integer, Set<Integer>> podzial = Dividing.readGraph(nazw_plik);
                    List<Set<Integer>> partition = Dividing.partitionGraph(podzial, parts, margin);
                    Visualisation.showVisualisation(podzial, partition, margin);
                } else if (tryb.equals("csrrg")) {
                    Map<Integer, Set<Integer>> wczytany = Dividing.readGraph(nazw_plik);
                    List<Set<Integer>> podzial = Dividing.partitionGraph(wczytany, parts, margin);
                    Dividing.saveGraphWithPartition(nazw_plik, nazw_plik_output, podzial);
                    Visualisation.showVisualisation(wczytany, podzial, margin);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Błąd wczytywania pliku: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Niepoprawny format liczby: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Help
        helpButton.addActionListener(e -> {
            JFrame help = new JFrame("Pomoc");
            help.setSize(400, 300);
            help.setLocationRelativeTo(frame);
            JLabel label = new JLabel("Tu wpisz instrukcje korzystania z programu");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            help.add(label);
            help.setVisible(true);
        });
    }
}