package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class PacientePanelController {

    private final SceneManager sceneManager;

    public PacientePanelController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void logout() {
        sceneManager.switchScene("login.xml");
    }
}