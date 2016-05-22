package aleksey2093;

import java.io.FileOutputStream;
import java.lang.String;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

public class GiveMeSettings {

    //загружает файл настроек
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
    //фильтр и количество.
    public byte[] getFilter(byte bit) {
        if (bit == 1) {
            byte[] mass;
            Properties props = loadSettingFile();
            if (props == null) {
                mass = new byte[1];
                mass[0] = -1;
                return mass;
            }
            mass = new byte[2];
            mass[0] = Byte.parseByte(props.getProperty("filter"));
            mass[1] = Byte.parseByte(props.getProperty("count"));
            return mass;
        }
        byte[] mass = new byte[1];
        mass[0] = -1;
        return mass;
    }
    //не использовать!
    public Socket getSocket(int what) {
        Socket socket;
        String serverName;
        int serverPort;
        serverName = getServerName(what);
        serverPort = getServerPort(what);
        int err = 0;
        while (true) {
            try {
                socket = new Socket(serverName, serverPort);
                return socket;
            } catch (Exception ex) {
                System.out.println("Не удается подключиться");
                err++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (err > 9) {
                    System.out.println("Закончились попытки подключения");
                    return null;
                }
            }
        }
    }
    //1 - эталон, 2 - подписки, 3 - прослушка
    public String getServerName(int what) {
        if (what == 1)
            return loadSettingFile().getProperty("server.name_send");
        else if (what == 2)
            return loadSettingFile().getProperty("server.name_friend");
        else if (what == 3)
            return loadSettingFile().getProperty("server.name_listen");
        else
            return null;
    }
    //1 - эталон, 2 - подписки, 3 - прослушка
    public int getServerPort(int what) {
        if (what == 1)
            return Integer.parseInt(loadSettingFile().getProperty("server.port_send"));
        else if (what == 2)
            return Integer.parseInt(loadSettingFile().getProperty("server.port_friend"));
        else if (what == 3)
            return Integer.parseInt(loadSettingFile().getProperty("server.port_listen"));
        else
            return -1;
    }
    
    public byte[] getSocialStg() {
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
        } catch (Exception ex) {
            res = new byte[1];
            res[0] = -1;
            ex.printStackTrace();
        }
        return res;
    }
    //сохранение настроек
    public boolean setSaveSettingWindow(boolean[] tmp, String encr) {
        Properties properties = loadSettingFile();
        properties.setProperty("socialnetwork", (tmp[0] ? "0" : "1"));
        if (encr.equals("AES")) {
            properties.setProperty("encryption", "1");
        } else if (encr.equals("RSA")) {
            properties.setProperty("encryption", "2");
        } else if (encr.equals("ГОСТ")) {
            properties.setProperty("encryption", "3");
        }
        properties.setProperty("social.photo", (tmp[1] ? "1" : "0"));
        properties.setProperty("social.fio", (tmp[2] ? "1" : "0"));
        properties.setProperty("social.datebith", (tmp[3] ? "1" : "0"));
        properties.setProperty("social.city", (tmp[4] ? "1" : "0"));
        properties.setProperty("social.work", (tmp[5] ? "1" : "0"));
        properties.setProperty("social.phone", (tmp[6] ? "1" : "0"));
        try {
            properties.store(new FileOutputStream("src/main/resources/settingfile.properties"), null);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getLpkString(boolean b) {
        if (b) {
            return loadSettingFile().getProperty("sys.login");
        } else {
            return loadSettingFile().getProperty("sys.pass");
        }
    }

    public void setLpkString(boolean b, String string) {
        Properties properties = loadSettingFile();
        if (b) {
            properties.setProperty("sys.login", string);
        } else {
            properties.setProperty("sys.pass", string);
        }
        try {
            properties.store(new FileOutputStream("src/main/resources/settingfile.properties"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //логин пользователя и пароль. 1 - логин, 2 - пароль
    public byte[] getLpk(byte bit) {
        byte[] mass = new byte[1];
        mass[0] = -1;
        if (bit > 0 && bit < 4)//логин
        {
            String str = null;
            int key;
            if (bit == 3 && (key = Integer.parseInt(loadSettingFile().getProperty("sys.key"))) > -1) {
                mass = java.nio.ByteBuffer.allocate(4).putInt(key).array();
            } else {
                if (bit == 1) str = loadSettingFile().getProperty("sys.login");
                else if (bit == 2) str = loadSettingFile().getProperty("sys.pass");
                if (!str.equals("-1") && str != null) {
                    mass = new byte[str.length()];
                    for (int i = 0; i < str.length(); i++) {
                        mass[i] = (byte) str.toCharArray()[i];
                    }
                }
            }
        } else {
            mass[0] = -2;
            return mass;
        }
        return mass;
    }
    
    //тип шифрования
    public byte getEncryption() {
        try {
            return Byte.parseByte(loadSettingFile().getProperty("encryption"));
        } catch (Exception ex) {
            return -1;
        }
    }
}
