package com.mehinovic.kalah.servlet;

import com.mehinovic.kalah.game.Kalah;
import com.mehinovic.kalah.game.KalahConfiguration;
import com.mehinovic.kalah.game.entity.Pit;
import com.mehinovic.kalah.game.entity.Player;
import com.mehinovic.kalah.game.entity.Stone;
import com.mehinovic.kalah.game.entity.StoneContainer;
import com.mehinovic.kalah.game.entity.impl.RequestPlayer;
import com.mehinovic.kalah.game.events.KalahListener;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = {
    "/api"
})
public class KalahServlet extends HttpServlet {

    private static final long serialVersionUID = -4153650942420492867L;

    /**
     * session key for kalah game
     */
    public static final String GAME_KEY = "kalah.game";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final StringBuilder result = new StringBuilder("{\"game\":");
        PrintWriter out = null;
        try {
            out = resp.getWriter();

            // create the session if it does not exist
            final HttpSession session = req.getSession(true);
            final String action = req.getParameter("action");

            final boolean isNew = "new".equals(action);
            final boolean isPlay = "play".equals(action);

            // get the current game
            Kalah game = (Kalah) session.getAttribute(GAME_KEY);
            if (game == null && !isNew) {
                result.append("null");
            } else {
                // keep track of client exceptions
                Exception exc = null;

                // if the new action was passed, create a new game
                if (isNew) {
                    final KalahConfiguration config = new KalahConfiguration();

                    game = new Kalah(config, new RequestGameListener());

                    final Player playerOne = new RequestPlayer(game, 1);
                    final Player playerTwo = new RequestPlayer(game, 2);

                    game.setPlayerOne(playerOne);
                    game.setPlayerTwo(playerTwo);
                    game.startGame();
                } else if (isPlay) {
                    try {
                        // let the current player play the round
                        ((RequestPlayer)game.getCurrentPlayer()).play(req);
                    } catch (Exception ex) {
                        exc = ex;
                    }
                }

                // always save the current game back to session
                session.setAttribute(GAME_KEY, game);

                // always return the game state to the client
                result.append(game.toString());

                // add the problem to the response
                if (exc != null) {
                    result.append(", \"problem\":").append("\"")
                            .append(exc.getMessage()).append("\"");
                }

                // check if the player generated a free move
                final Player freeMovePlayer = ((RequestGameListener)game.getKalahListener()).getFreeMovePlayer();
                if (freeMovePlayer != null) {
                    result.append(", \"freeMovePlayer\":").append("\"")
                            .append(freeMovePlayer.toString()).append("\"");
                }

                // check if the game has ended
                if (game.isEndOfGame() != null) {
                    // check who won/lost
                    final Player whoWon = ((RequestGameListener)game.getKalahListener()).getWinner();
                    final Player whoLost = ((RequestGameListener)game.getKalahListener()).getLoser();

                    if (whoLost == null || whoWon == null) {
                        result.append(", \"tied\":true");
                    } else {
                        result.append(", \"winner\":").append("\"")
                                .append(whoWon.getPlayerName()).append("\"");
                        result.append(", \"loser\":").append("\"")
                                .append(whoLost.getPlayerName()).append("\"");
                    }
                }
            }
        } catch (IOException ex) {
        	result.append(", \"problem\":").append("\"")
            	.append("Error occured while processing your request").append("\"");
        } finally {
            resp.setContentType("application/json");
            if (out != null) {
                out.println(result.append("}").toString());
            }
        }
    }

    /**
     * only keeps track of the winner/loser
     */
    private static class RequestGameListener implements KalahListener {

        private Player winner;
        private Player loser;
        private Player freeMovePlayer;

        @Override
        public void gameStart() {}

        @Override
        public void gameEnd(Player whoWon, Player whoLost) {
            this.winner = whoWon;
            this.loser = whoLost;
        }

        public Player getWinner() {
            return this.winner;
        }

        public Player getLoser() {
            return this.loser;
        }

        public Player getFreeMovePlayer() {
            return this.freeMovePlayer;
        }

        @Override
        public void distStart(Player player, Pit fromPit) {
            this.freeMovePlayer = null;
        }

        @Override
        public void distEnd(Player player, Pit fromPit) {}

        @Override
        public void playerSwitch(Player newPlayer) {}

        @Override
        public void freeMove(Player forPlayer) {
            this.freeMovePlayer = forPlayer;
        }

        @Override
        public void pitEmpty(Pit pit) {}

        @Override
        public void stoneAdded(StoneContainer container, Stone stone) {}
    }
}