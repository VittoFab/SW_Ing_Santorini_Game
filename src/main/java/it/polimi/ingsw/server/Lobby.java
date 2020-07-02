package it.polimi.ingsw.server;

import it.polimi.ingsw.server.controller.GameController;

import java.net.Socket;
import java.util.ArrayList;


/**
 * Represents the lobby, where clients can join existing games or create new ones.
 */
public class Lobby {

    private final ArrayList<GameController> games;

    public Lobby() {
        games = new ArrayList<>();
    }

    /**
     * Allocates to a game, or creates a new one if all games are full.
     *
     * @param clientSocket client to allocate to a game.
     */
    public void allocateClient(Socket clientSocket) {

        System.out.println("Connected to " + clientSocket.getInetAddress());

        //search first empty spot in games
        boolean availableEmptySpot = false;

        GameController availableGame = null;

        for (GameController game : games) {

            if (!game.isEnded() && !game.isFull()) {
                availableGame = game;
                availableEmptySpot = true;
                break;
            }
        }

        //JOIN
        if (availableEmptySpot) {

            VirtualView newClient = new VirtualView(clientSocket, availableGame);
            availableGame.join(newClient);

        } else {
            //CREATE

            GameController newGame = new GameController();
            VirtualView newClient = new VirtualView(clientSocket, newGame);
            newGame.create(newClient);
            games.add(newGame);

        }
    }

    /**
     * Remove game from available games arraylist.
     *
     * @param game game to delete
     */
    public void deleteGame(GameController game) {
        games.remove(game);
    }
    //todo synchro on allocate client
    // più thread che vogliono prendere stessa partita
}
