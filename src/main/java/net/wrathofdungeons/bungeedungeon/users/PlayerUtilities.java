package net.wrathofdungeons.bungeedungeon.users;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.wrathofdungeons.bungeedungeon.MySQLManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class PlayerUtilities {
    public static HashMap<String, String> IP_COUNTRY_CACHE = new HashMap<String, String>();

    public static String getCountryCodeFromIP(String ip){
        if(IP_COUNTRY_CACHE.containsKey(ip)) return IP_COUNTRY_CACHE.get(ip);
        try {
            //URL url = new URL("http://api.key-ative.com/ip2location/v1/" + ip);
            URL url = new URL("http://ip-api.com/json/" + ip);
            InputStream stream = url.openStream();
            InputStreamReader inr = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(inr);
            String s = null;
            StringBuilder sb = new StringBuilder();
            while ((s = reader.readLine()) != null) {
                sb.append(s);
            }
            String result = sb.toString();

            JsonElement element = new JsonParser().parse(result);
            JsonObject obj = element.getAsJsonObject();

            String countryCode = obj.get("countryCode").toString();
            countryCode = countryCode.replace("\"", "");

            if(countryCode != null){
                IP_COUNTRY_CACHE.put(ip, countryCode);

                try {
                    PreparedStatement sql = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `ip_info` WHERE `ip` = ?");
                    sql.setString(1,ip);
                    ResultSet sqlRS = sql.executeQuery();

                    if(sqlRS.first()){
                        PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `ip_info` SET `country` = ? WHERE `ip` = ?");
                        ps.setString(1,countryCode);
                        ps.setString(2,ip);
                        ps.executeUpdate();
                        ps.close();
                    } else {
                        PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT IGNORE INTO `ip_info` (`ip`,`country`) VALUES(?,?)");
                        ps.setString(1,ip);
                        ps.setString(2,countryCode);
                        ps.executeUpdate();
                        ps.close();
                    }

                    MySQLManager.getInstance().closeResources(sqlRS,sql);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

            return countryCode;
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
