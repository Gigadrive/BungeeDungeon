package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.ban.Ban;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.users.Rank;

import java.util.UUID;

public class UnbanCommand extends Command {
    public UnbanCommand(){
        super("unban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer)sender;

            if(BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.hasPermission(Rank.MODERATOR)){
                    if(args.length >= 2){
                        BungeeDungeon.async(() -> {
                            try {
                                String name = args[0];

                                UUID uuid = PlayerUtilities.getUUIDFromName(name);

                                if(uuid != null){
                                    name = PlayerUtilities.getNameFromUUID(uuid);
                                    Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                                    Ban ban = Ban.getBan(uuid);
                                    if(ban != null && ban.isActive()){
                                        StringBuilder sb = new StringBuilder();
                                        for (int i = 1; i < args.length; i++) {
                                            sb.append(" ").append(args[i]);
                                        }
                                        String reason = sb.toString().substring(1);

                                        ban.unban(p.getUniqueId(),reason);
                                        BungeeDungeon.createStaffMessage(Rank.MODERATOR,u.getRank().getColor() + p.getName() + " " + ChatColor.GREEN + "has unbanned " + rank.getColor() + name + " " + ChatColor.GREEN + "with reason " + ChatColor.GRAY + reason + ChatColor.GREEN + ".");
                                    } else {
                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player isn't banned."));
                                    }
                                } else {
                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown UUID."));
                                }
                            } catch(Exception e){
                                e.printStackTrace();
                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                            }
                        });
                    } else {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/unban <Player> <Reason>"));
                    }
                } else {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown command."));
                }
            }
        } else {
            if(args.length >= 2){
                BungeeDungeon.async(() -> {
                    try {
                        String name = args[0];

                        UUID uuid = PlayerUtilities.getUUIDFromName(name);

                        if(uuid != null){
                            name = PlayerUtilities.getNameFromUUID(uuid);
                            Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                            Ban ban = Ban.getBan(uuid);
                            if(ban != null && ban.isActive()){
                                StringBuilder sb = new StringBuilder();
                                for (int i = 1; i < args.length; i++) {
                                    sb.append(" ").append(args[i]);
                                }
                                String reason = sb.toString().substring(1);

                                ban.unban(null,reason);
                                BungeeDungeon.createStaffMessage(Rank.MODERATOR,ChatColor.DARK_PURPLE + "BungeeConsole" + " " + ChatColor.GREEN + "has unbanned " + rank.getColor() + name + " " + ChatColor.GREEN + "with reason " + ChatColor.GRAY + reason + ChatColor.GREEN + ".");
                            } else {
                                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player isn't banned."));
                            }
                        } else {
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown UUID."));
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                    }
                });
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/unban <Player> <Reason>"));
            }
        }
    }
}
