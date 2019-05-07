/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Circle;

/**
 *
 * @author Barra
 */
public class Domino implements Serializable {

    //up can be right,down can be left
    private int number1, number2;

    public Domino(int n1, int n2) {
        number1 = n1;
        number2 = n2;
    }

    //public void setPosition(int p){position = p;}
    //public int getPositon(){return position;}
    public int getNumber1() {
        return number1;
    }

    public int getNumber2() {
        return number2;
    }

    public List<Integer> getBothNumbers() {
        List<Integer> aux = new ArrayList<>();
        aux.add(number1);
        aux.add(number2);
        return aux;
    }

    public int getTotalPoints() {
        return this.number1 + this.number2;
    }

    public boolean isDoble() {
        return this.number1 == this.number2;

    }

    public boolean equals(Domino d) {
        return this.toString().equals(d.toString());
    }

    /**
     * Given a number it draws the circles on its position
     *
     * @param number
     * @return
     */
    public GridPane drawDomino(int number) {

        GridPane g = new GridPane();
        Circle c1 = new Circle();
        c1.setRadius(3);
        Circle c2 = new Circle();
        c2.setRadius(3);
        Circle c3 = new Circle();
        c3.setRadius(3);
        Circle c4 = new Circle();
        c4.setRadius(3);
        Circle c5 = new Circle();
        c5.setRadius(3);
        Circle c6 = new Circle();
        c6.setRadius(3);
        g.setAlignment(Pos.CENTER);

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();

        switch (number) {
            case 1:
                g.add(c1, 1, 1);
                break;
            case 2:
                g.add(c1, 0, 2);
                g.add(c2, 2, 0);
                break;
            case 3:
                g.add(c1, 0, 2);
                g.add(c2, 1, 1);
                g.add(c3, 2, 0);
                break;
            case 4:
                g.add(c1, 0, 0);
                g.add(c2, 0, 2);
                g.add(c3, 2, 0);
                g.add(c4, 2, 2);

                break;
            case 5:
                g.add(c1, 0, 0);
                g.add(c2, 0, 2);
                g.add(c3, 1, 1);
                g.add(c4, 2, 0);
                g.add(c5, 2, 2);

                break;
            case 6:
                g.add(c1, 0, 0);
                g.add(c2, 0, 1);
                g.add(c3, 0, 2);
                g.add(c4, 2, 0);
                g.add(c5, 2, 1);
                g.add(c6, 2, 2);

                break;
            default:
                row1.setPercentHeight(50);
                break;
        }

        g.setVgap(2);
        g.setPrefHeight(60);
        row1.setVgrow(Priority.NEVER);
        row2.setVgrow(Priority.NEVER);
        row3.setVgrow(Priority.NEVER);

        row1.setPercentHeight(50);
        row2.setPercentHeight(50);
        row3.setPercentHeight(50);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(50);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(50);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(50);

        column1.setHalignment(HPos.CENTER);
        column2.setHalignment(HPos.CENTER);
        column3.setHalignment(HPos.CENTER);

        g.getColumnConstraints().addAll(column1, column2, column3);
        g.getRowConstraints().addAll(row1, row2, row3);

        return g;
    }

    public String toString() {
        return number1 + "/" + number2;
    }
     //DRAW DOMINO cada uno en una direccion(Vertocal,horizontal)
}
