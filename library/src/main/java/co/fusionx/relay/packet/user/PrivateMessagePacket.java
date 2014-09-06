package co.fusionx.relay.packet.user;

import co.fusionx.relay.misc.WriterCommands;
import co.fusionx.relay.packet.Packet;

public class PrivateMessagePacket implements Packet {

    public final String userNick;

    public final String message;

    public PrivateMessagePacket(String userNick, String message) {
        this.userNick = userNick;
        this.message = message;
    }

    @Override
    public String getLine() {
        return String.format(WriterCommands.PRIVMSG, userNick, message);
    }
}