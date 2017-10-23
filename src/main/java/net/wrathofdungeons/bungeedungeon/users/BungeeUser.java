package net.wrathofdungeons.bungeedungeon.users;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

public class BungeeUser {
    public static HashMap<String,BungeeUser> STORAGE = new HashMap<String,BungeeUser>();

    public static void load(UUID uuid){
        if(STORAGE.containsKey(uuid.toString())) return;

        new BungeeUser(uuid);
    }

    public static void unload(UUID uuid){
        if(STORAGE.containsKey(uuid.toString())){
            STORAGE.remove(uuid.toString()).saveData();
        }
    }

    public static boolean isLoaded(UUID uuid){
        return get(uuid) != null;
    }

    public static BungeeUser get(UUID uuid){
        return STORAGE.getOrDefault(uuid.toString(),null);
    }

    public static boolean isLoaded(ProxiedPlayer p){
        return isLoaded(p.getUniqueId());
    }

    public static BungeeUser get(ProxiedPlayer p){
        return get(p.getUniqueId());
    }

    private UUID uuid;
    private ProxiedPlayer p;
    private Rank rank;

    private boolean joined = false;

    public BungeeUser(UUID uuid){
        this.uuid = uuid;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
            ps.setString(1,uuid.toString());

            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                this.rank = Rank.valueOf(rs.getString("rank"));

                STORAGE.put(uuid.toString(),this);
            } else {
                PreparedStatement insert = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `users` (`uuid`) VALUES(?);");
                insert.setString(1,uuid.toString());
                insert.executeUpdate();
                insert.close();

                new BungeeUser(uuid);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setProxiedPlayer(ProxiedPlayer p){
        this.p = p;
        saveData();
    }

    public ProxiedPlayer getProxiedPlayer() {
        return p;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean hasPermission(Rank minRank){
        return getRank().getID() >= minRank.getID();
    }

    public boolean isStaff(){
        return hasPermission(Rank.MODERATOR);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void callJoin(){
        if(!joined && getProxiedPlayer() != null){
            joined = true;

            p.setDisplayName(Util.limitString(getRank().getColor() + p.getName(),16));
        }
    }

    public void saveData(){
        try {
            if(getProxiedPlayer() != null){
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `users` SET `username` = ?, `rank` = ? WHERE `uuid` = ?");
                ps.setString(1,getProxiedPlayer().getName());
                ps.setString(2,getRank().toString());
                ps.setString(3,getUUID().toString());
                ps.executeUpdate();
                ps.close();
            } else {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `users` SET `rank` = ? WHERE `uuid` = ?");
                ps.setString(1,getRank().toString());
                ps.setString(2,getUUID().toString());
                ps.executeUpdate();
                ps.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
