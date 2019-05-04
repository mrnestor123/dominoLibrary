/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import business_logic.DataStore;
import business_logic.Domino;
import business_logic.DominoBox;
import business_logic.Player;
import business_logic.Table;
import connection.Client;
import connection.Message;
import connection.Server;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.swing.event.DocumentEvent;

/**
 * FXML Controller class
 *
 * @author Barra
 */
public class DominosRoomController implements Initializable {

    //Variables for the interface
    @FXML
    private GridPane yourDominoes;
    @FXML
    private GridPane oppDominoes;
    @FXML
    private Label totalpoints, stackedDominoes, playerpoints;
    @FXML
    private Pane dominoboard;
    @FXML
    private AnchorPane mainpannel;
    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;
    private DataStore d;

    //Variables for playing the game
    private Table t;
    private int dominoesplayed = 0;
    int playerTurn = 0;
    //For knowing who is the one playing
    private Player currentplayer;
    //Not implemented yet. Maybe wont need
    private List<Player> opponents = new ArrayList<>();
    private Client client;

    //Variables for creating the  connection
    boolean isServer = false;

    /**
     * We initialize the stage with the player,the name of the game,the number
     * of players and the maximum points
     *
     * @param stage
     * @param c
     * @param s
     * @param game
     * @param current
     */
    public void initStage(Stage stage, Client c, Server s, Table game, Player current) {

        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        client = c;
        currentplayer = current;
        handleClientActions(c);
        //We send to the server that we have joined the game
        try {
            c.send(new Message("Join ", game));
        } catch (Exception ex) {
            Logger.getLogger(DominosRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void goBack(ActionEvent event) {
        primaryStage.setTitle(prevTitle);
        primaryStage.setScene(prevScene);

    }

    @FXML
    private void moveDominoes(ActionEvent e) {
        t = new Table(150, 730, 432);
        totalpoints.setText(Integer.toString(t.getMaxPoints()));
        playerpoints.setText(Integer.toString(currentplayer.getGameScore()));
//        moveallDominoes(shuffleddominoes, currentplayer);
    }

    /**
     * Moves the domino in the game. The boolean is to check who is the one
     * moving the domino if its the opponent we dont know which is his dominoBox so 
     * we loop through it.
     *
     * @param v
     * @param p
     * @param from
     */
    @FXML
    private void moveDomino(DominoBox v, Player p, boolean opponent) {
        //int positionDomino = Integer.parseInt(v.getId());
        v.setMinSize(v.getPrefWidth(), v.getPrefHeight());
        System.out.println("Entering moveDomino with domino " +  v.getDomino().toString() +" " + p.toString());
        Double[] aux = t.placeaDomino(p, v.getDomino());
        if (!opponent) {
            int i = 0;
            for (Node n : yourDominoes.getChildren()) {
                if (n.equals(v)) {
                    System.out.print("The domino is in position" + i);
                }
                i++;
            }
            yourDominoes.getChildren();
            yourDominoes.getChildren().remove(v);
            
            System.out.println("The dominoes size is" + yourDominoes.getChildren().size());
            v.relocate(aux[0], aux[1]);
            v.setRotate(aux[2]);
            dominoboard.getChildren().add(v);
            //changeTurn();
        } else {
            ObservableList<Node> dominoes = oppDominoes.getChildren();
            Node chosenDomino = null;
            for (Node n : dominoes) {
                if (n.getId().equals(v.getId())) {
                    chosenDomino = n;
                }
            }
            if (chosenDomino != null) {
                chosenDomino.setStyle("-fx-background-color:white; -fx-border-color:black;");
                chosenDomino.relocate(aux[0], aux[1]);
                chosenDomino.setRotate(aux[2]);
                oppDominoes.getChildren().remove(chosenDomino);
                dominoboard.getChildren().add(chosenDomino);
            }
        }
        changeTurn();        
    }

    /**
     * Given the table we create the game for Client and Server
     *
     * @param t
     */
    private void populateGame() {
        t.handleDominoes();

        if (!t.getStack().isEmpty()) {
            stackedDominoes.setText("" + t.getStack().size());
        };

        List<Player> players = t.getPlayers();
        t.setCoordinates(dominoboard.getWidth(), dominoboard.getHeight());
        currentplayer = t.getPlayerinTable(currentplayer.getName());
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.get(i).getDominoes().size(); j++) {
                DominoBox v = new DominoBox(players.get(i).getDominoes().get(j));
                if (players.get(i).getName().equals(currentplayer.getName())) {
                    v.createDomino(false);
                    yourDominoes.add(v, j, 0);
                    v.setOnMouseClicked((e) -> {
                        if (t.validMove(v.getDomino())) {
                            moveDomino(v, currentplayer, false);
                            try {
                                client.send(new Message("Movement " + v.getId() + " " + currentplayer.getName() + " " + t.getName(), v));
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    });
                } else {
                    v.createDomino(true);
                    oppDominoes.add(v, j, 0);
                }
            }
        }
        //For changing the turns between players
        playerTurn = t.getPlayerStarting();
        changeTurn();
    }

    /**
     * Method for playing the game. Checks if the player can play the tile , if
     * not he draws in the stack. Disables the gridpane of the opponent.
     */
    private void changeTurn() {
        boolean movement = false;
        ArrayList<DominoBox> drawedDominoes = new ArrayList();

        do {
            if (currentplayer.getName().equals(t.getPlayers().get(playerTurn).getName())) {
                yourDominoes.setDisable(false);
                if (t.CanMakeaMove(t.getPlayers().get(playerTurn))) {
                    movement = true;
                    playerTurn++;
                } else {
                    ColumnConstraints added = new ColumnConstraints();
                    DominoBox drawedDomino = new DominoBox(t.getDominoFromStack(currentplayer));
                    drawedDomino.createDomino(false);
                    drawedDominoes.add(drawedDomino);
                    System.out.println(drawedDomino.getDomino().toString());
                    System.out.print(currentplayer.getDominoes().size() + " dominoes in the gridpane");
                    drawedDomino.setOnMouseClicked((e) -> {
                        if (t.validMove(drawedDomino.getDomino())) {
                            moveDomino(drawedDomino, currentplayer, false);
                            try {
                                Message m = new Message("Movement" + drawedDomino.getId(), drawedDomino, currentplayer);
                                client.send(m);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                        }
                    });
                    drawedDomino.setId(drawedDomino.getDomino().toString());
                    yourDominoes.addColumn(yourDominoes.getChildren().size(), drawedDomino);
                }
            } else {
                System.out.println("WAITING FOR NEXT TURN");
                yourDominoes.setDisable(true);
                movement = true;
                playerTurn++;
            }
        } while (!movement);

        if (!drawedDominoes.isEmpty()) {
            Message m = new Message("Draw");
            m.setDrawedDominoes(drawedDominoes);
            try {
                client.send(m);
            } catch (Exception ex) {
                Logger.getLogger(DominosRoomController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        playerTurn = playerTurn % 2;
    }

    /**
     * We create the game if the player is the first one i.e the server
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boolean sent = false;
        try {
            //connection.startConnection();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        if (isServer) {
            System.out.print("Waiting for client");
        }
    }

    private void handleClientActions(Client c) {

        c.setCallBack(data -> {
            Platform.runLater(() -> {
                String tokens[] = data.getText().split(" ");
                String cmd = tokens[0];
                if ("Start".equalsIgnoreCase(cmd)) {
                    t = data.getTable();
                    populateGame();
                    for (Player p : t.getPlayers()) {
                        if (!currentplayer.getName().equals(p)) {
                            opponents.add(p);
                        }
                    }
                } else if (cmd.equalsIgnoreCase("Movement")) {
                    data.getDominoMoved().setId(tokens[1]);
                    System.out.println("Opponent name" + tokens[2]);
                    moveDomino(data.getDominoMoved(), t.getPlayerinTable(tokens[2]), true);
                }
            });
        });
    }
}

/**
 * private void moveallDominoes(List<Domino> dominoes, Player p) { if
 * (dominoboard.getChildren().size() > 0) {
 * dominoboard.getChildren().removeAll(dominoboard.getChildren()); } for (Domino
 * d : dominoes) { DominoBox v = new DominoBox(d); v.setPrefSize(40, 80);
 * v.setStyle("-fx-background-color:white; -fx-border-color: black;"); Separator
 * s = new Separator(); GridPane num1 = d.drawDomino(d.getNumber1());
 * num1.setPadding(new Insets(3, 3, 3, 3)); GridPane num2 =
 * d.drawDomino(d.getNumber2()); num2.setPadding(new Insets(3, 3, 3, 3));
 * v.getChildren().add(num1); v.getChildren().add(1, s);
 * v.getChildren().add(num2); if (t.validMove(v)) { moveDomino(v, p.getName(),
 * false); } } }
 *
 */
