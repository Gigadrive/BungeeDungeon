package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.Rank;

public class QuitListener implements Listener {
    @EventHandler
    public void onQuit(PlayerDisconnectEvent e){
        ProxiedPlayer p = e.getPlayer();

        if(BungeeDungeon.ON_SERVER.contains(p.getUniqueId().toString())){
            BungeeDungeon.ON_SERVER.remove(p.getUniqueId().toString());
        }

        if (BungeeUser.isLoaded(p)) {
            BungeeUser u = BungeeUser.get(p);

            if (u.hasPermission(Rank.MODERATOR))
                BungeeDungeon.createStaffMessage(ChatColor.DARK_RED + "[STAFF] " + u.getRank().getColor() + p.getName() + ChatColor.RED + " is now " + ChatColor.DARK_RED + "offline" + ChatColor.RED + ".", Rank.MODERATOR);

            BungeeUser.unload(p.getUniqueId());
        }
    }
}
