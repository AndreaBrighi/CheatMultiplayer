package it.unibo.ds.model.cards.italian

import it.unibo.ds.model.cards.Rank

/**
 * Enum with the italian-suit cards' rank
 */
enum class ItalianRanks(override val order: Int, override val shortName: String) : Rank {
    ACE(1, "Ace"),
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    KNAVE(8, "Knave"),
    KNIGHT(9, "Knight"),
    KING(10, "King"),
}
