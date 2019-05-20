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
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Barra
 */
public class ServerWorker extends Thread {

    private Socket clientSocket;
    private String playerName, room;
    private Server server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Consumer<Message> onCallback;
    private boolean justentered;

    public ServerWorker(Server server, Socket clientSocket, Consumer<Message> onCallback) {
        this.onCallback = onCallback;
        this.server = server;
        this.clientSocket = clientSocket;
        justentered = true;
        room = "";
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

    public String getRoom() {
        return room;
    }

    private void broadcastMessage(Message data) {
        String name = this.playerName;
        for (ServerWorker sw : server.getWorkerList()) {
            if (!name.equals(sw.getName())) {
                sw.send(data);
            }
        }
    }

    private void sendinSameRoom(Message data) {
        String room = this.room;
        for (ServerWorker sw : server.getWorkerList()) {
            if (sw.getRoom().equals(room)) {
                sw.send(data);
            }
        }
    }

    private void directMessage(Message data) throws Exception {
        String room = this.room;
        String sender = this.playerName;
        for (ServerWorker sw : server.getWorkerList()) {
            if (sw.getRoom().equals(room) && !sw.getPlayerName().equals(sender)) {
                sw.send(data);
            }
        }
    }

    public void send(Message data) {
        try {
            out.writeObject(data);
        } catch (Exception e) {

            System.out.println("Could not send message");
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException, Exception {
        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        if (justentered) {
            for (Entry<String, Table> e : server.getRooms().entrySet()) {

                this.send(new Message("Create ", e.getValue()));
            }
            justentered = false;
        }
        try {
            while (true) {
                Message data = (Message) in.readObject();
                String tokens[] = data.getText().split(" ");
                String cmd = tokens[0];
                System.out.println("ServerWorker received: " + data.getText());
                //New user
                if (cmd.equalsIgnoreCase("Connect")) {
                    this.playerName = tokens[1];
                } //Creating a game;
                else if (cmd.equalsIgnoreCase("create")) {
                    this.broadcastMessage(data);
                    server.addRoom(data);
                } //Joining a game 
                else if (cmd.equalsIgnoreCase("join")) {
                    joinedPlayer(data);
                } else if (cmd.equalsIgnoreCase("Movement") || cmd.equalsIgnoreCase("Draw") || cmd.equalsIgnoreCase("Restart") || cmd.equalsIgnoreCase("Blocked") || cmd.equalsIgnoreCase("End")) {
                    this.directMessage(data);
                } else if (cmd.equalsIgnoreCase("Remove")) {
                    this.broadcastMessage(data);
                } else if (cmd.equalsIgnoreCase("close")) {
                    /**
                     * System.out.println("Closing the socket"); in.close();
                     * out.close();
                    clientSocket.close();*
                     */
                    break;
                } else if (cmd.equalsIgnoreCase("closeServer")) {
                    System.out.println("Closing server");
                    server.getSocket().close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
            clientSocket.close();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void joinedPlayer(Message data) throws Exception {
        this.room = data.getTable().getName();
        Map<String, Table> rooms = server.getRooms();
        Table game = data.getTable();

        if (rooms.containsKey(game.getName())) {
            rooms.put(game.getName(), game);
            if (game.getPlayers().size() == game.getNumPlayers()) {
                this.sendinSameRoom(new Message("start ", game));
                this.broadcastMessage(new Message("Remove ", data.getTable()));
                server.getRooms().remove(game.getName());
            }
        }
        //Moving a domino. Client to client message.
    }
}
