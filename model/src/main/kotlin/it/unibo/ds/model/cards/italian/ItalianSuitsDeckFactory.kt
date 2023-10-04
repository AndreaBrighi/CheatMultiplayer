package it.unibo.ds.model.cards.italian

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Deck
import it.unibo.ds.model.cards.DeckFactory
import it.unibo.ds.model.cards.exception.NotDivideFairlyException

/**
 * A class that create italian-suit card's decks
 */
class ItalianSuitsDeckFactory : DeckFactory<Card> {

    private fun getDeckFrom(cards: Set<Card>): Deck<Card> {
        return object : Deck<Card> {
            override val firstCard: Card?
                get() = cards.firstOrNull()
            override val lastCard: Card?
                get() = cards.lastOrNull()

            override fun get(i: Int): Card? {
                return cards.elementAtOrNull(i)
            }

            override fun shuffle(): Deck<Card> {
                return getDeckFrom(cards.shuffled().toSet())
            }

            override fun divideCardsFairly(players: Int): Pair<List<Set<Card>>, Deck<Card>> {
                if (players == 0 || cards.size % players != 0 || cards.isEmpty())
                    throw NotDivideFairlyException()
                return Pair(
                    cards.chunked(cards.size / players)
                        .map { it.toSet() }, getEmptyDeck()
                )
            }

            override fun asList(): List<Card> {
                return cards.toList()
            }

            override val isEmpty: Boolean = cards.isEmpty()

            override val size: Int = cards.size

            override fun extractCardAt(i: Int): Pair<Card?, Deck<Card>> {
                return if (cards.isEmpty())
                    Pair(null, this)
                else if (i >= cards.size)
                    Pair(null, this)
                else
                    Pair(cards.elementAt(i), getDeckFrom(cards.minus(cards.elementAt(i))))
            }

        }
    }

    /**
     * Create an empty deck
     */
    override fun getEmptyDeck(): Deck<Card> =
        getDeckFrom(setOf())


    /**
     * Create a whole Italian-suit card's decks
     */
    override fun getFullDeck(): Deck<Card> {
        val deck = ItalianSuits.entries.flatMap { suit ->
            ItalianRanks.entries.map { rank ->
                ItalianCard(rank, suit)
            }
        }.toSet()
        return getDeckFrom(deck)
    }
}
