package it.unibo.ds.model.game.cards

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException
import it.unibo.ds.model.game.exception.CardsCompleteRankDropException
import it.unibo.ds.model.game.exception.CardsNotInHandException

/**
 * Interface of the player's hand
 *
 * @param T the type of the cards
 *
 * @property cards return the hand as a list of cards
 */
interface PlayerHand<T : Card> : IncrementingCardsAccumulator<T> {

    val cards: Set<T>

    /**
     * Drop the card considered equal
     *
     * @return a pair with the cards dropped as a set and the new hand
     */
    fun dropEqualCards(): Pair<Set<Card>, PlayerHand<Card>>

    /**
     * check if the given cards can be removed from the hand
     *
     *
     * @return true if the cards can be removed
     */
    fun canCardsBeRemoved(cards: Set<T>): Boolean

    /**
     * Return the cards that can be dropped from the hand (cards with same rank)
     *
     * @return the cards that can be dropped
     */
    fun cardsThatCanBeDropped(): Set<Card>

    /**
     * Remove the input card from the hand
     *
     * @param cards the cards to remove
     *
     * @throws CardsNotInHandException if any card in input isn't in the hand
     * @throws CardsCompleteRankDropException if there are 4 cards with same rank and the cards list contains one or more of those
     */
    @Throws(CardsNotInHandException::class, CardsCompleteRankDropException::class)
    fun removeCards(cards: Set<T>): PlayerHand<T>

    /**
     * Add the input card to the hand
     *
     * @param cards the list of card to add
     *
     * @throws CardsAlreadyPresentException if any card is already in the hand
     */
    @Throws(CardsAlreadyPresentException::class)
    override fun addCards(cards: Set<T>): PlayerHand<T>
}
