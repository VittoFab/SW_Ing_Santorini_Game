package it.polimi.ingsw.controller.god;

import it.polimi.ingsw.controller.GodController;
import it.polimi.ingsw.controller.UnableToBuildException;
import it.polimi.ingsw.controller.UnableToMoveException;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.WorkerMoveMap;

import java.util.ArrayList;

public class Charon extends God {

    public String description = "Before your Worker moves, you may force a neighboring opponent Worker to the space directly on the other side of your Worker, if that space is unoccupied.";


    public Charon(GodController godController) {
        super(godController);
    }

    @Override
    public void evolveTurn(Worker worker) throws UnableToMoveException, UnableToBuildException {
        forceMoveEnemy(worker);
        move(worker);
        win(worker);
        build(worker);
    }

    public void forceMoveEnemy(Worker worker) {

        if (!godController.wantToMoveEnemy())
            return;

        WorkerMoveMap moveMap = updateMoveMap(worker);
        Board board = worker.getPlayer().getGame().getBoard();


        ArrayList<Worker> neighboringEnemies = moveMap.neighboringEnemyWorkers();
        int newEnemyX;
        int newEnemyY;

        if(!neighboringEnemies.isEmpty()) {

            //for each neighboring enemy calculates opposite position
            //and removes them from arraylist if opposite position is occupied
            for(Worker Enemy : neighboringEnemies) {
                newEnemyX = 2 * worker.getPosition().getX() - Enemy.getPosition().getX();
                newEnemyY = 2 * worker.getPosition().getY() - Enemy.getPosition().getY();
                Cell newEnemyPosition = board.findCell(newEnemyX,newEnemyY);

                if(newEnemyPosition.isOccupied() || newEnemyX>4 || newEnemyX<0 || newEnemyY<0 || newEnemyY>4)
                    neighboringEnemies.remove(Enemy);
            }

            //now in neighboringEnemies there are only enemy workers that can be displaced

            Worker enemyToMove = godController.ForceMoveEnemy(neighboringEnemies, worker);

            if(enemyToMove == null)
                return;

            int newEnemyToMoveX = 2 * worker.getPosition().getX() - enemyToMove.getPosition().getX();
            int newEnemyToMoveY = 2 * worker.getPosition().getY() - enemyToMove.getPosition().getY();

            enemyToMove.setPosition(newEnemyToMoveX,newEnemyToMoveY);

        }

    }


    public WorkerMoveMap updateMoveMap(Worker worker) {
        WorkerMoveMap moveMap = worker.getMoveMap();

        moveMap.cannotStayStill();
        moveMap.cannotMoveInDomeCell();
        moveMap.cannotMoveInFriendlyWorkerCell();
        moveMap.updateMoveUpRestrictions();

        if(!moveMap.anyAvailableMovePosition())
            //todo Controller lose

        return moveMap;
    }


    public GodController getGodController() {
        return godController;
    }
}
