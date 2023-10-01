package it.unibo.ds.model.cards

/**
 * Interface that represent an accumulator of a game cards
 *
 * @param T the type of the card of the accumulator
 *
 * @property isEmpty return true if there are no cards in the accumulator
 * @property size the number of card in the accumulator
 *
 */
interface CardsAccumulator<T : Card> {

    val isEmpty: Boolean

    val size: Int
}
