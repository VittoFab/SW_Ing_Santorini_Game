package it.polimi.ingsw;

import it.polimi.ingsw.view.VirtualClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server
{
    public final static int SOCKET_PORT = 7777;


    public static void main(String[] args)
    {
        ServerSocket socket;
        try {
            socket = new ServerSocket(SOCKET_PORT);
        } catch (IOException e) {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }

        while (true) {
            try {
                /* accepts connections; for every connection we accept,
                 * create a new Thread executing a ClientHandler */
                Socket client = socket.accept();
                VirtualClient clientHandler = new VirtualClient(client);

                //separate clienthandler class vs virtualClient
                Thread thread = new Thread(clientHandler, "server_" + client.getInetAddress());
                thread.start();
            } catch (IOException e) {
                System.out.println("connection dropped");
            }
        }
    }



    //called by server right after accept
    public void joinGame() {
        //create clientView
        clientViews.add(new VirtualClient()); //fare check se può essere accolto da game o è full

        //connect to existing game
        //or create new one :



        /*
         * if(gameExists)
         *   join();
         * else
         *   setUpGame();
         * */


    }


}
