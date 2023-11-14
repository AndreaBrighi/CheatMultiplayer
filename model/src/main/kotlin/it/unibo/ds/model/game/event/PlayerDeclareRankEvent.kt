package it.unibo.ds.model.game.event

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank

/**
 * Represents an event where a player declares a rank.
 *
 * @property player The player who declares a rank.
 * @property cardsPlayed The cards played by the player.
 * @property declaredCardNumber The number of cards declared by the player.
 * @property rank The rank declared by the player.
 */
class PlayerDeclareRankEvent(
    override val player: String,
    val cardsPlayed: Set<Card>,
    val declaredCardNumber: Int,
    val rank: Rank
) : Event