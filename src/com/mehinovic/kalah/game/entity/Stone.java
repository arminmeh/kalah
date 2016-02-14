package com.mehinovic.kalah.game.entity;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents stones that will be placed in players pits or stores
 */
public final class Stone implements Serializable {
    
    private static final long serialVersionUID = 7075048896013032916L;

    /**
     * this counter hands out sequential id's to stones,
     * we need unique stone's per game
     */
    private static final AtomicInteger STONE_COUNTER = new AtomicInteger();


    /**
     * the player that this stone currently belongs to
     */
    private transient Player currentPlayer;
    private final int stoneId;

    /**
     * creates a new stone with a unique id
     */
    public Stone() {
        this.stoneId = STONE_COUNTER.getAndIncrement();
    }

    /**
     * sets the new owner of this stone
     * @param player the player to set the owner to
     */
    public synchronized void setCurrentPlayer(final Player player) {
        this.currentPlayer = player;
    }

    /**
     * retrieves the player that this stone belongs to
     * @return the current player, may be null
     */
    public synchronized Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.stoneId;

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

        final Stone other = (Stone) obj;
        if (this.stoneId != other.stoneId) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Stone{"
                + "currentPlayer=" + currentPlayer
                + ", stoneId=" + stoneId
                + '}';
    }
}
