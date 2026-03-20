package co.edu.unicauca.piedraazul.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class SceneManager {

    private final ApplicationContext applicationContext;
    private Stage primaryStage;

    public SceneManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void switchScene(String fxmlFile) {
        if (primaryStage == null) {
            throw new IllegalStateException("PrimaryStage no ha sido inicializado en SceneManager.");
        }

        try {
            URL fxmlLocation = getClass().getResource("/fxml/" + fxmlFile);

            if (fxmlLocation == null) {
                throw new IllegalArgumentException("No se encontró el archivo FXML: /fxml/" + fxmlFile);
            }

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            loader.setControllerFactory(applicationContext::getBean);

            Parent root = loader.load();

            Scene newScene;

            if (primaryStage.getScene() == null) {
                newScene = new Scene(root);
            } else {
                double currentWidth = primaryStage.getWidth();
                double currentHeight = primaryStage.getHeight();

                if (currentWidth <= 0 || currentHeight <= 0) {
                    newScene = new Scene(root);
                } else {
                    newScene = new Scene(root, currentWidth, currentHeight);
                }
            }

            URL cssLocation = getClass().getResource("/styles/app.css");
            if (cssLocation != null) {
                newScene.getStylesheets().add(cssLocation.toExternalForm());
            }

            primaryStage.setScene(newScene);
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar la escena: " + fxmlFile, e);
        }
    }
}