/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import business_logic.Table;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Barra
 */
public class Server extends Thread {

    private final int port;
    private ArrayList<ServerWorker> workerList = new ArrayList<>();
    private Consumer<Message> onCallback;
    private List<Message> historymessages = new ArrayList<>();
    private Map<String, Integer> rooms = new HashMap<>();

    public Server(int port, Consumer<Message> onCallback) {
        this.port = port;
        this.onCallback = onCallback;
    }

    public List<ServerWorker> getWorkerList() {
        return workerList;
    }

    public List<Message> getHistory() {
        return historymessages;
    }

    public void addMessage(Message m) {
        historymessages.add(m);
    }

    public Map<String, Integer> getRooms() {
        return rooms;
    }

    public void addRoom(Message m) {
        rooms.put(m.getTable().getName(), 0);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("About to accept client connection");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from" + clientSocket);
                ServerWorker worker = new ServerWorker(this, clientSocket, this.onCallback);
                workerList.add(worker);
                worker.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
