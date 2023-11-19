package it.unibo.ds.model.game.event

/**
 * Represents a game event consumer.
 *
 */
fun interface EventConsumer {

    /**
     * Consumes the given event.
     *
     * @param event The event to consume.
     */
    fun consume(event: Event)
}