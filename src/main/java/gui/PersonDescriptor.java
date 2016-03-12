package gui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class PersonDescriptor extends HBox {


    private Label name;
    private Label city;
    private Label birthday;

    public PersonDescriptor(/**/) {
        name = new Label("xo-xo");
        this.getChildren().addAll(name);
    }
}
