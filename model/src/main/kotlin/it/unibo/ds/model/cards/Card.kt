package it.unibo.ds.model.cards

/**
 * Interface that represent a game card
 *
 * @property rank the rank of the game card depends on the cards game
 * @property suit the suit of the game card depends on the cards game
 */
interface Card {
    val rank: Rank
    val suit: Suit
}
