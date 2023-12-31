package it.unibo.ds.model.cards

import it.unibo.ds.model.cards.exception.NotDivideFairlyException

/**
 * Interface that represent a Deck of a game cards
 *
 * @param T the type of the card of the deck
 *
 * @property firstCard return the first card on the top of the deck and remove it from the deck (if the deck is empty return null)
 * @property lastCard return the first card on the bottom of the deck and remove it from the deck (if the deck is empty return null)
 * @property isEmpty return true if there are no cards in the deck
 * @property size the number of card in the deck
 */
interface Deck<T : Card> : CardsAccumulator<T> {

    val firstCard: T?

    val lastCard: T?

    /**
     * Return the card at [i] position
     *
     * @param i the index of the card in the deck
     *
     * @return the card at position i, if [i] is greater or equal deck size return null
     */
    operator fun get(i: Int): T?

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
     * @return a Pair of list of set of cards and the empty deck
     * @throws NotDivideFairlyException if size % players is not 0
     */
    @Throws(NotDivideFairlyException::class)
    fun divideCardsFairly(players: Int): Pair<List<Set<T>>, Deck<T>>

    /**
     * Extract the card at [i] position from the deck
     *
     * @param i the index of the card in the deck
     *
     * @return a Pair of the card at position [i] and the deck without the card,
     * if the deck is empty return a Pair of null and the deck,
     * if [i] is greater or equal deck size return a Pair of null and the deck
     */
    fun extractCardAt(i: Int): Pair<T?, Deck<T>>

    /**
     * return the deck as list of cards
     *
     * @return a List of card, the whole deck (don't remove them)
     */
    fun asList(): List<T>
}
