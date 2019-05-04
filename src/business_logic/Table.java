   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Barra
 */
public class Table implements Serializable {

    private List<Player> players = new ArrayList<>();
    private List<Domino> playedDominoes = new ArrayList<>();
    private List<Domino> stack = new ArrayList<>();
    //List of shuffled dominoes 
    private List<Domino> shuffledDominoes = new ArrayList<>();

    //The name of the table
    String name;
    //for knowing how many players we have
    int numPlayers;
    //for knowing the last two numbers 
    private int end[];

    private TableModel tmodel;

    //for knowing on which direction the dominoes are moving
    private int[] directionend;

    private double[] endx, endy;
    //Variables for moving the dominoes
    private double width, height;

    private int maxPoints;

    public Table(String name, int maxPoints, int numPlayers) {
        this.name = name;
        this.maxPoints = maxPoints;
        this.numPlayers = numPlayers;
        directionend = new int[]{0, 2};
        end = new int[]{100, 100};
        endy = new double[2];
        endx = new double[2];
        mixDominoes();
    }

    /**
     * The constructor for creating a table.We create a table with a width and a
     * height and a maximum number of points. The variables end1 and end2 are
     * binded to an impossible value. No movements are made when created
     * directionright is set to 0 which is right direction and directionleft to
     * 2 which is the left direction
     *
     * @param m
     * @param w
     * @param h
     */
    public Table(int m, double w, double h) {
        maxPoints = m;
        width = w;
        height = h;
        directionend = new int[]{0, 2};
        end = new int[]{100, 100};
        endy = new double[2];
        endx = new double[2];
        endy[0] = endy[1] = height / 2 - 80 / 2;
        endx[0] = endx[1] = width / 2 - 40 / 2;

    }

    public String getName() {
        return this.name;
    }

