package it.unibo.ds.model.game

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.game.cards.DiscardPile
import it.unibo.ds.model.game.cards.FieldPile
import it.unibo.ds.model.game.event.*

class GameImpl(
    private var discardPileImpl: DiscardPile<Card>,
    private var fieldPileImpl: FieldPile<Card>,
    override val players: List<String>,
) : Game {

    override val discardPile: DiscardPile<Card>
        get() = discardPileImpl

    override val fieldPile: FieldPile<Card>
        get() = fieldPileImpl
    override val declaredRank: Rank?
        get() = declaredRankImpl
    override val lastDeclaredCardNumber: Int?
        get() = lastDeclaredCardNumberImpl

    override val currentPlayer: String
        get() = players[turn % players.size]

    override fun startGame() {
        notify(StartGameEvent(currentPlayer))
    }

    private var eventConsumers: Set<EventConsumer> = emptySet()

    private var declaredRankImpl: Rank? = null

    private var lastDeclaredCardNumberImpl: Int? = null

    private var turn = 0

    override fun register(eventConsumer: EventConsumer) {
        eventConsumers = eventConsumers + eventConsumer
    }

    override fun unregister(eventConsumer: EventConsumer) {
        eventConsumers = eventConsumers - eventConsumer
    }

    override fun notify(event: Event) {
        eventConsumers.forEach { it.consume(event) }
    }

    private fun previousPlayer(): String {
        return players[(turn - 1) % players.size]
    }

    private fun doubtCorrectly(): Boolean =
        lastDeclaredCardNumber != fieldPileImpl.lastCardsPlayed.size ||
                fieldPileImpl.lastCardsPlayed.any { it.rank != declaredRank }

    override fun consume(event: Event) {
        when (event) {

            is PlayerDeclareRankEvent -> {
                declaredRankImpl = event.rank
                fieldPileImpl = fieldPileImpl.addCards(event.cardsPlayed)
                lastDeclaredCardNumberImpl = event.declaredCardNumber
                turn = (turn + 1) % players.size
                notify(PlayerTurnEvent(currentPlayer))
            }

            is DoubtEvent -> {
                if (previousPlayer() == event.lastPlayerWhoPlayed) {
                    turn = if (doubtCorrectly()) {
                        notify(DrawCardsEvent(previousPlayer(), fieldPileImpl.allCards))
                        players.indexOf(event.player)
                    } else {
                        notify(DrawCardsEvent(event.player, fieldPileImpl.allCards))
                        players.indexOf(previousPlayer())
                    }
                    notify(PlayerCanChooseRankEvent(currentPlayer))
                }
            }

            is DiscardCardEvent -> {
                discardPileImpl.areCardsToDiscard(event.cardDiscarded)
            }

            is PlayCardsEvent -> {
                fieldPileImpl = fieldPileImpl.addCards(event.cardsPlayed)
                lastDeclaredCardNumberImpl = event.declaredCardNumber
                turn = (turn + 1) % players.size
                notify(PlayerTurnEvent(currentPlayer))
            }
        }
    }
}