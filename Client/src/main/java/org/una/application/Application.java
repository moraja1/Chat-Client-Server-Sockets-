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

        Controller controller =new Controller(new View(), new Model());
    }
}
