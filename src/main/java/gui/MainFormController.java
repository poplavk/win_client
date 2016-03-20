package gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;

public class MainFormController {
    @FXML
    private ScrollPane scrollPaneResult;
    @FXML
    private GridPane gridPaneShortSettings;
    private GridPane gridPane = new GridPane();
    @FXML
    private BorderPane borderPane;
    @FXML
    private VBox vBoxSlider;
    private Label labelSlider = new Label();
    public void initialize() {
        getvBoxSlider();
        getScrollPaneResult();
    }
    public GridPane getGridPaneShortSettings() {
        gridPaneShortSettings.getChildren().addAll(getvBoxSlider());
        return gridPaneShortSettings;
    }
    public VBox getvBoxSlider() {
        Label label = new Label("Slider:");
        Slider slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setValue(80);
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
                labelSlider.setText(String.format("%.0f", new_val));
            }
        });
        vBoxSlider.getChildren().addAll(label,slider, labelSlider);
        return vBoxSlider;
    }
    public ScrollPane getScrollPaneResult() {
        for(int i = 0; i < 30; i++  ) {
            //VBox vBox = getBlock();
            PersonDescriptor personDescriptor = new PersonDescriptor();
            gridPane.add(personDescriptor,0,i);
        }
        scrollPaneResult.setContent(gridPane);
        return scrollPaneResult;
    }

    public void handleMenuItemNewFind(ActionEvent actionEvent) {

    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
        Stage stage = new Stage();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("settingForm.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 400, 400);
        stage.setTitle("Настройки");
        stage.setScene(scene);
        stage.show();
    }

    public void handleMenuItemExit(ActionEvent actionEvent) {
    }
}