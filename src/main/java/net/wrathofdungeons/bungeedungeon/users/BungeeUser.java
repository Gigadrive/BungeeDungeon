package net.wrathofdungeons.bungeedungeon.users;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.DefaultFontInfo;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
        return p != null && isLoaded(p.getUniqueId());
    }

    public static BungeeUser get(ProxiedPlayer p){
        return get(p.getUniqueId());
    }

    private UUID uuid;
    private ProxiedPlayer p;
    private Rank rank;
    private ArrayList<String> friends;
    private ArrayList<String> friendRequests;

    private boolean joined = false;
    private UserSettingsManager settingsManager;

    public ProxiedPlayer lastMsg;

    public BungeeUser(UUID uuid){
        this.uuid = uuid;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
            ps.setString(1,uuid.toString());

            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                this.rank = Rank.valueOf(rs.getString("rank"));
                this.settingsManager = BungeeDungeon.GSON.fromJson(rs.getString("settings"),UserSettingsManager.class);

                reloadFriends();
                reloadFriendRequests();
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

    public void reloadFriends(){
        if(friends == null){
            friends = new ArrayList<String>();
        } else {
            friends.clear();
        }

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `friendships` WHERE `player1` = ? OR `player2` = ?");
            ps.setString(1,uuid.toString());
            ps.setString(2,uuid.toString());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                String player1 = rs.getString("player1");
                String player2 = rs.getString("player2");

                if(player1.equals(uuid.toString())){
                    friends.add(player2);
                } else {
                    friends.add(player1);
                }
            }

            MySQLManager.getInstance().closeResources(rs,ps);

            if(STORAGE.containsKey(uuid.toString())) reloadSpigotFriends();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void reloadFriendRequests(){
        if(friendRequests == null){
            friendRequests = new ArrayList<String>();
        } else {
            friendRequests.clear();
        }

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `friend_requests` WHERE `to` = ?");
            ps.setString(1,uuid.toString());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                friendRequests.add(rs.getString("from"));
            }

            MySQLManager.getInstance().closeResources(rs,ps);

            if(!STORAGE.containsKey(uuid.toString())){
                STORAGE.put(uuid.toString(),this);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void sendCenteredMessage(String message){
        sendCenteredMessage(message,true);
    }

    public String sendCenteredMessage(String message, boolean send){
        ProxiedPlayer player = p;
        int CENTER_PX = 154;
        int MAX_PX = 250;

        message = ChatColor.translateAlternateColorCodes('&', message);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
        int charIndex = 0;
        int lastSpaceIndex = 0;
        String toSendAfter = null;
        String recentColorCode = "";
        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                recentColorCode = "ยง" + c;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else if(c == ' ') lastSpaceIndex = charIndex;
            else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
            if(messagePxSize >= MAX_PX){
                toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1, message.length());
                message = message.substring(0, lastSpaceIndex + 1);
                break;
            }
            charIndex++;
        }
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        String s = sb.toString() + message;
        if(send) player.sendMessage(s);
        if(toSendAfter != null) sendCenteredMessage(toSendAfter);
        return s;
    }

    public void reloadSettings(){
        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `users` WHERE `uuid` = ?");
            ps.setString(1,uuid.toString());

            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                settingsManager = BungeeDungeon.GSON.fromJson(rs.getString("settings"),UserSettingsManager.class);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void reloadSpigotFriends(){
        if(p != null && p.getServer() != null) BungeeDungeon.sendToBukkit(p.getServer(),"reloadFriends",p.getName());
    }

    public void setProxiedPlayer(ProxiedPlayer p){
        this.p = p;
        saveData();
    }

    public UserSettingsManager getSettings() {
        return settingsManager;
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

    public ArrayList<String> getFriends() {
        return friends;
    }

    public ArrayList<String> getFriendRequests() {
        return friendRequests;
    }

    public void clearCaches(){
        PlayerUtilities.UUID_RANK_CACHE.remove(p.getUniqueId());
        PlayerUtilities.NAME_UUID_CACHE.remove(p.getName());
        PlayerUtilities.UUID_NAME_CACHE.remove(p.getUniqueId());
        PlayerUtilities.UUID_FRIENDREQUESTS_CACHE.remove(p.getUniqueId());
        PlayerUtilities.UUID_SETTINGS_CACHE.remove(p.getUniqueId());
    }

    public void callJoin(){
        if(!joined && getProxiedPlayer() != null){
            joined = true;

            clearCaches();

            p.setDisplayName(Util.limitString(getRank().getColor() + p.getName(),16));

            if(getFriendRequests().size() == 1){
                p.sendMessage(new ComponentBuilder(ChatColor.AQUA + "You have " + ChatColor.YELLOW + getFriendRequests().size() + ChatColor.AQUA + " open friend request. Click here to review it.").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend requests")).create());
            } else if(getFriendRequests().size() > 1){
                p.sendMessage(new ComponentBuilder(ChatColor.AQUA + "You have " + ChatColor.YELLOW + getFriendRequests().size() + ChatColor.AQUA + " open friend requests. Click here to review them.").event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend requests")).create());
            }
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
