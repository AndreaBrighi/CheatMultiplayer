package it.unibo.ds.model.game.cards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.ds.model.cards.italian.ItalianCard
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.cards.italian.ItalianSuits
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException
import it.unibo.ds.model.game.exception.CardsCompleteRankDropException
import it.unibo.ds.model.game.exception.CardsNotInHandException

class PlayerHandTest : StringSpec({

    val emptySize = 0
    val oneCardSize = 1
    val cheatFactory = CheatAccumulatorFactory()
    val swordsKing = ItalianCard(ItalianRanks.KING, ItalianSuits.SWORDS)
    val aceOfSwords = ItalianCard(ItalianRanks.ACE, ItalianSuits.SWORDS)
    val fullRankKings = setOf(
        swordsKing,
        ItalianCard(ItalianRanks.KING, ItalianSuits.CUPS),
        ItalianCard(ItalianRanks.KING, ItalianSuits.COINS),
        ItalianCard(ItalianRanks.KING, ItalianSuits.CLUBS),
    )

    "Create a player hand, empty" {
        val hand = cheatFactory.createPlayerHand(setOf())
        hand.size shouldBe emptySize
        hand.isEmpty shouldBe true
    }

    "Create a player hand, not empty" {
        val hand = cheatFactory.createPlayerHand(setOf(aceOfSwords))
        hand.size shouldBe oneCardSize
        hand.isEmpty shouldBe false
    }

    "Player hand add cards" {
        val hand = cheatFactory.createPlayerHand(setOf())
        val newHand = hand.addCards(setOf(aceOfSwords))
        newHand.size shouldBe oneCardSize
        newHand.isEmpty shouldBe false
    }

    "Player add card, but it is already present" {
        val hand = cheatFactory.createPlayerHand(setOf(aceOfSwords))
        val exception = shouldThrow<CardsAlreadyPresentException> {
            hand.addCards(setOf(aceOfSwords))
        }
        exception.duplicatedCards shouldBe setOf(aceOfSwords)
    }

    "Player hand remove cards" {
        val hand = cheatFactory.createPlayerHand(setOf(aceOfSwords))
        val newHand = hand.removeCards(setOf(aceOfSwords))
        newHand.size shouldBe emptySize
        newHand.isEmpty shouldBe true
    }

    "Player remove cards from his hand, but they are not present" {
        val hand = cheatFactory.createPlayerHand(setOf(aceOfSwords))
        val exception = shouldThrow<CardsNotInHandException> {
            hand.removeCards(setOf(swordsKing))
        }
        exception.notPresentCards shouldBe setOf(swordsKing)

    }

    "Player remove a card, but it is a complete rank" {
        val hand = cheatFactory.createPlayerHand(fullRankKings)
        hand.canCardsBeRemoved(fullRankKings) shouldBe false
        shouldThrow<CardsCompleteRankDropException> {
            hand.removeCards(fullRankKings)
        }
    }

    "Player hand drop cards" {
        val hand = cheatFactory.createPlayerHand(fullRankKings)
        val cardsToDrop = hand.cardsThatCanBeDropped()
        cardsToDrop.size shouldBe fullRankKings.size
        cardsToDrop shouldBe fullRankKings
        val (cards, newHand) = hand.dropEqualCards()
        cards shouldBe fullRankKings
        newHand.size shouldBe emptySize
        newHand.isEmpty shouldBe true
    }

    "Player hand drop cards, but there are not cards to drop" {
        val hand = cheatFactory.createPlayerHand(setOf(aceOfSwords))
        val cardsToDrop = hand.cardsThatCanBeDropped()
        cardsToDrop.size shouldBe emptySize
        cardsToDrop shouldBe setOf()
        val (cards, newHand) = hand.dropEqualCards()
        cards shouldBe setOf()
        newHand.size shouldBe oneCardSize
        newHand.isEmpty shouldBe false
    }

    "Player hand drop cards, not all cards can be dropped" {
        val hand = cheatFactory.createPlayerHand(fullRankKings + aceOfSwords)
        val cardsToDrop = hand.cardsThatCanBeDropped()
        cardsToDrop.size shouldBe fullRankKings.size
        cardsToDrop shouldBe fullRankKings
        val (cards, newHand) = hand.dropEqualCards()
        cards shouldBe fullRankKings
        newHand.size shouldBe oneCardSize
        newHand.isEmpty shouldBe false
        newHand.cards shouldBe setOf(aceOfSwords)
    }
})
