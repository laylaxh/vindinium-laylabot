package com.layla.vindiniumclient.bot.advanced.murderbot;


import com.layla.vindiniumclient.bot.BotMove;
import com.layla.vindiniumclient.bot.BotUtils;
import com.layla.vindiniumclient.bot.advanced.AdvancedGameState;
import com.layla.vindiniumclient.bot.advanced.Mine;
import com.layla.vindiniumclient.bot.advanced.Vertex;
import com.layla.vindiniumclient.dto.GameState;
import com.layla.vindiniumclient.dto.GameState.Position;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Decides if we should take some mines on the way to whatever it was we were doing.
 *
 * This decisioner will decide if its worth going after a near-by mine.
 *
 * Maslov says, "self actualization."
 */

public class EnRouteLootingDecisioner implements Decision<AdvancedMurderBot.GameContext, BotMove> {

	private final static Logger logger = LogManager.getLogger(EnRouteLootingDecisioner.class);

	private final Decision<AdvancedMurderBot.GameContext, BotMove> noGoodMineDecisioner;

	// LAYLA: The attackDecisioner will attack a bot standing next to a pub if it's weak enough
	private final Decision<AdvancedMurderBot.GameContext, BotMove> attackDecisioner;

	public EnRouteLootingDecisioner(Decision<AdvancedMurderBot.GameContext, BotMove> noGoodMineDecisioner, 
			Decision<AdvancedMurderBot.GameContext, BotMove> attackDecisioner) {
		this.noGoodMineDecisioner = noGoodMineDecisioner;
		this.attackDecisioner = attackDecisioner;
	}

	@Override
	public BotMove makeDecision(AdvancedMurderBot.GameContext context) {
		GameState.Position myPosition = context.getGameState().getMe().getPos();
		Map<GameState.Position, Vertex> boardGraph = context.getGameState().getBoardGraph();

		// Are we next to a mine that isn't ours?
		for(Vertex currentVertex : boardGraph.get(myPosition).getAdjacentVertices()) {
			Mine mine = context.getGameState().getMines().get(currentVertex.getPosition());
			if(mine != null && (mine.getOwner() == null
					|| mine.getOwner().getId() != context.getGameState().getMe().getId())) {

				// Is it safe to take?

				// LAYLA: Looks for enemies within 3 tiles away
				List<GameState.Hero> enemies = BotUtils.getHeroesAround(context.getGameState(), context.getDijkstraResultMap(), 3);

				boolean myHealthIsGreater = false;

				// Check if my health is greater than both enemies by at least 40
				for (GameState.Hero enemy : enemies) {
					if ((context.getGameState().getMe().getLife() + 40) < enemy.getLife()) {
						myHealthIsGreater = false;
						break;
					} else myHealthIsGreater = true;
				}                

				if(myHealthIsGreater && BotUtils.getHeroesAround(context.getGameState(), context.getDijkstraResultMap(), 1).size() > 0) {
					logger.info("Mine found, another hero is too close, but my health is higher");
					return attackDecisioner.makeDecision(context);
				}
				logger.info("Taking a mine that we happen to already be walking by.");
				return BotUtils.directionTowards(myPosition, mine.getPosition());
			}
		}

		// Nope.
		logger.info("No opportunistic mines exist.");
		return noGoodMineDecisioner.makeDecision(context);
	}
}