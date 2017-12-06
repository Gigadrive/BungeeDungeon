package net.wrathofdungeons.bungeedungeon.tasks;

import javafx.print.PageLayout;
import net.md_5.bungee.api.ChatColor;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MotdManager;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.ban.Ban;
import net.wrathofdungeons.bungeedungeon.ban.BanReason;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.users.Rank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ReloadCachesTask implements Runnable {
    private final int delayInMinutes = 5;

    public static void _do(){
        new ReloadCachesTask().run();
    }

    @Override
    public void run() {
        BungeeDungeon.async(() -> {
            BungeeDungeon.getMotdManager().loadFromDatabase();

            PlayerUtilities.IP_COUNTRY_CACHE.clear();
            PlayerUtilities.NAME_UUID_CACHE.clear();
            PlayerUtilities.UUID_NAME_CACHE.clear();
            PlayerUtilities.UUID_SETTINGS_CACHE.clear();
            PlayerUtilities.UUID_FRIENDREQUESTS_CACHE.clear();

            BungeeDungeon.WHITELIST.clear();

            BanReason.init();
            Ban.STORAGE.clear();

            try {
                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("SELECT * FROM `whitelist`");
                ResultSet rs = ps.executeQuery();
                rs.beforeFirst();

                while(rs.next()){
                    BungeeDungeon.WHITELIST.add(UUID.fromString(rs.getString("uuid")));
                }

                MySQLManager.getInstance().closeResources(rs,ps);
            } catch(Exception e){
                e.printStackTrace();
            }

            //BungeeDungeon.createStaffMessage(ChatColor.GRAY + "[Staff] " + ChatColor.GOLD + "The cache has been reloaded.", Rank.ADMIN);
        });

        BungeeDungeon.getInstance().getProxy().getScheduler().schedule(BungeeDungeon.getInstance(),this,delayInMinutes, TimeUnit.MINUTES);
    }
}
