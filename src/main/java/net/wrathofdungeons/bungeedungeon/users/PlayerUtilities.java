package net.wrathofdungeons.bungeedungeon.users;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MySQLManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class PlayerUtilities {
    public static HashMap<UUID,Rank> UUID_RANK_CACHE = new HashMap<UUID,Rank>();
    public static HashMap<String,UUID> NAME_UUID_CACHE = new HashMap<String,UUID>();
    public static HashMap<UUID,String> UUID_NAME_CACHE = new HashMap<UUID,String>();
    public static HashMap<UUID,ArrayList<String>> UUID_FRIENDREQUESTS_CACHE = new HashMap<UUID,ArrayList<String>>();
    public static HashMap<UUID,HashMap<String,Boolean>> UUID_SETTINGS_CACHE = new HashMap<UUID,HashMap<String,Boolean>>();
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

    public static Rank getRankFromUUID(String uuid){
        return getRankFromUUID(UUID.fromString(uuid));
    }

    public static Rank getRankFromUUID(UUID uuid){
        if(uuid == null) return Rank.USER;

        if(BungeeDungeon.getInstance().getProxy().getPlayer(uuid) != null) return BungeeUser.get(BungeeDungeon.getInstance().getProxy().getPlayer(uuid)).getRank();

        if(UUID_RANK_CACHE.containsKey(uuid)){
            return UUID_RANK_CACHE.get(uuid);
        } else {
            Rank r = Rank.USER;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
                ps.setString(1,uuid.toString());

                ResultSet rs = ps.executeQuery();
                if(rs.first()){
                    r = Rank.valueOf(rs.getString("rank"));
                    UUID_RANK_CACHE.put(uuid,r);
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            return r;
        }
    }

    public static UUID getUUIDFromName(String name){
        if(name == null || name.isEmpty()) return null;

        if(BungeeDungeon.getInstance().getProxy().getPlayer(name) != null) return BungeeDungeon.getInstance().getProxy().getPlayer(name).getUniqueId();

        if(NAME_UUID_CACHE.containsKey(name)){
            return NAME_UUID_CACHE.get(name);
        } else {
            UUID uuid = null;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `username` = ?");
                ps.setString(1,name);

                ResultSet rs = ps.executeQuery();
                if(rs.first()){
                    name = rs.getString("username");
                    String u = rs.getString("uuid");
                    uuid = UUID.fromString(u);

                    NAME_UUID_CACHE.put(name,uuid);
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            if(uuid == null){
                String u = UUIDFetcher.getUUID(name);

                if(u != null && !u.isEmpty()){
                    uuid = UUID.fromString(u);
                }
            }

            return uuid;
        }
    }

    public static String getNameFromUUID(String uuid){
        return getNameFromUUID(UUID.fromString(uuid));
    }

    public static String getNameFromUUID(UUID uuid){
        if(uuid == null) return null;

        if(BungeeDungeon.getInstance().getProxy().getPlayer(uuid) != null) return BungeeDungeon.getInstance().getProxy().getPlayer(uuid).getName();

        if(UUID_NAME_CACHE.containsKey(uuid)){
            return UUID_NAME_CACHE.get(uuid);
        } else {
            String name = null;

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
                ps.setString(1,uuid.toString());

                ResultSet rs = ps.executeQuery();
                if(rs.first()){
                    name = rs.getString("username");

                    UUID_NAME_CACHE.put(uuid,name);
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            return name;
        }
    }

    public static ArrayList<String> getFriendRequestsToUUID(UUID uuid){
        if(uuid == null) return null;

        if(BungeeDungeon.getInstance().getProxy().getPlayer(uuid) != null && BungeeUser.isLoaded(uuid)) return BungeeUser.get(uuid).getFriendRequests();

        if(UUID_FRIENDREQUESTS_CACHE.containsKey(uuid)){
            return UUID_FRIENDREQUESTS_CACHE.get(uuid);
        } else {
            ArrayList<String> friendRequests = new ArrayList<String>();

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `friend_requests` WHERE `to` = ?");
                ps.setString(1,uuid.toString());

                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    friendRequests.add(rs.getString("from"));
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            UUID_FRIENDREQUESTS_CACHE.put(uuid,friendRequests);

            return friendRequests;
        }
    }
}
