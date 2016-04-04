package gui;



import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;


public class ResultsFormController {
    @FXML
    private ScrollPane scrollPaneResults;

    private GridPane gridPane = new GridPane();

    public void initialize() {
        getScrollPaneResult();
    }

    public ScrollPane getScrollPaneResult() {
        for(int i = 0; i < 30; i++  ) {
            PersonDescriptor personDescriptor = new PersonDescriptor();
            gridPane.add(personDescriptor,0,i);
        }
        scrollPaneResults.setContent(gridPane);
        return scrollPaneResults;
    }

}

