package com.mehinovic.kalah.game.entity;

import com.mehinovic.kalah.game.KalahConfiguration;

/**
 * Represents a Pit that belongs to a Player.
 */
public final class Pit extends StoneContainer {

    private static final long serialVersionUID = 4864882643115895129L;

    private final transient KalahConfiguration configuration;
    private final int pitId;

    /**
     * creates a new pit that belongs to the specified player
     * @param configuration the current game configuration
     * @param player the player that this pit belongs to
     * @param pitId the id of this pit
     */
    public Pit(final KalahConfiguration configuration, final Player player, final int pitId) {
        super(player, pitId);

        this.configuration = configuration;
        this.pitId = pitId;

        // ensure correct pitId was supplied
        if(pitId < 0 || pitId > this.configuration.getPits() - 1) {
        	throw new IllegalArgumentException("invalid pitId" + pitId + 
        		" supplied, must be between 0 and " + (this.configuration.getPits() - 1));
        }
        
       final int amtStones = this.configuration.getStones();

        // populate the internal stone list as this is a new object, and will only be created for a new game
        for (int i = 0; i < amtStones; i++) {
            final Stone newStone = new Stone();
            newStone.setCurrentPlayer(player);

            // accept this stone into the pit
            this.accept(newStone);
        }
    }

    /**
     * @return this pit's identifier
     */
    public int getPitId() {
        return this.pitId;
    }

    /**
     * Retrieves the pit on the opposite side of the game board.
     * @param oppositePlayer the player to use for retrieving the opposite pit
     * @return a valid non-null instance of a Pit
     */
    public Pit getOppositePit(final Player oppositePlayer) {
        return oppositePlayer.getPitById(this.configuration.getPits() - this.getPitId() - 1);
    }

    @Override
    public void containerEmpty() {
        this.getPlayer().getGame().getKalahListener().pitEmpty(this);
    }

    /**
     * If the last stone lands in an empty house owned by the player,
     * and the opposite house contains stones, both the last stone and the opposite
     * stones are captured and placed into the player's store.
     * @return a boolean indicating if the current player has another turn
     */
    @Override
    public boolean containerReceivedLast(final boolean normalMove) {
        final Player pitOwner = this.getPlayer();
        final Player currentPlayer = pitOwner.getGame().getCurrentPlayer();

        // if we have one stone, we just received another pit's last stone
        if (pitOwner.equals(currentPlayer) && this.amountOfStones() == 1) {
            final Pit oppositePit = this.getOppositePit(this.getPlayer().getOpponent());

            // if the opposite pit has stones, we transfer ours and theirs to our store
            if (oppositePit.amountOfStones() > 0) {
                final Store store = pitOwner.getStore();
                // take our new stone and the opponent's stones and transfer to our store
                oppositePit.distributeAll(store);
                // since we don't support free moves from this method, 
                // set this call as a special move
                this.distributeTo(store, false);
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "Pit{"
                + "pitId=" + this.getPitId()
                + ", playerId=" + this.getPlayer().getPlayerName()
                + ", stonesCnt=" + this.amountOfStones()
                + '}';
    }
}