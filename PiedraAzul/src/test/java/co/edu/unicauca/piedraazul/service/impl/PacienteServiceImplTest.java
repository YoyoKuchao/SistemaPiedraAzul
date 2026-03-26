package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.Paciente;
import co.edu.unicauca.piedraazul.model.enums.Genero;
import co.edu.unicauca.piedraazul.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceImplTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    @Test
    void buscarPorNumeroDocumentoDebeRetornarNullSiEsNulo() {
        assertNull(pacienteService.buscarPorNumeroDocumento(null));
        verifyNoInteractions(pacienteRepository);
    }

    @Test
    void buscarPorNumeroDocumentoDebeRetornarNullSiEsVacio() {
        assertNull(pacienteService.buscarPorNumeroDocumento("   "));
        verifyNoInteractions(pacienteRepository);
    }

    @Test
    void buscarPorNumeroDocumentoDebeHacerTrimYRetornarPaciente() {
        Paciente paciente = new Paciente();
        paciente.setNumeroDocumento("12345");

        when(pacienteRepository.findByNumeroDocumento("12345")).thenReturn(Optional.of(paciente));

        Paciente resultado = pacienteService.buscarPorNumeroDocumento(" 12345 ");

        assertNotNull(resultado);
        assertEquals("12345", resultado.getNumeroDocumento());
    }

    @Test
    void obtenerOCrearPacienteDebeRetornarExistenteSiYaEstaRegistrado() {
        Paciente existente = new Paciente();
        existente.setNumeroDocumento("999");
        existente.setNombres("Laura");

        when(pacienteRepository.findByNumeroDocumento("999")).thenReturn(Optional.of(existente));

        Paciente resultado = pacienteService.obtenerOCrearPaciente(
                "999", "CC", "Laura", "Ruiz", "3000000000",
                Genero.MUJER, LocalDate.of(2000, 1, 1), "laura@mail.com");

        assertSame(existente, resultado);
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }

    @Test
    void obtenerOCrearPacienteDebeGuardarNuevoPacienteSiNoExiste() {
        when(pacienteRepository.findByNumeroDocumento("111")).thenReturn(Optional.empty());
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Paciente resultado = pacienteService.obtenerOCrearPaciente(
                "111", "TI", "Carlos", "López", "3111111111",
                Genero.HOMBRE, LocalDate.of(2010, 5, 10), "carlos@mail.com");

        assertNotNull(resultado);
        assertEquals("111", resultado.getNumeroDocumento());
        assertEquals("TI", resultado.getTipoDocumento());
        assertEquals("Carlos", resultado.getNombres());
        assertEquals("López", resultado.getApellidos());
        assertEquals("3111111111", resultado.getCelular());
        assertEquals(Genero.HOMBRE, resultado.getGenero());
        assertEquals(LocalDate.of(2010, 5, 10), resultado.getFechaNacimiento());
        assertEquals("carlos@mail.com", resultado.getCorreo());
        verify(pacienteRepository).save(any(Paciente.class));
    }
    @Test
void obtenerOCrearPacienteDebeRetornarExistenteSiYaExisteDocumentoDos() {
    Paciente existente = new Paciente();
    existente.setNumeroDocumento("222");
    existente.setTipoDocumento("CC");
    existente.setNombres("Ana");
    existente.setApellidos("Lopez");

    when(pacienteRepository.findByNumeroDocumento("222")).thenReturn(Optional.of(existente));

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "222", "CC", "Otra", "Persona", "3000000000",
            Genero.MUJER, LocalDate.of(2000, 1, 1), "otra@mail.com"
    );

    assertSame(existente, resultado);
    verify(pacienteRepository, never()).save(any(Paciente.class));
}

@Test
void obtenerOCrearPacienteDebeGuardarCorreoNull() {
    when(pacienteRepository.findByNumeroDocumento("333")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "333", "CC", "Luis", "Martinez", "3111111111",
            Genero.HOMBRE, LocalDate.of(1999, 5, 10), null
    );

    assertNotNull(resultado);
    assertNull(resultado.getCorreo());
    assertEquals("333", resultado.getNumeroDocumento());
    verify(pacienteRepository).save(any(Paciente.class));
}

@Test
void obtenerOCrearPacienteDebeGuardarCelularNull() {
    when(pacienteRepository.findByNumeroDocumento("444")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "444", "CC", "Pedro", "Gomez", null,
            Genero.HOMBRE, LocalDate.of(1998, 7, 20), "pedro@mail.com"
    );

    assertNotNull(resultado);
    assertNull(resultado.getCelular());
    assertEquals("pedro@mail.com", resultado.getCorreo());
    verify(pacienteRepository).save(any(Paciente.class));
}

@Test
void obtenerOCrearPacienteDebeGuardarTipoDocumentoPasaporte() {
    when(pacienteRepository.findByNumeroDocumento("555")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "555", "PAS", "Maria", "Fernandez", "3009999999",
            Genero.MUJER, LocalDate.of(1995, 3, 15), "maria@mail.com"
    );

    assertNotNull(resultado);
    assertEquals("PAS", resultado.getTipoDocumento());
    assertEquals("Maria", resultado.getNombres());
    assertEquals("Fernandez", resultado.getApellidos());
}

@Test
void obtenerOCrearPacienteDebeConservarFechaNacimientoCorrecta() {
    when(pacienteRepository.findByNumeroDocumento("666")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    LocalDate fecha = LocalDate.of(2001, 12, 24);

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "666", "CC", "Sofia", "Ramirez", "3101234567",
            Genero.MUJER, fecha, "sofia@mail.com"
    );

    assertNotNull(resultado);
    assertEquals(fecha, resultado.getFechaNacimiento());
}

@Test
void obtenerOCrearPacienteDebeConservarGeneroMasculino() {
    when(pacienteRepository.findByNumeroDocumento("777")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "777", "CC", "Jorge", "Hernandez", "3001231234",
            Genero.HOMBRE, LocalDate.of(1990, 8, 8), "jorge@mail.com"
    );

    assertNotNull(resultado);
    assertEquals(Genero.HOMBRE, resultado.getGenero());
}

@Test
void obtenerOCrearPacienteDebeConservarGeneroFemenino() {
    when(pacienteRepository.findByNumeroDocumento("888")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Paciente resultado = pacienteService.obtenerOCrearPaciente(
            "888", "CC", "Paula", "Castro", "3008888888",
            Genero.MUJER, LocalDate.of(1992, 9, 9), "paula@mail.com"
    );

    assertNotNull(resultado);
    assertEquals(Genero.MUJER, resultado.getGenero());
}

@Test
void obtenerOCrearPacienteDebeBuscarUnaSolaVezPorDocumento() {
    when(pacienteRepository.findByNumeroDocumento("999")).thenReturn(Optional.empty());
    when(pacienteRepository.save(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));

    pacienteService.obtenerOCrearPaciente(
            "999", "CC", "Mario", "Diaz", "3007777777",
            Genero.HOMBRE, LocalDate.of(1997, 11, 11), "mario@mail.com"
    );

    verify(pacienteRepository, times(1)).findByNumeroDocumento("999");
    verify(pacienteRepository, times(1)).save(any(Paciente.class));
}
}
