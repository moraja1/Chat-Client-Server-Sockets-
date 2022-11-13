package org.una.presentation.view;

import org.una.application.Application;
import org.una.presentation.controller.Controller;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class ChatView extends JFrame{
    private JPanel panel;
    private JPanel loginPanel;
    private JTextField username;
    private JPasswordField clave;
    private JButton login;
    private JButton finish;
    private JPanel bodyPanel;
    private JTextField mensaje;
    private JButton post;
    private JTextPane messages;
    private JPanel ContactsPanel;
    private JButton logoutButton;
    private JPanel ContactsPanelList;
    private JList contactList;
    private JTextField srchBar;
    private JButton srchBtn;
    private Controller controller;

    public ChatView() {
        setSize(600,500);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("CHAT");
        try {
            setIconImage((new ImageIcon(Application.class.getResource("/logo.png"))).getImage());
        } catch (Exception e) {}
        setContentPane(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loginPanel.setVisible(true);
        getRootPane().setDefaultButton(login);
        bodyPanel.setVisible(false);

        DefaultCaret caret = (DefaultCaret) messages.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    public void initComponents(){
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!username.getText().isEmpty() && !new String(clave.getPassword()).isEmpty()){
                    username.setBackground(Color.white);
                    clave.setBackground(Color.white);
                    try {
                        controller.login();
                        username.setText("");
                        clave.setText("");
                    } catch (Exception ex) {
                        username.setBackground(Color.orange);
                        clave.setBackground(Color.orange);
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe llenar todos los datos",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.logout();
            }
        });
        finish.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        post.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = mensaje.getText();
                controller.post(text);
                mensaje.setText("");
            }
        });

        setVisible(true);
    }
    public void setController(Controller controller) {
        this.controller = controller;
    }
    public JPanel getPanel() {
        return panel;
    }
    public JPanel getLoginPanel() {
        return loginPanel;
    }

    public JPanel getBodyPanel() {
        return bodyPanel;
    }

    public JButton getPostButton() {
        return post;
    }
    public JTextPane getMessages() {
        return messages;
    }
    public JTextField getUsername() {
        return username;
    }
    public JPasswordField getClave() {
        return clave;
    }
    public void loginAccepted(String username) {
        setTitle(username.toUpperCase());
        getLoginPanel().setVisible(false);;
        getBodyPanel().setVisible(true);
        getRootPane().setDefaultButton(post);
        panel.validate();
    }
    public void setContactListValues(String[] contactList){
        this.contactList.setListData(contactList);
    }
}
