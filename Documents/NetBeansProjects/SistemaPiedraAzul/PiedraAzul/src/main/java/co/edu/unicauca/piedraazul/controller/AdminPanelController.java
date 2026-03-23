package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.dto.AgendadorTablaModel;
import co.edu.unicauca.piedraazul.model.dto.MedicoTablaModel;
import co.edu.unicauca.piedraazul.service.IAgendadorService;
import co.edu.unicauca.piedraazul.service.IMedicoService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import co.edu.unicauca.piedraazul.util.Vista;
import co.edu.unicauca.piedraazul.util.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminPanelController {

    // ── Navegación ────────────────────────────────────────────────────────────
    @FXML private Button gestionMedicosButton;
    @FXML private Button gestionAgendadoresButton;
    @FXML private Label  tituloSeccionLabel;
    @FXML private Label  subtituloSeccionLabel;
    @FXML private VBox   gestionMedicosSection;
    @FXML private VBox   gestionAgendadoresSection;

    // ── Formulario médico ─────────────────────────────────────────────────────
    @FXML private TextField     nombreCompletoField;
    @FXML private TextField     especialidadField;
    @FXML private TextField     intervaloField;
    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;

    // ── Formulario agendador ──────────────────────────────────────────────────
    @FXML private TextField     agendadorUsernameField;
    @FXML private PasswordField agendadorPasswordField;

    // ── Tabla médicos ─────────────────────────────────────────────────────────
    @FXML private TableView<MedicoTablaModel>              medicosTable;
    @FXML private TableColumn<MedicoTablaModel, Long>      idColumn;
    @FXML private TableColumn<MedicoTablaModel, String>    nombreColumn;
    @FXML private TableColumn<MedicoTablaModel, String>    especialidadColumn;
    @FXML private TableColumn<MedicoTablaModel, Integer>   intervaloColumn;
    @FXML private TableColumn<MedicoTablaModel, String>    usernameColumn;

    // ── Tabla agendadores ─────────────────────────────────────────────────────
    @FXML private TableView<AgendadorTablaModel>            agendadoresTable;
    @FXML private TableColumn<AgendadorTablaModel, Long>    agendadorIdColumn;
    @FXML private TableColumn<AgendadorTablaModel, String>  agendadorUsernameColumn;
    @FXML private TableColumn<AgendadorTablaModel, String>  agendadorStatusColumn;
    @FXML private TableColumn<AgendadorTablaModel, String>  agendadorRoleColumn;

    // ── Dependencias (interfaces, no clases concretas — principio D) ──────────
    private final SceneManager      sceneManager;
    private final IMedicoService    medicoService;
    private final IAgendadorService agendadorService;
    private final UserSession       userSession;

    public AdminPanelController(SceneManager sceneManager,
                                IMedicoService medicoService,
                                IAgendadorService agendadorService,
                                UserSession userSession) {
        this.sceneManager      = sceneManager;
        this.medicoService     = medicoService;
        this.agendadorService  = agendadorService;
        this.userSession       = userSession;
    }

    // ── Inicialización ────────────────────────────────────────────────────────

    @FXML
    private void initialize() {
        configurarTablaMedicos();
        configurarTablaAgendadores();
        cargarMedicos();
        cargarAgendadores();
        mostrarGestionMedicos();
    }

    private void configurarTablaMedicos() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreColumn.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        especialidadColumn.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        intervaloColumn.setCellValueFactory(new PropertyValueFactory<>("intervaloMinutos"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
    }

    private void configurarTablaAgendadores() {
        agendadorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        agendadorUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        agendadorStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        agendadorRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    // ── Navegación entre secciones ────────────────────────────────────────────

    @FXML
    private void mostrarGestionMedicos() {
        gestionMedicosSection.setVisible(true);
        gestionMedicosSection.setManaged(true);
        gestionAgendadoresSection.setVisible(false);
        gestionAgendadoresSection.setManaged(false);
        activarBoton(gestionMedicosButton, gestionAgendadoresButton);
        tituloSeccionLabel.setText("Gestión administrativa");
        subtituloSeccionLabel.setText(
                "Desde este panel el administrador puede registrar y consultar médicos.");
    }

    @FXML
    private void mostrarGestionAgendadores() {
        gestionMedicosSection.setVisible(false);
        gestionMedicosSection.setManaged(false);
        gestionAgendadoresSection.setVisible(true);
        gestionAgendadoresSection.setManaged(true);
        activarBoton(gestionAgendadoresButton, gestionMedicosButton);
        tituloSeccionLabel.setText("Gestión administrativa");
        subtituloSeccionLabel.setText(
                "Desde este panel el administrador puede registrar y consultar agendadores.");
    }

    private void activarBoton(Button activo, Button inactivo) {
        activo.getStyleClass().removeAll("menu-button", "menu-button-active");
        inactivo.getStyleClass().removeAll("menu-button", "menu-button-active");
        activo.getStyleClass().add("menu-button-active");
        inactivo.getStyleClass().add("menu-button");
    }

    // ── Acciones médico ───────────────────────────────────────────────────────

    @FXML
    private void registrarMedico() {
        try {
            String nombreCompleto = getText(nombreCompletoField);
            String especialidad   = getText(especialidadField);
            String intervaloTexto = getText(intervaloField);
            String username       = getText(usernameField);
            String password       = passwordField.getText() == null
                    ? "" : passwordField.getText().trim();

            if (nombreCompleto.isEmpty())
                throw new IllegalArgumentException("Debe ingresar el nombre completo.");
            if (especialidad.isEmpty())
                throw new IllegalArgumentException("Debe ingresar la especialidad.");
            if (intervaloTexto.isEmpty())
                throw new IllegalArgumentException("Debe ingresar el intervalo de atención.");
            if (username.isEmpty())
                throw new IllegalArgumentException("Debe ingresar el nombre de usuario.");
            if (password.isEmpty())
                throw new IllegalArgumentException("Debe ingresar la contraseña.");

            int intervalo = Integer.parseInt(intervaloTexto);
            if (intervalo <= 0)
                throw new IllegalArgumentException("El intervalo debe ser mayor que cero.");

            medicoService.registrarMedico(nombreCompleto, especialidad,
                    intervalo, username, password);

            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "Médico registrado correctamente.");
            limpiarFormularioMedico();
            cargarMedicos();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validación",
                    "El intervalo debe ser un número entero.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Ocurrió un error al registrar el médico.");
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

    // ── Acciones agendador ────────────────────────────────────────────────────

    @FXML
    private void registrarAgendador() {
        try {
            String username = getText(agendadorUsernameField);
            String password = agendadorPasswordField.getText() == null
                    ? "" : agendadorPasswordField.getText().trim();

            if (username.isEmpty())
                throw new IllegalArgumentException(
                        "Debe ingresar el nombre de usuario del agendador.");
            if (password.isEmpty())
                throw new IllegalArgumentException(
                        "Debe ingresar la contraseña del agendador.");

            agendadorService.registrarAgendador(username, password);

            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "Agendador registrado correctamente.");
            limpiarFormularioAgendador();
            cargarAgendadores();

        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Ocurrió un error al registrar el agendador.");
            e.printStackTrace();
        }
    }

    @FXML
    private void limpiarFormularioAgendador() {
        agendadorUsernameField.clear();
        agendadorPasswordField.clear();
    }

    // ── Carga de tablas ───────────────────────────────────────────────────────

    private void cargarMedicos() {
        List<MedicoTablaModel> filas = medicoService.listarTodos()
                .stream()
                .map(this::toMedicoTablaModel)
                .toList();
        medicosTable.setItems(FXCollections.observableArrayList(filas));
    }

    private void cargarAgendadores() {
        List<AgendadorTablaModel> filas = agendadorService.listarAgendadores()
                .stream()
                .map(this::toAgendadorTablaModel)
                .toList();
        agendadoresTable.setItems(FXCollections.observableArrayList(filas));
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private MedicoTablaModel toMedicoTablaModel(Medico m) {
        String username = m.getUser() != null ? m.getUser().getUsername() : "";
        return new MedicoTablaModel(m.getId(), m.getNombreCompleto(),
                m.getEspecialidad(), m.getIntervaloMinutos(), username);
    }

    private AgendadorTablaModel toAgendadorTablaModel(User u) {
        return new AgendadorTablaModel(u.getId(), u.getUsername(),
                u.getStatus().name(), u.getRole().name());
    }

    // ── Sesión ────────────────────────────────────────────────────────────────

    // Requerido por adminPanel.xml — botón "Volver"
    @FXML
    private void volver() {
        userSession.clear();
        sceneManager.switchScene(Vista.LOGIN);
    }

    // Requerido por adminPanel.xml — botón "Cerrar sesión"
    @FXML
    private void logout() {
        userSession.clear();
        sceneManager.switchScene(Vista.LOGIN);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
