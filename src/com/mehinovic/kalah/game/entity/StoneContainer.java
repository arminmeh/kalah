package com.mehinovic.kalah.game.entity;

import com.google.common.collect.Queues;
import java.io.Serializable;
import java.util.Objects;
import java.util.Queue;

/**
 * This class manages stones internally
 */
public abstract class StoneContainer implements Serializable {

    private static final long serialVersionUID = 2083442100764237031L;

    private final transient String containerId;
    private final transient Player player;
    private final Queue<Stone> stones;

    /**
     * New StoneContainer with the specified player as base
     * @param player the player to construct this container with
     * @param containerId the id to use as a base for this container
     */
    public StoneContainer(final Player player, int containerId) {
    	if(player == null) {
    		throw new IllegalArgumentException("Cannot construct a Stone Container witout a Player!");
    	}
    	
        this.player = player;
        this.stones = Queues.newConcurrentLinkedQueue();
        // containers need to be unique per game, this is accomplished
        // by assigning containers per player
        this.containerId = "container_" + this.getClass().getSimpleName().toLowerCase()
                + "_pId" + this.player.getPlayerId() + "_aId" + String.valueOf(containerId);
    }

    /**
     * Called when this container has been exhausted by the distributeTo call
     */
    public abstract void containerEmpty();

    /**
     * Called when this container has received the last stone from a source
     * @param normalMove indicates whether this call occurred as a result of a "normal move",
     * or a special move, such as taking an opponent's stones
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    public abstract boolean containerReceivedLast(final boolean normalMove);

    /**
     * @return Retrieves the amount of stones within this queue
     */
    public final int amountOfStones() {
        return this.stones.size();
    }

    /**
     * Distributes all stones to the specified container, calls containerEmpty()
     * if the current container is empty as a result of this operation
     * @param container the container to distribute the stones to
     */
    protected final void distributeAll(final StoneContainer container) {
        while (!this.stones.isEmpty()) {
            this.distributeTo(container, false);
        }
    }

    /**
     * Distributes a stone to the specified container, calls containerEmpty()
     * if the current container is empty as a result of this operation
     * @param container the container to distribute the stones to
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    protected final boolean distributeTo(final StoneContainer container) {
        return this.distributeTo(container, true);
    }

    /**
     * Distributes a stone to the specified container, calls containerEmpty()
     * if the current container is empty as a result of this operation
     * @param container the container to distribute the stones to
     * @param normalMove indicates whether this call occurred as a result of a "normal move",
     * or a special move, such as taking an opponent's stones
     * @return a boolean indicating if the current player has
     * another turn, since this is a turn based game
     */
    protected boolean distributeTo(final StoneContainer container, final boolean normalMove) {
        boolean anotherTurn = false;
        if (this.stones.size() > 0) {
            final Stone transferStone = this.stones.poll();
            if(transferStone == null) {
        		throw new IllegalStateException("stone to transfer was null!");
        	}

            // send the stone to the source container
            container.accept(transferStone);

            if (this.stones.isEmpty()) {
                // let the source know it is empty
                this.containerEmpty();

                // this distribute call just removed the last stone,
                // let the destination know
                anotherTurn = container.containerReceivedLast(normalMove);
            }
        }

        return anotherTurn;
    }

    /**
     * accepts a stone into this container
     * @param stone a valid non-null stone
     */
    protected final void accept(final Stone stone) {
        if(stone == null) {
    		throw new IllegalArgumentException("Cannot accept a null stone!");
    	}

        // set the current player for the stone
        stone.setCurrentPlayer(this.getPlayer());

        // add the new stone to this container
        this.stones.offer(stone);

        // let the game listener know this container received a stone
        this.getPlayer().getGame().getKalahListener()
                .stoneAdded(this, stone);
    }

    /**
     * Retrieves the player that owns this StoneContainer
     * @return a valid non-null instance of a Player
     */
    public final Player getPlayer() {
        return this.player;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.player);
        hash = 71 * hash + Objects.hashCode(this.containerId);

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

        final StoneContainer other = (StoneContainer) obj;
        if (!Objects.equals(this.containerId, other.containerId)) {
            return false;
        }

        if (!Objects.equals(this.player, other.player)) {
            return false;
        }

        return true;
    }
}
