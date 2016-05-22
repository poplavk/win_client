package gui;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class LoginFormController {
    @FXML
    private JFXTextField textFieldLogin;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton buttonLogin;

    public void initialize() {
        setupTextField();
    }

    public void setupTextField() {
        textFieldLogin.setPromptText("Введите логин");
        passwordField.setPromptText("Введите пароль");

        RequiredFieldValidator validatorLogin = new RequiredFieldValidator();
        RequiredFieldValidator validatorPassword = new RequiredFieldValidator();

        validatorLogin.setMessage("xox");
        validatorPassword.setMessage("oxo");

//        validatorLogin.setIcon(new Icon(AwesomeIcon.WARNING,"2em",";","error"));
        textFieldLogin.getValidators().add(validatorLogin);
//        textFieldLogin.focusedProperty().addListener((o,oldVal,newVal)->{
//            if(!newVal) textFieldLogin.validate();
//        });
        passwordField.getValidators().add(validatorPassword);
//        passwordField.focusedProperty().addListener((o,oldVal,newVal)->{
//            if(!newVal) passwordField.validate();
//        });
    }

    public void handleButtonLogin(ActionEvent actionEvent) {
        if ((textFieldLogin.getText().length() != 0) && (passwordField.getText().length() != 0)) {
            Stage s = (Stage) buttonLogin.getScene().getWindow();
            s.close();
            Stage stage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("mainForm.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("Pry 1.0");
            stage.setScene(scene);
            stage.getIcons().add(new Image("icon.png"));
            stage.show();
        }
        else {
            if (textFieldLogin.getText().length() == 0) textFieldLogin.validate();
            if (passwordField.getText().length() == 0) passwordField.validate();
        }
    }
}
