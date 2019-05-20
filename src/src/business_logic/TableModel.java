/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.io.Serializable;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Barra
 */
public class TableModel implements Serializable{

    private SimpleIntegerProperty numPlayers;
    private SimpleIntegerProperty maxPoints;
    private SimpleStringProperty nameofGame;
    private Table t;

    public TableModel(String nameofGame, Integer maxPoints, Integer numPlayers,Table t) {
        this.numPlayers = new SimpleIntegerProperty(numPlayers);
        this.nameofGame = new SimpleStringProperty(nameofGame);
        this.maxPoints = new SimpleIntegerProperty(maxPoints);
        this.t = t;
    }

    public int getNumPlayers() {
        return numPlayers.get();
    }
    public Table getTable(){
        return t;
    }

    public void setPlayers(int numPlayers) {
        this.numPlayers = new SimpleIntegerProperty(numPlayers);
    }

    public String getNameofGame() {
        return nameofGame.get();
    }

    public void setNameofGame(String Name) {
        this.nameofGame = new SimpleStringProperty(Name);
    }

    public int getMaxPoints() {
        return maxPoints.get();
    }
    
    public void setMaxPoints(int maxPoints){
         this.maxPoints = new SimpleIntegerProperty(maxPoints);
    }
}
