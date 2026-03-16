package co.edu.unicauca.piedraazul.controller;

import co.edu.unicauca.piedraazul.model.Cita;
import co.edu.unicauca.piedraazul.model.CitaTablaModel;
import co.edu.unicauca.piedraazul.model.Genero;
import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.Paciente;
import co.edu.unicauca.piedraazul.service.CitaService;
import co.edu.unicauca.piedraazul.service.MedicoService;
import co.edu.unicauca.piedraazul.service.PacienteService;
import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class AgendadorPanelController {

    @FXML
    private Button crearCitaButton;
    @FXML
    private Button consultarCitasButton;
    @FXML
    private Label tituloSeccionLabel;
    @FXML
    private Label subtituloSeccionLabel;
    @FXML
    private VBox crearCitaSection;
    @FXML
    private VBox consultarCitasSection;

    @FXML
    private ComboBox<String> tipoDocumentoCombo;
    @FXML
    private TextField numeroDocumentoField;
    @FXML
    private TextField celularField;
    @FXML
    private TextField nombresField;
    @FXML
    private TextField apellidosField;
    @FXML
    private ComboBox<Genero> generoCombo;
    @FXML
    private DatePicker fechaNacimientoPicker;
    @FXML
    private TextField correoField;
    @FXML
    private ComboBox<Medico> medicoCombo;
    @FXML
    private DatePicker fechaCitaPicker;
    @FXML
    private ComboBox<String> horaCombo;
    @FXML
    private TextArea observacionArea;

    @FXML
    private ComboBox<Medico> medicoBusquedaCombo;
    @FXML
    private DatePicker fechaBusquedaPicker;
    @FXML
    private Label cantidadCitasLabel;
    @FXML
    private TableView<CitaTablaModel> citasTable;
    @FXML
    private TableColumn<CitaTablaModel, Long> idColumn;
    @FXML
    private TableColumn<CitaTablaModel, String> pacienteColumn;
    @FXML
    private TableColumn<CitaTablaModel, String> documentoColumn;
    @FXML
    private TableColumn<CitaTablaModel, String> medicoColumn;
    @FXML
    private TableColumn<CitaTablaModel, String> fechaColumn;
    @FXML
    private TableColumn<CitaTablaModel, String> horaColumn;
    @FXML
    private TableColumn<CitaTablaModel, String> estadoColumn;

    private final SceneManager sceneManager;
    private final PacienteService pacienteService;
    private final MedicoService medicoService;
    private final CitaService citaService;

    public AgendadorPanelController(
            SceneManager sceneManager,
            PacienteService pacienteService,
            MedicoService medicoService,
            CitaService citaService
    ) {
        this.sceneManager = sceneManager;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.citaService = citaService;
    }

    @FXML
    private void initialize() {
        tipoDocumentoCombo.getItems().addAll(
                "Cédula de ciudadanía",
                "Tarjeta de identidad",
                "Cédula de extranjería",
                "Pasaporte"
        );

        generoCombo.getItems().addAll(Genero.values());

        horaCombo.getItems().addAll(
                "08:00", "08:30", "09:00", "09:30",
                "10:00", "10:30", "11:00", "11:30",
                "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00"
        );

        List<Medico> medicos = medicoService.listarTodos();
        medicoCombo.setItems(FXCollections.observableArrayList(medicos));
        medicoBusquedaCombo.setItems(FXCollections.observableArrayList(medicos));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        pacienteColumn.setCellValueFactory(new PropertyValueFactory<>("paciente"));
        documentoColumn.setCellValueFactory(new PropertyValueFactory<>("documento"));
        medicoColumn.setCellValueFactory(new PropertyValueFactory<>("medico"));
        fechaColumn.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horaColumn.setCellValueFactory(new PropertyValueFactory<>("hora"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        mostrarCrearCita();
    }

    @FXML
    private void mostrarCrearCita() {
        crearCitaSection.setVisible(true);
        crearCitaSection.setManaged(true);

        consultarCitasSection.setVisible(false);
        consultarCitasSection.setManaged(false);

        crearCitaButton.getStyleClass().removeAll("menu-button", "menu-button-active");
        consultarCitasButton.getStyleClass().removeAll("menu-button", "menu-button-active");

        crearCitaButton.getStyleClass().add("menu-button-active");
        consultarCitasButton.getStyleClass().add("menu-button");

        tituloSeccionLabel.setText("Gestión de citas");
        subtituloSeccionLabel.setText("Aquí el agendador puede registrar una nueva cita.");
    }

    @FXML
    private void mostrarConsultarCitas() {
        crearCitaSection.setVisible(false);
        crearCitaSection.setManaged(false);

        consultarCitasSection.setVisible(true);
        consultarCitasSection.setManaged(true);

        crearCitaButton.getStyleClass().removeAll("menu-button", "menu-button-active");
        consultarCitasButton.getStyleClass().removeAll("menu-button", "menu-button-active");

        crearCitaButton.getStyleClass().add("menu-button");
        consultarCitasButton.getStyleClass().add("menu-button-active");

        tituloSeccionLabel.setText("Consulta de citas");
        subtituloSeccionLabel.setText("Aquí el agendador puede consultar las citas de un médico por fecha.");
    }

    @FXML
    private void guardarCita() {
        try {
            validarCamposObligatorios();

            Paciente paciente = pacienteService.obtenerOCrearPaciente(
                    getText(numeroDocumentoField),
                    tipoDocumentoCombo.getValue(),
                    getText(nombresField),
                    getText(apellidosField),
                    getText(celularField),
                    generoCombo.getValue(),
                    fechaNacimientoPicker.getValue(),
                    getText(correoField)
            );

            citaService.crearCita(
                    paciente,
                    medicoCombo.getValue(),
                    fechaCitaPicker.getValue(),
                    LocalTime.parse(horaCombo.getValue()),
                    getText(observacionArea)
            );

            showAlert(Alert.AlertType.INFORMATION, "Éxito", "La cita fue registrada correctamente.");
            limpiarFormulario();
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.WARNING, "Validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al guardar la cita.");
            e.printStackTrace();
        }
    }

    @FXML
    private void buscarCitas() {
        Medico medico = medicoBusquedaCombo.getValue();
        LocalDate fecha = fechaBusquedaPicker.getValue();

        if (medico == null || fecha == null) {
            cantidadCitasLabel.setText("Cantidad de citas: 0");
            citasTable.setItems(FXCollections.observableArrayList());
            return;
        }

        List<Cita> citas = citaService.buscarPorMedicoYFecha(medico, fecha);

        List<CitaTablaModel> filas = citas.stream()
                .map(cita -> new CitaTablaModel(
                        cita.getId(),
                        cita.getPaciente().getNombreCompleto(),
                        cita.getPaciente().getNumeroDocumento(),
                        cita.getMedico().getNombreCompleto(),
                        cita.getFecha().toString(),
                        cita.getHora().toString(),
                        cita.getEstado().name()
                ))
                .toList();

        citasTable.setItems(FXCollections.observableArrayList(filas));
        cantidadCitasLabel.setText("Cantidad de citas: " + filas.size());
    }

    @FXML
    private void limpiarFormulario() {
        tipoDocumentoCombo.setValue(null);
        numeroDocumentoField.clear();
        celularField.clear();
        nombresField.clear();
        apellidosField.clear();
        generoCombo.setValue(null);
        fechaNacimientoPicker.setValue(null);
        correoField.clear();
        medicoCombo.setValue(null);
        fechaCitaPicker.setValue(null);
        horaCombo.setValue(null);
        observacionArea.clear();
    }

    @FXML
    private void volver() {
        sceneManager.switchScene("login.xml");
    }

    @FXML
    private void logout() {
        sceneManager.switchScene("login.xml");
    }

    private void validarCamposObligatorios() {
        if (tipoDocumentoCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de documento.");
        }
        if (getText(numeroDocumentoField).isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el número de documento.");
        }
        if (getText(nombresField).isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los nombres.");
        }
        if (getText(apellidosField).isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los apellidos.");
        }
        if (getText(celularField).isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el celular.");
        }
        if (generoCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar el género.");
        }
        if (medicoCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar el médico.");
        }
        if (fechaCitaPicker.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar la fecha de la cita.");
        }
        if (horaCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar la hora de la cita.");
        }
    }

    private String getText(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private String getText(TextArea area) {
        return area.getText() == null ? "" : area.getText().trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}