    public int getMaxPoints() {
        return this.maxPoints;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public List<Domino> getStack() {
        return this.stack;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public TableModel getTableModel() {
        return tmodel;
    }

    public List<Domino> getPlayedDominoes() {
        return playedDominoes;
    }

    public void setStackedDominoes(List<Domino> stack) {
        this.stack = stack;
    }

    public void setCoordinates(double width, double height) {
        this.width = width;
        this.height = height;

        endy[0] = endy[1] = height / 2 - 80 / 2;
        endx[0] = endx[1] = width / 2 - 40 / 2;
    }
    /**
     * Given a player we 
     * @param p 
     * @return  
     */
    public Player getPlayerinTable(String name){
        for(Player player: this.players){
            if(player.getName().equals(name)){
                return player;
            }
        }
        return null;
    }

    public void setPlayedDominoes(List<Domino> played) {
        this.playedDominoes = played;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setEnds(int end1, int end2) {
        end[0] = end1;
        end[1] = end2;
    }

    public TableModel createTableModel() {
        return new TableModel(name, maxPoints, numPlayers, this);
    }

    /**
     * We create all the dominoes and we assign them to the list of dominoes of
     * the table
     *
     * @return
     */
    public void mixDominoes() {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                Domino res =new Domino(i, j);
                shuffledDominoes.add(res);
            }
        }
        Collections.shuffle(shuffledDominoes);
    }

    /**
     * It handles the dominoes to the given players. It returns the stack or
     * null if there is no stack
     *
     * @param d
     * @param p
     * @param numPlayer
     * @return
     */
    public void handleDominoes() {
        int n = 0;
        List<Domino> aux = new ArrayList<>();
        for (Player player : this.players) {            
            player.setDominoes(shuffledDominoes.subList(n, n += 7));
        }

        if (n < shuffledDominoes.size()) {
            stack.addAll(shuffledDominoes.subList(n, shuffledDominoes.size()));
        }
    }

    public boolean validMove(Domino domino) {
        Domino d = domino;

        return end[0] == 100 || end[0] == d.getNumber1() || end[0] == d.getNumber2() || end[1] == d.getNumber1() || end[1] == d.getNumber2();

    }

    public double getRotation(int e, boolean doble) {
        double rotation = 0;

        switch (directionend[e]) {
            case 0:
                rotation = 270;
                break;
            case 2:
                rotation = 90;
                break;
            case 3:
                rotation = 180;
                break;
        }

        // si se ha cambiado de direccion, o se cambiará al poner esta pieza
        // entonces no se gira.
        if (doble && !corner(e)) {
            rotation += 90;
        }

        return rotation;
    }

    public int findend(Domino d) {
        int e;

        if (end[0] == d.getNumber1()) {
            e = 0;
            end[0] = d.getNumber2();
        } else if (end[0] == d.getNumber2()) {
            e = 0;
            end[0] = d.getNumber1();

        } else if (end[1] == d.getNumber1()) {
            e = 1;
            end[1] = d.getNumber2();
        } else if (end[1] == d.getNumber2()) {
            e = 1;
            end[1] = d.getNumber1();
        } else {
            return -1;
        }

        return e;
    }

    /**
     * For positioning the domino Returns an array with the position x y and the
     * rotationAngle return position for currrent move, and update coordinates
     * and direction for next move
     *
     *
     * Returns 1000 if the game has finished completely i.e a player has reached
     * the maximum of points or 100 if the game has finished and you have to
     * start another game;
     *
     * @param p
     * @param d
     * @return
     */
    public Double[] placeaDomino(Player p, Domino d) {
        double rotation = 0;
        double x0, y0;
        System.out.println("Moving domino " + d.toString() + "in table " + this.getName());
        System.out.println("End1: " + end[0] + " End2: " + end[1]);


        //First tile
        if (end[0] == 100) {
            end[0] = d.getNumber1();
            end[1] = d.getNumber2();

            x0 = endx[0];
            y0 = endy[0];
            if (!d.isDoble()) {
                rotation = 90.0;
                endx[0] += 40;
                endx[1] -= 40;
            } else {
                endx[0] += 20;
                endx[1] -= 20;
            }
        } else {
            // get current coordinates
            int e = findend(d);
            rotation = getRotation(e, d.isDoble());
            if (end[e] == d.getNumber1()) {
                rotation += 180;
            }
            translate(e, d.isDoble());
            x0 = endx[e];
            y0 = endy[e];

            // calculate coordinates for next move
            translate(e, d.isDoble());
            drive(e);
        }
        
        playedDominoes.add(d);

      //Sale concurrentException al ejecutar esto.
     // Se supone que es por borrar un objeto mientras iteras pero no esta dentro de un loop.
        
      // if(
        p.makeaMove(d);
           //HACER algo si acaba. Repetir el juego . No implementado aún porque da Concurrent excepcion
       
              /**  if (!this.endGame(aux)) {
                    return new Double[]{100.0, 100.0, 0.0};
                } else if (this.endGame(aux)) {
                    return new Double[]{1000.0, 1000.0, 0.0};
                }**/
            
        
        return new Double[]{x0, y0, rotation};

    }

    /**
     * Given a number to match and the current direction that when direction ==
     * 0 we are going right direction == 1 we are going down ,direction ==2 we
     * are going left,direction == 3 we are going up
     *
     * returns the orientation of the domino and the number that matches it
     *
     * @param e
     * @param doble
     */
    public void translate(int e, boolean doble) {
        double step = 40;
        System.out.println("We are going in direction" + directionend[e]);

        if (doble && !corner(e)) {
            step = 20;
        }
        switch (directionend[e]) {
            case 0:
                endx[e] += step;
                break;
            case 1:
                endy[e] += step;
                break;
            case 2:
                endx[e] -= step;
                break;
            case 3:
                endy[e] -= step;
                break;
        }

    }

    /**
     * Checks if the direction needs to be changed. We add a margin of 10
     *
     *
     */
    private void drive(int e) {
        if (endx[e] + 120 >= width && directionend[e] == 0) {
            endx[e] += 20;
            endy[e] -= 20;
            directionend[e]++;
        } else if (endy[e] + 120 >= height && directionend[e] == 1) {
            endx[e] += 20;
            endy[e] += 20;
            directionend[e]++;
        } else if (endx[e] - 120 <= 0 && directionend[e] == 2) {
            endx[e] -= 20;
            endy[e] += 20;
            directionend[e]++;
        } else if (endy[e] - 80 <= 0 && directionend[e] == 3) {
            endx[e] -= 20;
            endy[e] -= 20;
            directionend[e] = 0;
        }
    }

    // check if in a corner to not twits doubles
    private boolean corner(int e) {
        if (endx[e] + 280 >= width && directionend[e] == 0) {
            return true;
        } else if (endy[e] + 280 >= height && directionend[e] == 1) {
            return true;
        } else if (endx[e] - 280 <= 0 && directionend[e] == 2) {
            return true;
        } else if (endy[e] - 280 <= 0 && directionend[e] == 3) {
            return true;
        }
        return false;
    }

    /**
     * Returns the position of the player with the highest double that starts
     * the game
     *
     * @return
     */
    public int getPlayerStarting() {
        int playerstarting = 0;
        int[] score = players.get(0).getMaxDomino();
        System.out.println("Player 0 has max score " + score[0] + "Its a double " + score[1] + "   1==true");
        for (int i = 1; i < players.size(); i++) {
            int[] aux = players.get(i).getMaxDomino();
            System.out.println("ENTERING THE LOOP FOR" + players.get(i).getName() + "with most domino" + aux[0] + "with double" + aux[1]);
            if (score[1] < aux[1]) {
                score = aux;
                playerstarting = i;
            } else if (aux[0] > score[0] && score[1] == aux[1]) {
                score = aux;
                playerstarting = i;
            }
        }
        System.out.print("Player " + players.get(playerstarting).getName() + "starts");
        return playerstarting;
    }

    /**
     * Checks wether a player can play a domino or not in all his dominos.
     * Returns true if the player passes the turn
     *
     * @param p
     * @return
     */
    public boolean CanMakeaMove(Player p) {
        List<Integer> dominoesNumbers = p.getAllNumbers();
        
        return playedDominoes.isEmpty() || dominoesNumbers.contains(end[0]) || dominoesNumbers.contains(end[1]);
    }

    /**
     * Given a number it gets a List of the played Dominoes with that number
     *
     * @return
     */
    /**
     * Returns a domino from the stack or null if there is no dominoes in the
     * stack
     *
     * @param p
     * @return
     */
    public Domino getDominoFromStack(Player p) {
        Domino aux = null;
        if (this.stack.size() > 0) {
            aux = stack.remove(stack.size() - 1);
            p.drawDomino(aux);
        }
        return aux;
    }

    /**
     * Precondition The player has run out of dominoes. Counts the total points
     * and sums them to the ones who won. Then checks if the game has finished.
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
     * Given a number it gets a List of the played Dominoes with that number
     *
     * @param n
     * @return
     */
    private List<Domino> playedDominoesfromNumber(int n) {
        List<Domino> result = new ArrayList();
        for (Domino d : playedDominoes) {
            if (d.getBothNumbers().contains(n)) {
                System.out.println("Adding domino" + d.toString());
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Checks if the game is blocked and then sums the most points to the player
     * that won i.e it had less points.
     *
     * @param p that blocked the game
     * @return
     */
    public boolean blockedGame() {
        int pointsperplayer = 0;
        if (playedDominoes.size() > 9) {
            System.out.println("ENTRAMOS AQUI");
            if (end[0] == end[1] && this.playedDominoesfromNumber(end[0]).size() == 7) {
                System.out.println("The game is blocked");
                Map<Integer, Integer> aux = new HashMap<>();
                for (int i = 0; i < players.size(); i++) {
                    if (aux.containsKey(players.get(i).getTeam())) {
                        aux.put(players.get(i).getTeam(), aux.get(players.get(i).getTeam()) + players.get(i).getDominoPoints());
                    } else {
                        aux.put(players.get(i).getTeam(), players.get(i).getDominoPoints());
                    }
                }
                int maxkey = 0;
                int maxValue = 0;
                int minValue = 1000;
                int minkey = 0;
                for (Entry<Integer, Integer> e : aux.entrySet()) {
                    if (maxValue < e.getValue()) {
                        maxValue = e.getValue();
                        maxkey = e.getKey();
                    } else if (minValue > e.getValue()) {
                        minValue = e.getValue();
                        minkey = e.getKey();
                    }
                }

                for (Player p : players) {
                    if (p.getTeam() == minkey) {
                        p.sumPoints(aux.get(maxkey));
                    }
                }

                return true;
                /**
                 * for (int i = 0; i < players.size(); i++) { Player res =
                 * players.get(i); if (aux.containsKey(res.getTeam())) {
                 * pointsperplayer = aux.get(res.getTeam()) +
                 * res.getDominoPoints(); aux.put(res.getTeam(),
                 * pointsperplayer); } else { aux.put(res.getTeam(),
                 * res.getDominoPoints()); } } int i = 0; for
                 * (Map.Entry<Integer, Integer> e : aux.entrySet()) {
                 * System.out.println("El equipo " + i + "tiene" + e.getValue()
                 * + "puntos"); }*
                 */
            }
        }
        return false;
    }

}
