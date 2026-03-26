package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.Cita;
import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.Paciente;
import co.edu.unicauca.piedraazul.model.enums.EstadoCita;
import co.edu.unicauca.piedraazul.repository.CitaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;

    private CitaServiceImpl citaService;

    private Paciente pacienteExistente;
    private Medico medicoExistente;

    @BeforeEach
    void setUp() {
        citaService = new CitaServiceImpl(citaRepository);

        pacienteExistente = new Paciente();
        pacienteExistente.setNumeroDocumento("123");
        pacienteExistente.setTipoDocumento("CC");
        pacienteExistente.setNombres("Laura");
        pacienteExistente.setApellidos("Ruiz");
        pacienteExistente.setCelular("3000000000");

        medicoExistente = new Medico();
        medicoExistente.setNombreCompleto("Carlos Pérez");
        medicoExistente.setEspecialidad("Cardiología");
        medicoExistente.setIntervaloMinutos(30);
    }

    @Test
    void debeCrearCitaCuandoHorarioEstaDisponible() {
        when(citaRepository.existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 25),
                LocalTime.of(10, 0)
        )).thenReturn(false);

        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.crearCita(
                pacienteExistente,
                medicoExistente,
                LocalDate.of(2026, 3, 25),
                LocalTime.of(10, 0),
                "Control general"
        );

        assertNotNull(resultado);
        assertEquals(pacienteExistente, resultado.getPaciente());
        assertEquals(medicoExistente, resultado.getMedico());
        assertEquals(LocalDate.of(2026, 3, 25), resultado.getFecha());
        assertEquals(LocalTime.of(10, 0), resultado.getHora());
        assertEquals(EstadoCita.PROGRAMADA, resultado.getEstado());
        assertEquals("Control general", resultado.getObservacion());

        verify(citaRepository).existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 25),
                LocalTime.of(10, 0)
        );
        verify(citaRepository).save(any(Cita.class));
    }

    @Test
    void noDebeCrearCitaSiHorarioYaEstaOcupado() {
        when(citaRepository.existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 25),
                LocalTime.of(10, 0)
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> citaService.crearCita(
                        pacienteExistente,
                        medicoExistente,
                        LocalDate.of(2026, 3, 25),
                        LocalTime.of(10, 0),
                        "Control repetido"
                )
        );

        assertEquals(
                "Ya existe una cita para ese médico en la fecha y hora seleccionadas.",
                ex.getMessage()
        );

        verify(citaRepository, never()).save(any(Cita.class));
    }

    @Test
    void debeGuardarObservacionCorrectamente() {
        when(citaRepository.existsByMedicoAndFechaAndHora(any(), any(), any())).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.crearCita(
                pacienteExistente,
                medicoExistente,
                LocalDate.of(2026, 4, 1),
                LocalTime.of(8, 30),
                "Paciente con dolor torácico"
        );

        assertEquals("Paciente con dolor torácico", resultado.getObservacion());
    }

    @Test
    void debeCrearCitaConEstadoProgramadaPorDefecto() {
        when(citaRepository.existsByMedicoAndFechaAndHora(any(), any(), any())).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.crearCita(
                pacienteExistente,
                medicoExistente,
                LocalDate.of(2026, 4, 2),
                LocalTime.of(9, 0),
                "Primera valoración"
        );

        assertEquals(EstadoCita.PROGRAMADA, resultado.getEstado());
    }

    @Test
    void debeConservarPacienteAsignadoEnLaCita() {
        when(citaRepository.existsByMedicoAndFechaAndHora(any(), any(), any())).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.crearCita(
                pacienteExistente,
                medicoExistente,
                LocalDate.of(2026, 4, 3),
                LocalTime.of(11, 0),
                "Consulta externa"
        );

        assertSame(pacienteExistente, resultado.getPaciente());
    }

    @Test
    void debeConservarMedicoAsignadoEnLaCita() {
        when(citaRepository.existsByMedicoAndFechaAndHora(any(), any(), any())).thenReturn(false);
        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cita resultado = citaService.crearCita(
                pacienteExistente,
                medicoExistente,
                LocalDate.of(2026, 4, 4),
                LocalTime.of(12, 0),
                "Seguimiento"
        );

        assertSame(medicoExistente, resultado.getMedico());
    }

    @Test
    void debeBuscarCitasPorMedicoYFecha() {
        List<Cita> citas = List.of(new Cita(), new Cita());

        when(citaRepository.findByMedicoAndFechaOrderByHoraAsc(
                medicoExistente,
                LocalDate.of(2026, 3, 25)
        )).thenReturn(citas);

        List<Cita> resultado = citaService.buscarPorMedicoYFecha(
                medicoExistente,
                LocalDate.of(2026, 3, 25)
        );

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        verify(citaRepository).findByMedicoAndFechaOrderByHoraAsc(
                medicoExistente,
                LocalDate.of(2026, 3, 25)
        );
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayCitasParaElMedicoEnLaFecha() {
        when(citaRepository.findByMedicoAndFechaOrderByHoraAsc(
                medicoExistente,
                LocalDate.of(2026, 3, 26)
        )).thenReturn(Collections.emptyList());

        List<Cita> resultado = citaService.buscarPorMedicoYFecha(
                medicoExistente,
                LocalDate.of(2026, 3, 26)
        );

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeContarCitasPorMedicoYFecha() {
        List<Cita> citas = List.of(new Cita(), new Cita(), new Cita());

        when(citaRepository.findByMedicoAndFechaOrderByHoraAsc(
                medicoExistente,
                LocalDate.of(2026, 3, 27)
        )).thenReturn(citas);

        long total = citaService.contarPorMedicoYFecha(
                medicoExistente,
                LocalDate.of(2026, 3, 27)
        );

        assertEquals(3, total);
    }

    @Test
    void debeRetornarCeroCuandoNoHayCitasParaContar() {
        when(citaRepository.findByMedicoAndFechaOrderByHoraAsc(
                medicoExistente,
                LocalDate.of(2026, 3, 28)
        )).thenReturn(Collections.emptyList());

        long total = citaService.contarPorMedicoYFecha(
                medicoExistente,
                LocalDate.of(2026, 3, 28)
        );

        assertEquals(0, total);
    }

    @Test
    void debeConsultarDisponibilidadAntesDeGuardarLaCita() {
        when(citaRepository.existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 29),
                LocalTime.of(7, 0)
        )).thenReturn(false);

        when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> invocation.getArgument(0));

        citaService.crearCita(
                pacienteExistente,
                medicoExistente,
                LocalDate.of(2026, 3, 29),
                LocalTime.of(7, 0),
                "Examen"
        );

        verify(citaRepository, times(1)).existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 29),
                LocalTime.of(7, 0)
        );
        verify(citaRepository, times(1)).save(any(Cita.class));
    }

    @Test
    void noDebeGuardarSiExisteConflictoDeHorario() {
        when(citaRepository.existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 30),
                LocalTime.of(14, 0)
        )).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> citaService.crearCita(
                        pacienteExistente,
                        medicoExistente,
                        LocalDate.of(2026, 3, 30),
                        LocalTime.of(14, 0),
                        "Choque de horario"
                )
        );

        verify(citaRepository, times(1)).existsByMedicoAndFechaAndHora(
                medicoExistente,
                LocalDate.of(2026, 3, 30),
                LocalTime.of(14, 0)
        );
        verify(citaRepository, never()).save(any(Cita.class));
    }
}