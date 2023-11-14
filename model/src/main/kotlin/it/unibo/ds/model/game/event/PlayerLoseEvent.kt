package it.unibo.ds.model.game.event

/**
 * Represents an event where a player loses.
 *
 * @property player The player who loses.
 */
data class PlayerLoseEvent( override val player: String): Event
