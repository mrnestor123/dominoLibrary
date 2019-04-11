/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import business_logic.DominoBox;
import business_logic.Player;
import business_logic.Table;
import java.io.Serializable;

/**
 *
 * @author Barra
 */
public class Message implements Serializable{
    
    private final String text;
    private Player p;
    private DominoBox v;
    private Table t;
    
    public Message(String text){
        this.text=text;
    }
    
    public Message(String text,Player p) {
        this.text = text;
        this.p = p;
    }
    
    
    public Message(String text,Table t){
        this.text=text;
        this.t =t;
    }
    
    public Message(String text,DominoBox v,Player p){
     this.text = text;
     this.v = v;
     this.p = p;
    }
    
    public String getMessage() {
        return text;
    }
     public Player getPlayer() {
        return p;
    }
    
    public DominoBox getDominoMoved(){
        return v;
    }
    
     public Table getTable(){
         return t;
     }
}
    
    
    

