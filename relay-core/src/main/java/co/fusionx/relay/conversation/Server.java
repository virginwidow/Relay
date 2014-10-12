package co.fusionx.relay.conversation;

import java.util.Collection;

import co.fusionx.relay.constant.Capability;
import co.fusionx.relay.configuration.SessionConfiguration;
import co.fusionx.relay.event.server.ServerEvent;
import co.fusionx.relay.sender.ServerSender;

public interface Server extends Conversation<ServerEvent>, ServerSender {

    public String getTitle();

    public SessionConfiguration getConfiguration();

    public Collection<Capability> getCapabilities();
}