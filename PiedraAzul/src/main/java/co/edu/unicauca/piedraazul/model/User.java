package co.edu.unicauca.piedraazul.model;

import co.edu.unicauca.piedraazul.observer.Subject;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    // Getters y setters — al cambiar estado notifica a los observers

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) {
        this.role = role;
    }

    public UserStatus getStatus() { return status; }

    // Al cambiar el status del usuario, notifica a las vistas suscritas
    public void setStatus(UserStatus status) {
        this.status = status;
        notifyObservers("Estado del usuario '" + this.username + "' cambió a: " + status);
    }
}