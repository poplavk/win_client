package hackDB;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Bill Gates on 15.05.2016.
 */
public class SQL implements DBInterface {
    Connection conn = null;
    String nameField;
    String lnameField;
    String linkField;
    String picField;
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
