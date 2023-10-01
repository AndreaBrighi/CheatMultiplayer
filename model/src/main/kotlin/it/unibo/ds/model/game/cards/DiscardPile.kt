package it.unibo.ds.model.game.cards

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException
import it.unibo.ds.model.game.exception.NotCompleteRankException

/**
 * Interface of the discard pile, where a player puts his cards when he discards them
 *
 * @param T the cards' type
 *
 * @property discardedRanks return the ranks of the discarded cards as a set
 */
interface DiscardPile<T : Card> : IncrementingCardsAccumulator<T> {

    val discardedRanks: Set<Rank>

    /**
     * Check if the list of cards can/has to be discarded
     *
     * @param cards the list of cards to check
     *
     * @return true if the cards, grouped by rank, have all suits
     */

    fun areCardsToDiscard(cards: Set<T>): Boolean

    /**
     * Add the input card to the discard pile
     *
     * @param cards the set of card to add
     *
     * @throws NotCompleteRankException if the cards, grouped by rank, haven't all suits
     * @throws CardsAlreadyPresentException if any of the input card is already in the pile
     */
    @Throws(CardsAlreadyPresentException::class, NotCompleteRankException::class)
    override fun addCards(cards: Set<T>): DiscardPile<T>
}
