package aleksey2093;

import java.io.FileOutputStream;
import java.lang.String;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class GiveMeSettings {

    public Properties loadSettingFile() {
        FileInputStream fis;
        Properties property = new Properties();
        try {
            fis = new FileInputStream("src/main/resources/settingfile.properties");
            property.load(fis);

        } catch (Exception e) {
            System.err.println("ОШИБКА: Файл настроек отсуствует!");
            property = null;
        }
        return property;
    }

    public byte[] getFilter(byte bit) {
        if (bit == 1) {
            byte[] mass;
            Properties props = loadSettingFile();
            if (props == null) { mass = new byte[1]; mass[0] = -1; return mass; }
            mass = new byte[2];
            mass[0] = Byte.parseByte(props.getProperty("filter"));
            mass[1] = Byte.parseByte(props.getProperty("count"));
            return mass;
        }
        byte[] mass = new byte[1]; mass[0] =  -1 ;
        return mass;
    }
    public Socket getSocket(boolean what) {
        //дописать
        Socket socket = null;
        String serverName;
        int serverPort;
        if (what) {
            serverName = getServerName(true);
            serverPort = getServerPort(true);
        } else {
            serverName = getServerName(false);
            serverPort = getServerPort(false);
        }
        int err = 0;
        while (true) {
            try {
                socket = new Socket(serverName, serverPort);
                break;
            } catch (Exception ex) {
                System.out.println("Не удается подключиться");
                err++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (err>9)
                {
                    System.out.println("Закончились попытки подключения");
                    return socket;
                }
            }
        }
        return socket;
    }

    public String getServerName(boolean what)
    {
        if (what)
        {
            return loadSettingFile().getProperty("server.name_send");
        }
        else
        {
            return loadSettingFile().getProperty("server.name");
        }
    }

    public int getServerPort (boolean what)
    {
        if (what == true)
        {
            return Integer.parseInt(loadSettingFile().getProperty("server.port_send"));
        }
        else
        {
            return Integer.parseInt(loadSettingFile().getProperty("server.port"));
        }
    }

    public byte[] getSocialStg()
    {
        byte[] res = new byte[9];
        Properties props = loadSettingFile();
        try {
            res[0] = Byte.parseByte(props.getProperty("socialnetwork"));
            res[1] = Byte.parseByte(props.getProperty("social.photo"));
            res[2] = Byte.parseByte(props.getProperty("social.fio"));
            res[3] = Byte.parseByte(props.getProperty("social.datebith"));
            res[4] = Byte.parseByte(props.getProperty("social.city"));
            res[5] = Byte.parseByte(props.getProperty("social.work"));
            res[6] = Byte.parseByte(props.getProperty("social.phone"));
            res[7] = Byte.parseByte(props.getProperty("social.del"));
            res[8] = Byte.parseByte(props.getProperty("social.close"));
        } catch (Exception ex)
        {
            res = new byte[1]; res[0] = -1;
        }
        return res;
    }
    public void  setSaveSettingWindow(boolean[] tmp, String encr)
    {
        Properties properties = loadSettingFile();
        if (tmp[0])
        {
            if (tmp[1]) {
                properties.setProperty("socialnetwork","3");
            }
            else {
                properties.setProperty("socialnetwork","1");
            }
        }
        else {
            if (tmp[1]) {
                properties.setProperty("socialnetwork","2");
            } else {
                properties.setProperty("socialnetwork","0");
            }
        }
        if (encr == "AES") { properties.setProperty("encryption","1"); }
        else if (encr == "RSA") { properties.setProperty("encryption","2"); }
        else if (encr == "ГОСТ") { properties.setProperty("encryption","3"); }
        properties.setProperty("social.photo",(tmp[2]?"1":"0"));
        properties.setProperty("social.fio",(tmp[3]?"1":"0"));
        properties.setProperty("social.datebith",(tmp[4]?"1":"0"));
        properties.setProperty("social.city",(tmp[5]?"1":"0"));
        properties.setProperty("social.work",(tmp[6]?"1":"0"));
        properties.setProperty("social.phone",(tmp[7]?"1":"0"));
        try {
            properties.store(new FileOutputStream("src/main/resources/settingfile.properties"),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getLpk(byte bit)
    {
        byte[] mass = new byte[1]; mass[0] = -1;
        if (bit > 0 && bit < 4)//логин
        {
            String str = null; int key;
            if (bit == 3 && (key = Integer.parseInt(loadSettingFile().getProperty("sys.key"))) > -1) {
                mass = java.nio.ByteBuffer.allocate(4).putInt(key).array();
            }
            else {
                if (bit == 1) str = loadSettingFile().getProperty("sys.login");
                else if (bit == 2) str = loadSettingFile().getProperty("sys.pass");
                if (str != "-1" && str != null) {
                    mass = new byte[str.length()];
                    for (int i = 0; i < str.length(); i++) {
                        mass[i] = (byte) str.toCharArray()[i];
                    }
                }
            }
        }
        else { mass[0] = -2; return mass; }
        return mass;
    }

    public byte getEncryption()
    {
        try {
            return Byte.parseByte(loadSettingFile().getProperty("encryption"));
        }
        catch (Exception ex)
        {
            return -1;
        }
    }
}
