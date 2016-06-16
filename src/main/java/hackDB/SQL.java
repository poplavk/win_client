package hackDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Suharev on 15.05.2016.
 * Класс для работы с СУБД, работающих на SQL
 * Наследует интерфейс DBI
 * Содержит в себе 4 метода для формирования нужных запросов
 * один метод для их исполнения,
 * метод для установки соединения с БД
 * и метод для разрыва соединения
 */
 
public class SQL implements DBInterface {
    Connection conn = null;
    String nameField;
    String lnameField;
    String linkField;
    String picField;
    
    /**
     * Метод принимает запрос в строковом виде от одной из формируюших его функций,
     * выполняет его
     * в случае успеха, кладёт полученные данные в строковый ArrayList
     * @param input Запрос в строковом виде
     * @return полученные результате в виде строкового ArrayList
     */
    public ArrayList<String> QuerySQL(String input)
    {
        ArrayList<String> list = new ArrayList<String>();
        if (conn != null) {
            try {
                Statement m_Statement = conn.createStatement();
                ResultSet m_ResultSet = m_Statement.executeQuery(input);
                ResultSetMetaData rsmd = m_ResultSet.getMetaData();
                byte cnt;
                if(rsmd.getColumnCount()>0) {
                    while (m_ResultSet.next()) {
                        cnt=1;
                        while(cnt<=rsmd.getColumnCount()) {
                            list.add(m_ResultSet.getString(cnt));
                            cnt++;
                        }
                        return list;
                    }
                }else{
                    System.out.println("В базе нет такого человека, либо у него нет нужных данных");
                    return null;
                }
            }catch (SQLException ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else
        {
            System.out.println("Невозможно установить связь с базой");
            return null;
        }
        return null;
    }

    /**
     * Метод, формирующий запрос для получения фотографий пользователей, ФИО которых совпадает с запрашиваемым.
     * затем этот запрос передаётся в метод QuerySQL, в котором выполняется.
     * @param name Имя искомого пользователя
     * @param Sname Фамилия искомого пользователя
     * @param DBname Название БД, в которой его надо искать
     * @return Хранимые в базе фотографии в строковом виде.
     * Такой возвращаемый формат оправдан тем, что мы не знаем, в каком виде может храниться изображение,
     * а String может хранить что угодно
     */
    public ArrayList<String> GetPhoto(String name, String Sname, String DBname)
    {
        ArrayList<String> list;
        if (conn != null) {
            try {
                String query = "SELECT "+picField+" FROM "+DBname+" "+
                               "WHERE "+nameField+" = '"+name+"' and "+
                               lnameField+" = '"+Sname+"'";
                list=QuerySQL(query);
                return list;
            }catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else
        {
            System.out.println("Невозможно установить связь с базой");
            return null;
        }
    }

    /**
     * Метод, формирующий запрос для получения ссылок пользователей, ФИО которых совпадает с запрашиваемым,
     * затем этот запрос передаётся в метод QuerySQL, в котором выполняется.
     * @param name Имя искомого пользователя
     * @param Sname Фамилия искомого пользователя
     * @param DBname Название БД, в которой его надо искать
     * @return Хранимые в базе ссылки в строковом виде.
     */
    public ArrayList<String> GetLink(String name, String Sname, String DBname)
    {
        ArrayList<String> list;
        if (conn != null) {
            try {
                String query = "SELECT "+linkField+" FROM "+DBname+" "+
                        "WHERE "+nameField+" = '"+name+"' and "+
                        lnameField+" = '"+Sname+"'";
                list=QuerySQL(query);
                return list;
            }catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else
        {
            System.out.println("Невозможно установить связь с базой");
            return null;
        }
    }
    
    /**
     * Метод, формирующий запрос для получения ФИО пользователей, ссылка которых совпадает с запрашиваемой,
     * затем этот запрос передаётся в метод QuerySQL, в котором выполняется.
     * @param Link Имя искомого пользователя
     * @param DBname Название БД, в которой его надо искать
     * @return Хранимые в базе ФИО в строковом виде.
     */
    public ArrayList<String> GetName(String Link, String DBname)
    {
        ArrayList<String> list;
        if (conn != null) {
            try {
                String query = "SELECT "+nameField+","+lnameField+" FROM "+DBname+" "+
                        "WHERE "+linkField+" = '"+Link+"'";
                list=QuerySQL(query);
                return list;
            }catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else
        {
            System.out.println("Невозможно установить связь с базой");
            return null;
        }
    }
    
    /**
     * Метод, формирующий запрос для получения всей известноой информации о пользователях,
     * ФИО которых совпадает с запрашиваемым,
     * затем этот запрос передаётся в метод QuerySQL, в котором выполняется.
     * @param name Имя искомого пользователя
     * @param Sname Фамилия искомого пользователя
     * @param DBname Название БД, в которой его надо искать
     * @return Данные о человеке в строковом виде.
     */
    public ArrayList<String> GetData(String name, String Sname, String DBname)
    {
        ArrayList<String> list;
        if (conn != null) {
            try {
                String query = "SELECT * FROM "+DBname+" "+
                        "WHERE "+nameField+" = '"+name+"' and "+
                        lnameField+" = '"+Sname+"'";
                list=QuerySQL(query);
                return list;
            }catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else
        {
            System.out.println("Невозможно установить связь с базой");
            return null;
        }
    }
    
    /**
     * Установка соединения с СУБД на SQL-подобных БД
     * @param address - IP-адрес сервера БД
     * @param port - TCP-порт, который прослушивает сервер
     * @param DBNAme - Название нужной нам БД на сервере
     * @param user - имя поьзователя
     * @param pass - пароль
     * @return true, если соединение удалось установить
     * false, если это не удалось по какой-либо причине
     */
    public boolean DBConnect(String address,int port,String DBNAme, String user, String pass) {
        try
        {
            conn = DriverManager.getConnection("jdbc:sqlserver://"+address+":"+port+";DatabaseName="+DBNAme,user,pass);
            if (conn != null)
            {
                System.out.println("Соединение установлено. Можно работать");
                return true;
            }
        }
        catch (SQLException ex)
        {
            //ex.printStackTrace();
            System.out.println("Неправильно указаны данные сервера БД. Прерывание работы");
            return false;
        }
        return false;
    }
    
    /**
     * Метод разрывает соединение с сервером БД, если оно существует
     */
    public void CloseConn()
    {
        try
        {
            if (conn != null && !conn.isClosed())
            {
                conn.close();
                System.out.println("Соединение закрыто");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
