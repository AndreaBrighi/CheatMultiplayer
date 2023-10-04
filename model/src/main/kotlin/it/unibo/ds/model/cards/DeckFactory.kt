package it.unibo.ds.model.cards

/**
 * Interface used to fabricate new deck of Card
 *
 * @param T the type of card of the deck
 */
interface DeckFactory<T : Card> {

    /**
     * Create an empty deck
     */
    fun getEmptyDeck(): Deck<T>

    /**
     * Create a full deck of card and return it
     *
     * @return a deck of cards
     */
    fun getFullDeck(): Deck<T>
}
