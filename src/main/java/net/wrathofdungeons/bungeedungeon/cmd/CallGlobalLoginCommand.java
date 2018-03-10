package net.wrathofdungeons.bungeedungeon.cmd;

import de.dytanic.cloudnet.api.CloudNetAPI;
import de.dytanic.cloudnet.network.ServerInfo;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.Util;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;

import java.util.ArrayList;

public class CallGlobalLoginCommand extends Command {
    public CallGlobalLoginCommand(){
        super("callgloballogin");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            if(args.length == 3){
                ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);

                if(BungeeUser.isLoaded(p)){
                    BungeeUser u = BungeeUser.get(p);

                    if(Util.isValidInteger(args[1])){
                        int level = Integer.parseInt(args[1]);
                        String className = args[2];

                        ArrayList<String> servers = new ArrayList<String>();

                        for(String id : CloudNetAPI.getInstance().getServers("Game")){
                            ServerInfo info = CloudNetAPI.getInstance().getServerInfo(id);
                            net.md_5.bungee.api.config.ServerInfo server = BungeeDungeon.getInstance().getProxy().getServerInfo(id);

                            if(server != null) if(server.getName() != null && !servers.contains(server.getName())){
                                BungeeDungeon.sendToBukkit(server,"callGlobalLogin",p.getUniqueId().toString(),String.valueOf(level),className);
                                servers.add(server.getName());
                            }
                        }

                        for(String id : CloudNetAPI.getInstance().getServers("Test")){
                            ServerInfo info = CloudNetAPI.getInstance().getServerInfo(id);
                            net.md_5.bungee.api.config.ServerInfo server = BungeeDungeon.getInstance().getProxy().getServerInfo(id);

                            if(server != null) if(server.getName() != null && !servers.contains(server.getName())){
                                BungeeDungeon.sendToBukkit(server,"callGlobalLogin",p.getUniqueId().toString(),String.valueOf(level),className);
                                servers.add(server.getName());
                            }
                        }
                    }
                }
            }
        }
    }
}
