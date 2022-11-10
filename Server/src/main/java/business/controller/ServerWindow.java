package business.controller;

import data.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ServerWindow extends JFrame {
    private Server server;

    public ServerWindow(Server server){
        this.server = server;

        setBounds(50, 50, 250, 150);
        setLayout(new BorderLayout());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.close();
            }
        });
        JButton closeBtn = new JButton("Cerrar Servidor");
        closeBtn.setBounds(new Rectangle(100, 50));
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.close();
                System.exit(0);
            }
        });
        add(closeBtn, BorderLayout.CENTER);
        setVisible(true);
    }
}
