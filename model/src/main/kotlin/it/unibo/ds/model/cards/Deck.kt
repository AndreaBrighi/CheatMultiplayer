package it.unibo.ds.model.cards

import it.unibo.ds.model.cards.exception.NotDivideFairlyException

/**
 * Interface that represent a Deck of a game cards
 *
 * @param T the type of the card of the deck
 *
 * @property firstCard return the first card on the top of the deck and remove it from the deck
 * @property lastCard return the first card on the bottom of the deck and remove it from the deck
 * @property isEmpty return true if there are no cards in the deck
 * @property size the number of card in the deck
 */
interface Deck<T : Card> : CardsAccumulator<T> {

    val firstCard: T

    val lastCard: T

    /**
     * Return the card at i position
     *
     * @param i the index of the card in the deck
     *
     * @return the card at position i
     * @throws IndexOutOfBoundsException if i greater or equal deck size
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(i: Int): T

    /**
     * Shuffle the current deck of card
     *
     * @return the current deck, shuffled
     */
    fun shuffle(): Deck<T>

    /**
     * Divide all the deck in list with equal number of cards from the deck, that became empty
     *
     * @param players the number of players
     *
     * @return A list of size players, each contains a list with (size % players) cards
     * @throws NotDivideFairlyException if size % players is not 0
     */
    @Throws(NotDivideFairlyException::class)
    fun divideCardsFairly(players: Int): List<List<T>>

    /**
     * return the deck as list of cards
     *
     * @return a List of card, the whole deck (don't remove them)
     */
    fun asList(): List<T>
}
