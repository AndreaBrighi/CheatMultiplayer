package it.unibo.ds.model.game.event

import it.unibo.ds.model.cards.Card

/**
 * Represents an event where a player draws cards.
 *
 * @property player The player who draws cards.
 * @property cardsDrawn The cards to drawn.
 */
data class DrawCardsEvent(
    override val player: String,
    val cardsDrawn: Set<Card>
) : Event
