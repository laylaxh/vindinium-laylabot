package com.layla.vindiniumclient.bot.advanced.murderbot;

import com.layla.vindiniumclient.bot.BotMove;
import com.layla.vindiniumclient.bot.BotUtils;
import com.layla.vindiniumclient.bot.advanced.AdvancedGameState;
import com.layla.vindiniumclient.bot.advanced.Mine;
import com.layla.vindiniumclient.bot.advanced.Vertex;
import com.layla.vindiniumclient.dto.GameState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public EnRouteLootingDecisioner(Decision<AdvancedMurderBot.GameContext, BotMove> noGoodMineDecisioner) {
        this.noGoodMineDecisioner = noGoodMineDecisioner;
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
                if(BotUtils.getHeroesAround(context.getGameState(), context.getDijkstraResultMap(), 1).size() > 0) {
                    logger.info("Mine found, but another hero is too close.");
                    return noGoodMineDecisioner.makeDecision(context);
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
