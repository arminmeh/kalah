package com.mehinovic.kalah.game.entity.impl;

import com.mehinovic.kalah.game.Kalah;
import com.mehinovic.kalah.game.entity.Player;

import javax.servlet.http.HttpServletRequest;

/**
 * Player that plays from the parameters within a request object
 */
public class RequestPlayer extends Player {

    private static final long serialVersionUID = 6871288296476778133L;

    public RequestPlayer(Kalah game, int playerId) {
        super(game, playerId);
    }

    /**
     * plays a round of kalah from the specified pitId
     * @param req the request to use to retrieve the pitId
     * @throws KalahException if any play errors occur
     */
    public void play(final HttpServletRequest req) throws IllegalArgumentException, IllegalStateException {
    	if(req == null) {
    		throw new IllegalArgumentException("the request was null!");
    	}
    	
    	Integer pitId = 0;
    	try {
    		pitId = Integer.parseInt(req.getParameter("pitId"));
    	} 
    	catch(Exception e) {
    		throw new IllegalArgumentException("Cannot parse pit Id");
    	}
    	
    	this.distributeFromPit(pitId);
    }
}