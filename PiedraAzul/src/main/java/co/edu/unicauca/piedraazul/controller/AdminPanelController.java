package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.AgendadorTablaModel;
import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.MedicoTablaModel;
import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.service.AdminService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import co.edu.unicauca.piedraazul.util.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminPanelController {

    @FXML
    private Button gestionMedicosButton;
    @FXML
    private Button gestionAgendadoresButton;
    @FXML
    private Label tituloSeccionLabel;
    @FXML
    private Label subtituloSeccionLabel;
    @FXML
    private VBox gestionMedicosSection;
    @FXML
    private VBox gestionAgendadoresSection;

    @FXML
    private TextField nombreCompletoField;
    @FXML
    private TextField especialidadField;
    @FXML
    private TextField intervaloField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField agendadorUsernameField;
    @FXML
    private PasswordField agendadorPasswordField;

    @FXML
    private TableView<MedicoTablaModel> medicosTable;
    @FXML
    private TableColumn<MedicoTablaModel, Long> idColumn;
    @FXML
    private TableColumn<MedicoTablaModel, String> nombreColumn;
    @FXML
    private TableColumn<MedicoTablaModel, String> especialidadColumn;
    @FXML
    private TableColumn<MedicoTablaModel, Integer> intervaloColumn;
    @FXML
    private TableColumn<MedicoTablaModel, String> usernameColumn;

    @FXML
    private TableView<AgendadorTablaModel> agendadoresTable;
    @FXML
    private TableColumn<AgendadorTablaModel, Long> agendadorIdColumn;
    @FXML
    private TableColumn<AgendadorTablaModel, String> agendadorUsernameColumn;
    @FXML
    private TableColumn<AgendadorTablaModel, String> agendadorStatusColumn;
    @FXML
    private TableColumn<AgendadorTablaModel, String> agendadorRoleColumn;

    private final SceneManager sceneManager;
    private final AdminService adminService;
    private final UserSession userSession;

    public AdminPanelController(SceneManager sceneManager, AdminService adminService, UserSession userSession) {
        this.sceneManager = sceneManager;
        this.adminService = adminService;
        this.userSession = userSession;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        especialidadColumn.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        intervaloColumn.setCellValueFactory(new PropertyValueFactory<>("intervaloMinutos"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        agendadorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        agendadorUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        agendadorStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        agendadorRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

        cargarMedicos();
        cargarAgendadores();
        mostrarGestionMedicos();
    }

    @FXML
    private void mostrarGestionMedicos() {
        gestionMedicosSection.setVisible(true);
        gestionMedicosSection.setManaged(true);

        gestionAgendadoresSection.setVisible(false);
        gestionAgendadoresSection.setManaged(false);

        gestionMedicosButton.getStyleClass().removeAll("menu-button", "menu-button-active");
        gestionAgendadoresButton.getStyleClass().removeAll("menu-button", "menu-button-active");

        gestionMedicosButton.getStyleClass().add("menu-button-active");
        gestionAgendadoresButton.getStyleClass().add("menu-button");

        tituloSeccionLabel.setText("Gestión administrativa");
        subtituloSeccionLabel.setText("Desde este panel el administrador puede registrar y consultar médicos.");
    }

    @FXML
    private void mostrarGestionAgendadores() {
        gestionMedicosSection.setVisible(false);
        gestionMedicosSection.setManaged(false);

        gestionAgendadoresSection.setVisible(true);
        gestionAgendadoresSection.setManaged(true);

        gestionMedicosButton.getStyleClass().removeAll("menu-button", "menu-button-active");
        gestionAgendadoresButton.getStyleClass().removeAll("menu-button", "menu-button-active");

        gestionMedicosButton.getStyleClass().add("menu-button");
        gestionAgendadoresButton.getStyleClass().add("menu-button-active");

        tituloSeccionLabel.setText("Gestión administrativa");
        subtituloSeccionLabel.setText("Desde este panel el administrador puede registrar y consultar agendadores.");
    }

    @FXML
    private void registrarMedico() {
        try {
            String nombreCompleto = getText(nombreCompletoField);
            String especialidad = getText(especialidadField);
            String intervaloTexto = getText(intervaloField);
            String username = getText(usernameField);
            String password = passwordField.getText() == null ? "" : passwordField.getText().trim();

            if (nombreCompleto.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el nombre completo.");
            }
            if (especialidad.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar la especialidad.");
            }
            if (intervaloTexto.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el intervalo de atención.");
            }
            if (username.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el nombre de usuario.");
            }
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar la contraseña.");
            }

            Integer intervalo = Integer.parseInt(intervaloTexto);

            if (intervalo <= 0) {
                throw new IllegalArgumentException("El intervalo debe ser mayor que cero.");
            }

            adminService.registrarMedico(
                    nombreCompleto,
                    especialidad,
                    intervalo,
                    username,
                    password
            );

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Médico registrado correctamente.");
            limpiarFormularioMedico();
            cargarMedicos();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validación", "El intervalo debe ser numérico.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al registrar el médico.");
            e.printStackTrace();
        }
    }

    @FXML
    private void registrarAgendador() {
        try {
            String username = getText(agendadorUsernameField);
            String password = agendadorPasswordField.getText() == null ? "" : agendadorPasswordField.getText().trim();

            if (username.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el nombre de usuario del agendador.");
            }
            if (password.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar la contraseña del agendador.");
            }

            adminService.registrarAgendador(username, password);

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Agendador registrado correctamente.");
            limpiarFormularioAgendador();
            cargarAgendadores();

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al registrar el agendador.");
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarFormularioMedico() {
        nombreCompletoField.clear();
        especialidadField.clear();
        intervaloField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    private void limpiarFormularioAgendador() {
        agendadorUsernameField.clear();
        agendadorPasswordField.clear();
    }

    @FXML
    private void volver() {
        sceneManager.switchScene("login.xml");
    }

    @FXML
    private void logout() {
        userSession.clear();
        sceneManager.switchScene("login.xml");
    }

    private void cargarMedicos() {
        List<MedicoTablaModel> filas = adminService.listarMedicos()
                .stream()
                .map(this::toTablaModel)
                .toList();

        medicosTable.setItems(FXCollections.observableArrayList(filas));
    }

    private void cargarAgendadores() {
        List<AgendadorTablaModel> filas = adminService.listarAgendadores()
                .stream()
                .map(this::toAgendadorTablaModel)
                .toList();

        agendadoresTable.setItems(FXCollections.observableArrayList(filas));
    }

    private MedicoTablaModel toTablaModel(Medico medico) {
        String username = medico.getUser() != null ? medico.getUser().getUsername() : "";
        return new MedicoTablaModel(
                medico.getId(),
                medico.getNombreCompleto(),
                medico.getEspecialidad(),
                medico.getIntervaloMinutos(),
                username
        );
    }

    private AgendadorTablaModel toAgendadorTablaModel(User user) {
        return new AgendadorTablaModel(
                user.getId(),
                user.getUsername(),
                user.getStatus().name(),
                user.getRole().name()
        );
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