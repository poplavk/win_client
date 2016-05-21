package hackDB;

import java.util.ArrayList;

/**
 * Created by Bill Gates on 13.05.2016.
 */
public interface DBInterface {
    ArrayList<String> GetPhoto(String a, String b, String c);

    ArrayList<String> GetLink(String a, String b, String c);

    ArrayList<String> GetName(String a, String b);

    ArrayList<String> GetData(String a, String b, String c);

    boolean DBConnect(String address, int port, String DBNAme, String user, String pass);

    void CloseConn();
}
