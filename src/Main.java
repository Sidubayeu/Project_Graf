import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;
import java.io.IOException;
import java.io.File;

public class Main  {
    public static int margin = 10;
    public static String tryb = "txt";
    public static int parts = 2;
    public static String nazw_plik;

    public static void main(String[] args){
        JFrame frame = new JFrame("Menu");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920, 800);

        // Button dla potwierdzenia
        JButton button  = new JButton("Wprowadz");
        button.setBounds(660, 350, 200, 50);
        frame.add(button);


        //Button dla pomocy
        JButton pomoc = new JButton("Pomoc");
        pomoc.setBounds(660, 425, 200, 50);
        frame.add(pomoc);
        pomoc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                help();
            }
        });



        //Przyjęcie tryb
        JLabel for_tryb = new JLabel("Tryb:");
        for_tryb.setBounds(100, 100, 300, 30);
        frame.add(for_tryb);
        String[] trybOptions = {"txt", "bin"};
        JComboBox<String> comboBox_tryb = new JComboBox<>(trybOptions);;
        comboBox_tryb.setBounds(100, 125, 100, 25);
        frame.add(comboBox_tryb);

        //Przyjęcie pliku
        JLabel for_plik = new JLabel("File Path:");
        for_plik.setBounds(200, 100, 300, 30);
        frame.add(for_plik);
        JTextField textField_plik = new JTextField();
        textField_plik.setBounds(200, 125, 200, 25);
        frame.add(textField_plik);
        JButton browseButton = new JButton("Browse...");
        browseButton.setBounds(350, 125, 100, 25);
        frame.add(browseButton);
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fileChooser.getSelectedFile();
                    textField_plik.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        // Przyjęcie parts
        JLabel for_parts = new JLabel("Number of parts:");
        for_parts.setBounds(400, 100, 300, 30);
        frame.add(for_parts);
        JTextField textField_parts = new JTextField();
        textField_parts.setBounds(400, 125, 200, 25);
        frame.add(textField_parts);

        //Przyjęcie marginesu
        JLabel for_margin = new JLabel("Margines:");
        for_margin.setBounds(600, 100, 300, 30);
        frame.add(for_margin);
        JTextField textField_margin = new JTextField();
        textField_margin.setBounds(600, 125, 200, 25);
        frame.add(textField_margin);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    tryb = (String) comboBox_tryb.getSelectedItem();
                    nazw_plik = textField_plik.getText();
                    if (tryb.equals("txt")) {
                        parts = Integer.parseInt(textField_parts.getText());
                        margin = Integer.parseInt(textField_margin.getText());
                    }

                    List<Set<Integer>> podzial = Dividing.divideGraph(nazw_plik, parts);
                    Visualisation.show(nazw_plik, podzial);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid number format: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }

    public static void help(){
        JFrame help = new JFrame("Help");
        help.setSize(1920, 800);
        help.setLayout(null);
        JLabel for_help = new JLabel("Tu wpisać pomoc:");
        for_help.setBounds(600, 100, 300, 30);
        help.add(for_help);
        help.setVisible(true);
    }



}

