package it.unibo.ds.model.game.cards

import it.unibo.ds.model.cards.Card
import it.unibo.ds.model.cards.Rank
import it.unibo.ds.model.cards.italian.ItalianRanks
import it.unibo.ds.model.game.exception.*

/**
 * Class that implement the interface AccumulatorFactory for the cards game Cheat
 */
class CheatAccumulatorFactory : AccumulatorFactory<Card> {

    /**
     * Create a new discard pile for a card game
     *
     * @return a DiscardPile object
     * @throws NotCompleteRankException if the cards are not a complete rank
     * @throws CardsAlreadyPresentException if any of the input card is already in the pile
     */
    override fun createDiscardPile(): DiscardPile<Card> {
        return createDiscardPile(setOf())
    }

    private fun createDiscardPile(discard: Set<ItalianRanks> = setOf()): DiscardPile<Card> {
        return object : DiscardPile<Card> {

            override val isEmpty: Boolean
                get() = discard.isEmpty()

            override val size: Int
                get() = discard.size * 4

            override val discardedRanks: Set<ItalianRanks>
                get() = discard.toSet()

            override fun areCardsToDiscard(cards: Set<Card>): Boolean {
                return cards.isNotEmpty() && cards.size % 4 == 0 && cards.groupBy { c -> c.rank }
                    .all { m -> m.value.groupBy { i -> i.suit }.size == 4 }
            }

            @Throws(NotCompleteRankException::class, CardsAlreadyPresentException::class)
            override fun addCards(cards: Set<Card>): DiscardPile<Card> {
                if (discard.any { c -> cards.map { r -> r.rank }.contains(c) }) {
                    throw CardsAlreadyPresentException(cards.filter { c -> discard.contains(c.rank) }.toSet())
                }
                if (!areCardsToDiscard(cards)) {
                    throw NotCompleteRankException()
                }
                return createDiscardPile(cards.groupBy { c -> c.rank as ItalianRanks }.keys)
            }
        }
    }

    /**
     * Create a new field pile for a card game
     *
     * @return a FieldPile object
     * @throws NoCardsPlayedException if the field pile is empty
     */
    override fun createFieldPile(): FieldPile<Card> {
        return createFieldPile(setOf())
    }

    fun createFieldPile(fieldPile: Set<Card> = setOf(), lasts: Set<Card> = setOf()): FieldPile<Card> {
        return object : FieldPile<Card> {
            override val isEmpty: Boolean
                get() = fieldPile.isEmpty()

            override val size: Int
                get() = fieldPile.size

            override val allCards: Set<Card>
                get() = fieldPile.toSet()

            override val lastCardsPlayed: Set<Card>
                get() {
                    if (fieldPile.isEmpty()) {
                        throw NoCardsPlayedException()
                    }
                    return lasts.toSet()
                }

            override fun addCards(cards: Set<Card>): FieldPile<Card> {
                if (fieldPile.any { c -> cards.contains(c) }) {
                    throw CardsAlreadyPresentException(cards.filter { c -> fieldPile.contains(c) }.toSet())
                }
                return createFieldPile(fieldPile + cards, cards)
            }

            override fun takeCards(): Pair<Set<Card>, FieldPile<Card>> {
                return Pair(fieldPile, createFieldPile())
            }
        }
    }

    /**
     * Create a new player's hand for a card game
     *
     * @param hand the initial cards' Set
     *
     * @return a PlayerHand object
     */
    override fun createPlayerHand(hand: Set<Card>): PlayerHand<Card> {
        return object : PlayerHand<Card> {

            override val cards = hand.toMutableSet()

            override val isEmpty: Boolean
                get() = cards.isEmpty()

            override val size: Int
                get() = cards.size

            override fun addCards(cards: Set<Card>): PlayerHand<Card> {
                if (this.cards.any { c -> cards.contains(c) }) {
                    throw CardsAlreadyPresentException(cards.filter { c -> this.cards.contains(c) }.toSet())
                }
                return createPlayerHand(this.cards + cards)
            }

            override fun dropEqualCards(): Pair<Set<Card>, PlayerHand<Card>> {
                val ranks = completeRanks()
                val cardsToDrop = cards.filter { c -> ranks.contains(c.rank) }.toSet()
                return Pair(cardsToDrop, createPlayerHand(cards - cardsToDrop))
            }

            override fun canCardsBeRemoved(cards: Set<Card>): Boolean {
                return cards.all { c -> this.cards.contains(c) } &&
                        cards.none { c -> completeRanks().contains(c.rank) }
            }

            override fun cardsThatCanBeDropped(): Set<Card> {
                return cards.filter { c -> completeRanks().contains(c.rank) }.toSet()
            }

            @Throws(CardsNotInHandException::class, CardsCompleteRankDropException::class)
            override fun removeCards(cards: Set<Card>): PlayerHand<Card> {
                if (!this.cards.containsAll(cards)) {
                    throw CardsNotInHandException(cards.filter { c -> !this.cards.contains(c) }.toSet())
                }
                if (!canCardsBeRemoved(cards)) {
                    throw CardsCompleteRankDropException(this, cards)
                }
                return createPlayerHand(this.cards - cards)
            }

            private fun completeRanks(): Set<Rank> {
                return cards.groupingBy { c -> c.rank }
                    .eachCount()
                    .filter { i -> i.value == 4 }.keys
            }
        }
    }
}
