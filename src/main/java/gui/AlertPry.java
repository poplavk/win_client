package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
// класс для красивых диалоговых окон
public class AlertPry {
    public Alert alert;

    public AlertPry(String messageInfo, String message) {
        alert = new Alert(Alert.AlertType.INFORMATION);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add("alert.css");

        alert.setTitle("Pry 1.0");
        alert.setHeaderText(messageInfo);
        alert.setContentText(message);
        alert.setGraphic(new ImageView(new Image("gentleman.png")));

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("icon.png"));
    }

    public void showAndWait() {
        alert.showAndWait();
    }
}
