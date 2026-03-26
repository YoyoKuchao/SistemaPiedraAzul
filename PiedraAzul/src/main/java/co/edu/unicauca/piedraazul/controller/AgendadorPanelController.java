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
import javafx.scene.control.DateCell;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final LocalTime HORA_INICIO_MANANA = LocalTime.of(8, 0);
    private static final LocalTime HORA_FIN_MANANA = LocalTime.of(12, 0);
    private static final LocalTime HORA_INICIO_TARDE = LocalTime.of(14, 0);
    private static final LocalTime HORA_FIN_TARDE = LocalTime.of(18, 0);

    private static final Pattern SOLO_NUMEROS = Pattern.compile("\\d+");
    private static final Pattern CORREO_VALIDO = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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

    horaCombo.setItems(FXCollections.observableArrayList());

    medicoCombo.valueProperty().addListener((obs, anterior, actual) -> cargarHorasDisponibles());
    fechaCitaPicker.valueProperty().addListener((obs, anterior, actual) -> cargarHorasDisponibles());

    numeroDocumentoField.focusedProperty().addListener((obs, antes, ahora) -> {
        if (!ahora) {
            autocompletarPacientePorDocumento();
        }
    });

    numeroDocumentoField.setOnAction(event -> autocompletarPacientePorDocumento());

    configurarDatePickers();

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
                    LocalTime.parse(horaCombo.getValue(), FORMATO_HORA),
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
            showAlert(Alert.AlertType.WARNING, "Validación", "Debe seleccionar médico y fecha para realizar la búsqueda.");
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
                        cita.getHora().format(FORMATO_HORA),
                        cita.getEstado().name()
                ))
                .toList();

        citasTable.setItems(FXCollections.observableArrayList(filas));
        cantidadCitasLabel.setText("Cantidad de citas: " + filas.size());

        if (filas.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Sin resultados", "No se encontraron citas para el médico y la fecha seleccionados.");
        }
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
        horaCombo.getItems().clear();
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

    private void configurarDatePickers() {
        fechaCitaPicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });

        fechaBusquedaPicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()));
            }
        });

        fechaNacimientoPicker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isAfter(LocalDate.now()));
            }
        });
    }

    private void validarCamposObligatorios() {
        if (tipoDocumentoCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar el tipo de documento.");
        }

        String numeroDocumento = getText(numeroDocumentoField);
        if (numeroDocumento.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el número de documento.");
        }
        if (!SOLO_NUMEROS.matcher(numeroDocumento).matches()) {
            throw new IllegalArgumentException("El número de documento solo debe contener números.");
        }

        if (getText(nombresField).isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los nombres.");
        }

        if (getText(apellidosField).isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar los apellidos.");
        }

        String celular = getText(celularField);
        if (celular.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el celular.");
        }
        if (!SOLO_NUMEROS.matcher(celular).matches()) {
            throw new IllegalArgumentException("El celular solo debe contener números.");
        }

        if (generoCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar el género.");
        }

        String correo = getText(correoField);
        if (!correo.isEmpty() && !CORREO_VALIDO.matcher(correo).matches()) {
            throw new IllegalArgumentException("El correo electrónico no tiene un formato válido.");
        }

        if (medicoCombo.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar el médico.");
        }

        if (fechaCitaPicker.getValue() == null) {
            throw new IllegalArgumentException("Debe seleccionar la fecha de la cita.");
        }

        if (fechaCitaPicker.getValue().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden registrar citas en fechas pasadas.");
        }

        if (horaCombo.getValue() == null || horaCombo.getValue().isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar la hora de la cita.");
        }
    }

    private void cargarHorasDisponibles() {
        horaCombo.getItems().clear();
        horaCombo.setValue(null);

        Medico medicoSeleccionado = medicoCombo.getValue();
        LocalDate fechaSeleccionada = fechaCitaPicker.getValue();

        if (medicoSeleccionado == null || fechaSeleccionada == null) {
            return;
        }

        if (fechaSeleccionada.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.WARNING, "Fecha inválida", "No se pueden asignar citas en fechas pasadas.");
            return;
        }

        Integer intervalo = medicoSeleccionado.getIntervaloMinutos();

        if (intervalo == null || intervalo <= 0) {
            intervalo = 30;
        }

        List<String> horasGeneradas = generarHorasPorIntervalo(intervalo);

        List<Cita> citasExistentes = citaService.buscarPorMedicoYFecha(medicoSeleccionado, fechaSeleccionada);

        Set<String> horasOcupadas = citasExistentes.stream()
                .map(cita -> cita.getHora().format(FORMATO_HORA))
                .collect(Collectors.toSet());

        List<String> horasDisponibles = horasGeneradas.stream()
                .filter(hora -> !horasOcupadas.contains(hora))
                .toList();

        horaCombo.setItems(FXCollections.observableArrayList(horasDisponibles));

        if (horasDisponibles.isEmpty()) {
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Sin disponibilidad",
                    "No hay horas disponibles para el médico seleccionado en esa fecha."
            );
        }
    }

    private List<String> generarHorasPorIntervalo(int intervaloMinutos) {
        List<String> horas = new ArrayList<>();

        agregarRangoHoras(horas, HORA_INICIO_MANANA, HORA_FIN_MANANA, intervaloMinutos);
        agregarRangoHoras(horas, HORA_INICIO_TARDE, HORA_FIN_TARDE, intervaloMinutos);

        return horas;
    }

    private void agregarRangoHoras(List<String> horas, LocalTime inicio, LocalTime fin, int intervaloMinutos) {
        LocalTime horaActual = inicio;

        while (horaActual.isBefore(fin)) {
            horas.add(horaActual.format(FORMATO_HORA));
            horaActual = horaActual.plusMinutes(intervaloMinutos);
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
    @FXML
private void autocompletarPacientePorDocumento() {
    String numeroDocumento = getText(numeroDocumentoField);

    if (numeroDocumento.isEmpty()) {
        return;
    }

    if (!SOLO_NUMEROS.matcher(numeroDocumento).matches()) {
        showAlert(Alert.AlertType.WARNING, "Validación", "El número de documento solo debe contener números.");
        return;
    }

    Paciente paciente = pacienteService.buscarPorNumeroDocumento(numeroDocumento);

    if (paciente == null) {
        limpiarDatosPacienteManteniendoDocumento();
        return;
    }

    if (paciente.getTipoDocumento() != null) {
        tipoDocumentoCombo.setValue(paciente.getTipoDocumento());
    } else {
        tipoDocumentoCombo.setValue(null);
    }

    nombresField.setText(valorSeguro(paciente.getNombres()));
    apellidosField.setText(valorSeguro(paciente.getApellidos()));
    celularField.setText(valorSeguro(paciente.getCelular()));
    correoField.setText(valorSeguro(paciente.getCorreo()));

    if (paciente.getGenero() != null) {
        generoCombo.setValue(paciente.getGenero());
    } else {
        generoCombo.setValue(null);
    }

    fechaNacimientoPicker.setValue(paciente.getFechaNacimiento());
}
private void limpiarDatosPacienteManteniendoDocumento() {
    tipoDocumentoCombo.setValue(null);
    nombresField.clear();
    apellidosField.clear();
    celularField.clear();
    generoCombo.setValue(null);
    fechaNacimientoPicker.setValue(null);
    correoField.clear();
}

private String valorSeguro(String valor) {
    return valor == null ? "" : valor;
}
}