package it.unibo.ds.model.game.event

/**
 * Represents a game event.
 *
 * @property player The player who generated the event.
 */
interface Event {
    val player: String
}
