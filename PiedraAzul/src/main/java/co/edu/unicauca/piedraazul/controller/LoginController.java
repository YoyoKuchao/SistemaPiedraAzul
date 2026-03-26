package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.service.IUserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import co.edu.unicauca.piedraazul.util.Vista;
import co.edu.unicauca.piedraazul.util.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class LoginController implements Observer {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;

    private final IUserService userService;
    private final SceneManager sceneManager;
    private final UserSession  userSession;

    public LoginController(IUserService userService,
                           SceneManager sceneManager,
                           UserSession userSession) {
        this.userService  = userService;
        this.sceneManager = sceneManager;
        this.userSession  = userSession;
    }

    @FXML
    private void initialize() {
        userService.attach(this);
    }

    @FXML
    private void login() {
        String username = usernameField.getText() == null
                ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null
                ? "" : passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos requeridos",
                    "Debe ingresar usuario y contraseña.");
            return;
        }

        try {
            User user = userService.authenticate(username, password);

            if (user != null) {
                userSession.setCurrentUser(user);
                switch (user.getRole()) {
                    case ADMIN     -> sceneManager.switchScene(Vista.ADMIN_PANEL);
                    case AGENDADOR -> sceneManager.switchScene(Vista.AGENDADOR_PANEL);
                    case MEDICO    -> sceneManager.switchScene(Vista.MEDICO_PANEL);
                    case PACIENTE  -> sceneManager.switchScene(Vista.PACIENTE_PANEL);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de navegación",
                    "No se pudo cargar el panel: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        userService.detach(this);
        sceneManager.switchScene(Vista.REGISTER);
    }

    @Override
    public void update(String message) {
        // Observer de eventos internos — sin efectos en la UI
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
