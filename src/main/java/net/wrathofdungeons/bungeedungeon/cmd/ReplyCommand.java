package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;

public class ReplyCommand extends Command {
    public ReplyCommand(){
        super("reply",null,"r");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.lastMsg != null && ProxyServer.getInstance().getPlayer(u.lastMsg.getName()) != null){
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 0; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String msg = sb.toString();
                    BungeeDungeon.getInstance().getProxy().getPluginManager().dispatchCommand(p,"msg " + u.lastMsg.getName() + " " + msg);
                } else {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have to message someone before using this command."));
                }
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player is not online."));
        }
    }
}
