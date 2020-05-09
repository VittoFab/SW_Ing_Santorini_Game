package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.ViewClient;
import it.polimi.ingsw.server.model.*;
import it.polimi.ingsw.server.controller.god.*;


import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controls the flow of the setup of the game.
 */
public class GameController {

    private Game game;
    private TurnHandler turnHandler;
    private GodController godController;
    private final ArrayList<God> godsDeck;
    //private volatile int playersConnected;
    private final ExecutorService executorPlayerAdder;

    public GameController() {
        //playersConnected = 0;
        game = null;
        turnHandler = null;
        //viewSelector = new ViewSelector();
        godsDeck = new ArrayList<>(14);
        executorPlayerAdder = Executors.newCachedThreadPool();

    }


    /**
     * Sets up game and starts the logic flow.
     */
    public synchronized void setUpGame(ViewClient firstClient) {


        /*String viewType = viewSelector.askTypeofView();

        if (viewType.toUpperCase().equals("CLI"))

        else if(viewType.toUpperCase().equals("GUI"))
            view = new GUIMainView(this);
        else
            viewSelector.genericError();
        */

        godController = new GodController(this);
        createDeckGods();

        //firstClient.beginningView();

        int numOfPlayers = firstClient.askNumberOfPlayers();

        game = new Game(numOfPlayers);

        turnHandler = new TurnHandler(game, this);

    }

    /**
     * Adds a player to the game.
     *
     * @param client View of the new player.
     */
    public void addPlayer(ViewClient client) {
        //playersConnected++;

        //prints client connected in server
        client.connected();
        //cannot accept other clients before writing "start"
        client.beginningView();


        setUpObserverView(client);

        setPlayerNickname(client);
        setPlayerColor(client);

        //Thread viewClient = new Thread(client);
        //viewClient.start();
    }


    private void setUpObserverView(ViewClient client) {

        for (int i = 0; i < Board.SIDE; i++) {
            for (int j = 0; j < Board.SIDE; j++) {

                game.getBoard().findCell(i, j).register(client);
            }
        }

    }

    public void removeClientObserver(ViewClient client){

        for (int i = 0; i < Board.SIDE; i++) {
            for (int j = 0; j < Board.SIDE; j++) {

                game.getBoard().findCell(i, j).remove(client);
            }
        }


    }

    /**
     * Lets the player choose his nickname.
     *
     * @param client view of the player.
     */
    private void setPlayerNickname(ViewClient client) {

        while (true) {

            String chosenNickname = client.askPlayerNickname();

            if (nicknameIsAvailable(chosenNickname) && chosenNickname.length() > 0) {
                Player newPlayer = game.addPlayer(chosenNickname, client);
                client.setPlayer(newPlayer);
                return;
            }

            client.notAvailableNickname();
        }
    }

    /**
     * Lets the player choose his color.
     *
     * @param client view of the player.
     */
    private void setPlayerColor(ViewClient client) {

        boolean colorCorrectlyChosen = false;

        while (!colorCorrectlyChosen) {

            String chosenColor = client.askPlayerColor();

            if (colorIsAvailable(chosenColor) && colorIsValid(chosenColor)) {
                client.getPlayer().setColor(Color.StringToColor(chosenColor));
                colorCorrectlyChosen = true;
            } else
                client.notAvailableColor();

        }

    }


    private boolean colorIsValid(String chosenColor) {
        return chosenColor.equals(Color.BLUE.name())
                || chosenColor.equals(Color.BEIGE.name())
                || chosenColor.equals(Color.WHITE.name());
    }

    private boolean nicknameIsAvailable(String chosenNickname) {

        for (Player player : game.getPlayers()) {

            if (chosenNickname.equals(player.getNickname()))
                return false;
        }

        return true;
    }

    private boolean colorIsAvailable(String chosenColor) {

        for (Player player : game.getPlayers()) {

            if (player.getColor() != null
                    && chosenColor.equals(player.getColor().toString()))
                return false;
        }

        return true;
    }

    /**
     * Creates the deck where we can find all the God cards.
     */
    private void createDeckGods() {
        godsDeck.add(new Apollo(godController));
        godsDeck.add(new Artemis(godController));
        godsDeck.add(new Athena(godController));
        godsDeck.add(new Atlas(godController));
        godsDeck.add(new Charon(godController));
        godsDeck.add(new Demeter(godController));
        godsDeck.add(new Hephaestus(godController));
        godsDeck.add(new Hera(godController));
        godsDeck.add(new Hestia(godController));
        godsDeck.add(new Minotaur(godController));
        godsDeck.add(new Pan(godController));
        godsDeck.add(new Prometheus(godController));
        godsDeck.add(new Triton(godController));
        godsDeck.add(new Zeus(godController));
    }

    public void winGame(Player winner) {
        //winningView and losingView are blocking since they must return boolean (although unused)
        ViewClient winnerClient = winner.getClient();

        //print "you have won" in winner view
        winnerClient.winningView();
        winnerClient.killClient();
        //TODO AVOID TO DISCONNECT OTHERS
        //print "you have lost" in loser views
        for (Player player : game.getPlayers()) {

            if (player != winner) {
                player.getClient().losingView(winner.getNickname());
                player.getClient().killClient();
            }
        }

        turnHandler.stopTurnFlow();
    }

    public ArrayList<God> getGodsDeck() {
        return godsDeck;
    }

    public Game getGame() {
        return game;
    }

    public GodController getGodController() {
        return godController;
    }

    public TurnHandler getTurnHandler() {
        return turnHandler;
    }

    /*
    public synchronized int getPlayersConnected() {
        return playersConnected;
    }*/

    public ExecutorService getExecutorPlayerAdder() {
        return executorPlayerAdder;
    }

    public void handleGameDisconnection() {

        //if disconnection is due to a player disconnection
            for (Player player : game.getPlayers()) {
                ViewClient client = player.getClient();

                if (client.isInGame()) {
                    client.notifyOtherPlayerDisconnection();
                    client.killClient();//Sends shut down and sets inGame=false;
                }

            }
    }

    public void notifyPlayersOfLoss(String loserNickname) {

        for (Player player : game.getPlayers()) {
            player.getClient().notifyPlayersOfLoss(loserNickname);
        }

    }


}


