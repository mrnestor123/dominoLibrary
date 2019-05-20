/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Barra
 */
public class CreateGame {

    public void display(String title) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        window.setMinHeight(200);
        GridPane grid = new GridPane();

        Label name = new Label("Name of the game");
        TextField inputname = new TextField();

        Label numPlayers = new Label("Num of Players  ");
        ChoiceBox<Integer> nPlayers = new ChoiceBox();

        ObservableList<Integer> playerchoice = FXCollections.observableArrayList();
        playerchoice.addAll(2,3,4);
        
        Label MaxPoints = new Label("Max points");
        ChoiceBox<Integer> mPoints = new ChoiceBox();

        ObservableList<Integer> points = FXCollections.observableArrayList();
        playerchoice.addAll(100,150,200);
         
        
        Button createGame = new Button("Accept");
        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(e -> window.close());
        closeButton.setAlignment(Pos.CENTER_RIGHT);
        createGame.setAlignment(Pos.CENTER_LEFT);
        
        grid.add(name,0,0);
        grid.add(inputname,1,0, 2, 0);
        grid.add(numPlayers,1, 1);
        grid.add(nPlayers,1,1, 2, 0);
        grid.add(MaxPoints,2, 2);
        grid.add(mPoints,1,2, 2, 0);
        

        grid.add(createGame, 1, 2);
        grid.add(closeButton, 2, 2);
        grid.setPadding(new Insets(15, 15, 15, 15));
        grid.setAlignment(Pos.CENTER);

        VBox layout = new VBox();
        layout.getChildren().addAll(grid);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

}
