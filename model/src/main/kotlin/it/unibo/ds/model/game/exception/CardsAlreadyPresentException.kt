package it.unibo.ds.model.game.exception

import it.unibo.ds.model.cards.Card

/**
 * Exception throw when the cards' accumulator tried to add card already in it
 *
 * @param cards the list of duplicate cards
 *
 * @property duplicatedCards the list of duplicate cards
 */
class CardsAlreadyPresentException(cards: Set<Card>) : Exception() {
    val duplicatedCards = cards.toSet()
}
