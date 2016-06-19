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

/**
 * Класс для хранения и обработки изображения,
 * из которого получают эталон
 */
public class MyImage extends ImageView implements EventHandler <MouseEvent> {

    // текущие координаты позиции курсора мыши на экране приложения
    private double current_x;
    private double current_y;
    // реализация каскадного классификатора Хаара
    private CascadeClassifier faceCascade;

    // комплекснозначная матрица изображения
    Mat tmp;
    Mat tmp1;
    // комплекснозначная матрица изображения выбранного лица
    Mat subtmp;
    // массив комплекснозначных матрица для распознанных лиц
    MatOfRect faces;

    /**
     * Конструктор класса MyImage
     */
    public MyImage() {
        current_x = 0;
        current_y = 0;
        setOnMouseClicked(this);
        faceCascade = new CascadeClassifier();
        faceCascade.load("./resources/haarcascades/haarcascade_frontalface_alt.xml");
    }

    /**
     * Метод, устанавливающий в текущий класс следующие параметры:
     * @param tmp Mat матрица исходного изображения
     * @param faces массив Mat матриц распознанных лиц
     */
    public void setMapIm(Mat tmp, MatOfRect faces) {

        this.tmp=tmp;
        this.faces=faces;
    }

    /**
     * Метод, возвращающий Mat матрицу выбранного лица
     * @return subtmp Mat матрица выбранного лица
     */
    public Mat getMapIm() {

        return subtmp;
    }

    /**
     * Метод, возвращающий Mat матрицу исходного изображения
     * @return tmp1 Mat матрица исходного изображения
     */
    public Mat getMapTmp() {

        return tmp1;
    }

    /**
     * Метод, обнуляющий Mat матрицу изображения выбранного лица
     */
    public void setNullMapIm() {

        subtmp=null;
    }

    /**
     * Метод, срабатывающий по нажатию мыши на изображении.
     * Если курсор был внутри рамки с выделенным лицом, текущее изображение
     * заменяется на изображение с выделенным лицом
     * @param e событие мыши, которое нужно обработать
     */
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
                    this.setImage(mat2Image(tmp.submat(rect)));
                    this.setFitWidth(400);
                    this.setPreserveRatio(true);
                    this.setSmooth(true);
                    this.setCache(true);
                }
            }

        }
    }

    /**
     * Метод, который получает видеокадр из видеопотока, преобразует его в Mat матрицу,
     * обрабатывает её, и затем ковертирует её в изображение
     * @param capture кадр видеопотока
     * @return imageToShow - обработанное изображение, которое будет выведено на экран.
     */
    public  Image grabFrame(VideoCapture capture)
    {
        Image imageToShow = null;
        Mat frame = new Mat();

        if (capture.isOpened())
        {
            try
            {
                capture.read(frame);
                if (!frame.empty())
                {
                    tmp1=frame;
                    tmp1 = this.detectAndDisplay(tmp1);
                    imageToShow = mat2Image(frame);
                }

            }
            catch (Exception e)
            {
                System.err.println("ERROR: " + e);
            }
        }

        return imageToShow;
    }

    /**
     * Преобразование Mat объекта (OpenCV) в соответствующее изображение для JavaFX
     * @param frame представленный текущий кадр
     * @return Image созданное изображение
     */
    public Image mat2Image(Mat frame)
    {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    /**
     * Метод, который распознает на Mat объекте лица, выделяет их в рамку,
     * и возвращает обработанный Mat объект
     * @param tmp Mat матрица исходного изображения
     * @return tmp Mat матрица исходного изображения с выделенными лицами
     */
    public  Mat detectAndDisplay(Mat tmp)  {

        MatOfRect faces = new MatOfRect();

        this.faceCascade.detectMultiScale(tmp,faces);
        this.setMapIm(tmp, faces);
        for (Rect rect : faces.toArray()) {
            rect.y = rect.y / 10 * 8;
            rect.height = rect.height / 10 * 14;
            Imgproc.rectangle(tmp, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        return tmp;
    }
}
