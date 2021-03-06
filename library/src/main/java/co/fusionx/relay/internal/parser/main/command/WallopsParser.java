package co.fusionx.relay.internal.parser.main.command;

import android.util.Pair;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.event.server.WallopsEvent;
import co.fusionx.relay.util.ParseUtils;
import co.fusionx.relay.util.Utils;

public class WallopsParser extends CommandParser {

    WallopsParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String prefix) {
        // It is unlikely that the person who sent the wallops is in one of our channels - simply
        // send the nick and message rather than the spruced up nick
        final String sendingNick = ParseUtils.getNickFromPrefix(prefix);
        final String message = parsedArray.get(0);
        final Pair<String, List<FormatSpanInfo>> messageAndColors =
                Utils.parseAndStripColorsFromMessage(message);

        mServer.postAndStoreEvent(new WallopsEvent(mServer, messageAndColors.first,
                messageAndColors.second, sendingNick));
    }
}