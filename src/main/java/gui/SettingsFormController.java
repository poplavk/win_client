package gui;

import aleksey2093.GiveMeSettings;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

//класс-контроллер для окна настроек

public class SettingsFormController extends SettingsDescriptor {
    @FXML
    JFXComboBox choiceBoxEncryptType;
//    @FXML
//    CheckBox checkBoxVK;
//    @FXML
//    CheckBox checkBoxFB;
    @FXML
    JFXRadioButton radioButtonVK;
    @FXML
    JFXRadioButton radioButtonFB;
    @FXML
    JFXCheckBox checkBoxPhoto;
    @FXML
    JFXCheckBox checkBoxName;
    @FXML
    JFXCheckBox checkBoxBirthday;
    @FXML
    JFXCheckBox checkBoxCity;
    @FXML
    JFXCheckBox checkBoxJob;
    @FXML
    JFXCheckBox checkBoxPhone;
    @FXML
    JFXButton buttonSaveSettings;
    @FXML
    JFXButton buttonCancelSettings;

    SettingsDescriptor settingsDescriptor = new SettingsDescriptor();

    public void initialize() {
        getChoiceBoxEncryptType();
        getAllSettings();
    }

    public JFXComboBox getChoiceBoxEncryptType() {
        choiceBoxEncryptType.setItems(FXCollections.observableArrayList("AES", "RSA", "MD5"));
        switch (settingsDescriptor.getEncryptNow())
        {
            case 1:
                choiceBoxEncryptType.setValue("AES");
                break;
            case 2:
                choiceBoxEncryptType.setValue("RSA");
                break;
            case 3:
                choiceBoxEncryptType.setValue("MD5");
                break;
        }
        return choiceBoxEncryptType;
    }

    public void getAllSettings() {
        radioButtonVK.setSelected(settingsDescriptor.getSocialNetwork());
        radioButtonFB.setSelected(!settingsDescriptor.getSocialNetwork());
        checkBoxPhoto.setSelected(settingsDescriptor.getPhoto());
        checkBoxName.setSelected(settingsDescriptor.getFio());
        checkBoxBirthday.setSelected(settingsDescriptor.getBithDay());
        checkBoxCity.setSelected(settingsDescriptor.getCity());
        checkBoxJob.setSelected(settingsDescriptor.getWork());
        checkBoxPhone.setSelected(settingsDescriptor.getPhone());
    }

    public void handleCancelSettings(ActionEvent actionEvent) {
        getAllSettings();
        Stage stage = (Stage) buttonCancelSettings.getScene().getWindow();
        stage.close();
    }

    public void handleSaveSettings(ActionEvent actionEvent) {
        Thread thread = new Thread(() -> {
            new GiveMeSettings().setSaveSettingWindow(new boolean[]{
//                        checkBoxVK.isSelected(),
//                        checkBoxFB.isSelected(),
                    radioButtonVK.isSelected(),
//                        radioButtonFB.isSelected(),
                    checkBoxPhoto.isSelected(),
                    checkBoxName.isSelected(),
                    checkBoxBirthday.isSelected(),
                    checkBoxCity.isSelected(),
                    checkBoxJob.isSelected(),
                    checkBoxPhone.isSelected(),
                    }, choiceBoxEncryptType.getValue().toString()
            );
        });
        thread.setName("Сохранение настроек");
        thread.start();
        Stage stage = (Stage) buttonCancelSettings.getScene().getWindow();
        stage.close();
    }
}
