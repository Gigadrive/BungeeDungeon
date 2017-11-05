package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.users.Rank;

import java.util.UUID;

public class FriendCommand extends Command {
    public FriendCommand(){
        super("friend",null,"f","friends");
    }

    private void sendUsage(ProxiedPlayer p, BungeeUser u){
        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/friend list"));
        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/friend add <Player>"));
        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/friend remove <Player>"));
        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/friend accept <Player>"));
        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/friend deny <Player>"));
        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/friend requests"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("list")){
                        if(u.getFriends().size() > 0){
                            for(String id : u.getFriends()){
                                if(id == null || id.isEmpty()) continue;
                                UUID uuid = UUID.fromString(id);
                                String name = PlayerUtilities.getNameFromUUID(uuid);
                                Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                                if(name != null && !name.isEmpty() && rank != null){
                                    ProxiedPlayer p2 = BungeeDungeon.getInstance().getProxy().getPlayer(uuid);

                                    if(p2 != null){
                                        p.sendMessage(TextComponent.fromLegacyText(rank.getColor() + name + ChatColor.YELLOW + " - " + ChatColor.GREEN + p2.getServer().getInfo().getName()));
                                    } else {
                                        p.sendMessage(TextComponent.fromLegacyText(rank.getColor() + name + ChatColor.YELLOW + " - " + ChatColor.RED + "OFFLINE"));
                                    }
                                }
                            }
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Your friends list is empty."));
                        }
                    } else if(args[0].equalsIgnoreCase("requests")){

                    } else {
                        sendUsage(p,u);
                    }
                } else if(args.length == 2){
                    if(args[0].equalsIgnoreCase("add")){

                    } else if(args[0].equalsIgnoreCase("remove")){

                    } else if(args[0].equalsIgnoreCase("accept")){

                    } else if(args[0].equalsIgnoreCase("deny")){

                    } else {
                        sendUsage(p,u);
                    }
                } else {
                    sendUsage(p,u);
                }
            }
        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have to be a player to execute this command."));
        }
    }
}
