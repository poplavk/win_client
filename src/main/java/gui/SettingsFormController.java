package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;

public class SettingsFormController {
    @FXML
    ChoiceBox choiceBoxEncryptType;

    public void initialize() {
        getChoiceBoxEncryptType();
    }

    public ChoiceBox getChoiceBoxEncryptType() {
        choiceBoxEncryptType.setItems(FXCollections.observableArrayList("AES", "RSA", "ГОСТ"));
        choiceBoxEncryptType.setValue("AES");
        return choiceBoxEncryptType;
    }
}
