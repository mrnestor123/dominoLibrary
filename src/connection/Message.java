/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

import business_logic.Domino;
import business_logic.DominoBox;
import business_logic.Player;
import business_logic.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Barra
 */
public class Message implements Serializable {

    //Different texts for sending messages 0 == creating a table;
    private String text;
    private Player p;
    private DominoBox d;
    private Table t;
    private List<Domino> drawedDominoes = new ArrayList<>();
        
    /**
     * Constructor for sending text messages
     *
     * @param text
     */
    public Message(String text) {
        this.text = text;
    }
    
    /*
    For moving a domino
    */

    public Message(String text, DominoBox d) {
        this.text = text;
        this.d = d;
    }

    /**
     * For joining and creating a new game. Text = create Text = join Text =
     * start
     *
     */
    public Message(String text, Table t) {
        this.text = text;
        this.t = t;
    }

    /**
     * For moving the domino.
    *
     */
    public Message(String text, DominoBox d, Player p) {
        this.text = text;
        this.d = d;
        this.p = p;
    }

    public Message(String text, Player p) {
        this.text = text;
        this.p = p;
    }

    public String getText() {
        return this.text;
    }

    public Player getPlayer() {
        return p;
    }

    public DominoBox getDominoMoved() {
        return d;
    }
     public List<Domino> getDrawedDominoes(){
       return this.drawedDominoes ;
    }
     
    public void setDrawedDominoes(List<Domino> l){
        this.drawedDominoes.addAll(l);
    }

    public Table getTable() {
        return t;
    }

    public void setTable(Table t) {
        this.t = t;
    }
}
