/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private final Lock lock = new ReentrantLock();

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
    /**
     * For adding dominoes to a player. IF the boolean is true we empty its dominoes
     * @param d
     * @param clear 
     */
    public void setDominoes(List<Domino> d,boolean clear) {
        if(clear){
            this.dominoes.clear();
        }
        this.dominoes.addAll(d);
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
     * maximum domino is not a double THIS CAN BE OPTIMIZED
     *
     * @return
     */
    public int[] getMaxDomino() {
        int[] result = new int[2];
        result[0] = -10;
        //We iterate over the doubles
        for (int i = 0; i < this.dominoes.size(); i++) {
            if (this.dominoes.get(i).isDoble()) {
                if (result[0] < this.dominoes.get(i).getTotalPoints()) {
                    System.out.println("Biggest double is" + this.dominoes.get(i).toString());
                    result[0] = this.dominoes.get(i).getTotalPoints();
                    result[1] = 1;
                }
            }
        }
        //We iterate over all dominoes if there is no doubles
        if (result[0] < 0) {
            for (Domino d : this.dominoes) {
                if (result[0] < d.getTotalPoints()) {
                    System.out.println("Biggest domino is" + d.toString());
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
            System.out.println("Getting points of domino " + d.toString());
            dominoScore += d.getTotalPoints();
        }
        System.out.println("Total score" + dominoScore);
        return dominoScore;
    }

    /**
     * Player adds a dominoi to its list of Dominoes
     */
    public void drawDomino(Domino d) {
        this.dominoes.add(d);
        System.out.println("Player " + this.getName() + " added domino " + d.toString());
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

    /**
     * Removes the played domino. Returns true if the player has run out of
     * Dominoes
     *
     * @param d
     * @return 
     *
     */
    public boolean makeaMove(Domino d) {

        for(Iterator<Domino> it=this.dominoes.iterator();it.hasNext();){
                Domino toRemove = it.next();
                if(toRemove.toString().equals(d.toString())){
                    it.remove();
                }
        }
       

        return this.dominoes.isEmpty();
    }
}
