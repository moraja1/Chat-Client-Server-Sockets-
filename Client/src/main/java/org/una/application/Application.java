package org.una.application;

import org.una.presentation.controller.Controller;
import org.una.presentation.model.Model;
import org.una.presentation.view.View;

import javax.swing.*;

public class Application {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");}
        catch (Exception ex) {};

        Model model= new Model();
        View view = new View();
        Controller controller =new Controller(view, model);
    }
}
