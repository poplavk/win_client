package aleksey2093;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;

/**
 * Created by aleks on 10.04.2016.
 */
public class GetFriendsLastResult {

    //возвращает лист подписчиков при выборе подписчика в списке
    public ArrayList getLastResult(String loginfriend) {
        ArrayList<String> list = new ArrayList<String>();
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        Socket soc = null;
        DataOutputStream outputStream;
        DataInputStream inputStream;
        try {
            soc = new Socket(giveMeSettings.getServerName(false), giveMeSettings.getServerPort(false));
            outputStream = new DataOutputStream(soc.getOutputStream());
            inputStream = new DataInputStream(soc.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        byte[] login = giveMeSettings.getLpk((byte) 1);
        byte[] pass = giveMeSettings.getLpk((byte) 2);
        //байт шифр, байт тип, байт длинны логина, логин, байт длинны пароля, пароль, логин друга (чей результат будем смотреть)
        byte[] msg = new byte[1 + 1 + 1 + login.length + 1 + pass.length + loginfriend.getBytes().length];
        msg[0] = giveMeSettings.getEncryption();
        msg[1] = 3; int j = 3;
        msg[2] = (byte) login.length;
        for (int i=0;i<login.length;i++,j++)
            msg[j]=login[i];
        msg[j] = (byte) pass.length; j++;
        for (int i=0;i<pass.length;i++,j++)
            msg[j]=login[i];
        for (int i=0;i<loginfriend.getBytes().length;i++,j++)
            msg[j]=loginfriend.getBytes()[i];
        try {
            outputStream.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        int len = 0;
        while (len <= 0) {
            try {
                msg = new byte[inputStream.available()];
                len = inputStream.read(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            soc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        j=2;
        //дешефруем тут
        if (msg[1] != 2 && msg[1] != 3) {
            return list;
        }
        try {
            while (j < len) {
                int linklen = ByteBuffer.wrap(msg, j, 4).getInt(); j += 4;
                list.add(new String(msg, j, linklen, "UTF-8")); j+=linklen;
            }
        }
        catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }
}
