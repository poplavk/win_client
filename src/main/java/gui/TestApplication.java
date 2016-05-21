package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;

import java.io.File;


public class TestApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // load the FXML resource
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("mainForm.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Pry 1.0");

        stage.setScene(scene);
        stage.getIcons().add(new Image("icon.png"));
        stage.show();

        //MainFormController controller = loader.getController();
        //controller.init();
    }

    public static void main(String[] args) {
        // load the native OpenCV library
        System.load(System.getProperty("user.dir") + File.separator + "lib" + File.separator + Core.NATIVE_LIBRARY_NAME + ".dll");

        launch(args);
    }
}
