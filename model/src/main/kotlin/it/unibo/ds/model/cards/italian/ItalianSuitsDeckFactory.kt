package it.unibo.ds.model.cards.italian

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Deck
import it.unibo.ds.model.cards.DeckFactory
import it.unibo.ds.model.cards.exception.EmptyDeckException
import it.unibo.ds.model.cards.exception.NotDivideFairlyException
import java.util.*
import kotlin.random.Random

/**
 * A class that create italian-suit card's decks
 */
class ItalianSuitsDeckFactory : DeckFactory<Card> {

    /**
     * Create a whole Italian-suit card's decks
     */
    override fun getFullDeck(): Deck<Card> {
        val deck = ArrayList<Card>()
        for (suit in ItalianSuits.entries) {
            for (rank in ItalianRanks.entries) {
                deck.add(ItalianCard(rank, suit))
            }
        }
        return object : Deck<Card> {
            override val isEmpty: Boolean
                get() = deck.size == 0

            override val firstCard: Card
                /**
                 * return the first card on the top of the deck and remove it from the deck
                 *
                 * @throws EmptyDeckException if the deck is empty
                 */
                @Throws(EmptyDeckException::class)
                get() {
                    if (!isEmpty) {
                        return deck.removeAt(0)
                    } else {
                        throw EmptyDeckException()
                    }
                }
            override val lastCard: Card
                /**
                 * return the first card on the bottom of the deck and remove it from the deck
                 *
                 * @throws EmptyDeckException if the deck is empty
                 */
                @Throws(EmptyDeckException::class)
                get() {
                    if (!isEmpty) {
                        return deck.removeAt(deck.size - 1)
                    } else {
                        throw EmptyDeckException()
                    }
                }

            override fun shuffle(): Deck<Card> {
                if (!isEmpty) {
                    val initPosition = List(deck.size) { Random.nextInt(0, deck.size) }
                    val endPosition = List(deck.size) { Random.nextInt(0, deck.size) }
                    for (pos in initPosition) {
                        deck.add(pos, firstCard)
                    }
                    for (pos in endPosition) {
                        deck.add(pos, lastCard)
                    }
                }
                return this
            }

            @Throws(NotDivideFairlyException::class)
            override fun divideCardsFairly(players: Int): List<List<Card>> {
                when {
                    isEmpty -> throw EmptyDeckException()
                    players == 0 -> throw NotDivideFairlyException()
                    deck.size % players == 0 -> {
                        return deck.chunked(deck.size / players).also { deck.removeAll(deck.toSet()) }
                    }
                    else -> throw NotDivideFairlyException()
                }
            }

            override val size: Int
                get() = deck.size

            @Throws(IndexOutOfBoundsException::class)
            override operator fun get(i: Int): Card {
                if (i < deck.size) {
                    return deck[i]
                } else {
                    throw java.lang.IndexOutOfBoundsException()
                }
            }

            /**
             * return the deck as list of cards
             *
             * @return an unmodifiable List of card, the whole deck (don't remove them)
             */
            override fun asList(): List<Card> {
                return Collections.unmodifiableList(deck)
            }
        }
    }
}
