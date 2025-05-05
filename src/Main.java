import javax.swing.*;
import java.awt.event.*;
public class Main  {
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
        JTextField textField_tryb = new JTextField();
        textField_tryb.setBounds(100, 125, 100, 25);
        frame.add(textField_tryb);

        //Przyjęcie pliku
        JLabel for_plik = new JLabel("File Path:");
        for_plik.setBounds(200, 100, 300, 30);
        frame.add(for_plik);
        JTextField textField_plik = new JTextField();
        textField_plik.setBounds(200, 125, 200, 25);
        frame.add(textField_plik);

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

