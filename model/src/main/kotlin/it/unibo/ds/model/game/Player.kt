package it.unibo.ds.model.game

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.game.cards.PlayerHand
import it.unibo.ds.model.game.event.EventConsumer
import it.unibo.ds.model.game.event.EventSource

interface Player : EventConsumer, EventSource {

    val name: String

    val hand: PlayerHand<Card>

    val isTurn: Boolean

    val canDoubt: Boolean

    val canDeclareRank: Boolean

    fun playCards(cards: Set<Card>, declaredCardNumber: Int)

    fun doubt()

    fun discardCards()

    fun declareRank(cards: Set<Card>, declaredCardNumber: Int, rank: Rank)
}