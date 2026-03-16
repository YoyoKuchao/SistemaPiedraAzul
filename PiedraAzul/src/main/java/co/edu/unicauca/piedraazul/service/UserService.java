package co.edu.unicauca.piedraazul.service;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.observer.Observer;
import co.edu.unicauca.piedraazul.observer.Subject;
import co.edu.unicauca.piedraazul.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService extends Subject {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(User user, Observer vista) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            notifyObservers("Registro fallido: usuario '" + user.getUsername() + "' ya existe.");
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Suscribir la vista al modelo User antes de cambiar su estado
        user.attach(vista);

        // Al llamar setStatus, el modelo User notifica directamente a la vista
        user.setStatus(user.getStatus());

        userRepository.save(user);
        notifyObservers("Nuevo usuario registrado: " + user.getUsername() + " [" + user.getRole() + "]");
        return true;
    }

    // Método original sin observer para compatibilidad interna
    public boolean registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            notifyObservers("Registro fallido: usuario '" + user.getUsername() + "' ya existe.");
            return false;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        notifyObservers("Nuevo usuario registrado: " + user.getUsername() + " [" + user.getRole() + "]");
        return true;
    }

    public User authenticate(String username, String rawPassword) {
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Username ingresado: " + username);
        System.out.println("Password ingresado: " + rawPassword);

        // ===== DIAGNÓSTICO TEMPORAL - borrar después =====
        BCryptPasswordEncoder testEncoder = new BCryptPasswordEncoder();
        String testHash = testEncoder.encode("password");
        System.out.println("Hash generado ahora: " + testHash);
        System.out.println("Test matches (nuevo hash): " + testEncoder.matches("password", testHash));
        System.out.println("Match hardcoded: " + testEncoder.matches("password",
            "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi."));
        // ===== FIN DIAGNÓSTICO =====

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Usuario encontrado: " + user.getUsername());
            System.out.println("Hash en BD: " + user.getPassword());
            System.out.println("Hash length: " + user.getPassword().length());
            boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
            System.out.println("Passwords coinciden: " + matches);
            if (matches) {
                notifyObservers("Login exitoso: " + username + " [" + user.getRole() + "]");
                return user;
            }
        } else {
            System.out.println("Usuario NO encontrado en BD");
        }
        notifyObservers("Login fallido para: " + username);
        return null;
    }
}