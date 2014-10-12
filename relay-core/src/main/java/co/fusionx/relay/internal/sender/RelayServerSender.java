package co.fusionx.relay.internal.sender;

import javax.inject.Inject;

import co.fusionx.relay.internal.packet.server.JoinPacket;
import co.fusionx.relay.internal.packet.server.NickChangePacket;
import co.fusionx.relay.internal.packet.server.RawPacket;
import co.fusionx.relay.internal.packet.server.WhoisPacket;
import co.fusionx.relay.sender.ServerSender;

public class RelayServerSender implements ServerSender {

    private final PacketSender mPacketSender;

    @Inject
    public RelayServerSender(final PacketSender packetSender) {
        mPacketSender = packetSender;
    }

    @Override
    public void sendJoin(final String channelName) {
        mPacketSender.sendPacket(new JoinPacket(channelName));
    }

    @Override
    public void sendNick(final String newNick) {
        mPacketSender.sendPacket(new NickChangePacket(newNick));
    }

    @Override
    public void sendWhois(final String nick) {
        mPacketSender.sendPacket(new WhoisPacket(nick));
    }

    @Override
    public void sendRawLine(final String rawLine) {
        mPacketSender.sendPacket(new RawPacket(rawLine));
    }
}