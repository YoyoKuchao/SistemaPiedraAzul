package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.enums.UserRole;
import co.edu.unicauca.piedraazul.model.enums.UserStatus;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.service.IUserService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import co.edu.unicauca.piedraazul.util.Vista;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserController implements Observer {

    @FXML private TextField     firstNameField;
    @FXML private TextField     lastNameField;
    @FXML private TextField     secondNameField;
    @FXML private TextField     secondLastNameField;
    @FXML private TextField     usernameField;
    @FXML private TextField     phoneField;
    @FXML private DatePicker    birthDatePicker;
    @FXML private ComboBox<String> documentTypeCombo;
    @FXML private TextField     documentNumberField;
    @FXML private TextField     selectedRoleField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    // Dependencia sobre interfaz — principio D
    private final IUserService userService;
    private final SceneManager sceneManager;

    private UserRole selectedRole = UserRole.ADMIN;

    public RegisterUserController(IUserService userService,
                                  SceneManager sceneManager) {
        this.userService  = userService;
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        if (documentTypeCombo != null) {
            documentTypeCombo.setItems(FXCollections.observableArrayList(
                    "Cédula de ciudadanía", "Tarjeta de identidad",
                    "Cédula de extranjería", "Pasaporte"));
        }
        actualizarCampoRol();
    }

    @FXML private void selectAdminRole()   { selectedRole = UserRole.ADMIN;    actualizarCampoRol(); }
    @FXML private void selectPatientRole() { selectedRole = UserRole.PACIENTE; actualizarCampoRol(); }

    @FXML
    private void goToLogin() {
        sceneManager.switchScene(Vista.LOGIN);
    }

    @FXML
    private void register() {
        String primerNombre       = getText(firstNameField);
        String primerApellido     = getText(lastNameField);
        String usuario            = getText(usernameField);
        String numeroDocumento    = getText(documentNumberField);
        String contrasena         = getText(passwordField);
        String confirmarContrasena = getText(confirmPasswordField);
        String tipoDocumento      = documentTypeCombo != null
                ? documentTypeCombo.getValue() : null;

        if (primerNombre.isEmpty() || primerApellido.isEmpty()
                || usuario.isEmpty()
                || tipoDocumento == null || tipoDocumento.trim().isEmpty()
                || numeroDocumento.isEmpty()
                || contrasena.isEmpty()
                || confirmarContrasena.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Campos obligatorios",
                    "Debe completar todos los campos marcados con *.");
            return;
        }

        if (!contrasena.equals(confirmarContrasena)) {
            showAlert(Alert.AlertType.ERROR, "Validación",
                    "Las contraseñas no coinciden.");
            return;
        }

        User user = new User();
        user.setUsername(usuario);
        user.setPassword(contrasena);
        user.setRole(selectedRole);
        user.setStatus(UserStatus.ACTIVE);

        boolean registrado = userService.registerUser(user, this);

        if (registrado) {
            showAlert(Alert.AlertType.INFORMATION, "Registro exitoso",
                    "El usuario fue registrado correctamente.");
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registro fallido",
                    "El usuario ya existe o no pudo registrarse.");
        }
    }

    @FXML
    private void clearForm() {
        clearIfNotNull(firstNameField);
        clearIfNotNull(lastNameField);
        clearIfNotNull(secondNameField);
        clearIfNotNull(secondLastNameField);
        clearIfNotNull(usernameField);
        clearIfNotNull(phoneField);
        clearIfNotNull(documentNumberField);
        clearIfNotNull(passwordField);
        clearIfNotNull(confirmPasswordField);
        if (birthDatePicker != null)    birthDatePicker.setValue(null);
        if (documentTypeCombo != null)  documentTypeCombo.setValue(null);
        actualizarCampoRol();
    }

    @Override
    public void update(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Información", message);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void actualizarCampoRol() {
        if (selectedRoleField != null) {
            selectedRoleField.setText(
                    selectedRole != null ? selectedRole.name() : "");
        }
    }

    private void clearIfNotNull(TextField field) {
        if (field != null) field.clear();
    }

    private String getText(TextField field) {
        return (field == null || field.getText() == null)
                ? "" : field.getText().trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
