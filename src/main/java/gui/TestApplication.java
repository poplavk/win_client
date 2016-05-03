package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class TestApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    //@Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("mainForm.fxml"));

        Scene scene = new Scene(root, 860, 770);

        stage.setTitle("Pry 1.0");

        stage.setScene(scene);
        stage.show();
    }
}
