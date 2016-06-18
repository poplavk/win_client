package aleksey2093;

import encrypt.AES;
import encrypt.MD5;
import encrypt.RSA;
import gui.AlertPry;
import gui.ResultsFormController;
import hackIntoSN.GetSomePrivateData;
import hackIntoSN.PersonInfo;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс загрузки последнего результата пользователя
 */
public class GetFriendsLastResult {

    /**
     * Загрузка последнего результата подписчика в отдельном потока
     * @param friend имя подписчика
     */
    public void getLastResultThread(final String friend)
    {
        new Thread(() -> {
            getLastResult(friend);
        }).start();
    }

    /**
     * Загрузка последнего результата подписчика
     * @param friend имя подписчика
     */
    private void getLastResult(String friend) {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        Socket socket = getSocket(giveMeSettings);
        if (socket == null)
            return;
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            if (sendRequestToServer(giveMeSettings, outputStream, friend)) {
                waitServerMsg(giveMeSettings,inputStream, friend);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!socket.isClosed())
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /**
     * Подклечения сокета сервера подписок
     * @param giveMeSettings указатель на класс настроек
     * @return сокет сервера подписок
     */
    private Socket getSocket(GiveMeSettings giveMeSettings) {
        int err = 0;
        while (err < 3)
            try {
                return new Socket(giveMeSettings.getServerName(2), giveMeSettings.getServerPort(2));
            } catch (IOException e) {
                e.printStackTrace();
                err++;
            }
        return null;
    }

    /**
     * Отправка запроса на сервер
     * @param giveMeSettings указатель на класс настроек
     * @param outputStream указатель на выходной поток
     * @param friend имя подписчика
     * @return true в случае успеха, false - неудачи
     */
    private boolean sendRequestToServer(GiveMeSettings giveMeSettings, DataOutputStream outputStream, String friend) {
        try {
            byte[] login = giveMeSettings.getLpk(true);
            byte[] pass = giveMeSettings.getLpk(false);
            //байт шифр, байт тип, байт длинны логина, логин, байт длинны пароля, пароль, логин друга (чей результат будем смотреть)
            byte[] msg = new byte[1 + 1 + 1 + login.length + 1 + pass.length + 1 + friend.getBytes().length];
            msg[0] = giveMeSettings.getEncryption();
            msg[1] = 3;
            msg[2] = (byte) login.length;
            int j = 3;
            for (int i = 0; i < login.length; i++, j++)
                msg[j] = login[i];
            msg[j] = (byte) pass.length;
            j++;
            for (int i = 0; i < pass.length; i++, j++)
                msg[j] = pass[i];
            msg[j] = (byte)friend.getBytes().length; j++;
            for (int i = 0; i < friend.getBytes().length; i++, j++)
                msg[j] = friend.getBytes()[i];
            msg = giveMeSettings.getEncryptMsg(msg);
            outputStream.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    /**
     * Ожидание ответа от сервера
     * @param inputStream входной поток
     * @param friend имя подписчика
     */
    private void waitServerMsg(GiveMeSettings giveMeSettings, DataInputStream inputStream, String friend) {
        byte[] msg = new byte[1];
        int len = 0;
        while (len <= 0) {
            try {
                msg = new byte[inputStream.available()];
                len = inputStream.read(msg);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        msg = giveMeSettings.getDecryptMsg(msg);
        if (msg[0] == (byte) -1) {
            return;
        } else if (msg[1] == (byte) 103) {
            showDialogInform(1, null);
            return;
        } else if (msg[1] != 3) {
            return;
        } else if (len <= 3) {
            showDialogInform(2, friend);
            return;
        }
        formationListLinks(msg, len, friend);
    }

    /**
     * Формирование результата и вывод на экран. Возможно необходимо для подсистемы отправки эталона.
     * @param msg байт массив с сервера
     * @param len длинна массива
     * @param login имя текущего пользователя
     */
    public void resultSendPhoto(byte[] msg, int len, String login) {
        formationListLinks(msg,len,login);
    }

    /**
     * Фомирование результата и вывод на экран.
     * @param msg байт массив с сервера
     * @param len длинна массива
     * @param friend имя подписчика
     */
    private void formationListLinks(byte[] msg, int len, String friend) {
        ArrayList<String> arrayList = new ArrayList<>();
        int i = 2;
        while (i < len) {
            int len_link = ByteBuffer.wrap(msg, i, 4).getInt();
            i += 4;
            try {
                String link = new String(msg, i, len_link, "UTF-8");
                link = getIdFromLink(link);
                if (link != null && link.length() != 0)
                    arrayList.add(link);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i += len_link;
        }
        GetSomePrivateData getSomePrivateData = new GetSomePrivateData();
        showWindowResult(getSomePrivateData.vkGet(arrayList),friend);
    }

    /**
     * Отображение окна результата
     * @param list Данные по результату из социальной сети
     * @param friend имя подписчика
     */
    private void showWindowResult(final ArrayList<PersonInfo> list, final String friend) {
        Platform.runLater(new Runnable() {
            public void run() {
                Stage stage = new Stage();
                Parent root = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resultsForm.fxml"));
                    root = loader.load();
                    ResultsFormController resultsFormController = loader.getController();
                    resultsFormController.setParametr(list);
                    try {
                        resultsFormController.getScrollPaneResult();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert root != null;
                Scene scene = new Scene(root, 600, 790);
                stage.setTitle("Результаты поиска для подписки на " + friend);
                stage.setScene(scene);
//                stage.setResizable(false);
                stage.getIcons().add(new Image("icon.png"));
                stage.show();
            }
        });
    }

    /**
     * Получение id пользователя из ссылки
     * @param link ссылка
     * @return id пользователя
     */
    private String getIdFromLink(String link) {
        String tmp = "";
        if (link.toCharArray()[link.length() - 1] == '/')
            tmp = link.substring(0, link.length() - 2);
        int i = link.length() - 1;
        while (i >= 0 && link.toCharArray()[i] != '/'){
            tmp = link.toCharArray()[i] + tmp;
            i--;
        }
        return tmp;
    }

    /**
     * Отображение диалогового окна с информцией
     * @param what номер диалового окна
     * @param friend имя подписчика (необязательно, можно оставить пустым)
     */
    private void showDialogInform(final int what, final String friend) {
        Platform.runLater(() -> {
            if (what == 1) {
                System.out.println("Неправильный логин или пароль.");
                AlertPry alert = new AlertPry("Ошибка входа", "Неправильный логин или пароль");
                alert.showAndWait();
            } else if (what == 2) {
                System.out.println("Последний результат " + friend + "пуст.");
                AlertPry alert = new AlertPry("Пустой результат", new String("Результат " + friend + " оказался пуст."));
                alert.showAndWait();
            } else if (what == 3) {
                System.out.println("Ошибка при подключении к серверу. Метод загрузки последнего результата.");
                AlertPry alert = new AlertPry("Ошибка подключения", new String("Ошибка при подключении к серверу. " +
                        "Проверьте свое подключение к интернету и повторите попытку."));
                alert.showAndWait();
            }
        });
    }
}
