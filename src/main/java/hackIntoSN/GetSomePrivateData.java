package hackIntoSN;

import aleksey2093.GiveMeSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.exception.FacebookGraphException;
import com.restfb.types.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import javafx.scene.image.Image;

import java.util.Scanner;

/**
 * Created by Suharev on 20.03.2016.
 * Класс, работающий с API социальных сетей vkontakte и Facebook
 * Конкретно - запршивает оттуда данные о людях, опираясь на полученне ссылки
 * Содержит о одному методу для работы с каждо из соц. сетей
 * И два доп. метода для работы с vk (проверяют результаты)
 */

public class GetSomePrivateData {

    /**
     * Метод проверяет заполнено ли нужное нам поле у пользователя вк, дабы не нарваться на NullPntrExc в ходе работы
     * @param user Объект класса JsonElement, который проверяем
     * @param fname - имя поля внутри объекта, которое надо проверить
     * @return строковое значение объекта JsonElement или null, если оно пустое
     */
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
    
    /**
     * Метод проверяет заполнено ли нужное нам поле у пользователя вк, дабы не нарваться на NullPntrExc в ходе работы
     * Этот вариант должен принимать объект JSON с подполями
     * @param user Объект класса JsonElement, который проверяем
     * @param fname - имя поля внутри объекта, которое надо проверить
     * @param subname - имя подполя, которое надо проверить
     * @return строковое значение объекта JsonElement или null, если оно пустое*/    public String vkcheckfield(JsonElement user,String fname,String subname)
    {
        String field;
        try{
            JsonObject userObject = user.getAsJsonObject();
            JsonObject userObject2;
            JsonElement jsel;
            jsel = userObject.get(fname); userObject2=jsel.getAsJsonObject();
            field=userObject2.get(subname).getAsString();
        }catch(NullPointerException npe)
        {
            field="";
        }
        return field;
    }

    /**
     * Метод принимает ArrayList ссылок, отправляет их по одному в API сервиса и полуает в ответ данные о
     * людях, которые преобразовывает из JSON в результирующий массив объектов PersonInfo
     * @param links - массив идентификаторов пользователя, данные о которых надо получить
     * @return Массив объектов PersonInfo
     */    public ArrayList<PersonInfo> vkGet(ArrayList<String> links)
    {
        GiveMeSettings giveMeSettings = new GiveMeSettings();
        boolean[] setts = giveMeSettings.getSocialStg();
        if (setts.length == 1 && !setts[0])
            return null;
        String urlParameters = null;
        byte cnt=0;
        //String[] gender = {"Не указан", "Женский","Мужской"};//Пригодится, если захотим вывести пол
        URL url;
        HttpURLConnection connection = null;
        ArrayList<PersonInfo> results = new ArrayList<PersonInfo>();
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
                    PersonInfo pi = new PersonInfo();
                    if(setts[1])pi.image=new Image(vkcheckfield(user,"photo_max_orig"));
                    if(setts[2]){pi.first_name=vkcheckfield(user,"first_name"); pi.last_name=vkcheckfield(user,"last_name");}
                    if(setts[3])pi.birthday=vkcheckfield(user,"bdate");
                    if(setts[4]){pi.country=vkcheckfield(user,"country","title"); pi.city=vkcheckfield(user,"city","title");}
                    if(setts[5])pi.occupation=vkcheckfield(user,"occupation","name");
                    if(setts[6])pi.phone=vkcheckfield(user,"contacts","phone");
                    pi.link=links.get(cnt);
                    results.add(pi);
                }
            }catch(Exception e){
                System.out.println("Серверы vk не отвечают на запрос");
                e.printStackTrace();
                System.out.println(e.getMessage());
                assert connection != null;
                connection.disconnect();
                return null;
            }
            connection.disconnect();
            cnt++;
        }
        //Если надо проверить содержимое рез. массива
        for(int i=0;i<links.size();i++)
        {
            System.out.println(results.get(i));
        }
        return results;
    }

    /**
     * Метод принимает ArrayList ссылок, отправляет их по одному в API сервиса и полуает в ответ данные о
     * людях, которые выводит на экран (пока что)
     * @param links - массив идентификаторов пользователя, данные о которых надо получить
     * @return 0 в случае успеха
     */
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
