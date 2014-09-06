package co.fusionx.relay.parser.main;

import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import co.fusionx.relay.base.relay.RelayServer;
import co.fusionx.relay.constants.ServerCommands;
import co.fusionx.relay.constants.ServerReplyCodes;
import co.fusionx.relay.event.server.GenericServerEvent;
import co.fusionx.relay.event.server.WhoisEvent;
import co.fusionx.relay.parser.main.code.CodeParser;
import co.fusionx.relay.parser.main.command.CommandParser;
import co.fusionx.relay.parser.main.command.QuitParser;
import co.fusionx.relay.sender.relay.RelayInternalSender;
import co.fusionx.relay.sender.relay.RelayPacketSender;
import co.fusionx.relay.util.IRCUtils;
import co.fusionx.relay.util.ParseUtils;

public class ServerLineParser {

    private final RelayServer mServer;

    private final Map<String, CommandParser> mCommandParserMap;

    private final SparseArray<CodeParser> mCodeParser;

    private RelayInternalSender mInternalSender;

    private String mLine;

    public ServerLineParser(final RelayServer server) {
        mServer = server;
        mCodeParser = CodeParser.getParserMap(server);
        mCommandParserMap = CommandParser.getParserMap(server);
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader     the reader associated with the server stream
     * @param lineSender the writer to write to the server
     */
    public void parseMain(final BufferedReader reader, final RelayPacketSender lineSender)
            throws IOException {
        mInternalSender = new RelayInternalSender(lineSender);

        while ((mLine = reader.readLine()) != null) {
            final boolean quit = parseLine();
            if (quit) {
                return;
            }
        }
    }

    public String getCurrentLine() {
        return mLine;
    }

    /**
     * Parses a line from the server
     *
     * @return a boolean indicating whether the server has disconnected
     */
    boolean parseLine() {
        // RFC2812 states that an empty line should be silently ignored
        if (TextUtils.isEmpty(mLine)) {
            return false;
        }

        // Split the line
        final List<String> parsedArray = ParseUtils.splitRawLine(mLine, true);

        // Get the prefix if it exists
        final String prefix = ParseUtils.extractAndRemovePrefix(parsedArray);

        // Get the command
        final String command = parsedArray.remove(0);

        // Check if the command is a numeric code
        if (ParseUtils.isCommandCode(command)) {
            final int code = Integer.parseInt(command);
            parseServerCode(parsedArray, code);
        } else {
            return parserServerCommand(parsedArray, prefix, command);
        }
        return false;
    }

    // The server is sending a command to us - parse what it is
    private boolean parserServerCommand(final List<String> parsedArray, final String prefix,
            final String command) {
        switch (command) {
            case ServerCommands.PING:
                // Immediately respond & return
                final String source = parsedArray.get(0);
                mInternalSender.pongServer(source);
                return false;
            case ServerCommands.ERROR:
                // We are finished - the server has kicked us
                // out for some reason
                return true;
        }

        // Parse the command
        final CommandParser parser = mCommandParserMap.get(command);
        // Silently fail if the parser is null - just ignore this line
        if (parser == null) {
            return false;
        }
        parser.onParseCommand(parsedArray, prefix);

        if (parser instanceof QuitParser) {
            final QuitParser quitParser = (QuitParser) parser;
            return quitParser.isUserQuit();
        }
        return false;
    }

    private void parseServerCode(final List<String> parsedArray, final int code) {
        parsedArray.remove(0); // Remove the target of the reply - ourselves

        if (ServerReplyCodes.genericCodes.contains(code)) {
            final String message = parsedArray.get(0);
            mServer.postAndStoreEvent(new GenericServerEvent(mServer, message));
        } else if (ServerReplyCodes.whoisCodes.contains(code)) {
            final String response = IRCUtils.concatenateStringList(parsedArray);
            mServer.postAndStoreEvent(new WhoisEvent(mServer, response));
        } else if (ServerReplyCodes.doNothingCodes.contains(code)) {
            // Do nothing
        } else {
            final CodeParser parser = mCodeParser.get(code);
            if (parser == null) {
                Log.d("Relay", mLine);
            } else {
                parser.onParseCode(parsedArray, code);
            }
        }
    }
}