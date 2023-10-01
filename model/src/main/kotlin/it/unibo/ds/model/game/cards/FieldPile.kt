package it.unibo.ds.model.game.cards

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException

/**
 * Interface of the filed pile, where the players put their cards when play
 *
 * @param T the cards' type
 *
 * @property allCards return all cards as list
 * @property lastCardsPlayed return the group of cards played by the last player as a list
 */
interface FieldPile<T : Card> : IncrementingCardsAccumulator<T> {

    val allCards: Set<T>

    val lastCardsPlayed: Set<T>

    /**
     * Return all cards as list and remove them from the field
     *
     * @return a pair of the cards in the pile and the empty pile
     */
    fun takeCards(): Pair<Set<T>, FieldPile<T>>

    /**
     * Add the input card to the filed pile
     *
     * @param cards the list of card to add
     *
     * @throws CardsAlreadyPresentException if any card is already in the pile
     */
    @Throws(CardsAlreadyPresentException::class)
    override fun addCards(cards: Set<T>): FieldPile<T>
}
