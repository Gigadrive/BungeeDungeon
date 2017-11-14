package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class PluginMessageListener implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e){
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(e.getData()));

        if(e.getTag().equalsIgnoreCase("WDL|INIT") || (e.getTag().equalsIgnoreCase("PERMISSIONSREPL") && new String(e.getData()).contains("mod.worlddownloader"))){
            e.getSender().disconnect(TextComponent.fromLegacyText(ChatColor.RED + "We do not allow the use of World Downloader."));
        }

        try {
            String subchannel = dis.readUTF();

            if(subchannel.equalsIgnoreCase("globalcommand")){
                String executor = dis.readUTF();
                String command = dis.readUTF();

                //System.out.println("Incoming global command request for " + executor + ": \"" + command + "\"");

                if(executor.equalsIgnoreCase("CONSOLE") || executor.equalsIgnoreCase("BungeeConsole") || BungeeDungeon.getInstance().getProxy().getPlayer(executor) == null){
                    BungeeDungeon.getInstance().getProxy().getPluginManager().dispatchCommand(BungeeDungeon.getInstance().getProxy().getConsole(), command);
                } else {
                    BungeeDungeon.getInstance().getProxy().getPluginManager().dispatchCommand(BungeeDungeon.getInstance().getProxy().getPlayer(executor), command);
                }
            }
        } catch(IOException e1){

        } catch(Exception e1){
            e1.printStackTrace();
        }
    }
}
