package it.unibo.ds.model.game.exception

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.game.cards.PlayerHand

/**
 * Exception throw when the player tries to play one o more cards that complete a rank in his hand
 *
 * @param hand the player's hand
 * @param cards the played cards
 *
 * @property cantBeDroppedCards the list of cards that the player tries to play but can't
 */
class CardsCompleteRankDropException(hand: PlayerHand<Card>, cards: Set<Card>) : Exception() {

    val cantBeDroppedCards = cards.filter { c ->
        hand.cardsThatCanBeDropped().contains(c)
    }.toSet()
}
