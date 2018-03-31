package net.wrathofdungeons.bungeedungeon.listener;

import io.netty.channel.Channel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.wrathofdungeons.bungeedungeon.BungeeDungeon;
import net.wrathofdungeons.bungeedungeon.ConnectionEncoder;
import net.wrathofdungeons.bungeedungeon.MySQLManager;
import net.wrathofdungeons.bungeedungeon.Reflect;
import net.wrathofdungeons.bungeedungeon.ban.Ban;
import net.wrathofdungeons.bungeedungeon.users.BungeeUser;
import net.wrathofdungeons.bungeedungeon.users.PlayerUtilities;
import net.wrathofdungeons.bungeedungeon.vpn.VPNCheckResult;
import net.wrathofdungeons.bungeedungeon.vpn.VPNCheckUtil;

import java.sql.PreparedStatement;
import java.util.UUID;

public class LoginListener implements Listener {
    @EventHandler
    public void onLogin(LoginEvent e){
        UUID uuid = e.getConnection().getUniqueId();
        String usedIP = e.getConnection() != null && e.getConnection().getVirtualHost() != null ? e.getConnection().getVirtualHost().getHostName() : null;
        String ip = e.getConnection().getAddress().toString().replace("/","").split(":")[0];

        if(uuid != null){
            if(BungeeDungeon.WHITELIST_ENABLED){
                if(!BungeeDungeon.isWhitelisted(uuid)){
                    e.setCancelReason(ChatColor.RED + "You are not whitelisted.");
                    e.setCancelled(true);
                    return;
                }
            }

            Ban ban = Ban.getBan(uuid);
            if(ban != null && ban.isActive()){
                e.setCancelReason(ban.getDisconnectMessage());
                e.setCancelled(true);

                return;
            }

            if(!e.isCancelled()){
                double vpnvalue = 0;
                boolean load = true;

                VPNCheckResult result = VPNCheckUtil.getResult(ip);
                if(result != null){
                    vpnvalue = result.getResult();

                    if(result.isVPN()){
                        load = false;
                        e.setCancelReason(ChatColor.RED + "Your IP has been detected as malicious.\nPlease disable any ip changing software such as VPNs and try again.");
                        e.setCancelled(true);
                    }
                }

                final double v = vpnvalue;

                BungeeDungeon.async(() -> {
                    try {
                        PreparedStatement ps = MySQLManager.getInstance().getConnection().prepareStatement("INSERT INTO `logins` (`uuid`,`ip`,`country`,`usedIP`,`vpnCheckResult`) VALUES(?,?,?,?,?);");
                        ps.setString(1,uuid.toString());
                        ps.setString(2,ip);
                        ps.setString(3,PlayerUtilities.getCountryCodeFromIP(ip));
                        ps.setString(4,usedIP);
                        ps.setDouble(5,v);
                        ps.executeUpdate();
                        ps.close();
                    } catch(Exception e1){
                        e1.printStackTrace();
                    }
                });

                if(load) BungeeUser.load(uuid);
            }
        } else {
            e.setCancelReason(ChatColor.RED + "No UUID submitted.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e){
        try {
            ProxiedPlayer p = e.getPlayer();

            if (BungeeDungeon.ENABLE_LOADING_SCREEN_REMOVER) {
                Channel ch = (Channel) Reflect.get(Reflect.get(p, "ch"), "ch");
                ch.pipeline().addAfter("packet-encoder", "wodEncoder", new ConnectionEncoder(p));
            }
        } catch(Exception e1){
            e1.printStackTrace();
        }
    }
}
