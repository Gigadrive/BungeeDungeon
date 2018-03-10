package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {
    public PingCommand(){
        super("ping",null,"versatel");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer)
            ((ProxiedPlayer) sender).sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN.toString() + "Ping: " + ((ProxiedPlayer) sender).getPing() + " ms"));
    }
}
