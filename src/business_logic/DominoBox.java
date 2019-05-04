/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import connection.Message;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Barra
 */
public class DominoBox extends VBox implements Serializable {

    Domino d;

    public DominoBox(Domino domino) {
        super();
        d = domino;
    }

    public void setDomino(Domino dom) {
        d = dom;
    }

    public Domino getDomino() {
        return this.d;
    }
    public void createDomino(Boolean opponent) {
        this.setId(this.getDomino().toString());
        this.setPrefSize(40, 80);
        Separator s = new Separator();
        s.setStyle("-fx-border-size:2px; -fx-color:black;");
        GridPane num1 = d.drawDomino(d.getNumber1());
        num1.setPadding(new Insets(3, 3, 3, 3));
        GridPane num2 = d.drawDomino(d.getNumber2());
        num2.setPadding(new Insets(3, 3, 3, 3));
        this.getChildren().add(num1);
        this.getChildren().add(1, s);
        this.getChildren().add(num2);

        if (!opponent) {
            this.setStyle("-fx-background-color:white; -fx-border-color: black;");

        } else {
            this.setStyle("-fx-background-color:black; -fx-border-color: black;");
        }
    }

}
