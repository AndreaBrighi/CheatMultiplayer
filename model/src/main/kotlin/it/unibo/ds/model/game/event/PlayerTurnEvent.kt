package it.unibo.ds.model.game.event

/**
 * Represents an event where starts the turn of a player.
 *
 * @property player The player whose turn is starting.
 */
data class PlayerTurnEvent(
    override val player: String
) : Event
