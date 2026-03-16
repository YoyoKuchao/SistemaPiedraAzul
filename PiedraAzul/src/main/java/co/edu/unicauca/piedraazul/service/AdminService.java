package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.Medico;
import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.UserRole;
import co.edu.unicauca.piedraazul.model.UserStatus;
import co.edu.unicauca.piedraazul.repository.MedicoRepository;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final MedicoRepository medicoRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(
            UserRepository userRepository,
            MedicoRepository medicoRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.medicoRepository = medicoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Medico registrarMedico(
            String nombreCompleto,
            String especialidad,
            Integer intervaloMinutos,
            String username,
            String password
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.MEDICO);
        user.setStatus(UserStatus.ACTIVE);

        User userGuardado = userRepository.save(user);

        Medico medico = new Medico();
        medico.setNombreCompleto(nombreCompleto);
        medico.setEspecialidad(especialidad);
        medico.setIntervaloMinutos(intervaloMinutos);
        medico.setUser(userGuardado);

        return medicoRepository.save(medico);
    }

    public User registrarAgendador(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.AGENDADOR);
        user.setStatus(UserStatus.ACTIVE);

        return userRepository.save(user);
    }

    public List<Medico> listarMedicos() {
        return medicoRepository.findAll();
    }

    public List<User> listarAgendadores() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole() == UserRole.AGENDADOR)
                .toList();
    }
}