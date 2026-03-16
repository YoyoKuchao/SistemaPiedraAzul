package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.service.UserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import co.edu.unicauca.piedraazul.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final UserService userService;
    private final SceneManager sceneManager;
    private final UserSession userSession;

    public LoginController(UserService userService, SceneManager sceneManager, UserSession userSession) {
        this.userService = userService;
        this.sceneManager = sceneManager;
        this.userSession = userSession;
    }

    @FXML
    private void login() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos requeridos", "Debe ingresar usuario y contraseña.");
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null) {
            userSession.setCurrentUser(user);

            switch (user.getRole()) {
                case ADMIN -> sceneManager.switchScene("adminPanel.xml");
                case AGENDADOR -> sceneManager.switchScene("agendadorPanel.xml");
                case MEDICO -> sceneManager.switchScene("medicoPanel.xml");
                case PACIENTE -> sceneManager.switchScene("pacientePanel.xml");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void goToRegister() {
        sceneManager.switchScene("register.xml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}