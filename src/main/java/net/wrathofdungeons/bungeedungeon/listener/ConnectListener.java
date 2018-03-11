package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;

public class ConnectListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnect(ServerConnectEvent e){
        if(e.isCancelled()) return;

        ProxiedPlayer p = e.getPlayer();

        boolean firstJoin = !BungeeDungeon.ON_SERVER.contains(p.getUniqueId().toString());

        if(firstJoin){
            // TODO: Handle target server
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConnected(ServerConnectedEvent e){
        ProxiedPlayer p = e.getPlayer();

        boolean firstJoin = !BungeeDungeon.ON_SERVER.contains(p.getUniqueId().toString());

        if(firstJoin){
            BungeeDungeon.ON_SERVER.add(p.getUniqueId().toString());

            if(BungeeUser.isLoaded(p.getUniqueId())){
                BungeeUser u = BungeeUser.get(p.getUniqueId());
                u.callJoin();
            }
        }
    }
}
