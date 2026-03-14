package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationMain extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // FXML from src/main/resources/gui/UserInterface.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/UserInterface.fxml"));
        Parent root = loader.load();


        Scene scene = new Scene(root, 800, 360);


        stage.setTitle("Pairs");
        stage.setMinWidth(240);
        stage.setMinHeight(200);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}