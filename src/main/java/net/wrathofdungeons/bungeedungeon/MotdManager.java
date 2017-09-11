package net.wrathofdungeons.bungeedungeon;

import net.md_5.bungee.api.ChatColor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MotdManager {
    private String firstLine;
    private String secondLine;

    public MotdManager(){
        this.firstLine = ChatColor.RED + "Wrath of Dungeons";
        this.secondLine = null;
    }

    public String getFirstLine(){
        return this.firstLine;
    }

    public String getSecondLine() {
        return this.secondLine;
    }

    public void setFirstLine(String s){
        this.firstLine = s;
    }

    public void setSecondLine(String s){
        this.secondLine = s;
    }

    public String getConvertedString(){
        String s = "";

        if(getFirstLine() != null) s += getFirstLine();
        s += "\n";
        if(getSecondLine() != null) s += getSecondLine();

        return s;
    }

    public void loadFromDatabase(){
        BungeeDungeon.async(() -> {
            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `motd_manager` ORDER BY `time` DESC LIMIT 1");
                ResultSet rs = ps.executeQuery();

                if(rs.first()){
                    this.firstLine = ChatColor.translateAlternateColorCodes('&',rs.getString("line1"));
                    this.secondLine = ChatColor.translateAlternateColorCodes('&',rs.getString("line2"));
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }
        });
    }
}
