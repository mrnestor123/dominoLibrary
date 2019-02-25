/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Barra
 */
public class Domino {

    //up can be right,down can be left
    private int number1, number2;

    //1 you can match with the number1, 2 with the number2 and 0 if it is not played or first play
    private int direction;

    private boolean doble; //This is for when the number 1 is the same as number 2 for ex 6/6

    List<Integer> dominoesnumber = new ArrayList<>();

    public Domino(int n1, int n2, boolean dob, int dir) {
        number1 = n1;
        number2 = n2;
        doble = dob;
        direction = dir;
        dominoesnumber.add(n1);
        dominoesnumber.add(n2);
    }

    // public void setPosition(int p){position = p;}
    //public int getPositon(){return position;}
    public int getNumber1() {
        return number1;
    }

    public int getNumber2() {
        return number2;
    }

    public int getTotalPoints() {
        return this.number1 + this.number2;
    }

    public void setDirection(int d) {
        this.direction = d;
    }

    public int getDirection() {
        return direction;
    }

    /**
     * The object this is the domino in the table. 
     * Checks if it is the first play and if 
     * its not a double in the table in that case you can match it to both
     * numbers. In case of a double the direction does not matter
     *
     * @param d
     * @return
     */
    public boolean match(Domino d) {

        switch (this.direction) {
            case 1:
                if (d.dominoesnumber.contains(this.number1)) {
                    d.setDirection(this.setMatchingPart(d));
                    return true;
                }
                break;
            case 2:
                if (d.dominoesnumber.contains(this.number2)) {
                    d.setDirection(this.setMatchingPart(d));
                    return true;
                }
                break;
            case 0:
                if (d.dominoesnumber.contains(this.number2)){
                    this.setDirection(1);
                    d.setDirection(this.setMatchingPart(d));
                    return true;
                        
                }else if   (d.dominoesnumber.contains(this.number1)) {
                    this.setDirection(2);
                    d.setDirection(this.setMatchingPart(d));
                    return true;
                }
        }
        return false;
    }
/**
 * Returns the direction of the resulting domino 0 if it does not match
 * @param d
 * @return 
 */
    public int setMatchingPart(Domino d){
        int i =0;
        if(this.number1 == d.getNumber1() || this.number2 ==d.getNumber1()){
          return 2;
        } else if(this.number2 == d.getNumber2()||this.number1 ==d.getNumber2()){
          return 1;
        }
     return 0;   
    
     //DRAW DOMINO cada uno en una direccion(Vertocal,horizontal)
    
     /**
     *
     * Crosses the number that cant be matched
     *
     * @return
     */
    @Override
    public String toString() {
        String n1string = this.number1 + "";
        String n2string = this.number2 + "";

        if (this.getDirection() == 1) {
            n2string += "\u0268";
        } else if (this.getDirection() == 2) {
            n1string += "\u0268";
        }
        return n1string + "/" + n2string + " ";
    }
}
}
