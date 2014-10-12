package co.fusionx.relay.internal.parser;

import com.google.common.base.Optional;

import java.io.BufferedReader;
import java.io.IOException;

import javax.inject.Inject;

import co.fusionx.relay.parser.InputParser;
import co.fusionx.relay.parser.rfc.QuitParser;

public class BufferedInputParser implements QuitParser.QuitObserver {

    private final InputParser mInputParser;

    private boolean mKeepParsing = true;

    @Inject
    public BufferedInputParser(final InputParser inputParser) {
        mInputParser = inputParser;
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && mKeepParsing) {
            mInputParser.parseLine(line);
        }
    }

    @Override
    public void onQuit(final String prefix, final Optional<String> optionalReason) {
        mKeepParsing = false;
    }
}