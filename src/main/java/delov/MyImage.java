package delov;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;


import java.io.ByteArrayInputStream;


public class MyImage extends ImageView implements EventHandler <MouseEvent> {

    private double current_x;
    private double current_y;

    private CascadeClassifier faceCascade;

    Mat tmp;
    Mat tmp1;
    Mat subtmp;
    MatOfRect faces;

    public MyImage() {
        current_x = 0;
        current_y = 0;
        setOnMouseClicked(this);
        faceCascade = new CascadeClassifier();
        faceCascade.load("./resources/haarcascades/haarcascade_frontalface_alt.xml");
    }

    public void setMapIm(Mat tmp, MatOfRect faces) {

        this.tmp=tmp;
        this.faces=faces;
    }

    public Mat getMapIm() {

        return subtmp;
    }

    public Mat getMapTmp() {

        return tmp1;
    }

    public void setNullMapIm() {

        subtmp=null;
    }

    public void handle(MouseEvent e) {
        if (e.getEventType() == MouseEvent.MOUSE_CLICKED) {
            current_x = e.getX();
            current_y = e.getY();
            System.out.println(current_x + " " + current_y);

            for (Rect rect : faces.toArray()) {
                rect.y = rect.y / 10 * 8;
                rect.height = rect.height / 10 * 14;
                double a = rect.x / ((double)tmp.width()/ 400);
                double b = rect.y / ((double)tmp.width()/ 400);
                double c = rect.x / ((double)tmp.width()/400) + rect.width / ((double)tmp.width() / 400);
                double d = rect.y / ((double)tmp.width()/ 400) + rect.height / ((double)tmp.width() / 400);
                if ((current_x >= a) && (current_y >= b) && (current_x <= c) && (current_y <= d)) {
                    subtmp=tmp.submat(rect);
                    this.setImage(mat2Image(tmp.submat(rect)));//new Image(file.toURI().toString()));
                    this.setFitWidth(400);
                    this.setPreserveRatio(true);
                    this.setSmooth(true);
                    this.setCache(true);
                }
            }

        }
    }

    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    public  Image grabFrame(VideoCapture capture)
    {
        // init everything
        Image imageToShow = null;
        Mat frame = new Mat();

        // check if the capture is open
        if (capture.isOpened())
        {
            try
            {
                // read the current frame
                capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty())
                {
                    // face detection
                    tmp1=frame;
                    tmp1 = this.detectAndDisplay(tmp1);

                    // convert the Mat object (OpenCV) to Image (JavaFX)
                    imageToShow = mat2Image(frame);
                }

            }
            catch (Exception e)
            {
                // log the (full) error
                System.err.println("ERROR: " + e);
            }
        }

        return imageToShow;
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame
     *            the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */

    public Image mat2Image(Mat frame)
    {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    public  Mat detectAndDisplay(Mat tmp)  {

        MatOfRect faces = new MatOfRect();

        // detect faces
        this.faceCascade.detectMultiScale(tmp,faces);
        this.setMapIm(tmp, faces);
        // each rectangle in faces is a face: draw them!
        for (Rect rect : faces.toArray()) {
            rect.y = rect.y / 10 * 8;
            rect.height = rect.height / 10 * 14;
            Imgproc.rectangle(tmp, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        return tmp;
    }
}