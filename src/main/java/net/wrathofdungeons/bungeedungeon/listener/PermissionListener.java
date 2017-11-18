package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.Rank;

public class PermissionListener implements Listener {
    @EventHandler
    public void onCheck(PermissionCheckEvent e){
        if(e.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)e.getSender();

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.hasPermission(Rank.ADMIN)) e.setHasPermission(true);
            }
        }
    }
}
