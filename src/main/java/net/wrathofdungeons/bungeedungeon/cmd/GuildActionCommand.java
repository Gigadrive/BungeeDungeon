package net.wrathofdungeons.bungeedungeon.cmd;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.Util;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;

import java.util.ArrayList;

public class GuildActionCommand extends Command {
    public GuildActionCommand(){
        super("guildaction");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            if(args.length >= 2){
                String executor = args[0];
                ProxiedPlayer p = executor.equalsIgnoreCase("NONE") || BungeeDungeon.getInstance().getProxy().getPlayer(executor) == null ? null : BungeeDungeon.getInstance().getProxy().getPlayer(executor);

                if((p != null) || (p == null && executor.equalsIgnoreCase("NONE"))){
                    if(p == null || (p != null && BungeeUser.isLoaded(p))){
                        BungeeUser u = p != null ? BungeeUser.get(p) : null;

                        if(Util.isValidInteger(args[1])){
                            int guildID = Integer.parseInt(args[1]);

                            ArrayList<String> data = new ArrayList<String>();
                            data.add("guildAction");
                            data.add(p != null ? p.getName() : "NONE");
                            data.add(String.valueOf(guildID));

                            if(args[2].equalsIgnoreCase("chat")){
                                data.add("chat");

                                StringBuilder sb = new StringBuilder();
                                for (int i = 3; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                String message = sb.toString().trim();

                                data.add(message);
                            } else if(args[2].equalsIgnoreCase("reloadMembers")){
                                data.add("reloadMembers");
                            } else if(args[2].equalsIgnoreCase("disband")){
                                data.add("disband");
                            } else if(args[2].equalsIgnoreCase("message")){
                                data.add("message");

                                StringBuilder sb = new StringBuilder();
                                for (int i = 3; i < args.length; i++) {
                                    sb.append(args[i]).append(" ");
                                }
                                String message = sb.toString().trim();

                                data.add(message);
                            } else if(args[2].equalsIgnoreCase("")){

                            } else if(args[2].equalsIgnoreCase("")){

                            }

                            if(data.size() > 3){
                                ArrayList<String> servers = new ArrayList<String>();

                                for(ServerInfo info : CloudAPI.getInstance().getServers("Game")){
                                    net.md_5.bungee.api.config.ServerInfo server = BungeeDungeon.getInstance().getProxy().getServerInfo(info.getServiceId().getServerId());

                                    if(server != null) if(server.getName() != null && !servers.contains(server.getName())){
                                        BungeeDungeon.sendToBukkit(server,data.toArray(new String[]{}));
                                        servers.add(server.getName());
                                    }
                                }

                                for(ServerInfo info : CloudAPI.getInstance().getServers("Test")){
                                    net.md_5.bungee.api.config.ServerInfo server = BungeeDungeon.getInstance().getProxy().getServerInfo(info.getServiceId().getServerId());

                                    if(server != null) if(server.getName() != null && !servers.contains(server.getName())){
                                        BungeeDungeon.sendToBukkit(server,data.toArray(new String[]{}));
                                        servers.add(server.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
