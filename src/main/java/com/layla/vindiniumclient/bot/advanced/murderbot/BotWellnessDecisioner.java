package com.layla.vindiniumclient.bot.advanced.murderbot;

import java.util.List;
import java.util.Map;

import com.layla.vindiniumclient.bot.BotMove;
import com.layla.vindiniumclient.bot.BotUtils;
import com.layla.vindiniumclient.bot.advanced.Mine;
import com.layla.vindiniumclient.bot.advanced.Vertex;
import com.layla.vindiniumclient.dto.GameState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Decides if the bot is "well" (healthy) and acts accordingly.
 *
 * This decisioner will check to make sure the bot is healthy enough to play on and act accordingly.
 *
 * On Maslow's Hierarchy of needs, this one services psychological and safety needs.
 */
public class BotWellnessDecisioner implements Decision<AdvancedMurderBot.GameContext, BotMove> {

    private static final Logger logger = LogManager.getLogger(BotWellnessDecisioner.class);

    private final Decision<AdvancedMurderBot.GameContext, BotMove> yesDecisioner;
    private final Decision<AdvancedMurderBot.GameContext, BotMove> noDecisioner;

    public BotWellnessDecisioner(Decision<AdvancedMurderBot.GameContext, BotMove> yesDecisioner,
                                 Decision<AdvancedMurderBot.GameContext, BotMove> noDecisioner) {
        this.yesDecisioner = yesDecisioner;
        this.noDecisioner = noDecisioner;
    }

    @Override
    public BotMove makeDecision(AdvancedMurderBot.GameContext context) {

        GameState.Hero me = context.getGameState().getMe();
        Vertex myVertex = context.getGameState().getBoardGraph().get(me.getPos());

        // Do we have money for a pub?
        if(me.getGold() < 2) {
            // We're broke...pretend like we're healthy.
            logger.info("Bot is broke.  Fighting on even if its not healthy.");
            return yesDecisioner.makeDecision(context);
        }

        // Is the bot already next to a pub?  Perhaps its worth a drink
        for(Vertex currentVertex : myVertex.getAdjacentVertices()) {
            if(context.getGameState().getPubs().containsKey(
                    currentVertex.getPosition())) {
                if(me.getLife() < 60) {    // LAYLA changed from 80 to 60
                    logger.info("Bot is next to a pub already and could use health.");
                    return BotUtils.directionTowards(me.getPos(), currentVertex.getPosition());
                }

                // Once we find a pub, we don't care about evaluating the rest
                break;
            }
        }

        
        // LAYLA: Is the bot well?
        // If I have more than 25% of the total mines, make bot healthy at 50 instead of 30 (conservative)
        // However, if I don't have a lot of mines, declare bot healthy at 30
        int totalMineCount = context.getGameState().getMines().size();
        int myMineCount =  me.getMineCount();
        double mineRatio = (myMineCount / totalMineCount);
   
        if((mineRatio <= .25) && context.getGameState().getMe().getLife() >= 50) {
            logger.info("Bot is healthy.");
            return yesDecisioner.makeDecision(context);
        }
        else if ((mineRatio > .25) && context.getGameState().getMe().getLife() >= 30){
        	 logger.info("Bot is healthy.");
             return yesDecisioner.makeDecision(context);
        }
        else {
            logger.info("Bot is damaged.");
            return noDecisioner.makeDecision(context);
        }
    }
}
