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
import java.util.TreeMap;

/**
 *
 * @author Barra
 */
public class Table implements Serializable {

    private List<Player> players = new ArrayList<>();
    private List<Domino> playedDominoes = new ArrayList<>();
    private List<Domino> stackedDominoes = new ArrayList<>();

    //for knowing the last two numbers 
    private int end1, end2;

    //for knowing on which direction the dominoes are moving
    private int directionright, directionleft;

    //Variables for moving the dominoes
    private double width, height, positionLeftx, positionLefty, positionRightx, positionRighty;

    private int maxPoints;

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
        directionright = 0;
        directionleft = 2;
        end1 = 100;
        end2 = 100;

    }

    public int getMaxPoints() {
        return this.maxPoints;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Domino> getAllDominoes() {
        return playedDominoes;
    }

    /**
     * We create all the dominoes and we assign them to the list of dominoes of
     * the table
     *
     * @return
     */
    public List<Domino> mixDominoes() {
        List<Domino> aux = new ArrayList<>();
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                Domino res = new Domino(i, j);
                aux.add(res);
            }
        }
        Collections.shuffle(aux);
        return aux;
    }

    /**
     * It handles the dominoes to the given players. It returns the stack or
     * null if there is no stack or it is the first player. The integer is to
     * know if its the first player/second/third/fourth values will be first==0
     * second==7 third==15 fourth==22
     *
     * @param d
     * @param p
     * @param numPlayer
     * @return
     */
    public List<Domino> handleDominoes(List<Domino> d, Player p, int numPlayer) {

        List<Domino> list = new ArrayList<>();

        List<Domino> stack = new ArrayList<>();
        int n = numPlayer;

        list.addAll(d.subList(n, n + 7));

        p.setDominoes(list);
        if (n < d.size() && numPlayer != 0) {
            stack.addAll(d.subList(n, d.size()));
            return stack;
        }
        return null;
    }

    /**
     * For positioning the domino Returns an array with the position x y and the
     * rotationAngle
     *
     * @param p
     * @param d
     * @return
     */
    public Double[] placeaDomino(Player p, DominoBox d) {
        Double[] result = new Double[3];
        Double[] rotateValues = new Double[2];
        Double[] coordinates = new Double[3];
        double x = 0;
        double y = 0;
        double twist = 0;
        int directionaux = 0;
        String auxend1 = String.valueOf(end1);
        String auxend2 = String.valueOf(end2);

        int auxiliarend1 = end1;
        int auxiliarend2 = end2;

        if (end1 >= 10) {
            auxiliarend1 -= 10;
        }
        if (end2 >= 10 && end2 != 100) {
            auxiliarend2 -= 10;
        }

        System.out.println("Ends are " + end1 + " - " + end2);

        if (end1 == 100 && end2 == 100) {
            end1 = d.getDomino().getNumber1();
            end2 = d.getDomino().getNumber2();
            x = width / 2 - 40 / 2;
            y = height / 2 - 80 / 2;
            positionLeftx = positionRightx = x;
            positionLefty = positionRighty = y;
            if (!d.getDomino().isDoble()) {
                twist = 270.0;
            } else {
                end1 += 10;
                end2 += 10;
            }
            System.out.print("Moving domino" + d.getDomino().toString());
        } else {
            if (auxiliarend1 == d.getDomino().getNumber1() || auxiliarend1 == d.getDomino().getNumber2()) {
                //we substract 10 when its a double

                System.out.print("Moving domino" + d.getDomino().toString());
                x = positionLeftx;
                y = positionLefty;
                directionaux = drive(x, y, directionleft);
                if (directionleft == directionaux) {
                    if (auxend1.length() == 1) {
                        coordinates = this.move(x, y, directionleft, d, false);
                    } else {
                        coordinates = move(x, y, directionleft, d, true);
                    }
                    rotateValues = d.getDomino().rotateDomino(end1, directionleft);
                    x = coordinates[0];
                    y = coordinates[1];
                    positionLeftx = x;
                    positionLefty = y;
                    end1 = rotateValues[0].intValue();
                    twist = rotateValues[1];
                } else {
                    directionleft = directionaux;
                    rotateValues = d.getDomino().rotateDomino(end1, directionaux);
                    //We go from going left to going up
                    if (directionaux == 3) {
                        if (auxend1.length() == 1) {
                            x -= 60;
                            y -= 20;
                        } else if (d.getDomino().isDoble()) {
                            x -= 60;
                        } else {
                            y -= 20;
                        }
                    } //we go from going up to right
                    else if (directionaux == 0) {
                        if (auxend1.length() == 1 && !d.getDomino().isDoble()) {
                            x += 20;
                            y -= 60;
                            twist = rotateValues[1];
                        } else if (d.getDomino().isDoble()) {
                            y -= 60;
                            twist = 90;
                        } else {
                            x += 80;
                            twist = rotateValues[1];
                        }
                    }
                    positionLeftx = x;
                    positionLefty = y;
                    end1 = rotateValues[0].intValue();
                }
            } else if (auxiliarend2 == d.getDomino().getNumber2() || auxiliarend2 == d.getDomino().getNumber1()) {
                x = positionRightx;
                y = positionRighty;
                directionaux = drive(x, y, directionright);

                if (directionright == directionaux) {
                    if (auxend2.length() == 1) {
                        coordinates = move(x, y, directionright, d, false);
                    } else {
                        coordinates = move(x, y, directionright, d, true);
                    }
                    rotateValues = d.getDomino().rotateDomino(end2, directionright);
                    x = coordinates[0];
                    y = coordinates[1];

                    positionRightx = x;
                    positionRighty = y;
                    end2 = rotateValues[0].intValue();
                    twist = rotateValues[1];
                } else {
                    directionright = directionaux;
                    rotateValues = d.getDomino().rotateDomino(end2, directionright);

                    //We go from going right to going down
                    if (directionaux == 1) {
                        if (auxend1.length() == 1) {
                            x += 60;
                            y += 20;
                            twist = rotateValues[1];
                        } else if (d.getDomino().isDoble()) {
                            x += 60;
                            twist = 0;
                        } else {
                            twist = rotateValues[1];
                            y += 20;
                        }
                    } //we go from going down to left
                    else if (directionaux == 2) {
                        if (auxend1.length() == 1) {
                            x -= 20;
                            y += 60;
                        } else if (d.getDomino().isDoble()) {
                            y += 60;
                        } else {
                            x -= 80;
                        }
                    }

                    positionRightx = x;
                    positionRighty = y;
                    end2 = rotateValues[0].intValue();
                    twist = rotateValues[1];
                }
            }
        }
        result[0] = x;
        result[1] = y;
        result[2] = twist;
        return result;
    }

    /**
     * If direction == 0 we are going right 
     * direction == 1 we are going down
     * direction == 2 we are going left 
     * direction == 3 we are going up
     * Theparameter double is to know if the domino you are matching is a double
     *
     * @param x
     * @param y
     * @param direction
     * @return
     */
    private Double[] move(double x, double y, int direction, DominoBox d, boolean doble) {
        Double[] coordinates = new Double[2];
        coordinates[0] = x;
        coordinates[1] = y;
        switch (direction) {
            case 0:
                if (doble || d.getDomino().isDoble()) {
                    coordinates[0] += d.getWidth() + d.getWidth() / 2;
                    //coordinates[0] += 60;
                } else {
                    //coordinates[0] += 80;
                    coordinates[0] += d.getWidth() * 2;
                }
                break;
            case 1:
                if (doble || d.getDomino().isDoble()) {
                    coordinates[1] += d.getWidth() + d.getWidth() / 2;
                    //coordinates[1] += 60;
                } else {
                    coordinates[1] += d.getWidth() * 2;
                    //coordinates[1] += 80;

                }
                break;
            case 2:
                if (doble || d.getDomino().isDoble()) {
                    coordinates[0] -= d.getWidth() + d.getWidth() / 2 + 1;
                    //coordinates[0] -= 60;
                } else {
                    coordinates[0] -= d.getWidth() * 2 + 1;
                    //coordinates[0] -= 80;
                }
                break;
            case 3:
                if (doble || d.getDomino().isDoble()) {
                    coordinates[1] -= d.getWidth() + d.getWidth() / 2;
                    //coordinates[1] -= 60;
                } else {
                    coordinates[1] -= d.getWidth() * 2;
                    //coordinates[1] -= 80;
                }
                break;

        }
        return coordinates;
    }

    /**
     * Returns the position of the player with the highest double that starts the game
     * @return 
     */
    public int getPlayerStarting(){
        int playerstarting = 0;
        int[] score = players.get(0).getMaxDomino();
        int [] DoubleorNot = new int [players.size()]; 
        System.out.println("Player 1 has max score " + score);
        for(int i = 1; i<players.size();i++){
            int[] aux = players.get(i).getMaxDomino();
            if (score[1] <= aux[1] && aux[0]>score[0]){
                score=aux;
                playerstarting = i;
            }
        }      
        System.out.print("Player " + players.get(playerstarting).getName() + "starts");
        return playerstarting;
    }
    
    /**
     * Checks if the direction needs to be changed. We add a margin of 10
     *
     *
     */
    private int drive(double x, double y, int direction) {

        int nextdirection = direction;

        if (x + 80 >= width - 60 && direction == 0) {
            nextdirection++;
        } else if (y + 80 >= height - 60 && direction == 1) {
            nextdirection++;
        } else if (x - 80 <= 60 && direction == 2) {
            nextdirection++;
        } else if (y - 80 <= 60 && direction == 3) {
            nextdirection = 0;
        }
        return nextdirection;
    }

    /**
     * Checks wether a player can play a domino or not in all his dominos
     *
     * @param p
     * @return
     */
    public boolean PlayerPassesTurn(Player p) {
        List<Integer> dominoesNumbers = p.getAllNumbers();
        return !(dominoesNumbers.contains(end1) || dominoesNumbers.contains(end2)) && playedDominoes.size()>0;
    }
    
    /**
     * Checks if a domino can be played in the table or not
     * @param d
     * @return 
     */
    public boolean CanPlayDomino(Domino d){
        return end1==d.getNumber1() || end1==d.getNumber2() || end2==d.getNumber1() || end2==d.getNumber2();
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
        if (stackedDominoes.size() > 0) {
            aux = stackedDominoes.remove(stackedDominoes.size() - 1);
        }
        return aux;
    }

    /**
     * Precondition The player has ran out of dominoes Counts the total points
     * and sums them to the one who winned. Then checks if the game has
     * finished.
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
     * be in top NO ESTA BIEN!!!
     *
     * @param p that blocked the game
     * @return
     */
    public boolean blockedGame() {
        int pointsperplayer = 0;
        if (playedDominoes.size() > 9) {
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
