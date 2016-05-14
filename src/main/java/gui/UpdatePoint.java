package gui;

import javafx.scene.image.Image;

public class UpdatePoint {
    private Image imagePoint;
    private boolean isUpdated;

    public UpdatePoint(boolean updated) {
        isUpdated = updated;
        imagePoint = new Image("test4.jpg");
//        if (isUpdated == true) {
//            imagePoint = new Image();
//        }
    }
}
