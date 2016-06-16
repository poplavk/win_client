package hackDB;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.mongodb.*;

import com.google.gson.*;

/**
 * Created by Suharev on 15.05.2016.
 * Класс для работы с СУБД, работающих на MongoDB
 * Наследует интерфейс DBI
 * Содержит в себе 4 метода для формирования нужных запросов
 * метод для установки соединения с БД
 * и метод для разрыва соединения
 */
public class Mongo {
    DB db;
    MongoClient mongoClient;
    String nameParam;
    String nameField;
    String lnameField;
    String linkField;
    String picField;
    
    /**
     * Метод, формирующий запрос для получения фотографий пользователей, ФИО которых совпадает с запрашиваемым,
     * затем получающий ответ от сервера и преобразовывающий его в удобный для дальнейшей работы вид
     * @param name Имя искомого пользователя
     * @param Sname Фамилия искомого пользователя
     * @param Colname Название коллекции, в которой его надо искать
     * @return Хранимые в базе фотографии в строковом виде.
     * Такой возвращаемый формат оправдан тем, что мы не знаем, в каком виде может храниться изображение,
     * а String может хранить что угодно
     */
    ArrayList<String> GetPhoto(String name, String Sname, String Colname)
    {
        ArrayList<String> list= new ArrayList<String>();
        DBCollection coll = db.getCollection(Colname);
        BasicDBObject query = new BasicDBObject(nameParam+"."+nameField, new BasicDBObject("$eq", name))
                .append(nameParam+"."+lnameField, new BasicDBObject("$eq", Sname));
        DBCursor cursor = coll.find(query);
        if(cursor.size()>0) {
            try {
                while (true) {
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(cursor.next().toString()).getAsJsonObject();
                    JsonPrimitive jobj = new Gson().fromJson(obj.get(picField), JsonPrimitive.class);
                    String res = jobj.getAsString();
                    list.add(res);
                    if(!cursor.hasNext()) break;
                }
                cursor.close();
                return list;
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else {
            System.out.println("Такого человека нет в базе");
            return null;
        }
    }
    
     /**
     * Метод, формирующий запрос для получения ссылок пользователей, ФИО которых совпадает с запрашиваемым,
     * затем получающий ответ от сервера и преобразовывающий его в удобный для дальнейшей работы вид
     * @param name Имя искомого пользователя
     * @param Sname Фамилия искомого пользователя
     * @param Colname Название коллекции, в которой его надо искать
     * @return Хранимые в базе ссылки в строковом виде.
     */
    ArrayList<String> GetLink(String name, String Sname, String Colname)
    {
        ArrayList<String> list= new ArrayList<String>();
        DBCollection coll = db.getCollection(Colname);
        BasicDBObject query = new BasicDBObject(nameParam+"."+nameField, new BasicDBObject("$eq", name))
                .append(nameParam+"."+lnameField, new BasicDBObject("$eq", Sname));
        DBCursor cursor = coll.find(query);
        if(cursor.size()>0) {
            try {
                while (true) {
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(cursor.next().toString()).getAsJsonObject();
                    JsonPrimitive jobj = new Gson().fromJson(obj.get(linkField), JsonPrimitive.class);
                    String res = jobj.getAsString();
                    list.add(res);
                    if(!cursor.hasNext()) break;
                }
                cursor.close();
                return list;
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else {
            System.out.println("Такого человека нет в базе");
            return null;
        }
    }
    
    /**
     * Метод, формирующий запрос для получения ФИО пользователей, ссылка которых совпадает с запрашиваемой,
     * затем получающий ответ от сервера и преобразовывающий его в удобный для дальнейшей работы вид
     * @param Link Имя искомого пользователя
     * @param Colname Название коллекции, в которой его надо искать
     * @return Хранимые в базе ФИО в строковом виде.
     */
    ArrayList<String> GetName(String Link, String Colname)
    {
        ArrayList<String> list= new ArrayList<String>();
        DBCollection coll = db.getCollection(Colname);
        BasicDBObject query = new BasicDBObject(linkField, new BasicDBObject("$eq", Link));
        DBCursor cursor = coll.find(query);
        if(cursor.size()>0) {
            try {
                while (true) {
                    JsonParser parser = new JsonParser();
                    JsonObject obj = parser.parse(cursor.next().toString()).getAsJsonObject();
                    JsonObject jobj = new Gson().fromJson(obj.get(nameParam), JsonObject.class);
                    String res = jobj.get(nameField).getAsString() + jobj.get(lnameField).getAsString();
                    list.add(res);
                    if(!cursor.hasNext()) break;
                }
                cursor.close();
                return list;
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else {
            System.out.println("Такого человека нет в базе");
            return null;
        }
    }
    
    /**
     * Метод, формирующий запрос для получения всей известноой информации о пользователях,
     * ФИО которых совпадает с запрашиваемым,
     * затем получающий ответ от сервера и преобразовывающий его в удобный для дальнейшей работы вид
     * @param name Имя искомого пользователя
     * @param Sname Фамилия искомого пользователя
     * @param Colname Название коллекции, в которой его надо искать
     * @return Данные о человеке в строковом виде.
     */
    ArrayList<String> GetData(String name, String Sname, String Colname)
    {
        Gson gson = new Gson();
        ArrayList<String> list= new ArrayList<String>();
        Map<String, Object> map;
        DBCollection coll = db.getCollection(Colname);
        BasicDBObject query = new BasicDBObject(nameParam+"."+nameField, new BasicDBObject("$eq", name))
                .append(nameParam+"."+lnameField, new BasicDBObject("$eq", Sname));
        DBCursor cursor = coll.find(query);
        if(cursor.size()>0) {
            try {
                while (true) {
                    map = gson.fromJson(cursor.next().toString(), new TypeToken<Map<String, Object>>(){}.getType());
//                    map.forEach((x,y)-> list.add(y.toString()));
                    if(!cursor.hasNext()) break;
                }
                cursor.close();
                return list;
            } catch (Exception ex)
            {
                ex.printStackTrace();
                return null;
            }
        }
        else {
            System.out.println("Такого человека нет в базе");
            return null;
        }
    }
    
    /**
     * Установка соединения с СУБД на MongoDB
     * @param address - IP-адрес сервера БД
     * @param port - TCP-порт, который прослушивает сервер
     * @param DBNAme - Название нужной нам БД на сервере
     * @param user - имя поьзователя
     * @param pass - пароль
     * @return true, если соединение удалось установить
     * false, если это не удалось по какой-либо причине
     */
    boolean DBConnect(String address,int port,String DBNAme)
    {
        try {
            mongoClient = new MongoClient(address , port );
            db = mongoClient.getDB( DBNAme );
            return true;
        }catch(Exception ex){
            System.out.println("Неправильно указаны данные сервера БД. Прерывание работы");
            return false;
        }
    }
    
    /**
     * Метод разрывает соединение с сервером БД, если оно существует
     */
    public void CloseConn()
    {
        try
        {
            if (mongoClient != null)
            {
                mongoClient.close();
                System.out.println("Соединение закрыто");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
