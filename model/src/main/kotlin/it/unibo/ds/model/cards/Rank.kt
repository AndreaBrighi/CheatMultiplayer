package it.unibo.ds.model.cards

/**
 * Interface that represent a rank of a game card
 *
 * @property name the name of the rank
 * @property order the order oft the rank in the suit scale
 * @property shortName the short name of the rank (es. A, 2 ... 10, J, Q, K)
 */
interface Rank {
    val order: Int
    val name: String
    val shortName: String
}
