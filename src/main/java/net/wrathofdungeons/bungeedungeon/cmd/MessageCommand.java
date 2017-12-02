package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.users.Rank;

public class MessageCommand extends Command {
    public MessageCommand(){
        super("msg",null,"tell","w","say","whsiper");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                // TODO: Check mute

                if(args.length >= 2){
                    String receiver = args[0];
                    StringBuilder sb = new StringBuilder("");
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i]).append(" ");
                    }
                    String msg = sb.toString();

                    ProxiedPlayer p2 = ProxyServer.getInstance().getPlayer(receiver);
                    if(p2 != null && BungeeUser.isLoaded(p2)){
                        BungeeUser u2 = BungeeUser.get(p2);

                        if(PlayerUtilities.getSettingsFromUUID(p2.getUniqueId()).allowsPrivateMessages() || u.hasPermission(Rank.MODERATOR) || u.getFriends().contains(p2.getUniqueId().toString())){
                            u.lastMsg = p2;
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "[" + u.getRank().getColor() + p.getName() + ChatColor.GOLD + "->" + u2.getRank().getColor() + p2.getName() + ChatColor.GOLD + "]" + ChatColor.WHITE + " " + msg));
                            p2.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "[" + u.getRank().getColor() + p.getName() + ChatColor.GOLD + "->" + u2.getRank().getColor() + p2.getName() + ChatColor.GOLD + "]" + ChatColor.WHITE + " " + msg));
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player does not allow private messages."));
                        }
                    } else {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player is not online."));
                    }
                } else {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/msg <Player> <Message>"));
                }
            }
        } else {
            if(args.length >= 2){
                String receiver = args[0];
                StringBuilder sb = new StringBuilder("");
                for (int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }
                String msg = sb.toString();

                ProxiedPlayer p2 = ProxyServer.getInstance().getPlayer(receiver);
                if(p2 != null && BungeeUser.isLoaded(p2)){
                    BungeeUser u2 = BungeeUser.get(p2);

                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "[" + ChatColor.DARK_PURPLE + "BungeeConsole" + ChatColor.GOLD + "->" + u2.getRank().getColor() + p2.getName() + ChatColor.GOLD + "]" + ChatColor.WHITE + " " + msg));
                    p2.sendMessage(TextComponent.fromLegacyText(ChatColor.GOLD + "[" + ChatColor.DARK_PURPLE + "BungeeConsole" + ChatColor.GOLD + "->" + u2.getRank().getColor() + p2.getName() + ChatColor.GOLD + "]" + ChatColor.WHITE + " " + msg));
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player is not online."));
                }
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/msg <Player> <Message>"));
            }
        }
    }
}
