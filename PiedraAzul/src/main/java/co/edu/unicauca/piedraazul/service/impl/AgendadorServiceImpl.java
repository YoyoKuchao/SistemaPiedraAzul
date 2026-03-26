package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.enums.UserRole;
import co.edu.unicauca.piedraazul.model.enums.UserStatus;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import co.edu.unicauca.piedraazul.service.IAgendadorService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendadorServiceImpl implements IAgendadorService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AgendadorServiceImpl(UserRepository userRepository,
                                BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
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

    @Override
    public List<User> listarAgendadores() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == UserRole.AGENDADOR)
                .toList();
    }
}
