package it.polimi.ingsw.client;

import it.polimi.ingsw.serializableObjects.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * This class allows to handle the connection with the server
 */
public class NetworkHandler implements Runnable {



    private boolean connected;
    private final Socket server;
    private ObjectOutputStream outputStm;
    private ObjectInputStream inputStm;
    private final Client client;


    public NetworkHandler(Socket server, Client client) {
        this.server = server;
        connected = true;
        this.client = client;
    }

    public void init() {

        try {
            outputStm = new ObjectOutputStream(server.getOutputStream());
            inputStm = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            System.out.println("server has died");
        } catch (ClassCastException e) {
            System.out.println("protocol violation");
        }

    }

    public boolean isConnected() {
        return connected;
    }


    @Override
    public void run() {

        init();

        while (connected) {

            try {

                Object returnedValue = handleServerRequest();

                handleClientResponse(returnedValue);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        try {
            server.close();
        } catch (IOException e) {
            System.out.println("CATCH SERVER CLOSE");
            e.printStackTrace();
        }
    }


    /**
     * Allows to manage the requests received.
     *
     * @throws IOException            generated by inputStream
     * @throws ClassNotFoundException generated by inputStream
     */
    private Object handleServerRequest() throws IOException, ClassNotFoundException {

        Message receivedMessage = (Message) inputStm.readObject();

        if (receivedMessage == null)
            return null;

        if (receivedMessage.getMethod().equals("shutdownClient")) {
            connected = false;
            return null;
        }

        return client.update(receivedMessage);
    }


    public synchronized void handleClientResponse(Object clientResponse) throws IOException {

        if (clientResponse == null)
            return;

        outputStm.writeObject(clientResponse);

    }


}
