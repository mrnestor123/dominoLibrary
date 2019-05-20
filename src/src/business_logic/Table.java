   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import dominolibrary.DominosRoomController;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private DominosRoomController room;

    //The name of the table
    String name;
    //for knowing how many players we have
    int numPlayers;
    //for knowing the last two numbers 
    private int end[];

    private TableModel tmodel;

    //for knowing on which direction the dominoes are moving
    private int[] directionend, radius;
    //Variables for positioning
    private double[] endx, endy;
    //Variables for moving the dominoes
    private double width, height;

    private int maxPoints;

    private boolean[] directionchanged;

    public Table(String name, int maxPoints, int numPlayers) {
        this.name = name;
        this.maxPoints = maxPoints;
        this.numPlayers = numPlayers;
        directionend = new int[]{0, 2};
        end = new int[]{100, 100};
        endy = new double[2];
        endx = new double[2];
        radius = new int[]{200, 200};
        directionchanged = new boolean[]{false, false};
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
        mixDominoes();
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

    public void setShuffledDominoes(List<Domino> l) {
        this.shuffledDominoes = l;
    }

    public List<Domino> getStack() {
        return this.stack;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Domino> getShuffledDominoes() {
        return shuffledDominoes;
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
     *
     * @param p
     * @return
     */
    public Player getPlayerinTable(String name) {
        for (Player player : this.players) {
            if (player.getName().equals(name)) {
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

        if (shuffledDominoes.isEmpty()) {
            for (int i = 0; i <= 6; i++) {
                for (int j = i; j <= 6; j++) {
                    Domino res = new Domino(i, j);
                    shuffledDominoes.add(res);
                }
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
            player.setDominoes(shuffledDominoes.subList(n, n += 7), true);
        }

        if (n < shuffledDominoes.size()) {
            stack.addAll(shuffledDominoes.subList(n, shuffledDominoes.size()));
            System.out.println("LAST DOMINO OF STACK" + stack.get(stack.size() - 1));
        }
    }

    public boolean validMove(Domino domino) {
        Domino d = domino;

        return end[0] == 100 || end[0] == d.getNumber1() || end[0] == d.getNumber2() || end[1] == d.getNumber1() || end[1] == d.getNumber2();

    }

    public double getRotation(int e, boolean doble) {
        double rotation = 0;

        System.out.println("Changed direction " + directionchanged[e]);

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
        if (doble && !corner(e) && !directionchanged[e]) {
            rotation += 90;
        }

        return rotation;
    }

    public int[] findend(Domino d) {
        int e = 0;
        int times = 0;
        int match = 0;
        if (end[0] == d.getNumber1() || end[0] == d.getNumber2()) {
            e = 0;
            times++;
        }
        if (end[1] == d.getNumber1() || end[1] == d.getNumber2()) {
            e = 1;
            times++;
        }
        return new int[]{e, times};
    }

    /**
     * For positioning the domino Returns an array with the position x y and the
     * rotationAngle return position for currrent move, and update coordinates
     * and direction for next move
     *
     * @param p
     * @param d
     * @param decision
     * @return
     */
    public Double[] placeaDomino(Player p, Domino d, int decision) {
        double rotation = 0;
        double x0 = 0;
        double y0 = 0;
        int e = 0;
        System.out.println("Moving domino " + d.toString() + "in table " + this.getName());
        System.out.println("End1: " + end[0] + " End2: " + end[1]);
        int[] aux = new int[2];
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
            aux[1] = 1;
            playedDominoes.add(d);
        } else {
            // get current end
            aux = findend(d);
            e = aux[0];
            if (aux[1] == 2 && decision != 10) {
                e = decision;
                System.out.println("Choosed End " + decision);
                aux[1]=1;
            }
            if (aux[1] != 2) {
                end[e] = d.getOppositeNumber(d.getMatch(end[e]+1));
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
                playedDominoes.add(d);

            }
        }
        //1 when it is the regular 
       //2  you can choose the end 
        //0 ended round
        //4  ended Game
        //3 blocked
        if (p.makeaMove(d)) {
            if (!this.endGame(p)) {
                return new Double[]{x0, y0, rotation, 0.0};
            } else {
                return new Double[]{x0, y0, rotation, 4.0};
            }
        } else if (this.blockedGame()) {
            if (!this.endGame(p)) {
                return new Double[]{x0, y0, rotation, 3.0};
            } else {
                return new Double[]{x0, y0, rotation, 4.0};
            }
        }
        System.out.println(aux[1] + " times");
        /**
         * *
         */
        return new Double[]{x0, y0, rotation, (double) aux[1]};
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

        if (doble && !corner(e) && !directionchanged[e]) {
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
     */
    private void drive(int e) {
        if (endx[e] + 120 >= width - radius[e] && directionend[e] == 0) {
            endx[e] += 20;
            endy[e] -= 20;
            directionend[e]++;
            radius[e] -= 60;
            directionchanged[e] = true;
        } else if (endy[e] + 120 >= height - radius[e] && directionend[e] == 1) {

            endx[e] += 20;
            endy[e] += 20;
            directionend[e]++;
            radius[e] -= 60;
            directionchanged[e] = true;
        } else if (endx[e] - 120 <= radius[e] && directionend[e] == 2) {
            endx[e] -= 20;
            endy[e] += 20;
            radius[e] -= 60;
            directionend[e]++;
            directionchanged[e] = true;
        } else if (endy[e] - 80 <= radius[e] && directionend[e] == 3) {
            endx[e] -= 20;
            endy[e] -= 20;
            radius[e] -= 60;
            directionend[e] = 0;
            directionchanged[e] = true;
        } else {
            directionchanged[e] = false;
        }
    }

    // check if in a corner to not twist doubles
    private boolean corner(int e) {
        if (endx[e] + 200 >= width - radius[e] && directionend[e] == 0) {
            return true;
        } else if (endy[e] + 200 >= height - radius[e] && directionend[e] == 1) {
            return true;
        } else if (endx[e] - 200 <= radius[e] && directionend[e] == 2) {
            return true;
        } else if (endy[e] - 200 <= radius[e] && directionend[e] == 3) {
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
        System.out.println("Player" + players.get(0).getName() + " has max score " + score[0] + "Its a double " + score[1] + "   1==true");
        for (int i = 1; i < players.size(); i++) {
            int[] aux = players.get(i).getMaxDomino();
            System.out.println("ENTERING THE LOOP FOR" + players.get(i).getName() + "with highest domino" + aux[0] + "with double" + aux[1]);
            if (score[1] < aux[1]) {
                score = aux;
                playerstarting = i;
            } else if (aux[0] > score[0] && score[1] == aux[1]) {
                score = aux;
                playerstarting = i;
            }
        }

        return playerstarting;
    }

    /**
     * Checks wether a player can play a domino or not in all his dominoes.
     * Returns true if the player can make a move
     *
     * @param p
     * @return
     */
    public boolean CanMakeaMove(Player p) {
        List<Integer> dominoesNumbers = p.getAllNumbers();
        System.out.println("There is " + this.stack.size() + " more dominoes in the stack");
        return this.stack.isEmpty() || playedDominoes.isEmpty() || dominoesNumbers.contains(end[0]) || dominoesNumbers.contains(end[1]);
    }

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
        int pointsteamMate = 0;
        for (int i = 0; i < players.size(); i++) {
            Player aux = players.get(i);
            if (p.getTeam() != aux.getTeam()) {
                points += aux.getDominoPoints();
            } else {
                pointsteamMate += aux.getDominoPoints();
            }
        }
        if (pointsteamMate > points) {
            points = pointsteamMate;
        }
        p.sumPoints(points);
        createNewGame();
        return p.getGameScore() >= maxPoints;
    }

    private void createNewGame() {
        playedDominoes.clear();
        stack.clear();
        directionend = new int[]{0, 2};
        end = new int[]{100, 100};
        endy = new double[2];
        endx = new double[2];
        endy[0] = endy[1] = height / 2 - 80 / 2;
        endx[0] = endx[1] = width / 2 - 40 / 2;
        radius = new int[]{200, 200};
    }

    /**
     * Given a number it gets a List of the played Dominoes with that number
     *
     * @param n
     * @return
     */
    private List<Domino> playedDominoesfromNumber(int n) {
        List<Domino> aux = new ArrayList();
        for (Domino d : playedDominoes) {
            if (d.getBothNumbers().contains(n)) {
                System.out.println("Adding domino" + d.toString());
                aux.add(d);
            }
        }
        return aux;
    }

    /**
     * Returns true if the game is blocked
     *
     * @param p that blocked the game
     * @return
     */
    public boolean blockedGame() {
        int pointsperplayer = 0;
        if (playedDominoes.size() > 9) {
            if (end[0] == end[1]) {
                List<Domino> aux = new ArrayList();
                for (Domino d : playedDominoes) {
                    if (d.getBothNumbers().contains(end[0])) {
                        System.out.println("Adding domino" + d.toString());
                        aux.add(d);
                    }
                }
                return aux.size() == 7;
            }
        }
        return false;
    }

    /**
     * Precondition the game is blocked.
     *
     * Returns 1 or two players depending if the game is 2vs2 or 1v1.
     */
    public List<Player> winnerWhenBlocked(Player p) {
        System.out.println("The game is blocked");
        Map<Integer, Integer> aux = new HashMap<>();
        for (Player player : players) {
            if (p.getTeam() == player.getTeam()) {
                aux.put(player.getTeam(), aux.get(player.getTeam()) + player.getDominoPoints());
            } else {
                aux.put(player.getTeam(), aux.get(player.getTeam()) + player.getDominoPoints());
            }
        }
        int winnerkey = 0;
        int winnervalue = aux.get(winnerkey);
        int loserKey = 1;
        int loserValue = aux.get(loserKey);

        System.out.println("Winner is" + aux.get(winnerkey));
        if (aux.get(winnerkey) > aux.get(loserKey)) {
            winnerkey = 1;
            winnervalue = aux.get(winnerkey);
            loserKey = 0;
            loserValue = aux.get(loserKey);
            System.out.println("Loser is" + aux.get(loserKey));

        } else if (aux.size() > 2) {
            if (aux.get(winnerkey) > aux.get(2)) {
                winnerkey = 1;
                winnervalue = 0;
            }
            if (aux.get(2) < aux.get(loserKey)) {
                loserKey = 2;
                loserValue = aux.get(loserKey);
            }
        }

        //We sum the points for the winner
        List<Player> winners = new ArrayList<>();
        for (Player player : players) {
            if (p.getTeam() == winnerkey) {
                p.sumPoints(aux.get(loserKey));
                winners.add(p);
            }
        }
        return winners;
    }
}
