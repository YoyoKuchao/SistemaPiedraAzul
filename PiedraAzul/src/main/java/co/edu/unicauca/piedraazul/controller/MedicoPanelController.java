package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.Cita;
import co.edu.unicauca.piedraazul.model.CitaMedicoTablaModel;
import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.service.CitaService;
import co.edu.unicauca.piedraazul.service.MedicoService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import co.edu.unicauca.piedraazul.util.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class MedicoPanelController {

    @FXML
    private Label bienvenidaLabel;
    @FXML
    private DatePicker fechaBusquedaPicker;
    @FXML
    private Label cantidadCitasLabel;

    @FXML
    private TableView<CitaMedicoTablaModel> citasTable;
    @FXML
    private TableColumn<CitaMedicoTablaModel, Long> idColumn;
    @FXML
    private TableColumn<CitaMedicoTablaModel, String> pacienteColumn;
    @FXML
    private TableColumn<CitaMedicoTablaModel, String> documentoColumn;
    @FXML
    private TableColumn<CitaMedicoTablaModel, String> fechaColumn;
    @FXML
    private TableColumn<CitaMedicoTablaModel, String> horaColumn;
    @FXML
    private TableColumn<CitaMedicoTablaModel, String> estadoColumn;
    @FXML
    private TableColumn<CitaMedicoTablaModel, String> observacionColumn;

    private final SceneManager sceneManager;
    private final UserSession userSession;
    private final MedicoService medicoService;
    private final CitaService citaService;

    private Medico medicoActual;

    public MedicoPanelController(
            SceneManager sceneManager,
            UserSession userSession,
            MedicoService medicoService,
            CitaService citaService
    ) {
        this.sceneManager = sceneManager;
        this.userSession = userSession;
        this.medicoService = medicoService;
        this.citaService = citaService;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pacienteColumn.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("hora"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));
        observacionColumn.setCellValueFactory(new PropertyValueFactory<>("observacion"));

        fechaBusquedaPicker.setValue(LocalDate.now());

        cargarMedicoActual();
        buscarMisCitas();
    }

    @FXML
    private void buscarMisCitas() {
        if (medicoActual == null) {
            cantidadCitasLabel.setText("Cantidad de citas: 0");
            citasTable.setItems(FXCollections.observableArrayList());
            return;
        }

        LocalDate fecha = fechaBusquedaPicker.getValue();
        if (fecha == null) {
            showAlert(Alert.AlertType.WARNING, "Validación", "Debe seleccionar una fecha.");
            return;
        }

        List<Cita> citas = citaService.buscarPorMedicoYFecha(medicoActual, fecha);

        List<CitaMedicoTablaModel> filas = citas.stream()
                .map(cita -> new CitaMedicoTablaModel(
                        cita.getId(),
                        cita.getPaciente().getNombreCompleto(),
                        cita.getPaciente().getNumeroDocumento(),
                        cita.getFecha().toString(),
                        cita.getHora().toString(),
                        cita.getEstado().name(),
                        cita.getObservacion() == null ? "" : cita.getObservacion()
                ))
                .toList();

        citasTable.setItems(FXCollections.observableArrayList(filas));
        cantidadCitasLabel.setText("Cantidad de citas: " + filas.size());
    }

    @FXML
    private void logout() {
        userSession.clear();
        sceneManager.switchScene("login.xml");
    }

    private void cargarMedicoActual() {
        User currentUser = userSession.getCurrentUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Sesión", "No hay un usuario autenticado en sesión.");
            return;
        }

        medicoActual = medicoService.buscarPorUsernameUsuario(currentUser.getUsername()).orElse(null);

        if (medicoActual == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se encontró un perfil de médico asociado al usuario.");
            return;
        }

        bienvenidaLabel.setText("Bienvenido, " + medicoActual.getNombreCompleto());
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleLogout() {
    try {
        sceneManager.switchScene("login.xml");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}