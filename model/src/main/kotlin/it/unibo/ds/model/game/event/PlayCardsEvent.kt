package it.unibo.ds.model.game.event

import it.unibo.ds.model.cards.Card

/**
 * Represents an event where a player plays cards.
 *
 * @property player The player who draws cards.
 * @property cardsPlayed The cards played.
 * @property declaredCardNumber The number of cards declared by the player.
 */
data class PlayCardsEvent(
    override val player: String,
    val cardsPlayed: Set<Card>,
    val declaredCardNumber: Int
    ) : Event
