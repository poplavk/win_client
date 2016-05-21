package aleksey2093;

import gui.ResultsFormController;
import hackIntoSN.GetSomePrivateData;
import hackIntoSN.PersonInfo;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by aleks on 10.04.2016.
 */
public class GetFriendsLastResult {

    public void GetLastResultThread(final String friend)
    {
        new Thread(new Runnable() {
            public void run() {
                getLastResult(friend);
            }
        }).start();
    }
    //при выборе подписчика в списке возвращает ArrayList<String> содержащий id для передачи в подсистемы загрузки данных 
    public void getLastResult(String friend) {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        Socket socket = null;
        DataOutputStream outputStream;
        DataInputStream inputStream;
        try {
            socket = new Socket(giveMeSettings.getServerName(2), giveMeSettings.getServerPort(2));
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
                /* выключаем сокет прослушки, если он мешает (использует необходимый порт) */
            e.printStackTrace();
            return;
        }
        if (sendRequestToServer(giveMeSettings, outputStream, friend)) {
            waitServerMsg(inputStream, friend);
        }
        if (!socket.isClosed())
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private boolean sendRequestToServer(GiveMeSettings giveMeSettings, DataOutputStream outputStream, String friend) {
        try {
            byte[] login = giveMeSettings.getLpk((byte) 1);
            byte[] pass = giveMeSettings.getLpk((byte) 2);
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
            outputStream.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void waitServerMsg(DataInputStream inputStream, String friend) {
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
        //дешифруем полученное сообщение
        if (msg[1] == (byte) 103) {
            showDialogInform(true, null);
            return;
        } else if (msg[1] != 3) {
            return;
        } else if (len <= 3) {
            showDialogInform(false, friend);
            return;
        }
        formationListLinks(msg, len, friend);
    }

    private void formationListLinks(byte[] msg, int len, String friend) {
        ArrayList<String> arrayList = new ArrayList<String>();
        int i = 2;
        while (i < len) {
            int lenlink = ByteBuffer.wrap(msg, i, 4).getInt();
            i += 4;
            try {
                String link = new String(msg, i, lenlink, "UTF-8");
                link = getIdFromLink(link);
                if (link != null && link.length() != 0)
                    arrayList.add(link);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i += lenlink;
        }
        GetSomePrivateData getSomePrivateData = new GetSomePrivateData();
        showWindowResult(getSomePrivateData.vkGet(arrayList),friend);
    }

    private boolean showWindowResult(final ArrayList<PersonInfo> list, final String login) {
        Platform.runLater(new Runnable() {
            public void run() {
                Stage stage = new Stage();
                Parent root = null;
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("resultsForm.fxml"));
                    root = loader.load();
                    ResultsFormController resultsFormController = loader.<ResultsFormController>getController();
                    resultsFormController.setParametr(list);
                    resultsFormController.getScrollPaneResult();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Scene scene = new Scene(root, 600, 790);
                stage.setTitle("Результаты поиска для подписки на " + login);
                stage.setScene(scene);
//                stage.setResizable(false);
                stage.getIcons().add(new Image("icon.png"));
                stage.show();
            }
        });
        return true;
    }

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

    private void showDialogInform(final boolean what, final String string) {
        Platform.runLater(new Runnable() {
            public void run() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        if (what) {
            System.out.println("Неправильный логин или пароль.");
            alert.setHeaderText("Ошибка входа");
            alert.setContentText("Неправильный логин или пароль");
        } else {
            System.out.println("Последний результат " + string + "пуст");
            alert.setHeaderText("Пустой результат");
            alert.setContentText("Результат " + string + " оказался пуст");
        }
        alert.showAndWait();
            }
        });
    }
}
