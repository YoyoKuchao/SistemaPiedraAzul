package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.enums.UserRole;
import co.edu.unicauca.piedraazul.model.enums.UserStatus;
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
class AgendadorServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AgendadorServiceImpl agendadorService;

    @Test
    void registrarAgendadorDebeFallarSiUsernameYaExiste() {
        when(userRepository.findByUsername("agenda1")).thenReturn(Optional.of(new User()));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> agendadorService.registrarAgendador("agenda1", "123"));

        assertTrue(ex.getMessage().contains("ya existe"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registrarAgendadorDebeGuardarConRolYEstadoCorrecto() {
        when(userRepository.findByUsername("agenda1")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123")).thenReturn("hash123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = agendadorService.registrarAgendador("agenda1", "123");

        assertNotNull(resultado);
        assertEquals("agenda1", resultado.getUsername());
        assertEquals("hash123", resultado.getPassword());
        assertEquals(UserRole.AGENDADOR, resultado.getRole());
        assertEquals(UserStatus.ACTIVE, resultado.getStatus());
    }

    @Test
    void listarAgendadoresDebeFiltrarSoloRolAgendador() {
        User a1 = new User();
        a1.setRole(UserRole.AGENDADOR);
        User a2 = new User();
        a2.setRole(UserRole.ADMIN);
        User a3 = new User();
        a3.setRole(UserRole.AGENDADOR);

        when(userRepository.findAll()).thenReturn(List.of(a1, a2, a3));

        List<User> resultado = agendadorService.listarAgendadores();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(u -> u.getRole() == UserRole.AGENDADOR));
    }
}
