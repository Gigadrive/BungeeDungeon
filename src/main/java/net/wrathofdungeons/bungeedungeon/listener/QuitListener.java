package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerDisconnectEvent e){
        ProxiedPlayer p = e.getPlayer();

        if(BungeeDungeon.ON_SERVER.contains(p.getUniqueId().toString())){
            BungeeDungeon.ON_SERVER.remove(p.getUniqueId().toString());
        }

        BungeeUser.unload(p.getUniqueId());
    }
}
