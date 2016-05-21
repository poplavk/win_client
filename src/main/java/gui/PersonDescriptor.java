package gui;

import hackIntoSN.PersonInfo;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PersonDescriptor extends HBox {
    private Image photo;
    private Label name;
    private Label city;
    private Label birthday;

    public PersonDescriptor(PersonInfo personinfo/**/) {
//        new Image("test2.jpg");
//        new Label("Вася Пупкин");
//        new Label("13 марта");
//        new Label("Москва");
        photo = personinfo.image;
        name = new Label(personinfo.last_name + " " + personinfo.first_name);
        birthday = new Label(personinfo.birthday);
        city = new Label(personinfo.city);

        Label nameLabel = new Label("Фамилия Имя:");
        Label birthdayLabel = new Label("Дата рождения:");
        Label cityLabel = new Label("Город:");

        ImageView photoView = new ImageView(photo);
        photoView.setFitHeight(100);
        photoView.setFitWidth(100);

        VBox vBoxLabel = new VBox();
        VBox vBoxInfo = new VBox();

        vBoxLabel.getChildren().addAll(nameLabel,birthdayLabel,cityLabel);
        vBoxInfo.getChildren().addAll(name,birthday,city);
        HBox hBoxInfo = new HBox(photoView, vBoxLabel, vBoxInfo);

        hBoxInfo.getStylesheets().add("fxml.css");
        this.getChildren().addAll(hBoxInfo);
    }
}
