package net.wrathofdungeons.bungeedungeon.tasks;

import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MotdManager;
import net.wrathofdungeons.bungeedungeon.MySQLManager;

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

            BungeeDungeon.WHITELIST.clear();

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
        });

        BungeeDungeon.getInstance().getProxy().getScheduler().schedule(BungeeDungeon.getInstance(),this,delayInMinutes, TimeUnit.MINUTES);
    }
}
