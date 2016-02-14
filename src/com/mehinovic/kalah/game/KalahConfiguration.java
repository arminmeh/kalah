package com.mehinovic.kalah.game;

/**
 * Allows Kalah game configuration
 */
public final class KalahConfiguration {
    private final int pits = 6; //optimized for 2,4,6
    private final int stones = 6;

    /**
     * @return getter for pits
     */
    public int getPits() {
        return this.pits;
    }

    /**
     * @return getter for stones
     */
    public int getStones() {
        return this.stones;
    }
}