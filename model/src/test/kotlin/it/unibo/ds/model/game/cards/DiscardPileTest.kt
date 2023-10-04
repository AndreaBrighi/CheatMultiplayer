package it.unibo.ds.model.game.cards

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Deck
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.cards.italian.ItalianCard
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.cards.italian.ItalianSuits
import it.unibo.ds.model.cards.italian.ItalianSuitsDeckFactory
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException
import it.unibo.ds.model.game.exception.NotCompleteRankException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

class DiscardPileTest : StringSpec({
    val emptySize = 0
    val completeRankNumber = 4
    val deckFactory = ItalianSuitsDeckFactory()
    val cheatFactory = CheatAccumulatorFactory()
    val deck: Deck<Card> = deckFactory.getFullDeck()
    val allKing = ItalianSuits.entries.map { s -> ItalianCard(ItalianRanks.KING, s) }.toSet()
    val firstCard = deck[0]!!

    "Create a discard pile, empty by default" {
        val discard = cheatFactory.createDiscardPile()
        discard.size shouldBe emptySize
        discard.isEmpty shouldBe true
        discard.discardedRanks shouldBe setOf()
    }

    "Add cards to discard pile" {
        val discard = cheatFactory.createDiscardPile()
        discard.areCardsToDiscard(allKing) shouldBe true
        val newDiscard = discard.addCards(allKing)
        newDiscard.isEmpty shouldBe false
        newDiscard.size shouldBe completeRankNumber
        newDiscard.discardedRanks shouldBe setOf<Rank>(ItalianRanks.KING)
    }

    "Add cards to discard pile, but the card is already present" {
        val discard = cheatFactory.createDiscardPile()
        val newDiscard = discard.addCards(allKing)
        val exception = assertThrows<CardsAlreadyPresentException> {
            newDiscard.addCards(allKing)
        }
        assertEquals(allKing, exception.duplicatedCards)
    }

    "Add cards to discard pile, but there no cards" {
        val discard = cheatFactory.createDiscardPile()
        assertThrows<NotCompleteRankException> {
            discard.addCards(setOf())
        }
    }

    "Add cards to discard pile, but there are not enough cards" {
        val discard = cheatFactory.createDiscardPile()
        assertThrows<NotCompleteRankException> {
            discard.addCards(setOf(firstCard))
        }
    }

    "Add cards to discard pile, but the cards are not a complete rank" {
        val discard = cheatFactory.createDiscardPile()
        val sameSuitCards = deck.asList().filter { c ->
            c.suit == ItalianSuits.COINS
        }.take(4).toSet()
        discard.areCardsToDiscard(sameSuitCards) shouldBe false
        assertThrows<NotCompleteRankException> {
            discard.addCards(sameSuitCards)
        }
    }
})
