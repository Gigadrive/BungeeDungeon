package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;

import java.util.ArrayList;
import java.util.UUID;

public class PingListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPing(ProxyPingEvent e){
        ServerPing response = e.getResponse();

        String[] hoverInfo = new String[]{
                ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Wrath of Dungeons",
                "",
                ChatColor.YELLOW + "Website: " + ChatColor.AQUA + "https://wrathofdungeons.net"
        };

        ArrayList<ServerPing.PlayerInfo> a = new ArrayList<ServerPing.PlayerInfo>();
        for(String s : hoverInfo){
            a.add(new ServerPing.PlayerInfo(s,UUID.randomUUID()));
        }

        response.setDescription(BungeeDungeon.getMotdManager().getConvertedString());
        response.setPlayers(new ServerPing.Players(3000,BungeeDungeon.getInstance().getProxy().getOnlineCount(),a.toArray(new ServerPing.PlayerInfo[]{})));

        e.setResponse(response);
    }
}
