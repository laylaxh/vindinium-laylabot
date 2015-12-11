package com.layla.vindiniumclient.bot.advanced;

import com.layla.vindiniumclient.bot.BotMove;
import com.layla.vindiniumclient.dto.GameState;

import java.util.List;
import java.util.Map;

public interface AdvancedBot {

    public BotMove move(AdvancedGameState gameState);

    /**
     * Called before the game is started
     */
    public void setup();

    /**
     * Called after the game
     */
    public void shutdown();

}
