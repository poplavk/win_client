import java.net.*;
import java.io.*;
import java.util.Scanner;

import facebook4j.*;
import facebook4j.Facebook;
import facebook4j.api.UserMethods;
import facebook4j.auth.AccessToken;


/**
 * Created by Suharev on 20.03.2016.
 */

public class GetSomePrivateData {

    static Scanner in = new Scanner(System.in);

    public static int vkGet(String id)
    {
        String urlParameters = null;
        System.out.println("Берём данные из Вк");
        //Набор fields настраиваем
        urlParameters = "user_ids="+id+"&fields=photo_id,verified,sex,bdate,city,country,home_town,has_photo,photo_400_orig&v=5.50&access_token=";
        URL url;
        HttpURLConnection connection = null;
        try{
            url = new URL("https://api.vk.com/method/users.get");
            connection = (HttpURLConnection)url.openConnection(); //Пытаемся открыть соединение
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches (false);
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

            System.out.println(response);

        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Вк лежит, попробуйте в другой раз");
            return 0;
        }finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return 0;
    }

    public static int fbGet(String id)
    {
        System.out.println("Берём данные из FB");
        // Generate facebook instance.
        Facebook facebook = new FacebookFactory().getInstance();
        // appid & appsecret.
        facebook.setOAuthAppId("1687874214818985", "4eb0dd6c9447db6ea8b80fb384d851f8");
        // Get an access token from:
        // https://developers.facebook.com/tools/explorer
        //facebook.setOAuthPermissions("public_profile,user_about_me,user_birthday,user_hometown");
        String accessTokenString = "1687874214818985|rqc7mvB06PMP5glfNBuNM_Z6OMU";
        AccessToken at = new AccessToken(accessTokenString);
        // Set access token.
        facebook.setOAuthAccessToken(at);

        //100011665834097
        try {
            System.out.println(facebook.getPictureURL(id, 1000,1000));
            System.out.println(facebook.getUser(id));
        }catch(FacebookException fe)
        {
            System.out.println("Нет такого пользователя "+fe.getErrorMessage()+" код: "+fe.getErrorCode());
        }
        return 0;
    }


    public static void main(String[] args) {

        System.out.println("Введите источник данных (fb or vk) или exit для выхода");
        String choice = null;
        while(true)
        {
            choice = in.nextLine();
            System.out.println(choice);
            if (choice.equals("fb") || choice.equals("vk"))
            {
                break;
            }
            if (choice.equals("exit"))
            {
                System.exit(0);
            }
            System.out.println("Неверный ввод, нужно ввести \"fb\" или \"vk\"");
        }
        System.out.println("Укажите идентификатор нужного вам человека");
        String sid=null;
        sid = in.nextLine();
        System.out.println(sid);
        if (choice.equals("vk")) {
                vkGet(sid);
        }
        if(choice.equals("fb")) {
                fbGet(sid);
        }
        System.exit(0);
    }
}
