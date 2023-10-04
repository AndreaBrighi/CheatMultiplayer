package it.unibo.ds.model.cards

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.beGreaterThan
import io.kotest.matchers.ints.beLessThan
import io.kotest.matchers.shouldBe
import it.unibo.ds.model.cards.exception.NotDivideFairlyException
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.cards.italian.ItalianSuits
import it.unibo.ds.model.cards.italian.ItalianSuitsDeckFactory

class ItalianCardTest : StringSpec({

    val deckFactory = ItalianSuitsDeckFactory()
    val deck = deckFactory.getFullDeck()

    "create italian card's deck" {
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
        deck.isEmpty shouldBe false
    }

    "create empty italian card's deck" {
        val emptyDeck = deckFactory.getEmptyDeck()
        emptyDeck.size shouldBe 0
        emptyDeck.asList().groupingBy { c -> c.suit }.eachCount() shouldBe mapOf()
        emptyDeck.asList().groupingBy { c -> c.rank }.eachCount() shouldBe mapOf()
        emptyDeck.isEmpty shouldBe true
    }

    "shuffle italian card's deck check not same position" {
        val shuffledDeck = deck.shuffle()
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
        val shuffledDeck = deck.shuffle().asList()
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
        val (hands4, newDeck) = deck.divideCardsFairly(4)
        hands4.size shouldBe 4
        hands4.forAll { it.size shouldBe 10 }
        newDeck.size shouldBe 0
    }

    "divide italian card's deck for 5 players" {
        val (hands5, newDeck) =deck.divideCardsFairly(5)
        hands5.size shouldBe 5
        hands5.forAll { it.size shouldBe 8 }
        newDeck.size shouldBe 0
    }

    "divide italian card's deck shouldn't have more cards than the deck and unique cards" {
        val (hands5, newDeck) = deck.divideCardsFairly(5)
        hands5.stream().mapToInt { i -> i.size }.sum() shouldBe 40
        hands5.stream().flatMap { i -> i.stream() }.distinct().count() shouldBe 40
        newDeck.size shouldBe 0
    }

    "divide italian card's deck for 6 players should throw NotDivideFairlyException" {
        shouldThrow<NotDivideFairlyException> { deck.divideCardsFairly(6) }
    }

    "divide italian card's deck for 0 players should throw NotDivideFairlyException" {
        shouldThrow<NotDivideFairlyException> { deck.divideCardsFairly(0) }
    }

    "divide italian card's deck that is empty should throw NotDivideFairlyException" {
        val emptyDeck = deckFactory.getEmptyDeck()
        shouldThrow<NotDivideFairlyException> { emptyDeck.divideCardsFairly(5) }
    }

    "first card from an empty deck should be null" {
        val (_, newDeck) = deck.divideCardsFairly(5)
        newDeck.firstCard shouldBe null
    }

    "last card from an empty deck should be null" {
        val (_, newDeck) = deck.divideCardsFairly(5)
        newDeck.lastCard shouldBe null
    }

    "get card deck" {
        deck[0]!!.rank shouldBe ItalianRanks.ACE
        deck[0]!!.suit shouldBe ItalianSuits.CUPS
        deck[39]!!.rank shouldBe ItalianRanks.KING
        deck[39]!!.suit shouldBe ItalianSuits.SWORDS
        deck.size shouldBe 40
    }

    "get card deck error if index out of bound" {
        deck[40] shouldBe null
    }

    "extract card from italian card's deck"{
        val (card, newDeck) = deck.extractCardAt(0)
        card!!.rank shouldBe ItalianRanks.ACE
        card.suit shouldBe ItalianSuits.CUPS
        newDeck.size shouldBe 39
    }

    "extract card from italian card's deck error if index out of bound"{
        val (card, newDeck) = deck.extractCardAt(40)
        card shouldBe null
        newDeck.size shouldBe 40
    }

    "extract card from italian card's deck error if deck is empty"{
        val emptyDeck = deckFactory.getEmptyDeck()
        val (card, newDeck) = emptyDeck.extractCardAt(0)
        card shouldBe null
        newDeck.size shouldBe 0
    }
})
