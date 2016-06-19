package aleksey2093;

import gui.AlertPry;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton.ButtonType;


public class RequestFriendList {

    private ArrayList<String> listfrends = new ArrayList<>();


    public ArrayList<String> getListFriends() {
        boolean res = downloadListFriends();
        if (res) {
            System.out.println("Подписки получены и сейчас будут выведены на экран");
            return listfrends;
        } else {
            System.out.println("Подписки не получены");
            return new ArrayList<>();
        }
    }

    private boolean downloadListFriends() {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        Socket socket = getSocket(giveMeSettings);
        if (socket == null) {
            errSocket = true;
            return false;
        }
        try {
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            if (sendMsgToServer(giveMeSettings, outputStream))
                if (readMsgFromServer(giveMeSettings, inputStream))
                    return true;
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean errAuth = false;
    private boolean errSocket = false;

    public boolean isErrAuth() { return errAuth; }
    public boolean isErrSocket() { return errSocket; }

    private boolean readMsgFromServer(GiveMeSettings giveMeSettings, DataInputStream inputStream) {
        int len = 0, err = 0;
        byte[] msg = new byte[1];
        try {
            while (len == 0) {
                msg = new byte[inputStream.available()];
                len = inputStream.read(msg);
                if (err > 10)
                {
                    showDialogInformation(3);
                    errSocket = true;
                    return false;
                } else
                try {
                    err++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //дешифруем
            msg = giveMeSettings.getDecryptMsg(msg);
            if (msg[0] == (byte)-1) {
                return false;
            } else if (msg[1] == (byte) 101) {
                errAuth = true;
                showDialogInformation(1);
                return false;
            } else if (msg[1] != (byte) 1) {
                System.out.println("Получили неправильный ответ с сервера в ответ на запрос подписок. Тип: " + msg[1]);
                return false;
            }
            System.out.println("Получен ответ на запрос подписок. Начинается обработка данных.");
            if (!formationListFriends(msg, len))
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean formationListFriends(byte[] msg, int len) {
        int j = 2;
        if (len <= j) {
            System.out.println("Метод подписок: Подписчиков нет");
            return false;
        }
        ArrayList<String> stringArrayList = new ArrayList<>();
        while (j < len) {
            /* ключ пока нигде не используется, поэтому просто будем собирать, но не хранить
            * как вариант можно выводить его в списке подписок и по клику отправлять его
            * на сервер, а не строковый логин */
            int key = java.nio.ByteBuffer.wrap(msg, j, 4).getInt();
            j += 4;
            int len_login = msg[j];
            j++;
            try {
                String tess = new String(msg, j, len_login, "UTF-8");
                stringArrayList.add(tess);
            } catch (UnsupportedEncodingException e) {
                System.out.println("Ошибка обработки имени подписчика в методе загрузки подписок");
                e.printStackTrace();
            }
            j += len_login;
        }
        listfrends = stringArrayList;
        return listfrends.size() != 0;
    }

    private boolean sendMsgToServer(GiveMeSettings giveMeSettings, DataOutputStream outputStream) {
        byte[] login = giveMeSettings.getLpk(true);
        byte[] pass = giveMeSettings.getLpk(false);
        byte[] message_byte = new byte[1 + 1 + 1 + login.length + 1 + pass.length];
        int j = 0;
        try {
            message_byte[j] = giveMeSettings.getEncryption();
            j++;
            message_byte[j] = 1;
            j++;
            message_byte[j] = (byte) login.length;
            j++;
            for (int i = 0; i < login.length; i++, j++) {
                message_byte[j] = login[i];
            }
            message_byte[j] = (byte) pass.length;
            j++;
            for (int i = 0; i < pass.length; i++, j++) {
                message_byte[j] = pass[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        try {
            message_byte = giveMeSettings.getEncryptMsg(message_byte);
            outputStream.write(message_byte);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        System.out.println("Отправлен запрос на получение подписок");
        return true;
    }

    private Socket getSocket(GiveMeSettings giveMeSettings) {
        int err = 0;
        while (true) {
            try {
                return new Socket(giveMeSettings.getServerName(2), giveMeSettings.getServerPort(2));
            } catch (Exception ex) {
                err++;
                if (err > 9) {
                    showDialogInformation(2);
                    return null;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("Ошибка подключения в методе загрузки подписок: " + ex.toString());
            }
        }
    }

    private void showDialogInformation(int what) {
        if (what == 1)
            Platform.runLater(() -> {
                System.out.println("Неправильный логин или пароль.");
                AlertPry alert = new AlertPry("Ошибка входа", "Неправильный логин или пароль.");
                alert.showAndWait();
            });
        else if (what == 2)
            Platform.runLater(() -> {
                System.out.println("Не удается подключиться к серверву");
                AlertPry alert = new AlertPry("Нет подключения", "Не удается подключиться к серверу, проверьте настройки подключения к сети Интернет.");
                alert.showAndWait();
            });
        else if (what == 3)
            Platform.runLater(() -> {
                System.out.println("Сервер не отвечает");
                AlertPry alert = new AlertPry("Нет ответа", "Сервер не отвечает на запросы. Попробуйте перезапустить приложение.");
                alert.showAndWait();
            });
    }
}
