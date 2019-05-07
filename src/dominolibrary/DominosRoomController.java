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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Barra
 */
public class DominosRoomController implements Initializable {

    //Variables for the interface
    @FXML
    private GridPane yourDominoes, oppDominoes;
    @FXML
    private Label totalpoints, stackedDominoes, playerpoints, opponentName, currentName, startingPlayer, oppPoints, oppTotalPoints;
    @FXML
    private Pane dominoboard;
    @FXML
    private AnchorPane mainpannel;
    @FXML
    private Circle currentPCircle, oppCircle;

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
        moveallDominoes(t.getShuffledDominoes(),currentplayer);
    }

    /**
     * Moves the domino in the game. The boolean is to check who is the one
     * moving the domino if its the opponent we dont know which is his dominoBox
     * so we loop through it.
     *
     * @param v
     * @param p
     * @param from
     */
    private void moveDomino(DominoBox v, Player p, boolean opponent) {
        //int positionDomino = Integer.parseInt(v.getId());
        v.setMinSize(v.getPrefWidth(), v.getPrefHeight());
        System.out.println("Entering moveDomino with domino " + v.getDomino().toString() + " " + p.getName());
        Double[] aux = t.placeaDomino(p, v.getDomino());
        if (!opponent) {
            Integer dominoPlayed = GridPane.getColumnIndex(v);
            deleteColumn(v, yourDominoes);
            v.relocate(aux[0], aux[1]);
            v.setRotate(aux[2]);
            dominoboard.getChildren().add(v);
          if (aux.length > 3) {
                if (aux[3] == 0.0) {
                    newRound(p);
                    populateGame(true);
                    try {
                        client.send(new Message("Restart " + ". " + currentplayer.getName() + " " + t.getName(), t));
                    } catch (Exception ex) {
                        Logger.getLogger(DominosRoomController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (aux[3] == 1.0) {
                    endGame();
                }else{
                    blockedGame();
                    populateGame(true);
                     try {
                        client.send(new Message("Blocked " + ". " + currentplayer.getName() + " " + t.getName(), t));
                    } catch (Exception ex) {
                        Logger.getLogger(DominosRoomController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                return;
            }

        } else {
            Node chosenDomino = deleteColumn(v, oppDominoes);
            chosenDomino.setStyle("-fx-background-color:white; -fx-border-color: black;");
            chosenDomino.relocate(aux[0], aux[1]);
            chosenDomino.setRotate(aux[2]);
            dominoboard.getChildren().add(chosenDomino);
        }
        if (aux.length == 3) {
            changeTurn();
        }

    }

    private void newRound(Player winner) {

        if (winner.getName().equals(currentplayer.getName())) {
            playerpoints.setText("" + currentplayer.getGameScore());
        } else {
            oppPoints.setText("" + winner.getGameScore());
        }

        dominoboard.getChildren().clear();
        oppDominoes.getChildren().clear();
        yourDominoes.getChildren().clear();
    }

    private void endGame() {

    }

    static Node deleteColumn(DominoBox v, GridPane grid) {
        Node res = null;
        Integer resultposition = 100;
        System.out.println("Deleting domino" + v.getId());
        for (Node n : grid.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(n);
            int r = columnIndex == null ? 0 : columnIndex;
            System.out.println("Passing through domino" + n.getId());
            if (v.getId().equals(n.getId())) {
                res = n;
                resultposition = columnIndex;
            } else if (r > resultposition) {
                System.out.println("decrementing row " + r);
                GridPane.setColumnIndex(n, columnIndex - 1);
            }
        }
        System.out.println("The node is in position " + resultposition);
        grid.getChildren().remove(res);

        return res;
    }

    /**
     * Given the table we create the game for all the clients
     *
     * @param t
     */
    private void populateGame(boolean mix) {
        currentName.setText(currentplayer.getName());
        totalpoints.setText("" + t.getMaxPoints());
        if (mix) {
            t.mixDominoes();
        }
        t.handleDominoes();
        if (!t.getStack().isEmpty()) {
            stackedDominoes.setText("" + t.getStack().size());
        };

        List<Player> players = t.getPlayers();
        t.setCoordinates(dominoboard.getWidth(), dominoboard.getHeight());
        currentplayer = t.getPlayerinTable(currentplayer.getName());

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setTeam(i % 2);
            for (int j = 0; j < players.get(i).getDominoes().size(); j++) {
                DominoBox v = new DominoBox(players.get(i).getDominoes().get(j));
                if (players.get(i).getName().equals(currentplayer.getName())) {
                    v.createDomino(false);
                    yourDominoes.add(v, j, 0);
                    v.setOnMouseClicked((e) -> {
                        if (t.validMove(v.getDomino())) {
                            try {
                                client.send(new Message("Movement " + v.getId() + " " + currentplayer.getName() + " " + t.getName(), v));
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                            moveDomino(v, currentplayer, false);
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
        ArrayList<Domino> drawedDominoes = new ArrayList();

        do {
            if (currentplayer.getName().equals(t.getPlayers().get(playerTurn).getName())) {
                currentPCircle.setFill(Color.GREEN);
                oppCircle.setFill(Color.RED);
                yourDominoes.setDisable(false);
                if (t.CanMakeaMove(t.getPlayers().get(playerTurn))) {
                    movement = true;
                    playerTurn++;
                } else {
                    ColumnConstraints added = new ColumnConstraints();
                    DominoBox drawedDomino = new DominoBox(t.getDominoFromStack(currentplayer));
                    drawedDomino.createDomino(false);
                    drawedDominoes.add(drawedDomino.getDomino());

                    System.out.print(currentplayer.getDominoes().size() + " dominoes in the gridpane");
                    drawedDomino.setOnMouseClicked((e) -> {
                        if (t.validMove(drawedDomino.getDomino())) {
                            try {
                                Message m = new Message("Movement " + drawedDomino.getId() + " " + currentplayer.getName() + " " + t.getName(), drawedDomino, currentplayer);
                                client.send(m);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                            moveDomino(drawedDomino, currentplayer, false);
                        }
                    });
                    drawedDomino.setId(drawedDomino.getDomino().toString());
                    yourDominoes.addColumn(yourDominoes.getChildren().size(), drawedDomino);
                }

            } else {
                currentPCircle.setFill(Color.RED);
                oppCircle.setFill(Color.GREEN);
                System.out.println("WAITING FOR NEXT TURN");
                yourDominoes.setDisable(true);
                movement = true;
                playerTurn++;
            }
        } while (!movement);

        System.out.println(
                "Player drawed :" + drawedDominoes.size());
        if (!drawedDominoes.isEmpty()) {
            Message m = new Message("Draw " + "." + " " + currentplayer.getName() + " " + t.getName());
            m.setDrawedDominoes(drawedDominoes);
            try {
                client.send(m);
            } catch (Exception ex) {
                Logger.getLogger(DominosRoomController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            stackedDominoes.setText("" + t.getStack().size());
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
                    populateGame(false);
                    System.out.println("Currentplayer name is " + currentplayer.getName());
                    for (Player p : t.getPlayers()) {
                        if (!currentplayer.getName().equals(p)) {
                            opponents.add(p);
                            opponentName.setText(p.getName());
                            oppPoints.setText("" + p.getGameScore());
                        }
                    }
                } else if (cmd.equalsIgnoreCase("Movement")) {
                    data.getDominoMoved().setId(tokens[1]);
                    System.out.println("Opponent name" + tokens[2]);
                    moveDomino(data.getDominoMoved(), t.getPlayerinTable(tokens[2]), true);
                } else if (cmd.equalsIgnoreCase("Draw")) {
                    List<Domino> l = data.getDrawedDominoes();
                    int removedElement = t.getStack().size() - 1;
                    for (Domino d : l) {
                        DominoBox drawed = new DominoBox(d);
                        drawed.createDomino(true);
                        oppDominoes.addColumn(oppDominoes.getChildren().size(), drawed);
                        System.out.println("Removing domino" + t.getStack().get(removedElement).toString());
                        t.getStack().remove(removedElement--);
                    }
                    t.getPlayerinTable(tokens[2]).setDominoes(l, false);
                    stackedDominoes.setText("" + t.getStack().size());
                } else if (cmd.equalsIgnoreCase("Restart")) {
                    Player p = t.getPlayerinTable(tokens[2]);
                    t = data.getTable();
                    newRound(p);
                    populateGame(false);
                }
            });
        });
    }

    private void moveallDominoes(List<Domino> dominoes, Player p) {
        if (dominoboard.getChildren().size() > 0) {
            dominoboard.getChildren().removeAll(dominoboard.getChildren());
        }
        for (Domino d : dominoes) {
            DominoBox v = new DominoBox(d);
            v.setPrefSize(40, 80);
            v.setStyle("-fx-background-color:white; -fx-border-color: black;");
            Separator s = new Separator();
            GridPane num1 = d.drawDomino(d.getNumber1());
            num1.setPadding(new Insets(3, 3, 3, 3));
            GridPane num2 = d.drawDomino(d.getNumber2());
            num2.setPadding(new Insets(3, 3, 3, 3));
            v.getChildren().add(num1);
            v.getChildren().add(1, s);
            v.getChildren().add(num2);
            if (t.validMove(v.getDomino())) {
                moveDomino(v, p, false);
            }
        }
    }

    private void blockedGame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
