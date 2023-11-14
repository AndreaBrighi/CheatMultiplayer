package it.unibo.ds.model.game.event

/**
 * Represents an event where the game starts.
 *
 * @property player The player who start playing.
 */
class StartGameEvent(override val player: String) : Event