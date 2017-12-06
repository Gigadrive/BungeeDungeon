package net.wrathofdungeons.bungeedungeon.ban;

import net.md_5.bungee.api.ChatColor;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Ban {
    public static ArrayList<Ban> STORAGE = new ArrayList<Ban>();

    public static Ban getBan(int id){
        return getBan(id,true);
    }

    public static Ban getBan(int id, boolean retry){
        for(Ban ban : STORAGE) if(ban.getId() == id) return ban;

        new Ban(id);

        return retry ? getBan(id,false) : null;
    }

    public static Ban getBan(UUID uuid){
        Ban b = null;

        for(Ban ban : STORAGE) if(ban.getUUID().toString().equals(uuid.toString()) && ban.isActive()) b = ban;

        if(b == null){
            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `bans` WHERE `uuid` = ? AND `active` = ?");
                ps.setString(1,uuid.toString());
                ps.setBoolean(2,true);

                ResultSet rs = ps.executeQuery();
                if(rs.first()){
                    b = Ban.getBan(rs.getInt("id"));

                    if(!b.isActive()) b = null;
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        return b;
    }

    private int id;
    private UUID uuid;
    private int banReason;
    private Timestamp time;
    private Timestamp endDate;
    private UUID staff;
    private boolean active;
    private UUID unbanStaff;
    private Timestamp unbanTime;
    private String unbanReason;

    public Ban(int id){
        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `bans` WHERE `id` = ? AND `active` = ?");
            ps.setInt(1,id);
            ps.setBoolean(2,true);
            ResultSet rs = ps.executeQuery();

            if(rs.first()){
                this.id = id;
                this.uuid = UUID.fromString(rs.getString("uuid"));
                this.banReason = rs.getInt("reason");
                this.time = rs.getTimestamp("time");
                this.endDate = rs.getTimestamp("endDate");
                this.staff = rs.getString("staff") != null ? UUID.fromString(rs.getString("staff")) : null;
                this.active = rs.getBoolean("active");
                this.unbanStaff = rs.getString("unban.staff") != null ? UUID.fromString(rs.getString("unban.staff")) : null;
                this.unbanTime = rs.getTimestamp("unban.time");
                this.unbanReason = rs.getString("unban.reason");

                checkExpiry();

                STORAGE.add(this);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getId() {
        checkExpiry();
        return id;
    }

    public UUID getUUID() {
        checkExpiry();
        return uuid;
    }

    public BanReason getBanReason() {
        checkExpiry();
        return BanReason.getBanReason(banReason);
    }

    public Timestamp getTime() {
        checkExpiry();
        return time;
    }

    public Timestamp getEndDate() {
        checkExpiry();
        return endDate;
    }

    public UUID getStaff() {
        checkExpiry();
        return staff;
    }

    public boolean isActive() {
        checkExpiry();
        return active;
    }

    public UUID getUnbanStaff() {
        checkExpiry();
        return unbanStaff;
    }

    public Timestamp getUnbanTime() {
        checkExpiry();
        return unbanTime;
    }

    public String getUnbanReason() {
        checkExpiry();
        return unbanReason;
    }

    public String getDisconnectMessage(){
        if(getEndDate() == null){
            return "" +
                            ChatColor.WHITE + "Your account has been banned\n" +
                            ChatColor.WHITE + "from the Wrath of Dungeons network!\n" +
                            "\n" +
                            ChatColor.DARK_RED + "Reason: " + ChatColor.RED + getBanReason().getName() +
                            "\n" +
                            ChatColor.DARK_AQUA + "Appeal at " + ChatColor.GOLD + "wrathofdungeons.net";
        } else {
            /*long seconds = new Timestamp(System.currentTimeMillis()).getTime()-getEndDate().getTime() / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            String time = days + " days " + hours % 24 + " hours " + minutes % 60 + " minutes " + seconds % 60 + " seconds";*/

            long different = new Timestamp(System.currentTimeMillis()).getTime()-getEndDate().getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = (different / daysInMilli)/-1;
            different = different % daysInMilli;

            long elapsedHours = (different / hoursInMilli)/-1;
            different = different % hoursInMilli;

            long elapsedMinutes = (different / minutesInMilli)/-1;
            different = different % minutesInMilli;

            long elapsedSeconds = (different / secondsInMilli)/-1;

            String time = elapsedDays + " days " + elapsedHours + " hours " + elapsedMinutes + " minutes " + elapsedSeconds + " seconds";

            return "" +
                            ChatColor.WHITE + "Your account has been banned\n" +
                            ChatColor.WHITE + "from the Wrath of Dungeons network!\n" +
                            "\n" +
                            ChatColor.DARK_RED + "Reason: " + ChatColor.RED + getBanReason().getName() + "\n" +
                            ChatColor.DARK_RED + "Expiry: " + ChatColor.RED + time + "\n" +
                            "\n" +
                            ChatColor.DARK_AQUA + "Appeal at " + ChatColor.GOLD + "wrathofdungeons.net";
        }
    }

    public void unban(UUID staff, String reason) throws SQLException {
        if(active){
            active = false;
            STORAGE.remove(this);

            Timestamp now = new Timestamp(System.currentTimeMillis());
            String name = PlayerUtilities.getNameFromUUID(uuid);

            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `bans` SET `active` = ?, `unban.staff` = ?, `unban.time` = ?, `unban.reason` = ? WHERE `id` = ?");
            ps.setBoolean(1,active);
            ps.setString(2,staff != null ? staff.toString() : null);
            ps.setTimestamp(3,now);
            ps.setString(4,reason);
            ps.setInt(5,id);
            ps.executeUpdate();
            ps.close();
        }
    }

    private void checkExpiry(){
        if(active){
            Timestamp now = new Timestamp(System.currentTimeMillis());

            if(endDate != null && now.after(endDate)){
                active = false;
                STORAGE.remove(this);

                BungeeDungeon.async(() -> {
                    try {
                        PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("UPDATE `bans` SET `active` = ? WHERE `id` = ?");
                        ps.setBoolean(1,active);
                        ps.setInt(2,id);
                        ps.executeUpdate();
                        ps.close();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
