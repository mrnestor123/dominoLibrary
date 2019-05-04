/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import business_logic.Table;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Barra
 */
public class ServerWorker extends Thread {

    private Socket clientSocket;
    private String playerName;
    private Server server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Consumer<Message> onCallback;
    private boolean justentered;
    private String room;
    

    public ServerWorker(Server server, Socket clientSocket, Consumer<Message> onCallback) {
        this.onCallback = onCallback;
        this.server = server;
        this.clientSocket = clientSocket;
        justentered = true;
        room ="";
        playerName = "";
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public String getRoom(){
        return room;
    }

    private void broadcastMessage(Message data) throws IOException, Exception {
        for (ServerWorker sw : server.getWorkerList()) {
            sw.send(data);
        }
    }
    private void directMessage(Message data) throws Exception {
        String[] tokens = data.getText().split(" ");
        String room = tokens[3];
        String sender = tokens[2];
        for (ServerWorker sw : server.getWorkerList()) {
            if(sw.getRoom().equals(room) && !sw.getPlayerName().equals(sender))
            sw.send(data);
        }  
    }

    public void send(Message data) throws Exception {
        out.writeObject(data);
    }

    private void handleClientSocket() throws IOException, InterruptedException, Exception {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        if (justentered) {
            for (Message m : server.getHistory()) {
                this.send(m);
            }
            justentered = false;
        }
        try {
            while (true) {
                Message data = (Message) in.readObject();
                String tokens[] = data.getText().split(" ");
                String cmd = tokens[0];
                System.out.println("ServerWorker received: " + data.getText());
//                onCallback.accept(data);
                //New user
                if (cmd.equalsIgnoreCase("Connect")) {
                    this.playerName = tokens[1];
                } //Creating a game;
                else if (cmd.equalsIgnoreCase("create")) {
                    this.broadcastMessage(data);
                    server.addRoom(data);
                    server.addMessage(data);
                }
                //Joining a game 
                else if (cmd.equalsIgnoreCase("join")) {
                    this.room = data.getTable().getName();
                    this.broadcastMessage(data);
                    Table game = data.getTable();
                    
                    if (server.getRooms().containsKey(game.getName())) {
                        server.getRooms().put(game.getName(), server.getRooms().get(game.getName()) + 1);
                        if (server.getRooms().get(game.getName()) == game.getNumPlayers()) {
                            this.broadcastMessage(new Message("start", data.getTable()));
                            server.getRooms().remove(game.getName());
                        }
                    }
                    //Moving a domino. Client to client message.
                } else if(cmd.equalsIgnoreCase("Movement")){
                    this.directMessage(data);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
            clientSocket.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
