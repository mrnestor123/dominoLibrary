/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 *
 * @author Barra
 */
public abstract class NetworkConnection {

    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<Message> onReceiveCallback;

    public NetworkConnection(Consumer<Message> onCallback) {
        this.onReceiveCallback = onCallback;
        connectionThread.setDaemon(true);
    }

    public void startConnection() throws Exception {
        connectionThread.start();
    }

    public void send(Message data) throws Exception {
        connectionThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception {
        connectionThread.socket.close();
    }

    protected abstract boolean isServer();

    protected abstract String getIP();

    protected abstract int getPort();

    private class ConnectionThread extends Thread {

        private Socket socket;
        private ObjectOutputStream out;

        public void run() {
            try {
                ServerSocket server = isServer() ? new ServerSocket(getPort()) : null;
                Socket socket = isServer() ? server.accept() : new Socket(getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);
                while (true) {
                    Message data = (Message) in.readObject();
                    onReceiveCallback.accept(data);
                }
                
            } catch (Exception e) {

            }
        }

    }
}
