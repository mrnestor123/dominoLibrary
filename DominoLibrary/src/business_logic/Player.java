/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;

/**
 *
 * @author Barra
 */
public class Player implements Serializable {

    private String name;
    private List<Domino> dominoes = new ArrayList<>();

    //team = 0 | 1 when playing 1vs1 or 2vs2 
    //team = 0 | 1 | 2 when playing 1vs1vs1
    private int team, wins, gameScore;

    //When we create a player he has 0 wins and its gamescore its 0
    //We dont know his team yet
    public Player(String n) {
        gameScore = 0;
        wins = 0;
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

    public void setTeam(int t) {
        team = t;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
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

    /**
     * Returns the highest double. If there is no double returns the highest
     * domino. Returns a 0 when the maximumdomino is a doble and a 1 when the
     * maximum domino is not a double
     *
     * @return
     */
    public int[] getMaxDomino() {
        int[] result = new int[2];
        result[0] = 0;
        //We iterate over the doubles
        for (int i = 0;i < this.dominoes.size(); i++) {
            if (this.dominoes.get(i).isDoble()) {
                if (result[0] < this.dominoes.get(i).getTotalPoints()) {
                    result[0] = this.dominoes.get(i).getTotalPoints();
                    result[1] = 1;
                }
            }
        }
        //We iterate over all dominoes if there is no doubles
        if (result[0] == 0) {
            for (Domino d : this.dominoes) {
                if (result[0] < d.getTotalPoints()) {
                    result[0] = d.getTotalPoints();
                    result[1] = 0;
                }
            }
        }
        return result;
    }

    /**
     * When the game is finished counts the total points of each domino
     *
     * @return
     */
    public int getDominoPoints() {
        int dominoScore = 0;
        for (Domino d : this.dominoes) {
            dominoScore += d.getTotalPoints();
        }
        return dominoScore;
    }

    /**
     * R
     *
     * @return a list with all the numbers of the domino of the player
     */
    public List<Integer> getAllNumbers() {

        List<Integer> aux = new ArrayList();

        for (Domino d : this.dominoes) {
            aux.addAll(d.getBothNumbers());

        }
        return aux;
    }

    public void makeaMove(Domino d) {
        //this.dominoes.remove(d);
    }
}
