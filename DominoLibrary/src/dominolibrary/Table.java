/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Barra
 */
public class Table {

    private List<Player> players = new ArrayList<>();
    private List<Domino> unplayeddominoes = new ArrayList<>();
    private List<Domino> playedDominoes = new ArrayList<>();
    //for knowing the last two dominoes played
    private Map<Integer, Domino> lastdominoes = new HashMap<>();

    private List<Domino> stackedDominoes = new ArrayList<>();

    private int maxPoints;

    public Table(int m) {
        maxPoints = m;
    }

    public void setPlayers(List<Player> p) {
        players = p;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Domino> getPlayedDominoes() {
        return playedDominoes;
    }

    public Map<Integer, Domino> getLastDominoes() {
        return lastdominoes;
    }
//change to mix all dominoes
    void populateTable() {
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
                if (j >= i) {
                    Domino res = new Domino(i, j, j == i, 0);
                    unplayeddominoes.add(res);
                }
            }
        }
        Collections.shuffle(unplayeddominoes);
    }

    public void handleDominoes() {
        int n = 0;
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setDominoes(unplayeddominoes.subList(n, n + 7));
            n += 7;
        }
        if (n < unplayeddominoes.size()) {
            stackedDominoes = unplayeddominoes.subList(n, unplayeddominoes.size());
        }
    }

    /**
     * Precondition player still has dominos
     *
     */
    public boolean placeaDomino(Player p, Domino d) {
       List<Domino> aux = new ArrayList<>();
        if (playedDominoes.isEmpty()) {
            playedDominoes.add(d);
            lastdominoes.put(d.getDirection(), d);  
            //unplayeddominoes.remove(d);
            //p.makeaMove(d);
            return true;
        } else {
            for (Map.Entry<Integer, Domino> e : lastdominoes.entrySet()) {
                if (e.getValue().match(d)) {
                    playedDominoes.add(d);
                    //unplayeddominoes.remove(d); 
                     if (lastdominoes.size() == 2) {
                        lastdominoes.replace(e.getKey(),d);
                    }else{
                         lastdominoes.put(d.getDirection(), d);
                     }
                     return true;
                }
            }
           
        }
        //p.makeaMove(d);
        return false;
    }

/**
 * Precondition The player has ended the game Counts the total points and sums
 * them to the one who winned. Then checks if the game has finished.
 *
 * @param p
 * @return if gamehas ended
 */
public boolean endGame(Player p) {
        int points = 0;
        for (int i = 0; i < players.size(); i++) {
            Player aux = players.get(i);
            if (p.getTeam() != aux.getTeam()) {
                points += aux.getDominoPoints();
            }
        }
        p.sumPoints(points);

        return p.getGameScore() >= maxPoints;
    }

    /**
     * Checks if the game is blocked and then sums the most points to the player
     * that won i.e it had less points
     *
     * For that I created a map ordered in descending order so the winner will
     * be in top
     *
     * @param p that blocked the game
     * @return
     */
    public boolean blockedGame() {
        int pointsperplayer = 0;
        if (playedDominoes.size() > 9 && lastdominoes.get(1).match(lastdominoes.get(2))) {
            Map<Integer, Integer> aux = new TreeMap<>(Collections.reverseOrder());
            for (int i = 0; i < players.size(); i++) {
                Player res = players.get(i);
                if (aux.containsKey(res.getTeam())) {
                    pointsperplayer = aux.get(res.getTeam()) + res.getDominoPoints();
                    aux.put(res.getTeam(), pointsperplayer);
                } else {
                    aux.put(res.getTeam(), res.getDominoPoints());
                }
            }
            int i = 0;
            for (Map.Entry<Integer, Integer> e : aux.entrySet()) {
                System.out.println("El equipo " + i + "tiene" + e.getValue() + "puntos");
            }
            return true;
        } else {
            return false;
        }
    }
}
