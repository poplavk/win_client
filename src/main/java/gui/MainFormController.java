package gui;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;

public class MainFormController {
    @FXML
    private ScrollPane scrollPaneResult;
    private GridPane gridPane = new GridPane();
    @FXML
    private BorderPane borderPane;
   // public HBox hBox = new HBox();
   // public HBox hBox2 = new HBox();
   // public VBox vBox = new VBox();
    public void initialize(){
        getScrollPaneResult();
    }
    public VBox getBlock(){
        Label label = new Label("label1");
        Label label2 = new Label("label2");
        TextField textField = new TextField();
        HBox hBox = new HBox();
        HBox hBox2 = new HBox();
        VBox vBox = new VBox();
        hBox.setSpacing(20);
        hBox.getChildren().addAll(label, label2);
        hBox2.getChildren().addAll(textField);
        vBox.setSpacing(15);
        vBox.getChildren().addAll(hBox, hBox2);
        return vBox;
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
}