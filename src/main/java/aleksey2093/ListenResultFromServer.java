package aleksey2093;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;

//import javax.swing.*;


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

    public void listen() {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        try {
            int err = 0;
            wh:
            while (true) {
                try {
                    serversock = new ServerSocket(giveMeSettings.getServerPort(false));
                } catch (Exception ex) {
                    System.out.println("Не удалось создать сокет для прослушивания ответов с сервера. Ошибка: " +
                            ex.getMessage());
                    Thread.sleep(1000);
                    err++;
                    if (err > 9) {
                        System.out.println("Количество попыток подключения больше 9. " +
                                "Проверьте настройки приложения.");
                        Thread.sleep(10000);
                        break;
                    }
                    continue;
                }
                while (true) {
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
                    if (message_byte[1] == (byte)102) {
                        System.out.println("Неправильный логин или пароль. Тип: " + message_byte[1]);
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText("Ошибка входа");
                        alert.setContentText("Неправильный логин или пароль");
                        alert.showAndWait();
                        break;
                    } else if (msgbyte[1] != 2) {
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Пришел результат");
                    alert.setHeaderText("У пользователя " + login + " новый результат");
                    alert.setContentText("Хотите посмотреть на результат '" + login + "'?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        System.out.println("Пользователь согласился посмотреть результат от " + login);
                    } else if (result.get() == ButtonType.CANCEL) {
                        continue;
                    }

                    //Мы получили от пользователя разрешение посмотреть на результат запрос от пользователя
                    int jb = 3 + msgbyte[2];
                /*и так мы получили список ссылок разделенных проблем.
                * Начинаем его обрабатывать и потом передать в систему выдачи */
                    if (jb >= len) {
                        System.out.println("Результат пользователя пуст");
                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Информация");
                        alert.setHeaderText("");
                        alert.setContentText("Результат " + login + " пуст");
                        alert.showAndWait();
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
        } catch (Exception ex) {
            System.out.println("Возникла ошибка при прослушивании данных");
        }
        try {
            socket.close();
        } catch (Exception ignored) {
        }
        try {
            serversock.close();
        } catch (Exception ignored) {
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
