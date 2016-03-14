package gui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PersonDescriptor extends HBox {
    private Image photo;
    private Label name;
    private Label city;
    private Label birthday;

    public PersonDescriptor(/**/) {
        photo = new Image("test2.jpg");
        name = new Label("Вася Пупкин");
        birthday = new Label("13 марта");
        city = new Label("Москва");

        Label nameLabel = new Label("Фамилия Имя:");
        Label birthdayLabel = new Label("Дата рождения:");
        Label cityLabel = new Label("Город:");

        ImageView photoView = new ImageView(photo);

        VBox vBoxLabel = new VBox();
        VBox vBoxInfo = new VBox();


        vBoxLabel.getChildren().addAll(nameLabel,birthdayLabel,cityLabel);
        vBoxInfo.getChildren().addAll(name,birthday,city);
        HBox hBoxInfo = new HBox(photoView, vBoxLabel, vBoxInfo);

        hBoxInfo.getStylesheets().add("fxml.css");
        this.getChildren().addAll(hBoxInfo);
    }
}
