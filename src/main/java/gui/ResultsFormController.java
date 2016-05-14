package gui;



import hackIntoSN.PersonInfo;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;


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

    public ScrollPane getScrollPaneResult() {
        if (personInfoArrayList != null && personInfoArrayList.size() > 0) {
            System.out.println("Загрузка всего");
            for (int i = 0; i < personInfoArrayList.size(); i++) {
                PersonDescriptor personDescriptor = new PersonDescriptor(personInfoArrayList.get(i));
                gridPane.add(personDescriptor, 0, i);
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

