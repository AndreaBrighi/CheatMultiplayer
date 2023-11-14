package it.unibo.ds.model.game.event

import it.unibo.ds.model.cards.Card

/**
 * Represents an event where a player discards cards.
 *
 * @property player The player who discards cards.
 * @property cardDiscarded The cards to discard.
 */
data class DiscardCardEvent(
    override val player: String,
    val cardDiscarded: Set<Card>
): Event
