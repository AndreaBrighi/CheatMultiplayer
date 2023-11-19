package it.unibo.ds.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.unibo.ds.model.cards.italian.ItalianCard
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.cards.italian.ItalianSuits
import it.unibo.ds.model.game.PlayerImpl
import it.unibo.ds.model.game.cards.CheatAccumulatorFactory
import it.unibo.ds.model.game.event.*

class PlayerTest : BehaviorSpec({

    given("A player") {
        val playerName = "player"
        `when`("he has 4 card with the same rank") {
            val cards = setOf(
                ItalianCard(ItalianRanks.ACE, ItalianSuits.COINS),
                ItalianCard(ItalianRanks.ACE, ItalianSuits.CUPS),
                ItalianCard(ItalianRanks.ACE, ItalianSuits.SWORDS),
                ItalianCard(ItalianRanks.ACE, ItalianSuits.CLUBS)
            )
            then("he can drop them") {
                val player = PlayerImpl(
                    playerName,
                    CheatAccumulatorFactory().createPlayerHand(cards)
                )
                val discardConsumer = mockk<EventConsumer>()
                every { discardConsumer.consume(any()) } returns Unit
                player.register(discardConsumer)
                player.discardCards()
                verify { discardConsumer.consume(DiscardCardEvent(playerName, cards)) }
            }
        }
        `when`("he has cards with different rank") {
            val cards = setOf(
                ItalianCard(ItalianRanks.ACE, ItalianSuits.COINS),
                ItalianCard(ItalianRanks.KING, ItalianSuits.CUPS),
                ItalianCard(ItalianRanks.FIVE, ItalianSuits.SWORDS),
                ItalianCard(ItalianRanks.FIVE, ItalianSuits.CLUBS)
            )
            val player = PlayerImpl(
                playerName,
                CheatAccumulatorFactory().createPlayerHand(cards)
            )
            and("he plays first") {
                player.consume(StartGameEvent(playerName))
                then("should be able to declare rank and play cards but not doubt") {
                    player.canDeclareRank shouldBe true
                    player.isTurn shouldBe true
                    player.canDoubt shouldBe false
                }
            }
            and("he plays in second position") {
                val otherPlayerName = "other"
                player.consume(StartGameEvent(otherPlayerName))
                then("should not be able to declare rank, play cards or not doubt") {
                    player.canDeclareRank shouldBe false
                    player.isTurn shouldBe false
                    player.canDoubt shouldBe false
                }
                `when`("after the first player plays") {
                    val cardsPlayed = setOf(ItalianCard(ItalianRanks.FOUR, ItalianSuits.COINS))
                    player.consume(
                        PlayerDeclareRankEvent(
                            otherPlayerName,
                            cardsPlayed,
                            1,
                            ItalianRanks.FOUR
                        )
                    )
                    player.consume(PlayerTurnEvent(playerName))
                    then("should be able to doubt") {
                        player.canDeclareRank shouldBe false
                        player.isTurn shouldBe true
                        player.canDoubt shouldBe true
                    }
                    `when`("he doubts") {
                        then("should generate the event") {
                            val doubtConsumer = mockk<EventConsumer>()
                            every { doubtConsumer.consume(any()) } returns Unit
                            player.register(doubtConsumer)
                            player.doubt()
                            verify { doubtConsumer.consume(DoubtEvent(playerName, otherPlayerName)) }
                        }
                    }
                }
            }
        }
    }
})