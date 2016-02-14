package com.mehinovic.kalah.game.entity;

/**
 * Store that may contain an arbitrary number of stones.
 */
public final class Store extends StoneContainer {

    private static final long serialVersionUID = -6076885206830186376L;

    public Store(final Player player) {
        //substitute the playerId as store id since players only have one store
        super(player, player.getPlayerId());
    }

    @Override
    public void containerEmpty() {
        return;
    }

    @Override
    public boolean containerReceivedLast(final boolean normalMove) {
        final Player storeOwner = this.getPlayer();
        if (normalMove) {
            // if the last stone lands in the player's store, the player gets an additional move.
            storeOwner.getGame().getKalahListener().freeMove(storeOwner);
            // indicate to the caller we have another move
            return true;
        }

        return false;
    }


    @Override
    public String toString() {
        return "Store{"
                + "storeId=" + this.getPlayer().getPlayerId()
                + ", playerName=" + this.getPlayer().getPlayerName()
                + ", stoneCnt=" + this.amountOfStones()
                + '}';
    }
}