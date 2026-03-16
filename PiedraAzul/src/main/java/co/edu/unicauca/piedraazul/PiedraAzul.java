package co.edu.unicauca.piedraazul;

import co.edu.unicauca.piedraazul.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
public class PiedraAzul extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(PiedraAzul.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager sceneManager = springContext.getBean(SceneManager.class);
        sceneManager.setPrimaryStage(primaryStage);

       primaryStage.setTitle("PiedraAzul");
       primaryStage.setWidth(1280);
       primaryStage.setHeight(760);
       primaryStage.setMinWidth(1100);
       primaryStage.setMinHeight(700);
       primaryStage.centerOnScreen();
        
        sceneManager.switchScene("login.xml");
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}