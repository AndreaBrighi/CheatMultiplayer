package it.unibo.ds.model.cards.italian

import it.unibo.ds.model.cards.Card

data class ItalianCard(override val rank: ItalianRanks, override val suit: ItalianSuits) : Card
