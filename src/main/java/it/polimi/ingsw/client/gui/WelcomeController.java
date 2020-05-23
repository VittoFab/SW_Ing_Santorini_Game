package it.polimi.ingsw.client.gui;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    public WelcomeController() {
    }

    @FXML
    private void next() {

        try {
            Parent root = GuiManager.connectLoader.load();
            Gui.getStage().setScene(new Scene(root));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //fadeIn();
    }
}
