//На случай, если понадобится что-то проверить
package hackDB;

import java.util.ArrayList;

/**
 * Created by Bill Gates on 15.05.2016.
 */

public class Main {
    public static void main(String[] args)
    {
        SQL mydb = new SQL();
        mydb.nameField="First_name";
        mydb.lnameField="Second_name";
        mydb.linkField="Link";
        mydb.picField="PImage";
        if(mydb.DBConnect("127.0.0.1",4226,"PryDB","sa","111111"))
        {
            ArrayList<String> list = new ArrayList<String>();
            //list = mydb.GetPhoto("Vlad","Ivanov","PersonInfo");
            list = mydb.GetData("Vlad","Ivanov","PersonInfo");
            //list = mydb.GetLink("Vlad","Ivanov","PersonInfo");
            //list = mydb.GetName("ivanov561","PersonInfo");
            for(int i=0;i<list.size();i++)
            {
                System.out.println(list.get(i));
            }
        }
        mydb.CloseConn();
        ArrayList<String> list;
        Mongo MDB = new Mongo();
        MDB.nameParam="name";
        MDB.nameField="first";
        MDB.lnameField="last";
        MDB.linkField="link";
        MDB.picField="picture";
        if(MDB.DBConnect("127.0.0.1",27017,"PryDB"))
        {
            System.out.println("Соедиение установлено. Можно работать");
            list=MDB.GetData("Lisa","Gillespie","persons");
            //list = MDB.GetName("magna","persons");
            //list = MDB.GetLink("Lisa","Gillespie","persons");
            //list = MDB.GetPhoto("Lisa","Gillespie","persons");
            System.out.println(list);
            MDB.CloseConn();
        }
    }
}
