package it.unibo.ds.model.game.cards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Deck
import it.unibo.ds.model.cards.italian.ItalianSuitsDeckFactory
import it.unibo.ds.model.game.exception.CardsAlreadyPresentException
import it.unibo.ds.model.game.exception.NoCardsPlayedException

class FieldPileTest : StringSpec({

    val emptySize = 0
    val oneCardSize = 1
    val deckFactory = ItalianSuitsDeckFactory()
    val cheatFactory = CheatAccumulatorFactory()
    val deck: Deck<Card> = deckFactory.getFullDeck()
    val field = cheatFactory.createFieldPile()
    val firstCard = deck[0]

    "Create a field pile, empty by default" {
        field.size shouldBe emptySize
        field.isEmpty shouldBe true
        field.allCards shouldBe setOf()
    }

    "Add cards to field pile" {
        val newField = field.addCards(setOf(firstCard))
        newField.isEmpty shouldBe false
        newField.size shouldBe oneCardSize
        newField.lastCardsPlayed shouldBe setOf(firstCard)
        newField.allCards shouldBe setOf(firstCard)
        newField.lastCardsPlayed shouldBe setOf(firstCard)
    }

    "Take card from the pile (not empty)" {
        val cardsToAdd = deck.asList()
            .drop(1)
            .take(2)
            .toSet()
        val totalCards = 2
        val newField = field.addCards(cardsToAdd)
        newField.size shouldBe totalCards
        val (cards, emptyField) = newField.takeCards()
        emptyField.isEmpty shouldBe true
        emptyField.size shouldBe emptySize
        emptyField.allCards shouldBe setOf()
        cards shouldBe cardsToAdd
    }

    "Take card from the pile (empty)" {
        val (cards, emptyField) = field.takeCards()
        emptyField.isEmpty shouldBe true
        emptyField.size shouldBe emptySize
        emptyField.allCards shouldBe setOf()
        cards shouldBe setOf()
    }

    "See last cards played (not empty)" {
        val cardsToAdd = deck.asList()
            .drop(1)
            .take(2)
            .toSet()
        val totalCards = 2
        val newField = field.addCards(cardsToAdd)
        newField.size shouldBe totalCards
        newField.lastCardsPlayed shouldBe cardsToAdd
    }

    "See last cards played (empty)" {
        field.isEmpty shouldBe true
        shouldThrow<NoCardsPlayedException> { field.lastCardsPlayed }
    }

    "See all cards played after take the cards" {
        val cardsToAdd = deck.asList()
            .drop(1)
            .take(2)
            .toSet()
        val totalCards = 2
        val newField = field.addCards(cardsToAdd)
        newField.size shouldBe totalCards
        val (_, emptyField) = newField.takeCards()
        emptyField.isEmpty shouldBe true
        shouldThrow<NoCardsPlayedException> { emptyField.lastCardsPlayed }
    }

    "Add cards to field pile, but the card is already present" {
        val newField = field.addCards(setOf(firstCard))
        val exception = shouldThrow<CardsAlreadyPresentException> { newField.addCards(setOf(firstCard)) }
        exception.duplicatedCards shouldBe setOf(firstCard)
    }
})
