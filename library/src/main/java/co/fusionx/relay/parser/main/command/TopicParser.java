package co.fusionx.relay.parser.main.command;

import com.google.common.base.Optional;

import java.util.List;

import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.base.relay.RelayChannel;
import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.event.channel.ChannelTopicEvent;
import co.fusionx.relay.function.Optionals;
import co.fusionx.relay.util.LogUtils;

public class TopicParser extends CommandParser {

    public TopicParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String prefix) {
        final ChannelUser user = mUserChannelInterface.getUserFromPrefix(prefix);
        final Optional<RelayChannel> optChan = mUserChannelInterface.getChannel(parsedArray.get(0));

        LogUtils.logOptionalBug(optChan, mServer);
        Optionals.ifPresent(optChan, channel -> {
            final String newTopic = parsedArray.get(1);
            channel.postAndStoreEvent(new ChannelTopicEvent(channel, user, newTopic));
        });
    }
}