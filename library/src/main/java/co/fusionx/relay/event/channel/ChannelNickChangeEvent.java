package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.Nick;
import co.fusionx.relay.internal.base.RelayMainUser;

public class ChannelNickChangeEvent extends ChannelEvent {

    public final Nick oldNick;

    public final Nick newNick;

    public final RelayMainUser relayUser;

    public ChannelNickChangeEvent(final Channel channel, final Nick oldNick,
            final RelayMainUser user) {
        super(channel);

        this.oldNick = oldNick;
        this.newNick = user.getNick();
        this.relayUser = user;
    }
}