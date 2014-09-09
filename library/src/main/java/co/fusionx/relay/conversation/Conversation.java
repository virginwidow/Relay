package co.fusionx.relay.conversation;

import java.util.List;

import co.fusionx.relay.event.Event;
import co.fusionx.relay.bus.GenericBus;

public interface Conversation<E extends Event> {

    public String getId();

    public List<? extends E> getBuffer();

    public GenericBus<Event> getSessionBus();

    public GenericBus<E> getBus();

    /**
     * Returns whether the conversation is valid - i.e. is it attached and managed by the session
     *
     * @return the validity of the conversation
     */
    public boolean isValid();
}