/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Barra
 */
public class Client {

    private final String ip;
    private final int port;
    private ObjectInputStream serverIn;
    private ObjectOutputStream serverOut;
    private Socket socket;
    private BufferedReader bufferedIn;
    private Consumer<Message> onCallback;
    private boolean startGame = false;

    public Client(String ip, int port, Consumer<Message> onCallback) {
        this.onCallback = onCallback;
        this.ip = ip;
        this.port = port;
    }

    public void setCallBack(Consumer<Message> onCallback) {
        this.onCallback = onCallback;
    }

    public void send(Message data) {
        try{
            serverOut.writeObject(data);
        }catch(Exception e){
            System.out.print("Could not send message");
        }
    }

    public boolean getstartGame() {
        return this.startGame;
    }

    public void close() {
        try {
            Message m = new Message("Close");
            serverOut.writeObject(m);
            serverIn.close();
            serverOut.close();
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public boolean connect() {
        try {
            this.socket = new Socket(ip, port);
            this.serverOut = new ObjectOutputStream(socket.getOutputStream());
            this.serverIn = new ObjectInputStream(socket.getInputStream());
            this.bufferedIn = new BufferedReader(new InputStreamReader(serverIn));
            startMessageReader();
            return true;
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startMessageReader() {
        Thread t = new Thread() {
            @Override
            public void run() {
                readMessageLoop();
            }
        };
        t.start();
    }

    private void readMessageLoop() {
        String line;
        try {

            while (!socket.isClosed()) {
                Message data = (Message) serverIn.readObject();
                String tokens[] = data.getText().split(" ");
                String cmd = tokens[0];

                System.out.println("Client received command: " + cmd);
                //we check if the message is for this client. Just when they move a tile. 

                onCallback.accept(data);

            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            try {
                socket.close();
            } catch (IOException ex1) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
