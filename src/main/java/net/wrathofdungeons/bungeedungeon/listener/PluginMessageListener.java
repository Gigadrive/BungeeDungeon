package net.wrathofdungeons.bungeedungeon.listener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class PluginMessageListener implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e){
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(e.getData()));

        if(e.getTag().equalsIgnoreCase("WDL|INIT") || (e.getTag().equalsIgnoreCase("PERMISSIONSREPL") && new String(e.getData()).contains("mod.worlddownloader"))){
            e.getSender().disconnect(TextComponent.fromLegacyText(ChatColor.RED + "We do not allow the use of World Downloader."));
        }
    }
}
