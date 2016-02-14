package com.mehinovic.kalah.game.entity;

import com.mehinovic.kalah.game.Kalah;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Player of Kalah game
 */
public abstract class Player implements Serializable {

    private static final long serialVersionUID = 383837985291719651L;

    private final transient Kalah game;
    private final Store store;
    private final Pit[] pits;

    private final int playerId;
    private final String playerName;

    /**
     * creates a new instance of a Player with identifier
     * @param game the game that this player is associated with
     * @param playerId an identifier for this player
     */
    public Player(final Kalah game, final int playerId) {
        this.playerId = playerId;
        this.playerName = "Player" + this.playerId;
        
        if(game == null) {
    		throw new IllegalArgumentException("Cannot create a Player without a valid Game!");
    	}

        this.game = game;
        // create a new empty store for this player, where his stones will be placed
        this.store = new Store(this);
        // initialize this players pits with the configured amount of pits per side
        this.pits = new Pit[this.game.getConfiguration().getPits()];
        for (int i = 0; i < this.pits.length; i++) {
            this.pits[i] = new Pit(this.game.getConfiguration(), this, i);
        }
    }

    /**
     * retrieves an immutable list of this player's pits
     * @return a non-null immutable list of this player's pits
     */
    protected final List<Pit> getPits() {
        return ImmutableList.copyOf(this.pits);
    }

    /**
     * retrieves this player's pit by the specified pitId
     * @param pitId a valid pitId
     * @return a valid pit, or null of none were found or an invalid id was supplied
     */
    protected final Pit getPitById(final int pitId) {
        Pit usePit = null;
        for (Pit pit : this.pits) {
            if (pit.getPitId() == pitId) {
                usePit = pit;
                break;
            }
        }

        return usePit;
    }
    
    /**
     * Count total number of stones in all players pits
     * @return sum of all stones in pits
     */
    public final int countPitStones() {
    	return this.countStones(false);
    }
    
    /**
     * Count total number of stones in store
     * @return sum of stones in store
     */
    public final int countStoreStones() {
    	return this.countStones(true);
    }

    /**
     * Count stones for a player
     * @param countTakenStones should the count stones in store or in the pits?
     * @return the sum of all stones in a player's store or pits
     */
    private final int countStones(final boolean countTakenStones) {
        final List<StoneContainer> containers = Lists.newArrayList();
        if (countTakenStones) {
            containers.add(this.store);
        } else {
        	containers.addAll(Arrays.asList(this.pits));
        }

        int sum = 0;
        for (StoneContainer container : containers) {
            sum += container.amountOfStones();
        }

        return sum;
    }

    /**
     * Starts the distribution process from the specified pitId
     * @param pitId the pitId to start distributing stones from
     * @throws KalahException thrown when any game validation issues occur
     */
    protected final void distributeFromPit(int pitId) throws IllegalStateException {
        // ensure we are allowed to play
        if (!this.equals(this.game.getCurrentPlayer())) {
            throw new IllegalStateException("It is not your turn " + this.playerName);
        }

         // look for the pit from which we should start
        final Pit usePit = this.getPitById(pitId);
        if (usePit == null) {
            throw new IllegalStateException("You cannot make move on that pit");
        }

        // check if there are any stones left
        if (usePit.amountOfStones() == 0) {
            throw new IllegalStateException("There are no stones in that pit");
        }

        final Player oppositePlayer = this.getOpponent();
        if(oppositePlayer == null || oppositePlayer.equals(this)) {
        	throw new IllegalStateException("Could not determine opposite player");
        }

        // establish a game ring, the player will start distribution from his pit
        // around this ring until his stones run out
        final LinkedList<StoneContainer> gameRing = Lists.newLinkedList();

        // add our own pits
        gameRing.addAll(this.getPits());

        // drop in our store, never in opponent's pit
        gameRing.add(this.getStore());

        // drop in opponent's pits
        gameRing.addAll(oppositePlayer.getPits());

        // let the listener know we are starting with distribution
        this.game.getKalahListener().distStart(this, usePit);

        // check if we have another move after this round
        boolean freeMove = false;

        // get next pit
        StoneContainer nextContainer = null;
        int stoneCount = usePit.amountOfStones();
        do {
            //drop first stone in next container
            final int currentIndex = gameRing.indexOf(nextContainer == null ? usePit : nextContainer);
            // get the next container to use
            nextContainer = currentIndex == (gameRing.size() - 1)
                     // if we were at the end, jump to the first one
                    ? gameRing.getFirst()
                     // else we just get the next one in our ring
                    : gameRing.get(currentIndex + 1);
                    
            // freeMove can only possibly be true on the last iteration, so we can do overwrite
            freeMove = usePit.distributeTo(nextContainer);
        } while (--stoneCount > 0);

        // let the listener know we have stopped distribution
        this.game.getKalahListener().distEnd(this, usePit);

        // end of a turn, check if the game has ended (a player has no more stones in a pit)
        // additionally, switch players, and updates scores
        Player endGamePlayer;
        if ((endGamePlayer = this.game.isEndOfGame()) != null) {
            // the other player moves all remaining stones to their store,
            // and the player with the most stones in their store wins.
            final Player endGameOpponent = endGamePlayer.getOpponent();
            final List<Pit> endGamePits = endGameOpponent.getPits();
            for (Pit pit : endGamePits) {
                // distribute all stones to my store
                pit.distributeAll(endGameOpponent.getStore());
            }

            // determine who won the game?
            final Player playerOne = this.getGame().getPlayerOne();
            final Player playerTwo = this.getGame().getPlayerTwo();

            final int p1Score = playerOne.countStoreStones();
            final int p2Score = playerTwo.countStoreStones();

            final Player whoWon = p1Score > p2Score
                    ? playerOne // player one won!
                    : p1Score == p2Score
                            ? null // tie
                            : playerTwo;
            final Player whoLost = whoWon == null
                    ? null : whoWon.getOpponent();

            this.game.endGame(whoWon, whoLost);
        } else if (!freeMove) {
            // change the player to the other player
            this.game.setCurrentPlayer(oppositePlayer);
            this.game.getKalahListener().playerSwitch(oppositePlayer);
        }
    };

    /**
     * @return retrieves the identifier for this player
     */
    public final int getPlayerId() {
        return this.playerId;
    }
    
    /**
     * @return retrieves the name for this player
     */
    public final String getPlayerName() {
        return this.playerName;
    }

    /**
     * retrieves the game this player is currently playing
     * @return a valid non-null instance of the game
     */
    public final Kalah getGame() {
        return this.game;
    }

    /**
     * retrieves this player's Store
     * @return a valid non-null instance of this player's store
     */
    public final Store getStore() {
        return this.store;
    }

    /**
     * retrieves the opponent for this player
     * @return a valid non-null Player instance
     */
    public final Player getOpponent() {
        return this.equals(this.game.getPlayerOne())
                ? this.game.getPlayerTwo() : this.game.getPlayerOne();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.playerId;

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Player other = (Player) obj;
        if (this.playerId != other.playerId) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Player{"
                + "playerId=" + this.playerId
                + ", playerName=" + this.getPlayerName()
                + '}';
    }
}