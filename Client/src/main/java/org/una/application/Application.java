package org.una.application;

import org.una.presentation.view.ChatView;
import org.una.presentation.controller.Controller;
import org.una.presentation.model.Model;

import javax.swing.*;
import java.time.LocalDate;

public class Application {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");}
        catch (Exception ex) {};

        Controller controller =new Controller(new ChatView(), new Model());
    }
}
