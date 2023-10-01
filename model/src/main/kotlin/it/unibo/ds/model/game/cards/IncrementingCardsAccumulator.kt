package it.unibo.ds.model.game.cards

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.CardsAccumulator
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException

/**
 * Interface of a cards' accumulator, that means that its number of cards can increase
 *
 * @param T the cards' type
 */
interface IncrementingCardsAccumulator<T : Card> : CardsAccumulator<T> {

    /**
     * Add the input card to the accumulator
     *
     * @param cards the list of card to add
     *
     * @throws CardsAlreadyPresentException if any of the input card is already present
     */
    @Throws(CardsAlreadyPresentException::class)
    fun addCards(cards: Set<T>): IncrementingCardsAccumulator<T>
}
