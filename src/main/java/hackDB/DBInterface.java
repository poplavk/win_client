package hackDB;

import java.util.ArrayList;

/**
 * Created by Suharev on 15.05.2016.
 * Интерфейс для методов, работающих с внешними базами данных
 * Содержит в себе 4 метода для формирования нужных запросов (подробнее описаны в соотв. наследуемых классах),
 * метод для установки соединения с БД
 * и метод для разрыва соединения
 */
 
public interface DBInterface {
    //Формирование запросов
    //Фотография по ФИО
    ArrayList<String> GetPhoto(String a, String b, String c);
    //Ссылка по ФИО
    ArrayList<String> GetLink(String a, String b, String c);
    //ФИО по ссылке
    ArrayList<String> GetName(String a, String b);
    //Все данные по ФИО
    ArrayList<String> GetData(String a, String b, String c);
    //Установка соединения
    boolean DBConnect(String address, int port, String DBNAme, String user, String pass);
    //Разрыв соединения
    void CloseConn();
}
