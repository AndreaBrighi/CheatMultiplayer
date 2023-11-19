package it.unibo.ds.model.game

import it.unibo.ds.model.cards.italian.ItalianSuitsDeckFactory
import it.unibo.ds.model.game.cards.CheatAccumulatorFactory

object GameFactory {

    fun createGame(playersName: List<String>): Pair<Game, List<Player>> {
        val (hands, _) = ItalianSuitsDeckFactory()
            .getFullDeck()
            .shuffle()
            .divideCardsFairly(playersName.size)
        val factory = CheatAccumulatorFactory()
        val players = playersName
            .mapIndexed { index, name ->
                PlayerImpl(
                    name,
                    factory.createPlayerHand(hands[index])
                )
            }
        val game = GameImpl(
            factory.createDiscardPile(),
            factory.createFieldPile(),
            playersName
        )
        players.forEach { it.register(game) }
        players.forEach(game::register)
        return game to players
    }
}