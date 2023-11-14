package it.unibo.ds.model.game.event

/**
 * Represents a game event source.
 *
 */
interface EventSource {

    /**
     * Registers the given event consumer to this event source.
     *
     * @param eventConsumer The event consumer to register.
     */
    fun register(eventConsumer: EventConsumer)

    /**
     * Unregisters the given event consumer from this event source.
     *
     * @param eventConsumer The event consumer to unregister.
     */
    fun unregister(eventConsumer: EventConsumer)

    /**
     * Notifies the given event to all the registered event consumers.
     *
     * @param event The event to notify.
     */
    fun notify(event: Event)
}