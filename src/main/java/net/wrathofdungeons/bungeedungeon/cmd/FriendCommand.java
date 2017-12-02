package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.users.Rank;

import java.sql.PreparedStatement;
import java.util.ArrayList;
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
                            BungeeDungeon.async(() -> {
                                for(String id : u.getFriends()){
                                    if(id == null || id.isEmpty()) continue;
                                    UUID uuid = UUID.fromString(id);
                                    String name = PlayerUtilities.getNameFromUUID(uuid);
                                    Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                                    if(name != null && !name.isEmpty() && rank != null){
                                        ProxiedPlayer p2 = BungeeDungeon.getInstance().getProxy().getPlayer(uuid);

                                        if(p2 != null){
                                            p.sendMessage(TextComponent.fromLegacyText(rank.getColor() + name + ChatColor.AQUA + " - " + ChatColor.GREEN + p2.getServer().getInfo().getName()));
                                        } else {
                                            p.sendMessage(TextComponent.fromLegacyText(rank.getColor() + name + ChatColor.AQUA + " - " + ChatColor.RED + "OFFLINE"));
                                        }
                                    }
                                }
                            });
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Your friends list is empty."));
                        }
                    } else if(args[0].equalsIgnoreCase("requests")){
                        if(u.getFriendRequests().size() > 0){
                            BungeeDungeon.async(() -> {
                                for(String id : u.getFriendRequests()){
                                    if(id == null || id.isEmpty()) continue;
                                    UUID uuid = UUID.fromString(id);
                                    String name = PlayerUtilities.getNameFromUUID(uuid);
                                    Rank rank = PlayerUtilities.getRankFromUUID(uuid);

                                    if(name != null && !name.isEmpty() && rank != null){
                                        p.sendMessage(new ComponentBuilder(name).color(rank.getColor()).append(" ").append("- ").color(ChatColor.AQUA).append(" ").append("[ACCEPT]").bold(true).color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend accept " + name)).append(" ").append("[DENY]").color(ChatColor.RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend deny " + name)).create());
                                    }
                                }
                            });
                        } else {
                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You don't have any open friend requests."));
                        }
                    } else {
                        sendUsage(p,u);
                    }
                } else if(args.length == 2){
                    if(args[0].equalsIgnoreCase("add")){
                        BungeeDungeon.async(() -> {
                            String name = args[1];
                            UUID uuid = PlayerUtilities.getUUIDFromName(name);

                            if(uuid != null){
                                name = PlayerUtilities.getNameFromUUID(uuid);

                                if(!u.getFriends().contains(uuid.toString())){
                                    if(!u.getFriendRequests().contains(uuid.toString())){
                                        ArrayList<String> friendRequests = PlayerUtilities.getFriendRequestsToUUID(uuid);

                                        if(friendRequests != null){
                                            if(!friendRequests.contains(p.getUniqueId().toString())){
                                                if(u.hasPermission(Rank.MODERATOR) || PlayerUtilities.getSettingsFromUUID(uuid).allowsFriendRequests()){
                                                    try {
                                                        PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `friend_requests` (`from`,`to`) VALUES(?,?);");
                                                        ps.setString(1,p.getUniqueId().toString());
                                                        ps.setString(2,uuid.toString());
                                                        ps.executeUpdate();
                                                        ps.close();

                                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You have sucessfully sent a friend request to " + ChatColor.YELLOW + name + ChatColor.AQUA + "."));

                                                        if(BungeeUser.isLoaded(uuid)){
                                                            BungeeUser u2 = BungeeUser.get(uuid);
                                                            ProxiedPlayer p2 = u2.getProxiedPlayer();

                                                            u2.reloadFriendRequests();

                                                            if(p2 != null){
                                                                p2.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You received a friend request from " + ChatColor.YELLOW + p.getName() + ChatColor.AQUA + "!"));
                                                                p2.sendMessage(new ComponentBuilder("Click here: ").color(ChatColor.AQUA).append("[ACCEPT]").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend accept " + p.getName())).append(" ").append("[DENY]").color(ChatColor.RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/friend deny " + p.getName())).create());
                                                            }
                                                        }
                                                    } catch(Exception e){
                                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "That player does not allow friend requests."));
                                                }
                                            } else {
                                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have already sent a request to that player."));
                                            }
                                        } else {
                                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                        }
                                    } else {
                                        BungeeDungeon.getInstance().getProxy().getPluginManager().dispatchCommand(p,"friend accept " + name);
                                    }
                                } else {
                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already friends with that player."));
                                }
                            } else {
                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown UUID."));
                            }
                        });
                    } else if(args[0].equalsIgnoreCase("remove")){
                        BungeeDungeon.async(() -> {
                            String name = args[1];
                            UUID uuid = PlayerUtilities.getUUIDFromName(name);

                            if(uuid != null){
                                name = PlayerUtilities.getNameFromUUID(uuid);

                                if(u.getFriends().contains(uuid.toString())){
                                    try {
                                        PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("DELETE FROM `friendships` WHERE (`player1` = ? AND `player2` = ?) OR (`player1` = ? AND `player2` = ?)");
                                        ps.setString(1,p.getUniqueId().toString());
                                        ps.setString(2,uuid.toString());
                                        ps.setString(3,uuid.toString());
                                        ps.setString(4,p.getUniqueId().toString());
                                        ps.executeUpdate();
                                        ps.close();

                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You have sucessfully removed " + ChatColor.YELLOW + name + ChatColor.AQUA + " from your friends list."));
                                        u.reloadFriends();

                                        if(BungeeUser.isLoaded(uuid)){
                                            BungeeUser u2 = BungeeUser.get(uuid);
                                            ProxiedPlayer p2 = u2.getProxiedPlayer();

                                            u2.reloadFriends();

                                            if(p2 != null){
                                                p2.sendMessage(TextComponent.fromLegacyText(ChatColor.YELLOW + p.getName() + ChatColor.AQUA + " has removed you from their friends list."));
                                            }
                                        }
                                    } catch(Exception e){
                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                        e.printStackTrace();
                                    }
                                } else {
                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already friends with that player."));
                                }
                            } else {
                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown UUID."));
                            }
                        });
                    } else if(args[0].equalsIgnoreCase("accept")){
                        BungeeDungeon.async(() -> {
                            String name = args[1];
                            UUID uuid = PlayerUtilities.getUUIDFromName(name);

                            if(uuid != null){
                                name = PlayerUtilities.getNameFromUUID(uuid);

                                if(!u.getFriends().contains(uuid.toString())){
                                    if(u.getFriendRequests().contains(uuid.toString())){
                                        ArrayList<String> friendRequests = u.getFriendRequests();

                                        if(friendRequests != null){
                                            if(friendRequests.contains(uuid.toString())){
                                                try {
                                                    PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `friendships` (`player1`,`player2`) VALUES(?,?);");
                                                    ps.setString(1,p.getUniqueId().toString());
                                                    ps.setString(2,uuid.toString());
                                                    ps.executeUpdate();
                                                    ps.close();

                                                    ps = MySQLManager.getInstance().getConnection().prepareStatement("DELETE FROM `friend_requests` WHERE `from` = ? AND `to` = ?");
                                                    ps.setString(1,uuid.toString());
                                                    ps.setString(2,p.getUniqueId().toString());
                                                    ps.executeUpdate();
                                                    ps.close();

                                                    u.reloadFriends();
                                                    u.reloadFriendRequests();

                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You are now friends with " + ChatColor.YELLOW + name + ChatColor.AQUA + "."));

                                                    if(BungeeUser.isLoaded(uuid)){
                                                        BungeeUser u2 = BungeeUser.get(uuid);
                                                        ProxiedPlayer p2 = u2.getProxiedPlayer();

                                                        u2.reloadFriends();
                                                        u2.reloadFriendRequests();

                                                        if(p2 != null){
                                                            p2.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You are now friends with " + ChatColor.YELLOW + p.getName() + ChatColor.AQUA + "."));
                                                        }
                                                    }
                                                } catch(Exception e){
                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have already sent a request to that player."));
                                            }
                                        } else {
                                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                        }
                                    } else {
                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You don't have a friend request from that player."));
                                    }
                                } else {
                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already friends with that player."));
                                }
                            } else {
                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown UUID."));
                            }
                        });
                    } else if(args[0].equalsIgnoreCase("deny")){
                        BungeeDungeon.async(() -> {
                            String name = args[1];
                            UUID uuid = PlayerUtilities.getUUIDFromName(name);

                            if(uuid != null){
                                name = PlayerUtilities.getNameFromUUID(uuid);

                                if(!u.getFriends().contains(uuid.toString())){
                                    if(u.getFriendRequests().contains(uuid.toString())){
                                        ArrayList<String> friendRequests = u.getFriendRequests();

                                        if(friendRequests != null){
                                            if(friendRequests.contains(uuid.toString())){
                                                try {
                                                    PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("DELETE FROM `friend_requests` WHERE `from` = ? AND `to` = ?");
                                                    ps.setString(1,uuid.toString());
                                                    ps.setString(2,p.getUniqueId().toString());
                                                    ps.executeUpdate();
                                                    ps.close();

                                                    u.reloadFriendRequests();

                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.AQUA + "You have sucessfully denied the friend request from " + ChatColor.YELLOW + name + ChatColor.AQUA + "."));
                                                } catch(Exception e){
                                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You have already sent a request to that player."));
                                            }
                                        } else {
                                            p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "An error occurred."));
                                        }
                                    } else {
                                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You don't have a friend request from that player."));
                                    }
                                } else {
                                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already friends with that player."));
                                }
                            } else {
                                p.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unknown UUID."));
                            }
                        });
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
