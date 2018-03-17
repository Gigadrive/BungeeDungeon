package net.wrathofdungeons.bungeedungeon;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.Respawn;

import java.util.List;

public class ConnectionEncoder extends MessageToMessageEncoder<DefinedPacket> {
    private ProxiedPlayer p;

    public ConnectionEncoder(ProxiedPlayer p){
        this.p = p;
    }

    public ProxiedPlayer getPlayer() {
        return p;
    }

    protected void encode(ChannelHandlerContext chc, DefinedPacket packet, List<Object> list) throws Exception {
        if(packet instanceof Respawn){
            Respawn respawnPacket = (Respawn)packet;

            int dimension = ((UserConnection)getPlayer()).getDimension();

            if(dimension != respawnPacket.getDimension())
                respawnPacket.setDimension(dimension);
        }

        list.add(packet);
    }
}
