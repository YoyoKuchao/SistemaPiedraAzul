package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.UserRole;
import co.edu.unicauca.piedraazul.model.UserStatus;
import co.edu.unicauca.piedraazul.service.UserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField middleNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField secondLastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private ComboBox<String> documentTypeCombo;
    @FXML
    private TextField documentNumberField;
    @FXML
    private TextField selectedRoleField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button adminRoleButton;
    @FXML
    private Button userRoleButton;

    private final UserService userService;
    private final SceneManager sceneManager;

    private UserRole selectedRole = UserRole.ADMIN;

    public RegisterUserController(UserService userService, SceneManager sceneManager) {
        this.userService = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        documentTypeCombo.getItems().addAll(
                "Cédula de ciudadanía",
                "Tarjeta de identidad",
                "Cédula de extranjería",
                "Pasaporte"
        );
        selectedRoleField.setText("ADMIN");
        applyRoleStyles();
    }

    @FXML
    private void selectAdminRole() {
        selectedRole = UserRole.ADMIN;
        selectedRoleField.setText("ADMIN");
        applyRoleStyles();
    }

    @FXML
    private void selectUserRole() {
        selectedRole = UserRole.PACIENTE;
        selectedRoleField.setText("PACIENTE");
        applyRoleStyles();
    }

    @FXML
    private void register() {
        String username = getText(usernameField);
        String password = getText(passwordField);
        String confirmPassword = getText(confirmPasswordField);

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos requeridos", "Usuario, contraseña y confirmación son obligatorios.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Contraseña inválida", "Las contraseñas no coinciden.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(selectedRole);
        user.setStatus(UserStatus.ACTIVE);

        boolean success = userService.registerUser(user);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Registro exitoso", "Usuario registrado correctamente.");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registro fallido", "El usuario ya existe.");
        }
    }

    @FXML
    private void clearForm() {
        firstNameField.clear();
        middleNameField.clear();
        lastNameField.clear();
        secondLastNameField.clear();
        usernameField.clear();
        phoneField.clear();
        birthDatePicker.setValue(null);
        documentTypeCombo.setValue(null);
        documentNumberField.clear();
        passwordField.clear();
        confirmPasswordField.clear();

        selectedRole = UserRole.ADMIN;
        selectedRoleField.setText("ADMIN");
        applyRoleStyles();
    }

    @FXML
    private void goBackToLogin() {
        sceneManager.switchScene("login.xml");
    }

    private void applyRoleStyles() {
        adminRoleButton.getStyleClass().removeAll("role-button", "role-button-inactive");
        userRoleButton.getStyleClass().removeAll("role-button", "role-button-inactive");

        if (selectedRole == UserRole.ADMIN) {
            adminRoleButton.getStyleClass().add("role-button");
            userRoleButton.getStyleClass().add("role-button-inactive");
        } else {
            adminRoleButton.getStyleClass().add("role-button-inactive");
            userRoleButton.getStyleClass().add("role-button");
        }
    }

    private String getText(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}