/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;

/**
 *
 * @author Barra
 */
public class Player {

    private String name;
    private List<Domino> dominoes = new ArrayList<>();

    //team = 0 | 1 when playing 1vs1 or 2vs2 
    //team = 0 | 1 | 2 when playing 1vs1vs1
    private int team, wins, gameScore;

    //When we create a player he has 0 wins and its gamescore its 0
    //We dont know his team yet
    public Player(String n) {
        gameScore = 0;
        wins=0;
        name = n;
    }

    public boolean endofGame() {
        return this.dominoes.isEmpty();
    }
     
    public void setDominoes(List<Domino> d) {
        this.dominoes = d;
    }
    
    public List<Domino> getDominoes() {
            return this.dominoes;        
    }
    
    public void setTeam(int t){
        team=t;
    }
    
    public String getName(){
        return name;
    }
    public int getTeam() {
        return team;
    }

    public int getGameScore() {
        return gameScore;
    }

    public void sumPoints(int p) {
        this.gameScore += p;
    }

    public void incrementwins() {
        wins++;
    }
//seguramente lo quite tmb
    public int getDominoPoints() {
        int dominoScore = 0;
        for (Domino d : this.dominoes) {
            dominoScore += d.getTotalPoints();
        }
        return dominoScore;
    }
    
    public void makeaMove(Domino d){
        //this.dominoes.remove(d);
    }
}
