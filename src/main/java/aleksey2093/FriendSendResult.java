package aleksey2093;

import gui.MainFormController;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class FriendSendResult {

    ArrayList<String> listfrends = new ArrayList<String>();

    public ArrayList getListFriends()
    {
        if (listfrends.toArray().length == 0) { give_me_please_friends(); }
        return listfrends;
    }

    public byte give_me_please_friends() {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        boolean ok = false;
        byte err = 0;
        Socket sock;
        DataOutputStream outputStream;
        DataInputStream inputStream;
        while (true) {
            try {
                if ((sock = giveMeSettings.getSocket(false)) == null) {
                    return -1;
                }
                outputStream = new DataOutputStream(sock.getOutputStream());
                inputStream = new DataInputStream(sock.getInputStream());
                break;
            } catch (Exception ex) {
                if (ex.getMessage().startsWith("В соединении отказано")) {
                    try {
                        Thread.sleep(3000);
                        err++;
                        if (err > 9) {
                            return -2;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.err.println("Ошиюка подключения " + ex.getMessage() + "\n" + ex.toString());
            }
        }
//запрашиваем список подписок
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
            return -3;
        }
        try {
            outputStream.write(message_byte);
        } catch (Exception ex) {
            ex.printStackTrace();
            return -4;
        }
        System.out.println("Отправлено");
        try {
            while (true) {
                int len = 0;
                while (len == 0) {
                    message_byte = new byte[inputStream.available()];
                    len = inputStream.read(message_byte);
                }
                if (message_byte[1] == (byte)101) {
                    System.out.println("Неправильный логин или пароль. Тип: " + message_byte[1]);
                    alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Информация");
                    alert.setHeaderText("Ошибка входа");
                    alert.setContentText("Неправильный логин или пароль");
                    alert.showAndWait();
                    break;
                } else if (message_byte[1] != (byte) 1) {
                    System.out.println("Получили левый ответ с сервера. Тип: " + message_byte[1]);
                    continue;
                } else {
                    System.out.println("Получен ответ. Начинается обработка данных.");
                }
                //нужно ли на сервер отправить ответ о принятии пакета или о том, что на пришла хрень
                try {
                    sock.close(); //закрываем сокет за ненадобностью
                } catch (Exception ex) {
                    if (sock.isConnected()) {
                        sock.close();
                    }
                }
                //тут будет вызов функции дешифруем все, что после нулевого байта
                if (len <= 2) {
                    System.out.println("Подписчкиво нет");
                    return 2;
                }
                j = 2;
                while (j<len){
                    /* ключ пока нигде не используется, поэтому просто будем собирать, но не хранить
                    * как вариант можно выводить его в списке подписок и по клику отправлять его
                    * на сервер, а не строковый логин */
                    int key = java.nio.ByteBuffer.wrap(message_byte,j,4).getInt();
                    j+=4;
                    int lenloginnow = message_byte[j]; j++;
                    String tess = new String(message_byte, j, lenloginnow, "UTF-8");
                    listfrends.add(tess); j+=lenloginnow;
                }
                System.out.println("Список подписок");
                for (int i = 0; i < listfrends.toArray().length; i++) {
                    System.out.println((i + 1) + "  key: " + i + " login: " + listfrends.get(i));
                }
                //getScrollPaneResult(listlogin);
                return 1;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return -5;
        }
    }
}
