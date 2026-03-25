package co.edu.unicauca.piedraazul.config;

import co.edu.unicauca.piedraazul.model.User;
import co.edu.unicauca.piedraazul.model.enums.UserRole;
import co.edu.unicauca.piedraazul.model.enums.UserStatus;
import co.edu.unicauca.piedraazul.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final IUserService userService;

    public DataInitializer(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        crearAdminPorDefectoSiNoExiste();
    }

    /**
     * Crea el usuario administrador inicial solo si no existe ninguno.
     * Las credenciales deben cambiarse en el primer inicio de sesión.
     */
    private void crearAdminPorDefectoSiNoExiste() {
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword("admin123");   // se encripta en UserServiceImpl
        admin.setRole(UserRole.ADMIN);
        admin.setStatus(UserStatus.ACTIVE);

        boolean creado = userService.registerUser(admin);
        if (creado) {
            log.info("Usuario admin creado. Cambie la contraseña en el primer inicio de sesión.");
        }
    }
}
