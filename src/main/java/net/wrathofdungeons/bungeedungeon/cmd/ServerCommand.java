package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.Rank;

public class ServerCommand extends Command {
    public ServerCommand(){
        super("server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.hasPermission(Rank.MODERATOR)){
                    if(args.length == 1){
                        String name = args[0];
                        ServerInfo info = ProxyServer.getInstance().getServerInfo(name);

                        if(info != null){
                            p.connect(info);
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That server could not be found."));
                        }
                    } else {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/server <Name>"));
                    }
                } else {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown command."));
                }
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have to be a player to execute this command."));
        }
    }
}
