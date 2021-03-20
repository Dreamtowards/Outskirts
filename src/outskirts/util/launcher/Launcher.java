package outskirts.util.launcher;

import javax.swing.*;
import java.awt.*;

public class Launcher extends JFrame {

    public Launcher() {

//        initLayout();
        basic b = new basic();
        add(b.basicPanel);

        initWindow();
    }

    private void initWindow() {

        setTitle("Launcher");
        setSize(820, 540);
//        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        pack();
        setVisible(true);
    }

    private void initLayout() {

        setLayout(new BorderLayout());

        JPanel pSettings = new JPanel();
        {
            pSettings.setLayout(new BoxLayout(pSettings, BoxLayout.Y_AXIS));

            JComboBox<String> sVersion = new JComboBox<>(new String[] { "1.13.0", "1.13.1"});
            pSettings.add(sVersion);

            JTextField tUsername = new JTextField();

            pSettings.add(tUsername);

        }
        add(pSettings, BorderLayout.WEST);

        JPanel pLaunch = new JPanel();
        {

            JButton btnLaunch = new JButton("Launch");
            btnLaunch.setBounds(400, 400, 120, 50);

            pLaunch.add(btnLaunch);
        }
        add(pLaunch, BorderLayout.CENTER);

    }

    public static void main(String[] args) {

        new Launcher();
    }

}
