package it.unibo.ds.model.game

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.game.cards.DiscardPile
import it.unibo.ds.model.game.cards.FieldPile
import it.unibo.ds.model.game.event.EventConsumer
import it.unibo.ds.model.game.event.EventSource

interface Game : EventSource, EventConsumer {

    val discardPile: DiscardPile<Card>

    val fieldPile: FieldPile<Card>

    val declaredRank: Rank?

    val lastDeclaredCardNumber: Int?

    val players: List<String>

    val currentPlayer: String

    fun startGame()
}