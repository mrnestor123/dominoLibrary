/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 *
 * @author Barra
 */
public class Client extends NetworkConnection{
    
    public String ip;
    private int port;
    
    public Client(String ip, int port,Consumer<Message> onReceiveCallback){
        super(onReceiveCallback);
        this.ip =ip;
        this.port= port;
    } 
    
    @Override
    protected boolean isServer(){
        return false;
    }
    
    @Override
    protected String getIP(){
        return ip;
    }
    
    @Override
    protected int getPort(){
        return port;
    }
    
}
