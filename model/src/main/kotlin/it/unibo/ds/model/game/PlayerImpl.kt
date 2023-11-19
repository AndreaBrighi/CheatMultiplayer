package it.unibo.ds.model.game

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.game.cards.PlayerHand
import it.unibo.ds.model.game.event.*
import it.unibo.ds.model.game.exception.CantDoubtException
import it.unibo.ds.model.game.exception.NotYourTurnException

class PlayerImpl(
    override val name: String,
    private var handImpl: PlayerHand<Card>
) : Player {

    private val eventConsumerSet: MutableSet<EventConsumer> = mutableSetOf()
    private var state: PLAYER_STATE? = null
    private var lastPlayerWhoPlayed: String? = null

    override val hand: PlayerHand<Card>
        get() = handImpl

    override val isTurn: Boolean
        get() = state == PLAYER_STATE.MY_TURN || state == PLAYER_STATE.FIRST_PLAY || state == PLAYER_STATE.DECLARE_RANK

    override val canDoubt: Boolean
        get() = state == PLAYER_STATE.CAN_DOUBT || state == PLAYER_STATE.MY_TURN
    override val canDeclareRank: Boolean
        get() = state == PLAYER_STATE.FIRST_PLAY || state == PLAYER_STATE.DECLARE_RANK

    override fun playCards(cards: Set<Card>, declaredCardNumber: Int) {
        handImpl = handImpl.removeCards(cards)
        notify(PlayCardsEvent(name, cards, declaredCardNumber))
    }

    override fun doubt() {
        lastPlayerWhoPlayed?.run {
            notify(DoubtEvent(name, this))
        } ?: throw CantDoubtException()
    }

    override fun discardCards() {
        val (cards, hand) = handImpl.dropEqualCards()
        handImpl = hand
        notify(DiscardCardEvent(name, cards))
    }

    override fun declareRank(cards: Set<Card>, declaredCardNumber: Int, rank: Rank) {
        if (canDeclareRank && handImpl.canCardsBeRemoved(cards)) {
            if (isFirstPlayOfTheGame()){
                if (firstPlayOfTheGameIsTrue(cards, declaredCardNumber, rank)){
                    notify(PlayerDeclareRankEvent(name, cards, declaredCardNumber, rank))
                }
            }
                if (firstPlayOfTheGameIsTrue(cards, declaredCardNumber, rank) ||
                    lastPersonalPlayIsTrue(cards, declaredCardNumber, rank)
                ) {
                    notify(PlayerDeclareRankEvent(name, cards, declaredCardNumber, rank))
                }

        } else {
            throw NotYourTurnException()
        }
    }

    private fun isLastPersonalPlay(cards: Set<Card>): Boolean {
        return handImpl.cards == cards
    }

    private fun isFirstPlayOfTheGame(): Boolean {
        return state == PLAYER_STATE.FIRST_PLAY
    }

    private fun firstPlayOfTheGameIsTrue(cards: Set<Card>, declaredCardNumber: Int, rank: Rank): Boolean {
        return state == PLAYER_STATE.FIRST_PLAY && cards.size == declaredCardNumber && cards.all { it.rank == rank }
    }


    private fun lastPersonalPlayIsTrue(cards: Set<Card>, declaredCardNumber: Int, rank: Rank): Boolean {
        return handImpl.cards == cards &&
                cards.size == declaredCardNumber &&
                cards.all { it.rank == rank }

    }

    override fun consume(event: Event) {
        when (event) {
            is StartGameEvent ->
                state = if (event.player == name) {
                    PLAYER_STATE.FIRST_PLAY
                } else {
                    PLAYER_STATE.WAITING_PLAY
                }

            is PlayerCanChooseRankEvent ->
                state = if (event.player == name) {
                    PLAYER_STATE.DECLARE_RANK
                } else {
                    PLAYER_STATE.WAITING_PLAY
                }

            is DrawCardsEvent ->
                if (event.player == name) {
                    handImpl = handImpl.addCards(event.cardsDrawn)
                }

            is PlayerTurnEvent ->
                state = if (event.player == name) {
                    PLAYER_STATE.MY_TURN
                } else {
                    if (state == PLAYER_STATE.WAITING_PLAY) {
                        PLAYER_STATE.CAN_DOUBT
                    } else {
                        PLAYER_STATE.WAITING_PLAY
                    }
                }

            is PlayerDeclareRankEvent, is PlayCardsEvent ->
                lastPlayerWhoPlayed = event.player

            is DoubtEvent ->
                lastPlayerWhoPlayed = null

        }
    }

    override fun register(eventConsumer: EventConsumer) {
        eventConsumerSet.add(eventConsumer)
    }

    override fun unregister(eventConsumer: EventConsumer) {
        eventConsumerSet.remove(eventConsumer)
    }

    override fun notify(event: Event) {
        eventConsumerSet.forEach { it.consume(event) }
    }

    companion object {
        private enum class PLAYER_STATE {
            FIRST_PLAY,
            WAITING_PLAY,
            CAN_DOUBT,
            WAIT_CONFIRM_DOUBT,
            MY_TURN,
            WAIT_CONFIRM_PLAYED_CARDS,
            DECLARE_RANK,
            WIN
        }
    }
}