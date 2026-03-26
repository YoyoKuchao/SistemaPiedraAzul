package co.edu.unicauca.piedraazul.service.impl;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.observer.Subject;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import co.edu.unicauca.piedraazul.service.IUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl extends Subject implements IUserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean registerUser(User user, Observer vista) {
        attach(vista);
        return registerUser(user);
    }

    @Override
    public boolean registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            notifyObservers("Registro fallido: usuario '" + user.getUsername() + "' ya existe.");
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        notifyObservers("Nuevo usuario registrado: " + user.getUsername()
                + " [" + user.getRole() + "]");
        return true;
    }

    @Override
    public User authenticate(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                notifyObservers("Login exitoso: " + username
                        + " [" + user.getRole() + "]");
                return user;
            }
        }

        notifyObservers("Login fallido para: " + username);
        return null;
    }
}
