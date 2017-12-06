package net.wrathofdungeons.bungeedungeon.ban;

import net.wrathofdungeons.bungeedungeon.MySQLManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BanReason {
    public static ArrayList<BanReason> STORAGE = new ArrayList<BanReason>();

    public static void init(){
        STORAGE.clear();

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`banReasons`");
            ResultSet rs = ps.executeQuery();

            while(rs.next()) new BanReason(rs.getInt("id"));

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static BanReason getBanReason(int id){
        for(BanReason b : STORAGE) if(b.getId() == id) return b;

        return null;
    }

    private int id;
    private String name;
    private String description;
    private Category category;
    private int icon;
    private int iconDurability;
    private PunishmentType typeOfPunishment;
    private int timeValue;
    private String timeUnit;

    public BanReason(int id){
        if(getBanReason(id) != null) return;

        try {
            PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `wrathofdungeons`.`banReasons` WHERE `id` = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();

            if(rs.first()){
                this.id = id;
                this.name = rs.getString("name");
                this.description = rs.getString("description");
                this.category = Category.valueOf(rs.getString("category"));
                this.icon = rs.getInt("icon");
                this.iconDurability = rs.getInt("iconDurability");
                this.typeOfPunishment = PunishmentType.valueOf(rs.getString("typeOfPunishment"));
                this.timeValue = rs.getInt("timeValue");
                this.timeUnit = rs.getString("timeUnit");

                STORAGE.add(this);
            }

            MySQLManager.getInstance().closeResources(rs,ps);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getIcon() {
        return icon;
    }

    public int getIconDurability() {
        return iconDurability;
    }

    public PunishmentType getTypeOfPunishment() {
        return typeOfPunishment;
    }

    public int getTimeValue() {
        return timeValue;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public long timeToAdd(){
        long l = getTimeValue();

        switch(getTimeUnit()){
            case "SECONDS":
                l *= 1000;
                break;
            case "MINUTES":
                l *= 1000*60;
                break;
            case "HOURS":
                l *= 1000*60*60;
                break;
            case "DAYS":
                l *= 1000*60*60*24;
                break;
            case "WEEKS":
                l *= 1000*60*60*24*7;
                break;
            case "MONTHS":
                l *= 1000*60*60*24*7*30;
                break;
            case "YEARS":
                l *= 1000*60*60*24*7*30*12;
                break;
        }

        return l;
    }

    public enum Category {
        GENERAL,HACKING,CHAT_BEHAVIOR
    }

    public enum PunishmentType {
        KICK,BAN,PERMBAN
    }
}
