package gui;


import aleksey2093.GiveMeSettings;
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
        textFieldLogin.setPromptText("Логин");
        passwordField.setPromptText("Пароль");

        RequiredFieldValidator validatorLogin = new RequiredFieldValidator();
        RequiredFieldValidator validatorPassword = new RequiredFieldValidator();

        validatorLogin.setMessage("Ввведите логин");
        validatorPassword.setMessage("Введите пароль");

        GiveMeSettings settings = new GiveMeSettings();
        String login = settings.getLpkString(true);
        String pass = settings.getLpkString(false);
        if (login != null)
            textFieldLogin.setText(login);
        if (pass != null)
            passwordField.setText(pass);
        textFieldLogin.getValidators().add(validatorLogin);
        passwordField.getValidators().add(validatorPassword);
//        validatorLogin.setIcon(new Icon(AwesomeIcon.WARNING,"2em",";","error"));
        //textFieldLogin.getValidators().add(validatorLogin);
//        textFieldLogin.focusedProperty().addListener((o,oldVal,newVal)->{
//            if(!newVal) textFieldLogin.validate();
//        });
        //passwordField.getValidators().add(validatorPassword);
//        passwordField.focusedProperty().addListener((o,oldVal,newVal)->{
//            if(!newVal) passwordField.validate();
//        });

    }

    public void handleButtonLogin(ActionEvent actionEvent) {
        if ((textFieldLogin.getText().length() != 0) && (passwordField.getText().length() != 0)) {
            GiveMeSettings settings = new GiveMeSettings();
            settings.setLpkString(true,textFieldLogin.getText());
            settings.setLpkString(false,passwordField.getText());

            Stage s = (Stage) buttonLogin.getScene().getWindow();
            s.close();
            Stage stage = new Stage();
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getClassLoader().getResource("mainForm.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Scene scene = new Scene(root, 1100, 600);
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
