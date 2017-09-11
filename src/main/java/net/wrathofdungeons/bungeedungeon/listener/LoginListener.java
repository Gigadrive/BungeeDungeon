package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;

import java.util.UUID;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(LoginEvent e){
        UUID uuid = e.getConnection().getUniqueId();

        if(uuid != null){
            if(BungeeDungeon.WHITELIST_ENABLED){
                if(!BungeeDungeon.isWhitelisted(uuid)){
                    e.setCancelReason(ChatColor.RED + "You are not whitelisted.");
                    e.setCancelled(true);
                }
            }
        } else {
            e.setCancelReason(ChatColor.RED + "No UUID submitted.");
            e.setCancelled(true);
        }
    }
}
