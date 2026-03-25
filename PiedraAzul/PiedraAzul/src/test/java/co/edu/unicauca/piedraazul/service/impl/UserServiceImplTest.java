package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.enums.UserRole;
import co.edu.unicauca.piedraazul.model.enums.UserStatus;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private Observer observer;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("julian");
        user.setPassword("1234");
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
    }

    @Test
    void registerUserDebeGuardarUsuarioConPasswordEncriptada() {
        when(userRepository.findByUsername("julian")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("hash123");

        boolean resultado = userService.registerUser(user, observer);

        assertTrue(resultado);
        assertEquals("hash123", user.getPassword());
        verify(userRepository).save(user);
        verify(observer).update(contains("Nuevo usuario registrado"));
    }

    @Test
    void registerUserDebeRetornarFalseSiUsuarioYaExiste() {
        when(userRepository.findByUsername("julian")).thenReturn(Optional.of(new User()));

        boolean resultado = userService.registerUser(user, observer);

        assertFalse(resultado);
        verify(userRepository, never()).save(any(User.class));
        verify(observer).update(contains("ya existe"));
    }

    @Test
    void authenticateDebeRetornarUsuarioSiPasswordEsCorrecto() {
        User guardado = new User();
        guardado.setUsername("julian");
        guardado.setPassword("hash123");
        guardado.setRole(UserRole.ADMIN);
        guardado.setStatus(UserStatus.ACTIVE);

        userService.attach(observer);
        when(userRepository.findByUsername("julian")).thenReturn(Optional.of(guardado));
        when(passwordEncoder.matches("1234", "hash123")).thenReturn(true);

        User resultado = userService.authenticate("julian", "1234");

        assertNotNull(resultado);
        assertEquals("julian", resultado.getUsername());
        verify(observer).update(contains("Login exitoso"));
    }

    @Test
    void authenticateDebeRetornarNullSiPasswordEsIncorrecto() {
        User guardado = new User();
        guardado.setUsername("julian");
        guardado.setPassword("hash123");

        userService.attach(observer);
        when(userRepository.findByUsername("julian")).thenReturn(Optional.of(guardado));
        when(passwordEncoder.matches("incorrecta", "hash123")).thenReturn(false);

        User resultado = userService.authenticate("julian", "incorrecta");

        assertNull(resultado);
        verify(observer).update(contains("Login fallido"));
    }

    @Test
    void authenticateDebeRetornarNullSiUsuarioNoExiste() {
        userService.attach(observer);
        when(userRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        User resultado = userService.authenticate("desconocido", "1234");

        assertNull(resultado);
        verify(observer).update(contains("Login fallido"));
        verify(passwordEncoder, never()).matches(any(), any());
    }
}
