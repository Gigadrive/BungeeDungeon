package net.wrathofdungeons.bungeedungeon;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeDungeon extends Plugin {
    private static BungeeDungeon instance;

    public void onEnable(){
        instance = this;
    }

    public static BungeeDungeon getInstance() {
        return instance;
    }
}
