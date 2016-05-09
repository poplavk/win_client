package aleksey2093;

import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by aleks on 10.04.2016.
 */
public class GetFriendsLastResult {

    //возвращает лист подписчиков при выборе подписчика в списке
    public ArrayList<String> getLastResult(String friend) {
        ArrayList<String> list = new ArrayList<String>();
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
            return null;
        }
        if (sendRequestToServer(giveMeSettings, outputStream, friend)) {
            list = waitServerMsg(inputStream, friend);
        }
        if (!socket.isClosed())
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        return list;
    }

    private boolean sendRequestToServer(GiveMeSettings giveMeSettings, DataOutputStream outputStream, String friend) {
        try {
            byte[] login = giveMeSettings.getLpk((byte) 1);
            byte[] pass = giveMeSettings.getLpk((byte) 2);
            //байт шифр, байт тип, байт длинны логина, логин, байт длинны пароля, пароль, логин друга (чей результат будем смотреть)
            byte[] msg = new byte[1 + 1 + 1 + login.length + 1 + pass.length + friend.getBytes().length];
            msg[0] = giveMeSettings.getEncryption();
            msg[1] = 3;
            msg[2] = (byte) login.length;
            int j = 3;
            for (int i = 0; i < login.length; i++, j++)
                msg[j] = login[i];
            msg[j] = (byte) pass.length;
            j++;
            for (int i = 0; i < pass.length; i++, j++)
                msg[j] = login[i];
            for (int i = 0; i < friend.getBytes().length; i++, j++)
                msg[j] = friend.getBytes()[i];
            outputStream.write(msg);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private ArrayList<String> waitServerMsg(DataInputStream inputStream, String friend) {
        ArrayList<String> arrayList = new ArrayList<String>();
        byte[] msg = new byte[1];
        int len = 0;
        while (len <= 0) {
            try {
                msg = new byte[inputStream.available()];
                len = inputStream.read(msg);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        //дешифруем полученное сообщение
        if (msg[1] == (byte) 103) {
            showDialogInform(true, null);
            return null;
        } else if (msg[1] != 3) {
            return null;
        } else if (len <= 3) {
            showDialogInform(false, friend);
            return null;
        }
        formationListLinks(msg, len, friend);
        return arrayList;
    }

    private ArrayList<String> formationListLinks(byte[] msg, int len, String friend) {
        ArrayList<String> arrayList = new ArrayList<String>();
        int i = 2;
        while (i < len) {
            int lenlink = ByteBuffer.wrap(msg, i, 4).getInt();
            i += 4;
            try {
                String link = new String(msg, i, lenlink, "UTF-8");
                link = getIdFromLink(link);
                if (link != null)
                    arrayList.add(link);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i += lenlink;
        }
        return arrayList;
    }

    private String getIdFromLink(String link) {
        String tmp = null;
        if (link.toCharArray()[link.length() - 1] == '/')
            link = link.substring(0, link.length() - 2);
        for (int i = link.length() - 1; i >= 0; i--) {
            tmp = link.toCharArray()[i] + tmp;
        }
        return tmp;
    }

    private void showDialogInform(boolean what, String string) {
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
}
