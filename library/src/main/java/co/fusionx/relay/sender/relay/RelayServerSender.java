package co.fusionx.relay.sender.relay;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.call.server.JoinCall;
import co.fusionx.relay.call.server.NickChangeCall;
import co.fusionx.relay.call.server.RawCall;
import co.fusionx.relay.call.server.WhoisCall;
import co.fusionx.relay.sender.ServerSender;

public class RelayServerSender implements ServerSender {

    private final RelayServer mServer;

    private final RelayServerLineSender mCallHandler;

    public RelayServerSender(final RelayServer server, final RelayServerLineSender callHandler) {
        mServer = server;
        mCallHandler = callHandler;
    }

    @Override
    public void sendQuery(final String nick, final String message) {
        // This is invalid - we don't have anything to send the server directly
    }

    @Override
    public void sendJoin(final String channelName) {
        mCallHandler.post(new JoinCall(channelName));
    }

    @Override
    public void sendNick(final String newNick) {
        mCallHandler.post(new NickChangeCall(newNick));
    }

    @Override
    public void sendWhois(final String nick) {
        mCallHandler.post(new WhoisCall(nick));
    }

    @Override
    public void sendRawLine(final String rawLine) {
        mCallHandler.post(new RawCall(rawLine));
    }
}