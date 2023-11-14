package it.unibo.ds.model.game.event

/**
 * Represents an event where a player wins.
 *
 * @property player The player who wins.
 */
data class PlayerWonEvent(override val player: String) : Event
