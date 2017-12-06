package net.wrathofdungeons.bungeedungeon.cmd;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.player.CloudPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.Util;
import net.wrathofdungeons.bungeedungeon.ban.Ban;
import net.wrathofdungeons.bungeedungeon.ban.BanReason;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.users.Rank;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

public class HandlePunishmentCommand extends Command {
    public HandlePunishmentCommand(){
        super("handlepunishment");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            ProxiedPlayer p = BungeeDungeon.getInstance().getProxy().getPlayer(args[0]);

            if(p != null && BungeeUser.isLoaded(p)){
                BungeeUser u = BungeeUser.get(p);

                if(u.hasPermission(Rank.MODERATOR)){
                    String name = args[1];

                    BungeeDungeon.async(() -> {
                        try {
                            UUID uuid = PlayerUtilities.getUUIDFromName(name);

                            if(uuid != null){
                                if(Util.isValidInteger(args[2])){
                                    BanReason banReason = BanReason.getBanReason(Integer.parseInt(args[2]));

                                    if(banReason != null){
                                        if(banReason.getTypeOfPunishment() == BanReason.PunishmentType.BAN || banReason.getTypeOfPunishment() == BanReason.PunishmentType.PERMBAN){
                                            Ban ban = Ban.getBan(uuid);
                                            if(ban == null || !ban.isActive()){
                                                Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                                                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `bans` (`uuid`,`reason`,`endDate`,`staff`) VALUES(?,?,?,?);", Statement.RETURN_GENERATED_KEYS);
                                                ps.setString(1,uuid.toString());

                                                int time = banReason.getTimeValue();
                                                Timestamp now = new Timestamp(System.currentTimeMillis());
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTimeInMillis(now.getTime());

                                                if(banReason.getTimeUnit().equalsIgnoreCase("SECONDS")){
                                                    cal.add(Calendar.SECOND,time);
                                                } else if(banReason.getTimeUnit().equalsIgnoreCase("MINUTES")){
                                                    cal.add(Calendar.MINUTE,time);
                                                } else if(banReason.getTimeUnit().equalsIgnoreCase("HOURS")){
                                                    cal.add(Calendar.HOUR,time);
                                                } else if(banReason.getTimeUnit().equalsIgnoreCase("DAYS")){
                                                    cal.add(Calendar.HOUR,time*24);
                                                } else if(banReason.getTimeUnit().equalsIgnoreCase("WEEKS")){
                                                    cal.add(Calendar.HOUR,time*24*7);
                                                } else if(banReason.getTimeUnit().equalsIgnoreCase("MONTHS")){
                                                    cal.add(Calendar.MONTH,time);
                                                } else if(banReason.getTimeUnit().equalsIgnoreCase("YEARS")){
                                                    cal.add(Calendar.YEAR,time);
                                                }

                                                ps.setInt(2,banReason.getId());
                                                ps.setTimestamp(3,new Timestamp(cal.getTime().getTime()));
                                                ps.setString(4,p.getUniqueId().toString());
                                                ps.executeUpdate();

                                                ResultSet rs = ps.getGeneratedKeys();
                                                int banID = -1;
                                                if(rs.first()) banID = rs.getInt(1);

                                                MySQLManager.getInstance().closeResources(rs,ps);

                                                if(banID > -1){
                                                    Ban b = new Ban(banID);

                                                    if(b.getBanReason() == banReason){
                                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "New ban rule created! ID: #" + banID));
                                                        BungeeDungeon.createStaffMessage(Rank.MODERATOR,u.getRank().getColor() + p.getName() + " " + ChatColor.GREEN + "has punished " + rank.getColor() + name + " " + ChatColor.GREEN + "with reason " + ChatColor.GRAY + b.getBanReason().getName() + ChatColor.GREEN + ".");

                                                        ProxiedPlayer p2 = BungeeDungeon.getInstance().getProxy().getPlayer(name);

                                                        if(p2 != null)
                                                            p2.disconnect(TextComponent.fromLegacyText(ban.getDisconnectMessage()));
                                                    } else {
                                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                                    }
                                                } else {
                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                                }
                                            } else {
                                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player is already banned."));
                                            }
                                        } else if(banReason.getTypeOfPunishment() == BanReason.PunishmentType.KICK){
                                            ProxiedPlayer p2 = BungeeDungeon.getInstance().getProxy().getPlayer(name);

                                            if(p2 != null){
                                                Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                                                PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `kicks` (`uuid`,`staff`,`reason`,`server`) VALUES(?,?,?,?);");
                                                ps.setString(1,uuid.toString());
                                                ps.setString(2,p.getUniqueId().toString());
                                                ps.setInt(3,banReason.getId());
                                                ps.setString(4, CloudAPI.getInstance().getOnlinePlayer(p2.getUniqueId()).getServer());
                                                ps.executeUpdate();

                                                ResultSet rs = ps.getGeneratedKeys();
                                                int kickID = -1;
                                                if(rs.first()) kickID = rs.getInt(1);

                                                MySQLManager.getInstance().closeResources(rs,ps);

                                                if(kickID > -1){
                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "New kick rule created! ID: #" + kickID));
                                                    BungeeDungeon.createStaffMessage(Rank.MODERATOR,u.getRank().getColor() + p.getName() + " " + ChatColor.GREEN + "has punished " + rank.getColor() + name + " " + ChatColor.GREEN + "with reason " + ChatColor.GRAY + banReason.getName() + ChatColor.GREEN + ".");

                                                    p2.disconnect(TextComponent.fromLegacyText("" +
                                                            ChatColor.WHITE + "Your account has been kicked\n" +
                                                            ChatColor.WHITE + "from the Wrath of Dungeons network!\n" +
                                                            "\n" +
                                                            ChatColor.DARK_RED + "Reason: " + ChatColor.RED + banReason.getName()));
                                                } else {
                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                                }
                                            } else {
                                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player is not online."));
                                            }
                                        }
                                    } else {
                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Error: Unknown ban reason."));
                                    }
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                        }
                    });
                }
            }
        }
    }
}
