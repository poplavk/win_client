package hackDB;

import java.util.ArrayList;
import java.util.Map;

import com.google.gson.reflect.TypeToken;
import com.mongodb.*;

import com.google.gson.*;

/**
 * Created by Bill Gates on 15.05.2016.
 */
public class Mongo {
    DB db;
    MongoClient mongoClient;
    String nameParam;
    String nameField;
    String lnameField;
    String linkField;
    String picField;
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
