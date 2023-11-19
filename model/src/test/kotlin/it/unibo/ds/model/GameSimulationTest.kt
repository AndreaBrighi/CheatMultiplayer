package it.unibo.ds.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import it.unibo.ds.model.game.GameFactory

class GameSimulationTest : BehaviorSpec({

    given("A Game") {
        `when`("there are 4 players in total") {
            val playersName = (1..4).map { "player$it" }
            val (game, players) = GameFactory.createGame(playersName)
            then("he has 10 cards in his hand") {
                players.forAll { it.hand.cards.size shouldBe 10 }
            }
            and("the game starts") {
                game.startGame()
                then("the first player should be able to declare a rank and should be is turn") {
                    players.first().canDeclareRank shouldBe true
                    players.first().isTurn shouldBe true
                    players.drop(1).forAll {
                        it.canDeclareRank shouldBe false
                        it.isTurn shouldBe false
                    }
                }
            }
        }
    }

    val playersName = (1..4).map { "player$it" }

    given("A game") {
        and("4 player") {
            `when`("The first player declare a rank") {
                and("play a card") {
                    val (game, players) = GameFactory.createGame(playersName)
                    players.forEach { it.discardCards() }
                    val firstPlayer = players.first()
                    game.startGame()
                    firstPlayer.canDeclareRank shouldBe true
                    firstPlayer.canDoubt shouldBe false
                    firstPlayer.isTurn shouldBe true
                    (players - firstPlayer).forEach { it.isTurn shouldBe false }
                    val card = firstPlayer.hand.cards.first()
                    firstPlayer.declareRank(
                        setOf(card),
                        1,
                        card.rank
                    )
                    firstPlayer.canDeclareRank shouldBe false
                    firstPlayer.canDoubt shouldBe false
                    firstPlayer.isTurn shouldBe false
                    (players - firstPlayer).forEach { it.canDoubt shouldBe true }
                    players[1].isTurn shouldBe true
                    game.declaredRank shouldBe card.rank
                    game.lastDeclaredCardNumber shouldBe 1
                    game.fieldPile.allCards shouldBe setOf(card)
                }
            }

            `when`("The second player play a card") {
                val (game, players) = GameFactory.createGame(playersName)
                players.forEach { it.discardCards() }
                val firstPlayer = players.first()
                val secondPlayer = players[1]
                game.startGame()
                secondPlayer.canDeclareRank shouldBe false
                secondPlayer.canDoubt shouldBe false
                secondPlayer.isTurn shouldBe false
                val card = firstPlayer.hand.cards.first()
                firstPlayer.declareRank(
                    setOf(card),
                    1,
                    card.rank
                )

            }
        }
    }
})