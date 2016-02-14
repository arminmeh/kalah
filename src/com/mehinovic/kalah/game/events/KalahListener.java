package com.mehinovic.kalah.game.events;

import com.mehinovic.kalah.game.entity.Pit;
import com.mehinovic.kalah.game.entity.Player;
import com.mehinovic.kalah.game.entity.Stone;
import com.mehinovic.kalah.game.entity.StoneContainer;

/**
 * Defines Kalah Event System.
 */
public interface KalahListener {

    /**
     * called when the game is in the started state.
     */
    public void gameStart();

    /**
     * called when the game has ended, the caller should have already determined the winner and loser
     * if the winner or loser is null, the game was tied
     * @param whoWon the player who won the game, or null for a tie
     * @param whoLost the player who lost the game, or null for a tie
     */
    public void gameEnd(final Player whoWon, final Player whoLost);

    /**
     * called when the specified player has started distributing their stones from the specified pit
     * @param player the player who started distribution
     * @param fromPit the pit that is being used
     */
    public void distStart(final Player player, final Pit fromPit);

    /**
     * called when the specified player has completed distributing their stones from the specified pit.
     * @param player the player who completed distribution
     * @param fromPit the pit that was being used
     */
    public void distEnd(final Player player, final Pit fromPit);

    /**
     * called when a game round ends, and the previous player does not receive a free round.
     * @param newPlayer the player that will now be allowed to make the next move
     */
    public void playerSwitch(final Player newPlayer);

    /**
     * called when the specified player's last stones has landed in his store.
     * he now receives a 'free' move, the player does not change.
     * @param forPlayer the player that receives the free move
     */
    public void freeMove(final Player forPlayer);

    /**
     * called when the specified pit has been depleted of all stones
     * @param pit the pit that is now empty
     */
    public void pitEmpty(final Pit pit);

    /**
     * called when an container (either a pit or a store) receives a stone
     * @param container the container that has received the specified stone
     * @param stone the stone that has been added to the specified container
     */
    public void stoneAdded(final StoneContainer container, final Stone stone);

}