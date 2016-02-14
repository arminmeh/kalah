package com.mehinovic.kalah.game;

import com.mehinovic.kalah.game.entity.Player;
import com.mehinovic.kalah.game.events.KalahListener;

/**
 * Instance of kalah game
 */
public final class Kalah {

    private final transient KalahListener listener;
    private final KalahConfiguration configuration;

    private boolean started = false;
    private Player currentPlayer;
    private Player playerOne;
    private Player playerTwo;

    /**
     * @param configuration The configuration for this game of Kalah
     * @param listener Used to keep track of events within the game
     */
    public Kalah(final KalahConfiguration configuration, final KalahListener listener) {
    	this.configuration = configuration;
    	this.listener = listener;
    }

    /**
     * Starts game
     */
    public void startGame() {
    	if(this.started) {
    		throw new UnsupportedOperationException("Game already started");
    	}

        this.started = true;
        this.getKalahListener().gameStart();
    }

    /**
     * Ends game
     * @param whoWon the player who won this game, null if tie
     * @param whoLost the player who lost this game, null if tie
     */
    public void endGame(final Player whoWon, final Player whoLost) {
    	if(!this.started) {
    		throw new UnsupportedOperationException("Game has not been started");
    	}

        this.started = false;
        this.getKalahListener().gameEnd(whoWon, whoLost);
    }

    /**
     * Check for game end. Game has ended if there are no stones left for one of the players
     * @return the player that no longer has any stones left, or null
     */
    public Player isEndOfGame() {
        final Player currPlayer = this.getCurrentPlayer();
        final Player opponent = currPlayer.getOpponent();

        // we need to find out who played last to figure out who to give the win to.
        if (currPlayer.countPitStones() == 0) {
            return currPlayer;
        // it's not the current player, check his opponent
        } else if (opponent.countPitStones() == 0) {
            return opponent;
        } else {
            return null;
        }
    }

    /**
     * sets the first player of this game
     * @param playerOne Player instance
     */
    public void setPlayerOne(final Player playerOne) {
        this.playerOne = playerOne;
    }

    /**
     * sets the second player of this game
     * @param playerTwo Player instance
     */
    public void setPlayerTwo(final Player playerTwo) {
        this.playerTwo = playerTwo;
    }

    /**
     * sets the current player of the Kalah game
     * @param player the current player
     */
    public synchronized void setCurrentPlayer(final Player player) {
        this.currentPlayer = player;
    }

    /**
     * @return an instance of the first player
     */
    public Player getPlayerOne() {
        return this.playerOne;
    }

    /**
     * @return an instance of the second player
     */
    public Player getPlayerTwo() {
        return this.playerTwo;
    }

    /**
     * @return the current player of the game,
     * always start with player one
     */
    public synchronized Player getCurrentPlayer() {
    	if(this.playerOne == null) {
    		throw new UnsupportedOperationException("Set players first");
    	}
        if (this.currentPlayer == null) {
            this.currentPlayer = this.playerOne;
        }
        return this.currentPlayer;
    }

    /**
     * @return an instance of the configuration options
     */
    public KalahConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * @return a valid instance of the game's listener
     */
    public KalahListener getKalahListener() {
        return this.listener;
    }

    /**
     * @return a JSON representation of the entire game state
     */
    @Override
    public String toString() {
        return KalahSerializer.serializeGame(this);
    }
}