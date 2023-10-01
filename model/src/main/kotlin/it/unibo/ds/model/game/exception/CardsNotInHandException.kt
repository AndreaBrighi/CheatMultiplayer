package it.unibo.ds.model.game.exception

import it.unibo.ds.model.cards.Card

/**
 * Exception throw when the player tries to play cards not in his hand
 *
 * @param cards the played cards not in hands
 *
 * @property notPresentCards the cards that aren't in hos hand
 */
class CardsNotInHandException(cards: List<Card>) : Exception() {

    val notPresentCards = cards.toList()
}
