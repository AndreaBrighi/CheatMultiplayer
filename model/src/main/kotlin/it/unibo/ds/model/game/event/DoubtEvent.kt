package it.unibo.ds.model.game.event

/**
 * Represents an event where a player doubts the last player who played.
 *
 * @property player The player who generated the event.
 * @property lastPlayerWhoPlayed The player who played the last card.
 */
data class DoubtEvent(
    override val player: String,
    val lastPlayerWhoPlayed: String,
) : Event