package aleksey2093;

import hackIntoSN.GetSomePrivateData;
//import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;


public class ListenResultFromServer {
    /*
    Класс постоянно прослушивает сообщения с сервера, чтобы поймать сообщение о входящем рузльтате подписчика.
     */
    public static Thread thread;
    public void startListen() {
        thread = new Thread(new Runnable() {
            public void run() {
                int err = 0;
                while (true) {
                    listen();
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ex) {
                        err++;
                        System.out.println("Ошибка прослушки " + err + ": " + ex.getMessage());
                    }
                }
            }
        });
        thread.setName("Прослушка сервера");
        thread.start();
        thread.isInterrupted();
    }
    
    public static ServerSocket serversock = null;
    public static Socket socket = null;
    
     public void listen()
    {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        try {
            int err = 0;
            while (true) {
                try {
                    serversock = new ServerSocket(giveMeSettings.getServerPort(false));
                } catch (Exception ex) {
                    System.out.println("Не удалось создать сокет для прослушивания ответов с сервера. Ошибка: " +
                            ex.getMessage());
                    Thread.sleep(1000);
                    err++;
                    if (err > 9) {
                        System.out.println("Количество попыток подключения больше 9.
                        Проверьте настройки приложения.");
                        Thread.sleep(10000);
                    }
                }

                socket = serversock.accept();
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());

                int len = 0;
                byte[] msgbyte;
                do { //запускаем прослушку
                    msgbyte = new byte[inputStream.available()];
                    len = inputStream.read(msgbyte);
                } while (len == 0); //получили сообщение
                //дешефруем все, что после первого байта
                /*
                * msgbyte[0] - шифрование
                * msgbyte[1] - тип сообщения
                * msgbyte[2] - длинна логина
                * msgbyte[3] - логин
                 * msgbyte[3+msgbyte[2]] - длинна ссылки
                 */
                if (msgbyte[1] != 4) {
                    System.out.println("Получили левое сообщение. Продолжаем прослушку.");
                    continue;
                }
                if (msgbyte[2] < 1) {
                    System.out.println("Сообщение неверно дешефровано или отправлено. " +
                            "Длинна логина указана как отрицательная. Продолжаем прослушку.");
                    continue;
                }
                try {
                    socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                String login = new String(msgbyte, 3, msgbyte[2], "UTF-8");

                System.out.println("Посмотреть результат пользователя - " + login + "? (yes/no)");
                /*int qeees = JOptionPane.showConfirmDialog(null, "Посмотреть результат пользователя - "
                        + login + "? (yes/no)", "Сообщение от " + login, JOptionPane.YES_NO_OPTION);
                if (qeees != JOptionPane.YES_OPTION)
                    break;*/

                //Мы получили от пользователя разрешение посмотреть на результат запрос от пользователя
                int jb = 3 + msgbyte[2];
                /*и так мы получили список ссылок разделенных проблем.
                * Начинаем его обрабатывать и потом передать в систему выдачи */
                if (jb >= len) {
                    System.out.println("Результат пользователя пуст");
                    //JOptionPane.showMessageDialog(null, "Результат пользователя пуст", "Пусто", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
                ArrayList<String> links = new ArrayList<String>();
                while (jb < len) {
                    int size = ByteBuffer.wrap(msgbyte, jb, 4).getInt();
                    jb += 4;
                    String link = new String(msgbyte, jb, size, "UTF-8");
                    link = getIdFromLink(link);
                    if (link != null) 
                        links.add(link);
                    jb += size;
                }
                //Вызов метода подсистемы подзгрузки из соц сетей, пока его нет просто печатаем
                for (int i = 0; i < links.toArray().length; i++) {
                    System.out.println("Ссылка по запросу пользователя (" + login + "): " + links.get(i));
                }
                //GetSomePrivateData getSomePrivateData = new GetSomePrivateData();
                //getSomePrivateData.vkGet(links, giveMeSettings.getSocialStg());
            }
        }
        catch (Exception ex)
        {
            System.out.println("Возникла ошибка при прослушивании данных");
        }
        //конец метода прослушки
    }
    private String getIdFromLink(String link)
    {
        String tmp = null;
        if (link.toCharArray()[link.length()-1] == '/')
            link = link.substring(0,link.length()-2);
        for (int i = link.length()-1; i >=0 ; i--)
        {
            tmp = link.toCharArray()[i] + tmp;
        }
        return tmp;
    }
}
