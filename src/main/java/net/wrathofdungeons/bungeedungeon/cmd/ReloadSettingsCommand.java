package net.wrathofdungeons.bungeedungeon.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;

public class ReloadSettingsCommand extends Command {
    public ReloadSettingsCommand(){
        super("reloadsettings");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)){
            if(args.length == 1){
                if(BungeeUser.isLoaded(ProxyServer.getInstance().getPlayer(args[0]))){
                    BungeeUser.get(ProxyServer.getInstance().getPlayer(args[0])).reloadSettings();
                }
            }
        }
    }
}
