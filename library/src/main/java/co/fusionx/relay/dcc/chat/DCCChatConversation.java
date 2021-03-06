package co.fusionx.relay.dcc.chat;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;

import java.util.List;

import co.fusionx.relay.base.FormatSpanInfo;
import co.fusionx.relay.internal.base.RelayAbstractConversation;
import co.fusionx.relay.internal.base.RelayServer;
import co.fusionx.relay.dcc.event.chat.DCCChatEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfActionEvent;
import co.fusionx.relay.dcc.event.chat.DCCChatSelfMessageEvent;
import co.fusionx.relay.dcc.pending.DCCPendingConnection;
import co.fusionx.relay.misc.RelayConfigurationProvider;
import co.fusionx.relay.util.Utils;

public class DCCChatConversation extends RelayAbstractConversation<DCCChatEvent> {

    private final Handler mCallHandler;

    private final DCCChatConnection mDCCChatConnection;

    private final DCCPendingConnection mPendingConnection;

    public DCCChatConversation(final RelayServer server,
            final DCCPendingConnection pendingConnection) {
        super(server);

        mPendingConnection = pendingConnection;

        mDCCChatConnection = new DCCChatConnection(mPendingConnection, this);

        final HandlerThread handlerThread = new HandlerThread("dccConnection");
        handlerThread.start();
        mCallHandler = new Handler(handlerThread.getLooper());
    }

    public void startChat() {
        mDCCChatConnection.startConnection();
    }

    public void sendMessage(final String message) {
        mCallHandler.post(() -> mDCCChatConnection.writeLine(message));

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        final Pair<String, List<FormatSpanInfo>> messageAndColors =
                Utils.parseAndStripColorsFromMessage(message);
        postAndStoreEvent(new DCCChatSelfMessageEvent(this,
                messageAndColors.first, messageAndColors.second));
    }

    public void sendAction(final String action) {
        final String line = String.format("\u0001ACTION %1$s\u0001", action);
        mCallHandler.post(() -> mDCCChatConnection.writeLine(line));

        if (RelayConfigurationProvider.getPreferences().isSelfEventHidden()) {
            return;
        }
        final Pair<String, List<FormatSpanInfo>> actionAndColors =
                Utils.parseAndStripColorsFromMessage(action);
        postAndStoreEvent(new DCCChatSelfActionEvent(this,
                actionAndColors.first, actionAndColors.second));
    }

    public void closeChat() {

    }

    // Conversation interface
    @Override
    public String getId() {
        return mPendingConnection.getDccRequestNick();
    }

    // Equality
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DCCChatConversation)) {
            return false;
        }

        final DCCChatConversation that = (DCCChatConversation) o;
        return mPendingConnection.getDccRequestNick()
                .equals(that.mPendingConnection.getDccRequestNick())
                && mServer.equals(that.mServer);
    }

    @Override
    public int hashCode() {
        int result = mServer.hashCode();
        result = 31 * result + mPendingConnection.hashCode();
        return result;
    }
}