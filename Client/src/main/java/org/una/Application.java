package org.una;

import org.una.presentation.controller.Controller;
import org.una.presentation.model.Model;
import org.una.presentation.view.View;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");}
        catch (Exception ex) {};

        window = new JFrame();

        Model model= new Model();
        View view = new View();
        Controller controller =new Controller(view, model);
        window.setSize(500,400);
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.setTitle("CHAT");
        try {
            window.setIconImage((new ImageIcon(Application.class.getResource("/logo.png"))).getImage());
        } catch (Exception e) {}
        window.setContentPane(view.getPanel());
        window.setVisible(true);
    }

    public static JFrame window;
}
