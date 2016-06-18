package gui;

import com.jfoenix.controls.JFXButton;
import hackIntoSN.PersonInfo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

//класс-контроллер для окна с результатами

public class ResultsFormController {

    private ArrayList<PersonInfo> personInfoArrayList;

    public void setParametr(ArrayList<PersonInfo> arrayList)
    {
        personInfoArrayList = arrayList;
    }

    @FXML
    private ScrollPane scrollPaneResults;
    private GridPane gridPane = new GridPane();

    public void initialize() {
        //getScrollPaneResult();
    }
//    метод для отображения результатов
    public ScrollPane getScrollPaneResult() throws URISyntaxException {
        if (personInfoArrayList != null && personInfoArrayList.size() > 0) {
            System.out.println("Загрузка всего");
            gridPane.setAlignment(Pos.CENTER);
            int row = 0;
            for (int i = 0; i < personInfoArrayList.size(); i++) {
                int sum_rows = 0;
                Image photo = personInfoArrayList.get(i).image;
                ImageView photoView = new ImageView();
                if (photo != null) {
                    photoView.setImage(photo);
                    photoView.setPreserveRatio(true);
                    photoView.setFitWidth(200);
//                    photoView.setFitHeight(200);
                    gridPane.add(photoView, 0, row);
                }
                if ((personInfoArrayList.get(i).first_name != null) && (personInfoArrayList.get(i).last_name != null)) {
                    gridPane.add(new Label("Фамилия Имя:"), 1, row);
                    gridPane.add(new Label(personInfoArrayList.get(i).last_name + " " + personInfoArrayList.get(i).first_name), 2, row);
                    row++;
                    sum_rows++;
                }
                if ((personInfoArrayList.get(i).birthday != null) && (!personInfoArrayList.get(i).birthday.equals(""))) {
                    gridPane.add(new Label("Дата рождения:"), 1, row);
                    //*************
                    gridPane.add(new Label(personInfoArrayList.get(i).birthday), 2, row);
                    row++;
                    sum_rows++;
                }
                if ((personInfoArrayList.get(i).city != null) && (!personInfoArrayList.get(i).city.equals(""))) {
                    gridPane.add(new Label("Город:"), 1, row);
                    if (personInfoArrayList.get(i).country != null){
                        gridPane.add(new Label(personInfoArrayList.get(i).country + ", " + personInfoArrayList.get(i).city), 2, row);
                    }
                    else {
                        gridPane.add(new Label(personInfoArrayList.get(i).city), 2, row);
                    }
                    row++;
                    sum_rows++;
                }
                if ((personInfoArrayList.get(i).phone != null) && (!personInfoArrayList.get(i).phone.equals(""))) {
                    gridPane.add(new Label("Телефон:"), 1, row);
                    gridPane.add(new Label(personInfoArrayList.get(i).phone), 2, row);
                    row++;
                    sum_rows++;
                }
                if ((personInfoArrayList.get(i).occupation != null) && (!personInfoArrayList.get(i).occupation.equals(""))) {
                    gridPane.add(new Label("Место работы:"), 1, row);
                    gridPane.add(new Label(personInfoArrayList.get(i).occupation), 2, row);
                    row++;
                    sum_rows++;
                }
                if (personInfoArrayList.get(i).link != null) {
                    JFXButton linkButton = new JFXButton();
                    linkButton.setText("Перейти на страницу");
                    linkButton.getStyleClass().add("button-raised");
                    URI url = new URI("https://vk.com/" + personInfoArrayList.get(i).link);
                    linkButton.setOnAction((ActionEvent e) -> {
                        if (Desktop.isDesktopSupported()) {
                            Desktop desktop = Desktop.getDesktop();
                            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                try {
                                    desktop.browse(url);
                                } catch (IOException e1) {
                                e1.printStackTrace();
                                }
                            }
                        }
                    });
                    gridPane.add(linkButton, 1, row);
                    gridPane.setColumnSpan(linkButton, 2);
                    row++;
                    sum_rows++;
                }
                if (photoView != null) {
                    gridPane.setRowSpan(photoView, sum_rows);
                }
//                PersonDescriptor personDescriptor = new PersonDescriptor(personInfoArrayList.get(i));
//                gridPane.add(personDescriptor, 0, i);
            }
        } else {
            for (int i = 0; i < 30; i++) {
                PersonDescriptor personDescriptor = new PersonDescriptor(null);
                gridPane.add(personDescriptor, 0, i);
            }
        }
        scrollPaneResults.setContent(gridPane);
        return scrollPaneResults;
    }

}

