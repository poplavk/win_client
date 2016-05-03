package gui;

import aleksey2093.FriendSendResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class MainFormController {
    @FXML
    private ScrollPane scrollPaneSubscription;
    @FXML
    private GridPane gridPaneShortSettings;
    private GridPane gridPane = new GridPane();
    @FXML
    private BorderPane borderPane;
    private VBox vBoxSlider = new VBox();
    private Label labelSlider = new Label();
    JFXSlider slider = new JFXSlider();
    @FXML
    MenuBar menuBarMain;

    public void initialize() {
        getGridPaneShortSettings();
        getScrollPaneResult();
    }

    public GridPane getGridPaneShortSettings() {
        gridPaneShortSettings.add(getvBoxSlider(), 2, 2);
        return gridPaneShortSettings;
    }

    public VBox getvBoxSlider() {
        Label label = new Label();
        HBox hBox = new HBox();
        slider.setMin(1);
        slider.setMax(50);
        slider.setValue(50);
//        slider.valueProperty().addListener(new ChangeListener<Number>() {
//            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
//                labelSlider.setText(String.format("%.0f", new_val));
//            }
//        });
        hBox.getChildren().addAll(slider, labelSlider);
        vBoxSlider.getChildren().addAll(label, hBox);
        return vBoxSlider;
    }

    public void updateVBoxSliderQuality() {
        slider.setMin(30);
        slider.setMax(100);
        slider.setValue(80);
    }

    public void updateVBoxSliderCount() {
        slider.setMin(1);
        slider.setMax(50);
        slider.setValue(50);
    }

    public ScrollPane getScrollPaneResult() {
        for (int i = 0; i < 30; i++) {
            SubscriptionDescriptor subscriptionDescriptor = new SubscriptionDescriptor("Подписка  " + i);
            gridPane.add(takeSubscriptionButton(subscriptionDescriptor), 0, i);
        }
      /*  FriendSendResult friendSendResult = new FriendSendResult();
        friendSendResult.give_me_please_friends();
        ArrayList<String> list = friendSendResult.getListFriends();

        for (int i = 0; i < list.toArray().length; i++) {
            SubscriptionDescriptor subscriptionDescriptor = new SubscriptionDescriptor(list.get(i));//("Подписка  " + i);
            gridPane.add(takeSubscriptionButton(subscriptionDescriptor), 0, i);
        }*/
        scrollPaneSubscription.setContent(gridPane);
        return scrollPaneSubscription;
    }


    private JFXButton takeSubscriptionButton(SubscriptionDescriptor subscriptionDescriptor) {
        final JFXButton buttonSubscription = new JFXButton();
        buttonSubscription.setText(subscriptionDescriptor.getSubscriptionName());
        buttonSubscription.getStyleClass().add("button-raised-sub");
        buttonSubscription.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                Stage stage = new Stage();
                Parent root = null;
                try {
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("resultsForm.fxml"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Scene scene = new Scene(root, 600, 790);
                stage.setTitle("Результаты поиска для подписки на " + buttonSubscription.getText());
                stage.setScene(scene);
                stage.show();
            };
        });
        return buttonSubscription;
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
        Stage stage = (Stage) menuBarMain.getScene().getWindow();
        stage.close();
    }

    public void handleRadioButtonCount(ActionEvent actionEvent) {
        updateVBoxSliderCount();
    }

    public void handleRadioButtonQuality(ActionEvent actionEvent) {
        updateVBoxSliderQuality();
    }
}
