package gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SubscriptionDescriptor {
    private String subscriptionName;


    public SubscriptionDescriptor(String name) {
        subscriptionName = new String(name);
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

}
