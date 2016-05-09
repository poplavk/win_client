package aleksey2093;

import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;


public class FriendSendResult {

    private ArrayList<String> listfrends = new ArrayList<String>();


    public ArrayList getListFriends() {
        boolean res = downloadListFriends();
        if (res) {
            return listfrends;
        } else {
            return null;
        }
    }

    private boolean downloadListFriends() {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        Socket socket = getSocket(giveMeSettings);
        if (socket == null)
            return false;
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



    private boolean readMsgFromServer(GiveMeSettings giveMeSettings, DataInputStream inputStream) {
        int len = 0;
        byte[] msg = new byte[1];
        try {
            while (len == 0) {
                msg = new byte[inputStream.available()];
                len = inputStream.read(msg);
            }
            //дешифруем
            if (msg[1] == (byte) 101) {
                showDialogInformation();
                return false;
            } else if (msg[1] != (byte) 1) {
                System.out.println("Получили неправильный ответ с сервера. Тип: " + msg[1]);
                return false;
            }
            System.out.println("Получен ответ. Начинается обработка данных.");
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
            System.out.println("Подписчиков нет");
            return false;
        }
        while (j < len) {
            /* ключ пока нигде не используется, поэтому просто будем собирать, но не хранить
            * как вариант можно выводить его в списке подписок и по клику отправлять его
            * на сервер, а не строковый логин */
            int key = java.nio.ByteBuffer.wrap(msg, j, 4).getInt();
            j += 4;
            int lenlogin = msg[j];
            j++;
            try {
                String tess = new String(msg, j, lenlogin, "UTF-8");
                listfrends.add(tess);
            } catch (UnsupportedEncodingException e) {
                System.out.println("Ошибка обработки имени подписчика");
                e.printStackTrace();
            }
            j += lenlogin;
        }
        return listfrends.size() != 0;
    }

    private boolean sendMsgToServer(GiveMeSettings giveMeSettings, DataOutputStream outputStream) {
        byte[] login = giveMeSettings.getLpk((byte) 1);
        byte[] pass = giveMeSettings.getLpk((byte) 2);
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
            outputStream.write(message_byte);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        System.out.println("Отправлено");
        return true;
    }

    private Socket getSocket(GiveMeSettings giveMeSettings) {
        int err = 0;
        while (true) {
            try {
                return new Socket(giveMeSettings.getServerName(2), giveMeSettings.getServerPort(2));
            } catch (Exception ex) {
                    err++;
                    if (err > 9)
                        return null;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                System.err.println("Ошибка подключения " + ex.getMessage() + "\n" + ex.toString());
            }
        }
    }

    private void showDialogInformation()
    {
        System.out.println("Неправильный логин или пароль.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText("Ошибка входа");
        alert.setContentText("Неправильный логин или пароль");
        alert.showAndWait();
    }
}
