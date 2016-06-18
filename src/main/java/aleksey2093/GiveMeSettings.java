package aleksey2093;

import encrypt.AES;
import encrypt.MD5;
import encrypt.RSA;

import java.io.FileOutputStream;
import java.lang.String;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Properties;

public class GiveMeSettings {

    /**
     * Загружает параметры из файла настроек
     * @return параметры файла настроек
     */
    private Properties loadSettingFile() {
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

    /**
     * Возвращает значения фильтров качества и количества
     * @return массив из двух переменных
     */
    public int[] getFilter() {
        Properties props = loadSettingFile();
        int[] mass = new int[2];
        mass[0] = Boolean.parseBoolean(props.getProperty("filter")) ? 1 : 0;
        mass[1] = Integer.parseInt(props.getProperty("count"));
        return mass;
    }


    /**
     * Возвращает имя сервера
     * @param what 1 - эталон, 2 - подписки, 3 - прослушка
     * @return имя Сервера
     */
    public String getServerName(int what) {
        switch (what) {
            case 1:
                return loadSettingFile().getProperty("server.name_send");
            case 2:
                return loadSettingFile().getProperty("server.name_friend");
            case 3:
                return loadSettingFile().getProperty("server.name_listen");
            default:
                return null;
        }
    }

    /**
     * Возвращает номер порта сервера
     * @param what 1 - эталон, 2 - подписки, 3 - прослушка
     * @return порт
     */
    public int getServerPort(int what) {
        switch (what) {
            case 1:
                return Integer.parseInt(loadSettingFile().getProperty("server.port_send"));
            case 2:
                return Integer.parseInt(loadSettingFile().getProperty("server.port_friend"));
            case 3:
                return Integer.parseInt(loadSettingFile().getProperty("server.port_listen"));
            default:
                return -1;
        }
    }


    /**
     * Возвращает массив настроек социальных сетей
     * @return массив настроек
     */
    public boolean[] getSocialSettings() {
        boolean[] res = new boolean[7];
        try {
            Properties props = loadSettingFile();
            res[0] = Boolean.parseBoolean(props.getProperty("socialnetwork"));
            res[1] = Boolean.parseBoolean(props.getProperty("social.photo"));
            res[2] = Boolean.parseBoolean(props.getProperty("social.fio"));
            res[3] = Boolean.parseBoolean(props.getProperty("social.datebith"));
            res[4] = Boolean.parseBoolean(props.getProperty("social.city"));
            res[5] = Boolean.parseBoolean(props.getProperty("social.work"));
            res[6] = Boolean.parseBoolean(props.getProperty("social.phone"));
        } catch (Exception ex) {
            res = new boolean[1];
            res[0] = false;
        }
        return res;
    }

    /**
     * Сохранение пользовательских настроек
     * @param tmp массив настроек соц. сетей
     * @param encr Тип шифрования
     * @return успешность выполнения операции сохранения
     */
    public boolean setSaveSettingWindow(boolean[] tmp, String encr) {
        Properties properties = loadSettingFile();
        properties.setProperty("socialnetwork", (tmp[0] + ""));
        switch (encr) {
            case "AES":
                properties.setProperty("encryption", "1");
                break;
            case "RSA":
                properties.setProperty("encryption", "2");
                break;
            case "ГОСТ":
                properties.setProperty("encryption", "3");
                break;
        }
        properties.setProperty("social.photo", (tmp[1]+""));
        properties.setProperty("social.fio", (tmp[2]+""));
        properties.setProperty("social.datebith", (tmp[3]+""));
        properties.setProperty("social.city", (tmp[4]+""));
        properties.setProperty("social.work", (tmp[5]+""));
        properties.setProperty("social.phone", (tmp[6]+""));
        try {
            properties.store(new FileOutputStream("src/main/resources/settingfile.properties"), null);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Возвращает логин пользователя или пароль
     * @param b логин (true), пароль (false)
     * @return логин/пароль
     */
    public String getLpkString(boolean b) {
        if (b) {
            return loadSettingFile().getProperty("sys.login");
        } else {
            return loadSettingFile().getProperty("sys.pass");
        }
    }

    /**
     * Сохраняет логин пользователя или пароль
     * @param b логин (true), пароль (false)
     * @param string Сохраняемое значение
     */
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

    /**
     * Возвращает логин пользователя или пароль в виде массива байт
     * @param bit логин (true), пароль (false)
     * @return массив байт
     */
    public byte[] getLpk(boolean bit) {
        byte[] mass = new byte[1];
        mass[0] = -1;
        String str;
        if (bit)
            str = loadSettingFile().getProperty("sys.login");
        else
            str = loadSettingFile().getProperty("sys.pass");
        if (str.length() == 0)
            mass[0] = -1;
        else
            mass = str.getBytes();
        return mass;
    }

    /**
     * Возвращает тип шифрования
     * @return тип шифрования
     */
    public byte getEncryption() {
        try {
            return Byte.parseByte(loadSettingFile().getProperty("encryption"));
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * Шифрование стандратного сообщения
     * @param msg массив байт сообщения
     * @return зашифрованный массив
     */
    public byte[] getEncryptMsg(byte[] msg) {
        /*try {
            byte encrypt_type = msg[0];
            byte[] tmp = new byte[msg.length-1];
            System.arraycopy(msg, 1, tmp, 0, tmp.length);
            int len = "abcabcaabcabcabc".length();
            switch (encrypt_type) {
                case 1:
                    msg = new AES("abcabcaabcabcabc").encrypt(tmp);
                    break;
                case 2:
                    msg = new RSA().encrypt(tmp);
                    break;
                case 3:
                    msg = new MD5().encrypt(tmp);
                    break;
                default:
                    return new byte[] { -1 };
            }
            tmp = new byte[msg.length + 1];
            tmp[0] = encrypt_type;
            System.arraycopy(msg, 0, tmp, 1, msg.length);
            return tmp;
        } catch (Exception ex) {
            return new byte[] { -1 };
        }*/
        return  msg;
    }

    /**
     * Дешифрование стандратного сообщения
     * @param msg массив байт сообщения
     * @return расщифрованный массив
     */
    public byte[] getDecryptMsg(byte[] msg)
    {
        /*try {
            byte bit = msg[0];
            byte[] tmp = new byte[msg.length-1];
            System.arraycopy(msg, 1, tmp, 0, tmp.length);
            switch (bit)
            {
                case 1:
                    msg = new AES("abcabcaabcabcabc").decrypt(tmp);
                    break;
                case 2:
                    msg = new RSA().decrypt(tmp);
                    break;
                case 3:
                    msg = new MD5().decrypt(tmp);
                    break;
                default:
                    return new byte[] { -1 };
            }
            tmp = new byte[msg.length+1];
            tmp[0] = bit;
            System.arraycopy(msg, 0, tmp, 1, bit);
            return tmp;
        } catch (Exception ex) {
            return new byte[] { -1 };
        }*/
        return msg;
    }
}
