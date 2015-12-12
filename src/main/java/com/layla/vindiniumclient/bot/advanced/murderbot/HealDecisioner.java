package com.layla.vindiniumclient.bot.advanced.murderbot;


import com.layla.vindiniumclient.bot.BotMove;
import com.layla.vindiniumclient.bot.BotUtils;
import com.layla.vindiniumclient.bot.advanced.Pub;
import com.layla.vindiniumclient.bot.advanced.Vertex;
import com.layla.vindiniumclient.dto.GameState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * Decides the best way to get healed.
 *
 * This decisioner will do its best to steer the bot towards a tavern without confrontation.
 *
 * On the Maslow Hierarchy, this falls under safety.
 */


//public class HealDecisioner implements Decision<AdvancedMurderBot.GameContext, BotMove> {
//
//    private static final Logger logger = LogManager.getLogger(HealDecisioner.class);
//
//    @Override
//    public BotMove makeDecision(AdvancedMurderBot.GameContext context) {
//        logger.info("Need to heal; running to nearest pub.");
//
//        Map<GameState.Position, AdvancedMurderBot.DijkstraResult> dijkstraResultMap = context.getDijkstraResultMap();
//
//        // LAYLA: Locate unoccupied pubs (no enemies next to it)
//        
//        Map<GameState.Position, GameState.Hero> heroesByPosition = context.getGameState().getHeroesByPosition();
//        Map<GameState.Position, Vertex> boardGraph = context.getGameState().getBoardGraph();
//
//        // Run to the nearest pub
//        Pub nearestPub = null;
//        AdvancedMurderBot.DijkstraResult nearestPubDijkstraResult = null;
//        for(Pub pub : context.getGameState().getPubs().values()) {
//        	boolean unoccupiedPubs = false;
//        	
//        	 for(Vertex pubVertex : boardGraph.get(context.getGameState().getPubs()).getAdjacentVertices()) {
//          	   // Is there a neighbor in this vertex
//                 GameState.Position neighboringPosition = pubVertex.getPosition();
//                 if(!heroesByPosition.containsKey(neighboringPosition)){
//              	   unoccupiedPubs = true;
//                 }
//              }
//        	
//            AdvancedMurderBot.DijkstraResult dijkstraToPub = dijkstraResultMap.get(pub.getPosition());
//            if(dijkstraToPub != null) {
//                if((nearestPub == null || nearestPubDijkstraResult.getDistance() >
//                    dijkstraToPub.getDistance()) ) {
//                    nearestPub = pub;
//                    nearestPubDijkstraResult = dijkstraResultMap.get(pub.getPosition());
//                }
//            }
//        }
//
//        if(nearestPub == null)
//            return BotMove.STAY;
//
//        // TODO How do we know that we're not walking too close to a foe?
//        GameState.Position nextMove = nearestPub.getPosition();
//        // While it's not right next to you, move towards it
//        while(nearestPubDijkstraResult.getDistance() > 1) {
//            nextMove = nearestPubDijkstraResult.getPrevious();
//            nearestPubDijkstraResult = dijkstraResultMap.get(nextMove);
//        }
//
//        return BotUtils.directionTowards(nearestPubDijkstraResult.getPrevious(), nextMove);
//    }
//}


public class HealDecisioner implements Decision<AdvancedMurderBot.GameContext, BotMove> {

	private static final Logger logger = LogManager.getLogger(HealDecisioner.class);

	@Override
	public BotMove makeDecision(AdvancedMurderBot.GameContext context) {
		logger.info("Need to heal; running to nearest pub.");

		Map<GameState.Position, AdvancedMurderBot.DijkstraResult> dijkstraResultMap = context.getDijkstraResultMap();


		// Run to the nearest pub
		Pub nearestPub = null;
		AdvancedMurderBot.DijkstraResult nearestPubDijkstraResult = null;
		for(Pub pub : context.getGameState().getPubs().values()) {
			AdvancedMurderBot.DijkstraResult dijkstraToPub = dijkstraResultMap.get(pub.getPosition());
			if(dijkstraToPub != null) {
				if(nearestPub == null || nearestPubDijkstraResult.getDistance() >
				dijkstraToPub.getDistance()) {
					nearestPub = pub;
					nearestPubDijkstraResult = dijkstraResultMap.get(pub.getPosition());
				}
			}
		}

		if(nearestPub == null)
			return BotMove.STAY;

		GameState.Position nextMove = nearestPub.getPosition();
		while(nearestPubDijkstraResult.getDistance() > 1){
//				&& (BotUtils.getHeroesAround(context.getGameState(), context.getDijkstraResultMap(), 2).size() > 0)) {
			nextMove = nearestPubDijkstraResult.getPrevious();
			nearestPubDijkstraResult = dijkstraResultMap.get(nextMove);
		}
		
//		Map<GameState.Position, GameState.Hero> heroesByPosition = context.getGameState().getHeroesByPosition();
//		while(nearestPubDijkstraResult.getDistance() > 1) {
//			// LAYLA: Check if enemy is too close (checks radius 2 around me)
//			if(!(BotUtils.getHeroesAround(context.getGameState(), context.getDijkstraResultMap(), 2).size() > 0)){
//				// Goes to next nearest pub
//				return BotUtils.directionTowards(nearestPubDijkstraResult.getPrevious(), nextMove);
//			}
//			// LAYLA : If there is an enemy that's adjacent to the pub, steer clear
//			if (!heroesByPosition.containsKey(nearestPub)){
//				// Goes to next nearest pub
//				return BotUtils.directionTowards(nearestPubDijkstraResult.getPrevious(), nextMove);
//		}
//			else{
//				nextMove = nearestPubDijkstraResult.getPrevious();
//				nearestPubDijkstraResult = dijkstraResultMap.get(nextMove);
//			}
//		}
								
						
		// Goes to next nearest pub
		return BotUtils.directionTowards(nearestPubDijkstraResult.getPrevious(), nextMove);
	}
}
