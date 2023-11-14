package it.unibo.ds.model.game.event

/**
 * Represents an event where a player can choose a rank.
 *
 * @property player The player who can choose a rank.
 */
class PlayerCanChooseRankEvent(override val player: String) : Event