package it.polimi.ingsw.client;

import it.polimi.ingsw.serializableObjects.Message;

public interface ServerObserver {

    void update(Message receivedMessage);

    //TODO add other methods that will be called on the client by the networkHandler
}

