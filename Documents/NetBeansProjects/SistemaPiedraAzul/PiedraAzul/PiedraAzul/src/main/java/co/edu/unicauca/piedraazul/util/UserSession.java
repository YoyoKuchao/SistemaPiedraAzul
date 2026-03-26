package co.edu.unicauca.piedraazul.util;

import co.edu.unicauca.piedraazul.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserSession {

    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void clear() {
        this.currentUser = null;
    }
}