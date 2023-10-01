package it.unibo.ds.model.cards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.beGreaterThan
import io.kotest.matchers.ints.beLessThan
import io.kotest.matchers.shouldBe
import it.unibo.ds.model.cards.exception.EmptyDeckException
import it.unibo.ds.model.cards.exception.NotDivideFairlyException
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.cards.italian.ItalianSuits
import it.unibo.ds.model.cards.italian.ItalianSuitsDeckFactory

class ItalianCardTest : StringSpec({

    val deckFactory = ItalianSuitsDeckFactory()

    "create italian card's deck" {
        val deck = deckFactory.getFullDeck()
        deck.size shouldBe 40
        deck.asList().groupingBy { c -> c.suit }.eachCount() shouldBe mapOf(
            ItalianSuits.CLUBS to 10,
            ItalianSuits.COINS to 10,
            ItalianSuits.CUPS to 10,
            ItalianSuits.SWORDS to 10,
        )
        deck.asList().groupingBy { c -> c.rank }.eachCount() shouldBe mapOf(
            ItalianRanks.ACE to 4,
            ItalianRanks.TWO to 4,
            ItalianRanks.THREE to 4,
            ItalianRanks.FOUR to 4,
            ItalianRanks.FIVE to 4,
            ItalianRanks.SIX to 4,
            ItalianRanks.SEVEN to 4,
            ItalianRanks.KNAVE to 4,
            ItalianRanks.KNIGHT to 4,
            ItalianRanks.KING to 4,
        )
        deck.asList().groupingBy { c -> Pair(c.rank, c.suit) }
            .eachCount()
            .all { i -> i.value == 1 } shouldBe true
    }

    "shuffle italian card's deck check not same position" {
        val shuffledDeck = deckFactory.getFullDeck().shuffle()
        val deck = deckFactory.getFullDeck()
        val confront = shuffledDeck.asList() zip deck.asList()
        var different = 0
        for (item in confront) {
            if (item.first != item.second) {
                different++
            }
        }
        different shouldBe beGreaterThan(30)
    }

    "shuffle italian card's deck check no scale" {
        val shuffledDeck = deckFactory.getFullDeck().shuffle().asList()
        var scale = 0
        for (i in 0 until shuffledDeck.size - 1) {
            if (shuffledDeck[i].suit == shuffledDeck[i + 1].suit && (
                    (shuffledDeck[i].rank.order == shuffledDeck[i + 1].rank.order + 1) ||
                        (shuffledDeck[i].rank.order == shuffledDeck[i + 1].rank.order + -1)
                    )
            ) {
                scale++
            }
        }
        scale shouldBe beLessThan(10)
    }

    "divide italian card's deck for 4 players" {
        val hands4 = deckFactory.getFullDeck().divideCardsFairly(4)
        hands4.size shouldBe 4
        hands4.forAll { it.size shouldBe 10 }
    }

    "divide italian card's deck for 5 players" {
        val hands5 = deckFactory.getFullDeck().divideCardsFairly(5)
        hands5.size shouldBe 5
        hands5.forAll { it.size shouldBe 8 }
    }

    "divide italian card's deck shouldn't have more cards than the deck and unique cards" {
        val hands5 = deckFactory.getFullDeck().divideCardsFairly(5)
        hands5.stream().mapToInt { i -> i.size }.sum() shouldBe 40
        hands5.stream().flatMap { i -> i.stream() }.distinct().count() shouldBe 40
    }

    "divide italian card's deck for 6 players should throw NotDivideFairlyException" {
        shouldThrow<NotDivideFairlyException> { deckFactory.getFullDeck().divideCardsFairly(6) }
    }

    "divide italian card's deck for 0 players should throw NotDivideFairlyException" {
        shouldThrow<NotDivideFairlyException> { deckFactory.getFullDeck().divideCardsFairly(0) }
    }

    "first card from an empty deck should throw EmptyDeckException " {
        val deck = deckFactory.getFullDeck()
        deck.divideCardsFairly(5)
        shouldThrow<EmptyDeckException> { deck.firstCard }
    }

    "last card from an empty deck should throw EmptyDeckException " {
        val deck = deckFactory.getFullDeck()
        deck.divideCardsFairly(5)
        shouldThrow<EmptyDeckException> { deck.lastCard }
    }

    "get card deck" {
        val deck = deckFactory.getFullDeck()
        deck[0].rank shouldBe ItalianRanks.ACE
        deck[0].suit shouldBe ItalianSuits.CUPS
        deck[39].rank shouldBe ItalianRanks.KING
        deck[39].suit shouldBe ItalianSuits.SWORDS
    }

    "get card deck error if index out of bound" {
        val deck = deckFactory.getFullDeck()
        shouldThrow<IndexOutOfBoundsException> { deck[40] }
    }
})
