package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.Rank;

public class StaffChatCommand extends Command {
    public StaffChatCommand(){
        super("a",null,"s");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.hasPermission(Rank.MODERATOR)){
                    if(args.length > 0){
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < args.length; i++) {
                            sb.append(" ").append(args[i]);
                        }
                        String message = sb.toString().substring(1);

                        BungeeDungeon.createStaffMessage(ChatColor.DARK_RED + "[" + u.getRank().getColor() + p.getName() + ChatColor.DARK_RED + "] " + ChatColor.RED + message, Rank.MODERATOR);
                    } else {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please enter a message."));
                    }
                } else {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown command."));
                }
            }
        } else {
            if(args.length > 0){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    sb.append(" ").append(args[i]);
                }
                String message = sb.toString().substring(1);

                BungeeDungeon.createStaffMessage(ChatColor.DARK_RED + "[CONSOLE] " + ChatColor.RED + message, Rank.MODERATOR);
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please enter a message."));
            }
        }
    }
}
