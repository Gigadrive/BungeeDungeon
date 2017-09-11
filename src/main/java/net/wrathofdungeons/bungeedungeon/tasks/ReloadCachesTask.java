package net.wrathofdungeons.bungeedungeon.tasks;

import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MotdManager;

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
        });

        BungeeDungeon.getInstance().getProxy().getScheduler().schedule(BungeeDungeon.getInstance(),this,delayInMinutes, TimeUnit.MINUTES);
    }
}
