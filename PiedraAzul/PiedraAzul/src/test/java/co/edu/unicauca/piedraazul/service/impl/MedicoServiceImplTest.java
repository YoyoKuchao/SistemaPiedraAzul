package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.enums.UserRole;
import co.edu.unicauca.piedraazul.model.enums.UserStatus;
import co.edu.unicauca.piedraazul.repository.MedicoRepository;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicoServiceImplTest {

    @Mock
    private MedicoRepository medicoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private MedicoServiceImpl medicoService;

    @Test
    void listarTodosDebeRetornarMedicos() {
        when(medicoRepository.findAll()).thenReturn(List.of(new Medico(), new Medico()));
        assertEquals(2, medicoService.listarTodos().size());
    }

    @Test
    void buscarPorIdDebeDelegarAlRepositorio() {
        Medico medico = new Medico();
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));

        Optional<Medico> resultado = medicoService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void registrarMedicoDebeFallarSiUsernameYaExiste() {
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> medicoService.registrarMedico("Ana", "Pediatría", 20, "doctor1", "abc"));

        assertTrue(ex.getMessage().contains("ya existe"));
        verify(userRepository, never()).save(any(User.class));
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    void registrarMedicoDebeCrearUsuarioYMedicoCorrectamente() {
        when(userRepository.findByUsername("doctor1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("abc")).thenReturn("hashabc");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(medicoRepository.save(any(Medico.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Medico resultado = medicoService.registrarMedico("Ana Torres", "Pediatría", 20, "doctor1", "abc");

        assertNotNull(resultado);
        assertEquals("Ana Torres", resultado.getNombreCompleto());
        assertEquals("Pediatría", resultado.getEspecialidad());
        assertEquals(20, resultado.getIntervaloMinutos());
        assertNotNull(resultado.getUser());
        assertEquals("doctor1", resultado.getUser().getUsername());
        assertEquals("hashabc", resultado.getUser().getPassword());
        assertEquals(UserRole.MEDICO, resultado.getUser().getRole());
        assertEquals(UserStatus.ACTIVE, resultado.getUser().getStatus());
    }

    @Test
    void guardarDebePersistirMedico() {
        Medico medico = new Medico();
        when(medicoRepository.save(medico)).thenReturn(medico);

        Medico resultado = medicoService.guardar(medico);

        assertSame(medico, resultado);
    }
}
