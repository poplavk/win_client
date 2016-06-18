package gui;

import aleksey2093.GetFriendsLastResult;
import aleksey2093.ListenResultFromServer;
import aleksey2093.RequestFriendList;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import delov.MyImage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainFormController {
    @FXML
    private ScrollPane scrollPaneSubscription;
    @FXML
    private GridPane gridPaneShortSettings;
    @FXML
    private JFXSlider sliderCount;
    @FXML
    private JFXSlider sliderQuality;
    @FXML
    private GridPane gridPane;// = new GridPane();
    @FXML
    private BorderPane borderPane;
    @FXML
    MenuBar menuBarMain;

    @FXML
    private MyImage image;
    @FXML
    private JFXButton MakeF;

    private Mat tmp;
    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;


    public void initialize() {
        new Thread(new Runnable() {
            public void run() {
                getScrollPaneResult();
            }
        }).start();
        new ListenResultFromServer().startListenThread(this);
//        this.capture = new VideoCapture(0);
    }

    public  MainFormController() {
        image = new MyImage();
        gridPane = new GridPane();
    }


    public ScrollPane getScrollPaneResult() {
//        for (int i = 0; i < 30; i++) {
//            SubscriptionDescriptor subscriptionDescriptor = new SubscriptionDescriptor("Подписка  " + i);
//            gridPane.add(takeSubscriptionButton(subscriptionDescriptor), 0, i);
//        }
        RequestFriendList requestFriendList = new RequestFriendList();
        ArrayList<String> list = requestFriendList.getListFriends();
        Platform.runLater(() -> {
            for (int i = 0; i < list.size(); i++)

            {
                SubscriptionDescriptor subscriptionDescriptor = new SubscriptionDescriptor(list.get(i));
                gridPane.add(takeSubscriptionButton(subscriptionDescriptor), 0, i);
                //Точка возле подписки, для редактированию пользователю недоступна
                RadioButton radioButton = new RadioButton();
                radioButton.setDisable(true);
                radioButton.setSelected(true);
                radioButton.getStyleClass().add("radio-button-new-data");
                gridPane.add(radioButton, 1, i);
            }
            scrollPaneSubscription.setContent(gridPane);
        });
        return scrollPaneSubscription;
    }


    private JFXButton takeSubscriptionButton(SubscriptionDescriptor subscriptionDescriptor) {
        final JFXButton buttonSubscription = new JFXButton();
        buttonSubscription.setText(subscriptionDescriptor.getSubscriptionName());
        buttonSubscription.getStyleClass().add("button-raised-sub");
        buttonSubscription.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent a) {
                GetFriendsLastResult getFriendsLastResult = new GetFriendsLastResult();
                getFriendsLastResult.getLastResultThread(buttonSubscription.getText());
                //Сброс зеленой точки по нажаию на подписку
                Button pushedButton = (Button) a.getTarget();
                int row = GridPane.getRowIndex(pushedButton);
                int col = GridPane.getColumnIndex(pushedButton) + 1;
                RadioButton radioButton = getNodeFromGridPane(col, row);
                radioButton.setSelected(false);
            }
        });
        return buttonSubscription;
    }

    public void changeRadioButton(String name) {
        for (Node node : gridPane.getChildren()) {
            if (Objects.equals(((JFXButton) node).getText(), name))
                getNodeFromGridPane(GridPane.getColumnIndex(node) + 1, GridPane.getRowIndex(node)).setSelected(true);
        }
    }

    private RadioButton getNodeFromGridPane(int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return (RadioButton) node;
            }
        }
        return null;
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
        stage.setResizable(false);
        stage.getIcons().add(new Image("icon.png"));
        stage.show();
    }

    public void handleMenuItemExit(ActionEvent actionEvent) {
        Stage stage = (Stage) menuBarMain.getScene().getWindow();
        stage.close();
    }

    public void handleRadioButtonCount(ActionEvent actionEvent) {
        sliderQuality.setDisable(true);
        sliderCount.setDisable(false);
    }

    public void handleRadioButtonQuality(ActionEvent actionEvent) {
        sliderQuality.setDisable(false);
        sliderCount.setDisable(true);
    }

    /**
     * The action triggered by pushing the button on the GUI
     */
    public void makeFoto(ActionEvent actionEvent) {
        this.capture = new VideoCapture(0);
        // set a fixed width for the frame
        image.setFitWidth(400);
        // preserve image ratio
        image.setPreserveRatio(true);

        if (!this.cameraActive)
        {
            // start the video capture
            this.capture.open(0);
            //sleep(1000);

            // is the video stream available?
            if (this.capture.isOpened())
            {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    public void run()
                    {
                        Image imageToShow = image.grabFrame(capture);
                        image.setImage(imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
                this.MakeF.setText("Сделать снимок");
            }
            else
            {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        }
        else
        {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.MakeF.setText("Сделать фото");

            // stop the timer
            try
            {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e)
            {
                // log the exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }

            // release the camera
            this.capture.release();
            // save mat of last frame
            this.tmp = image.getMapTmp();
        }
    }

    public void loadFoto (ActionEvent actionEvent) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            tmp = Imgcodecs.imread(file.toURI().getPath().substring(1));
            tmp = image.detectAndDisplay(tmp);
            image.setImage(image.mat2Image(tmp));//new Image(file.toURI().toString()));
            image.setFitWidth(400);
            image.setPreserveRatio(true);
            image.setSmooth(true);
            image.setCache(true);
        }
    }

    public void originalFoto (ActionEvent actionEvent) {
        image.setNullMapIm();
        image.setImage(image.mat2Image(tmp));//new Image(file.toURI().toString()));
        image.setFitWidth(400);
        image.setPreserveRatio(true);
        image.setSmooth(true);
        image.setCache(true);
    }

    public void handleMenuSaveImage (ActionEvent actionEvent) {
        FileChooser filesave = new FileChooser();
        filesave.setTitle("Сохранить изображение");
        filesave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File JPEG","*.jpg"),
                new FileChooser.ExtensionFilter("File PNG","*.png"),
                new FileChooser.ExtensionFilter("File BMP","*.bmp"));

        File file = filesave.showSaveDialog(null);
        if (file != null) {
            if (image.getMapIm() == null)
                Imgcodecs.imwrite(file.getPath(), tmp);
            else
                Imgcodecs.imwrite(file.getPath(), image.getMapIm () );
        }
    }
}
