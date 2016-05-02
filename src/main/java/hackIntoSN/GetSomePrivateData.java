package hackIntoSN;

import java.net.*;
import java.io.*;
import java.util.*;

import com.restfb.*;
import com.restfb.FacebookClient.*;

import com.google.gson.*;
import com.restfb.exception.*;
import com.restfb.types.*;

// GSON
// search.maven.org/remotecontent?filepath=com/google/code/gson/gson/2.6.2/gson-2.6.2.jar
// restfb
// http://mvnrepository.com/artifact/com.restfb/restfb/1.22.0
/**
 * Created by Suharev on 20.03.2016.
 * Updated on 26.04.2016.
 */

public class GetSomePrivateData {

    static Scanner in = new Scanner(System.in);
    //Эти 2 метода проверяют заполнено ли нужное нам поле у пользователя вк, дабы не нарваться на NullPntrExc в ходе работы
    public String vkcheckfield(JsonElement user,String fname)
    {
        String field=null;
        try{
            JsonObject userObject = user.getAsJsonObject();
            field=userObject.get(fname).getAsString();
        }catch(NullPointerException npe)
        {
            field=null;
        }
        return field;
    }
    //Этот метод отличается только тем, что проверяет объекты внутри объекта Json
    public String vkcheckfield(JsonElement user,String fname,String subname)
    {
        String field=null;
        try{
            JsonObject userObject = user.getAsJsonObject();
            JsonObject userObject2;
            JsonElement jsel;
            jsel = userObject.get(fname); userObject2=jsel.getAsJsonObject();
            field=userObject2.get(subname).getAsString();
        }catch(NullPointerException npe)
        {
            field=null;
        }
        return field;
    }

    //links - массив идентификаторов пользователя, которые хотим отправить, setts-массив с настройками
    public int vkGet(ArrayList<String> links, byte[] setts)
    {
        String urlParameters = null;
        byte cnt=0;
        //String[] gender = {"Не указан", "Женский","Мужской"};//Пригодится, если захотим вывести пол
        URL url;
        HttpURLConnection connection = null;
        String[][] results=new String[links.size()][7]; //Массив результатов, его отправить в нужный метод GUI стоит
        System.out.println("Берём данные из Вк");
        while(cnt<links.size())
        {
            //Формируем строку параметров и настраиваем сам запрос
            urlParameters = "user_ids="+links.get(cnt)+"&fields=id,sex,photo_max_orig,bdate,city,country,occupation,contacts&v=5.50&access_token=";
            try{
                url = new URL("https://api.vk.com/method/users.get");
                connection = (HttpURLConnection) url.openConnection(); //Пытаемся открыть соединение
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                //Отправляем запрос
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream ());
                wr.writeBytes (urlParameters);
                wr.flush ();
                wr.close ();

                //Получаем ответ
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();

                JsonParser parser = new JsonParser();
                JsonObject mainObject = parser.parse(response.toString()).getAsJsonObject();
                JsonArray pItem = mainObject.getAsJsonArray("response");
                //Заполняем массив результатов, отталкиваясь от массива настроек
                for (JsonElement user : pItem) {
                    if(setts[1]==1)results[cnt][0]=vkcheckfield(user,"photo_max_orig");
                    if(setts[2]==1){results[cnt][1]=vkcheckfield(user,"first_name"); results[cnt][2]=vkcheckfield(user,"last_name");}
                    if(setts[3]==1)results[cnt][3]=vkcheckfield(user,"bdate");
                    if(setts[4]==1){results[cnt][4]=vkcheckfield(user,"country","title"); results[cnt][5]=vkcheckfield(user,"city","title");}
                    if(setts[5]==1)results[cnt][5]=vkcheckfield(user,"occupation","name");
                    if(setts[6]==1)results[cnt][6]=vkcheckfield(user,"contacts","phone");
                    //Не знаю, что делать с последними двумя
                }
            }catch(Exception e){
                System.out.println("Серверы vk не отвечают на запрос");
                e.printStackTrace();
                System.out.println(e.getMessage());
                connection.disconnect();
                return -1;
            }
            connection.disconnect();
            cnt++;
        }
        //Если надо проверить содержимое рез. массива
        /*for(int i=0;i<links.size();i++)
        {
            for(int j=0;j<7;j++)
            {
                System.out.println(results[i][j]);
            }
        }*/
        return 0;
    }

    public int fbGet(ArrayList<String> links)
    {
        byte cnt=0;
        String[][] results=new String[links.size()][2];
        System.out.println("Берём данные из FB");
        String accessToken ="EAAXZCHNbZC9KkBAPNgRdVprb0rBJRgPgfYNAz3vv2gpW74KO1g38JnF1YVtgKtUDRChFJwafjZBmoY8guJlWBNL0woEWO0Jx7pZA4akFY56h1JlUlwQ2lhr1w0zWu9EyjZAjL2vgSpPQETTWD828jCfJWs0gcMNxjzdygyAR59SpVurdCri5YZBbYDG00PoiKD2ED2aL01zsb449q3VNEe";
        String appID = "1687874214818985";
        String appSecret = "4eb0dd6c9447db6ea8b80fb384d851f8";
        FacebookClient fbc = new DefaultFacebookClient(accessToken);
        AccessToken exAccessToken = fbc.obtainExtendedAccessToken(appID,appSecret);

        //100011665834097 Эти 2 идентификатора точно не удалены и на них можно проверить работу метода
        //100007537696927 Лучше вводить именно идентификаторы, ибо ники не принимаются GraphAPI
        while(cnt<links.size()) {
            try {
                User user = fbc.fetchObject(links.get(cnt), User.class);
                results[cnt][0] = user.getId();
                results[cnt][1] = user.getName();
            }catch(FacebookGraphException fge)
            {
                System.out.println("Пользователя с идентификатором "+links.get(cnt)+" в FB нет");
            }
            cnt++;
        }
        //Если надо проверить содержимое рез. массива
        /*for(int i=0;i<links.size();i++)
        {
            for(int j=0;j<2;j++)
            {
                System.out.println(results[i][j]);
            }
        }*/
        return 0;
    }
}
