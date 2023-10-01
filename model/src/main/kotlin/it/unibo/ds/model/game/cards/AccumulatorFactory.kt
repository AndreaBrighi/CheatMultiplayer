package it.unibo.ds.model.game.cards

import it.unibo.ds.model.cards.Card

/**
 * Interface for create cards accumulator using pattern factory
 *
 * @param T the card's type
 */
interface AccumulatorFactory<T : Card> {

    /**
     * Create a new discard pile for a card game
     *
     * @return a DiscardPile object
     */
    fun createDiscardPile(): DiscardPile<T>

    /**
     * Create a new field pile for a card game
     *
     * @return a FieldPile object
     */
    fun createFieldPile(): FieldPile<T>

    /**
     * Create a new player's hand for a card game
     *
     * @param hand the initial cards' set
     *
     * @return a PlayerHand object
     */
    fun createPlayerHand(hand: Set<Card>): PlayerHand<T>
}
