package aleksey2093;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;

//import javax.swing.*;


public class ListenResultFromServer {
    /*
    Класс постоянно прослушивает сообщения с сервера, чтобы поймать сообщение о входящем результате подписчика.
     */
    private static Thread thread;
    public void startListen() {
        thread = new Thread(new Runnable() {
            public void run() {
                int err = 0;
                while (true) {
                    listenServer();
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
    public void stopListen() {
        thread.stop();
    }

    private void listenServer() {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        ServerSocket serversocket = getServerSocket(giveMeSettings);
        if (serversocket == null)
            return;
        while (true) {
            try {
                Socket socket = serversocket.accept();
                startSocketNewAccept(socket);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    serversocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
        }
    }

    /*получение нового сокета для входящих соединенний*/
    private ServerSocket getServerSocket(GiveMeSettings giveMeSettings) {
        int err = 0;
        while (true) {
            try {
                return new ServerSocket(giveMeSettings.getServerPort(3));
            } catch (Exception ex) {
                System.out.println("Не удалось создать сокет для прослушивания ответов с сервера. Ошибка: " +
                        ex.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                err++;
                if (err > 9) {
                    System.out.println("Количество попыток подключения больше 9. " +
                            "Проверьте настройки приложения.");
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
        }
    }

    /*метод обработки нового соединения*/
    private boolean startSocketNewAccept(Socket socket) {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            int len = 0;
            byte[] msg = new byte[0];
            while (len == 0) { //запускаем прослушку
                msg = new byte[inputStream.available()];
                len = inputStream.read(msg);
            }
            msgPostsProcessing(msg, len);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean getResDialogWindow(int what, String login) {
        if (what == 1) {
            System.out.println("Посмотреть результат пользователя - " + login + "? (yes/no)");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Пришел результат");
            alert.setHeaderText("У пользователя " + login + " новый результат");
            alert.setContentText("Хотите посмотреть на результат '" + login + "'?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                System.out.println("Пользователь согласился посмотреть результат от " + login);
                return true;
            } else
                return false;
        } else if (what == 2) {
            System.out.println("Результат пользователя пуст");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText("");
            alert.setContentText("Результат " + login + " пуст");
            alert.showAndWait();
            return true;
        } else if (what == 3) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText("Ошибка входа");
            alert.setContentText("Неправильный логин или пароль");
            alert.showAndWait();
            return true;
        }
        return false;
    }
    private void msgPostsProcessing(byte[] msg, int len)
    {
        //дешифруем
        if (msg[1] == (byte)102) {
            System.out.println("Неправильный логин или пароль. Тип: " + msg[1]);
            getResDialogWindow(3,null);
        } else if (msg[1] != 2) {
            System.out.println("Получили левое сообщение. Продолжаем прослушку.");
        } else if (msg[2] < 1) {
            System.out.println("Сообщение неверно дешефровано или отправлено. " +
                    "Длинна логина указана как отрицательная. Продолжаем прослушку.");
        } else {
            try {
                String login = new String(msg, 3, msg[2], "UTF-8");
                if (!getResDialogWindow(1,login))
                    return;
                formationListLinks(msg,len,login);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void formationListLinks(byte[] msg, int len, String login) {
        //Мы получили от пользователя разрешение посмотреть на результат запрос от пользователя
        int jb = 3 + msg[2];
                /*и так мы получили список ссылок разделенных проблем.
                * Начинаем его обрабатывать и потом передать в систему выдачи */
        if (jb >= len) {
            getResDialogWindow(2,null);
        }
        ArrayList<String> links = new ArrayList<String>();
        while (jb < len) {
            int size = ByteBuffer.wrap(msg, jb, 4).getInt();
            jb += 4;
            try {
                String link = null;
                link = new String(msg, jb, size, "UTF-8");
                link = getIdFromLink(link);
                if (link != null)
                    links.add(link);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            jb += size;
        }
        for (int i = 0; i < links.toArray().length; i++) {
            System.out.println("Ссылка по запросу пользователя (" + login + "): " + links.get(i));
        }
        //GetSomePrivateData getSomePrivateData = new GetSomePrivateData();
        //getSomePrivateData.vkGet(links, giveMeSettings.getSocialStg());
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
}
