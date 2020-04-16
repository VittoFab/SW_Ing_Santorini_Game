package it.polimi.ingsw.controller;


import it.polimi.ingsw.controller.god.God;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.view.GodView;

import java.util.ArrayList;

/**
 * Manages IO of Gods.
 */
public class GodController {
    private final GodView godView;

    private final GameController gameController;


    public GodController(GodView godView, GameController gameController) {

        this.godView = godView;
        this.gameController = gameController;
    }



    /**
     * This method translates compass directions (N,S,E,...) into coordinates.
     *
     * @param compassInput Compass direction to be translated.
     * @return Variation in coordinates
     */
    @SuppressWarnings("ConstantConditions")
    public int[] getInputInCoordinates(String compassInput) {

        int[] result = new int[2];

        switch (compassInput) {
            case "N": {
                result[0] = -1;
                result[1] = 0;
                break;
            }
            case "NE": {
                result[0] = -1;
                result[1] = 1;
                break;
            }
            case "NW": {
                result[0] = -1;
                result[1] = -1;
                break;
            }
            case "S": {
                result[0] = 1;
                result[1] = 0;
                break;
            }
            case "SE": {
                result[0] = 1;
                result[1] = 1;
                break;
            }
            case "SW": {
                result[0] = 1;
                result[1] = -1;
                break;
            }
            case "W": {
                result[0] = 0;
                result[1] = -1;
                break;
            }
            case "E": {
                result[0] = 0;
                result[1] = 1;
                break;
            }
            case "U": {
                result[0] = 0;
                result[1] = 0;
                break;
            }
            default: {
                result = null;
                break;
            }

        }

        return result;

    }

    /**
     * This method returns the coordinates' variation of the selected movement.
     *
     * @return Coordinates' variation.
     */
    public int[] getInputMove() {

        return getInputInCoordinates(godView.askMovementDirection());
    }


    /**
     * Allows to manage the will to move again.
     *
     * @return True if the player wants to move again, False otherwise.
     */
    public boolean wantToMoveAgain() {
        return godView.askMoveAgain().equals("Y");
    }

    /**
     * Allows to get the right input for the God if the player wants to move up or not.
     * @return True if the player with his worker doesn't want to jump to an higher level, False otherwise.
     */
    public boolean wantToMoveUp() {

        String answer = godView.askWantToMoveUp();
        return answer.equals("Y");

    }


    /**
     * Allows to manage the will of moving an enemy on the board.
     *
     * @return True if the player wants to do it.
     */
    public boolean wantToMoveEnemy() {
        String answer = godView.askWantToMoveEnemy();
        return answer.equals("Y");
    }


    /**
     * Allows to manage the constriction of moving an enemy worker.
     *
     * @param enemyWorkers It's the list of the detected near enemyWorkers.
     * @param worker       It's the worker has selected for this turn.
     * @return The worker the player has chosen to move.
     */
    public Worker ForceMoveEnemy(ArrayList<Worker> enemyWorkers, Worker worker) {
        String workerToMove = godView.askWorkerToMove(enemyWorkers, worker);

        if (workerToMove == null)
            return null;

        int[] boardPosition = getInputInCoordinates(workerToMove);

        Worker chosenEnemy = worker.getPlayer().getGame().getBoard().findCell(boardPosition[0], boardPosition[1]).getWorker();

        return chosenEnemy;
    }

    public void displayBoard(){gameController.getView().printMap();}

    /**
     * This method returns the coordinates where a player wants to build and the specific building.
     *
     * @return Coordinates' variation and type of building.
     */
    public int[] getBuildingInput() {

        int[] buildingInput = new int[3];
        String[] playerInput = godView.askBuildingDirection();

        int[] playerInputCoord = getInputInCoordinates(playerInput[0]);
        buildingInput[0] = playerInputCoord[0];
        buildingInput[1] = playerInputCoord[1];

        if (playerInput[1].equals("B"))
            buildingInput[2] = 0;
        else
            buildingInput[2] = 1;

        return buildingInput;
    }


    /**
     * Allows to translate the players' answer to the will of build another time, all related to a specific God.
     *
     * @param god It's the specific god of the player.
     * @return True for the will of build, False otherwise.
     */
    public boolean wantToBuildAgain(God god) {
        String answer = null;
        if (god.getClass().getSimpleName().equals("Hephaestus"))
            answer = godView.askBuildAgainHephaestus();

        if (god.getClass().getSimpleName().equals("Demeter"))
            answer = godView.askBuildAgainDemeter();

        if (god.getClass().getSimpleName().equals("Hestia"))
            answer = godView.askBuildAgainHestia();

        if (god.getClass().getSimpleName().equals("Prometheus"))
            answer = godView.askBuildPrometheus();


        return answer.equals("Y");
    }


    /**
     * Allows to call the GameController to notify that a player has  won the game.
     */
    //TODO forse questo è possibile rivederlo - da mettere che non si interfacci con la view direttamente, ma col gameController??
    public void winGame(String winnerNickname) {
        gameController.getView().winningView(winnerNickname);
        System.exit(0);
    }


    /**
     * Allows to call the view to print the error screen
     */
    public void errorMoveScreen() {
        godView.printMoveErrorScreen();
    }


    /**
     * Allows to manage the decision of the player to retry the move of his worker.
     *
     * @return True if the player wants to retry.
     */
    public boolean errorMoveDecisionScreen() {
        return godView.printMoveDecisionError().equals("Y");
    }


    /**
     * Allows to manage the decision of the player to retry the build of his worker.
     *
     * @return True if the player wants to retry.
     */
    public boolean errorBuildDecisionScreen() {
        return godView.printBuildDecisionError().equals("Y");
    }


    /**
     * Allows to manage the error screen saw when there's not the possibility to build in the same position.
     */
    public void errorBuildInSamePosition() {
        godView.printBuildInSamePositionScreen();
    }


    /**
     * Allows to manage the error screen saw by the player when his building phase fails.
     */
    public void errorBuildScreen() {
        godView.printBuildGeneralErrorScreen();
    }


    /**
     * Allows to manage the error screen saw by the player when his dome cannot be built
     */
    public void errorBuildDomeScreen() {
        godView.printBuildDomeErrorScreen();
    }


    /**
     * Allows to manage the error screen saw by the player when his block cannot be built
     */
    public void errorBuildBlockScreen() {
        godView.printBuildBlockErrorScreen();
    }

}
