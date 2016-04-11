package gui;

import aleksey2093.GiveMeSettings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;

public class SettingsFormController extends SettingsDescriptor {
    @FXML
    ChoiceBox choiceBoxEncryptType;
    @FXML
    CheckBox checkBoxVK;
    @FXML
    CheckBox checkBoxFB;
    @FXML
    CheckBox checkBoxPhoto;
    @FXML
    CheckBox checkBoxName;
    @FXML
    CheckBox checkBoxBirthday;
    @FXML
    CheckBox checkBoxCity;
    @FXML
    CheckBox checkBoxJob;
    @FXML
    CheckBox checkBoxPhone;
    @FXML
    Button buttonSaveSettings;
    @FXML
    Button buttonCancel;

    SettingsDescriptor settingsDescriptor = new SettingsDescriptor();

    public void initialize() {
        getChoiceBoxEncryptType();
        getAllSettings();
    }

    public ChoiceBox getChoiceBoxEncryptType() {
        choiceBoxEncryptType.setItems(FXCollections.observableArrayList("AES", "RSA", "ГОСТ"));
        switch (settingsDescriptor.getEncryptNow())
        {
            case 1:
                choiceBoxEncryptType.setValue("AES");
                break;
            case 2:
                choiceBoxEncryptType.setValue("RSA");
                break;
            case 3:
                choiceBoxEncryptType.setValue("ГОСТ");
                break;
        }
        return choiceBoxEncryptType;
    }

    public void getAllSettings() {
        switch (settingsDescriptor.getSocialNetwork()) {
            case 0:
                checkBoxVK.setSelected(false);
                checkBoxFB.setSelected(false);
                break;
            case 1:
                checkBoxVK.setSelected(true);
                checkBoxFB.setSelected(false);
                break;
            case 2:
                checkBoxVK.setSelected(false);
                checkBoxFB.setSelected(true);
                break;
            case 3:
                checkBoxVK.setSelected(true);
                checkBoxFB.setSelected(true);
                break;
        }
        checkBoxPhoto.setSelected(settingsDescriptor.getPhoto());
        checkBoxName.setSelected(settingsDescriptor.getFio());
        checkBoxBirthday.setSelected(settingsDescriptor.getBithDay());
        checkBoxCity.setSelected(settingsDescriptor.getCity());
        checkBoxJob.setSelected(settingsDescriptor.getWork());
        checkBoxPhone.setSelected(settingsDescriptor.getPhone());
    }

    public void handleCancelSettings(ActionEvent actionEvent) {
        getAllSettings();
    }

    public void handleSaveSettings(ActionEvent actionEvent) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                new GiveMeSettings().setSaveSettingWindow(new boolean[]{
                        checkBoxVK.isSelected(),
                        checkBoxFB.isSelected(),
                        checkBoxPhoto.isSelected(),
                        checkBoxName.isSelected(),
                        checkBoxBirthday.isSelected(),
                        checkBoxCity.isSelected(),
                        checkBoxJob.isSelected(),
                        checkBoxPhone.isSelected(),
                        }, choiceBoxEncryptType.getValue().toString()
                );
            }
        });
        thread.setName("Сохранение настроек");
        thread.start();
    }
}
