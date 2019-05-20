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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
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
    private Label totalpoints, stackedDominoes, playerpoints, opponentName, currentName, startingPlayer, oppPoints, oppTotalPoints, waitingLabel;
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
     * @param server
     * @param game
     * @param current
     */
    public void initStage(Stage stage, Client c, Server server, Table game, Player current) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        stage.setTitle(game.getName());
        client = c;
        currentplayer = current;
        currentName.setText(currentplayer.getName());
        totalpoints.setText("" + game.getMaxPoints());
        oppTotalPoints.setText("" + game.getMaxPoints());
        handleClientActions(c);
        //We send to the server that we have joined the game
        c.send(new Message("Join ", current, game));
        primaryStage.setOnHiding(e -> {
            if (server != null) {
                server.close();
            }
            client.close();
        });
    }

    @FXML
    private void goBack() {
        primaryStage.setTitle(prevTitle);
        primaryStage.setScene(prevScene);
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
    private int moveDomino(DominoBox v, Player p, boolean opponent,int decision) {
        v.setMinSize(v.getPrefWidth(), v.getPrefHeight());
        Double[] aux = t.placeaDomino(p, v.getDomino(), 10);

        if (!opponent) {
            if (aux[0] != 0) {
                deleteColumn(v, yourDominoes);
                v.relocate(aux[0], aux[1]);
                v.setRotate(aux[2]);
                dominoboard.getChildren().add(v);
                changeTurn();
            }

            if (aux[3] == 0.0) {
                client.send(new Message("Restart " + ". " + currentplayer.getName() + " " + t.getName(), t));
                newRound(p);
                populateGame(true);
            } else if (aux[3] == 4.0) {
                client.send(new Message("End " + ". " + currentplayer.getName() + " " + t.getName(), t));
                endGame(p);
            } else if (aux[3] == 2.0) {
                int end = chooseEnd();
                System.out.println("Choosing end " + end);
                aux = t.placeaDomino(p, v.getDomino(), end);
                deleteColumn(v, yourDominoes);
                v.relocate(aux[0], aux[1]);
                v.setRotate(aux[2]);
                dominoboard.getChildren().add(v);
                changeTurn();
                return end;
            } else if (aux[3] == 3.0) {
                client.send(new Message("Blocked " + ". " + currentplayer.getName() + " " + t.getName(), t));
                blockedGame(p);
                populateGame(true);
            } else {
            }

        } else {
            if (aux[0] != 0 && decision == 10) {
                Node chosenDomino = deleteColumn(v, oppDominoes);
                chosenDomino.setStyle("-fx-background-color:white; -fx-border-color: black;");
                chosenDomino.relocate(aux[0], aux[1]);
                chosenDomino.setRotate(aux[2]);
                dominoboard.getChildren().add(chosenDomino);
                changeTurn();
            } else {
                System.out.println("Going to end " + decision);
                aux = t.placeaDomino(p, v.getDomino(), decision);
                Node chosenDomino = deleteColumn(v, oppDominoes);
                chosenDomino.setStyle("-fx-background-color:white; -fx-border-color: black;");
                chosenDomino.relocate(aux[0], aux[1]);
                chosenDomino.setRotate(aux[2]);
                dominoboard.getChildren().add(chosenDomino);
                changeTurn();
            }

        }
        return 10;
    }
    

    private void newRound(Player winner) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("End of round");
        window.setMinWidth(300);
        window.setMinHeight(200);
        VBox layout = new VBox();
        Label text = new Label("Player " + winner.getName() + "has won the round. You got " + winner.getGameScore() + " points");
        layout.getChildren().addAll(text);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        window.close();
        if (winner.getName().equals(currentplayer.getName())) {
            playerpoints.setText("" + currentplayer.getGameScore());
        } else {
            oppPoints.setText("" + winner.getGameScore());
        }
        dominoboard.getChildren().clear();
        oppDominoes.getChildren().clear();
        yourDominoes.getChildren().clear();
    }

    private void endGame(Player p) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("END OF GAME");
        window.setMinWidth(300);
        window.setMinHeight(200);
        VBox layout = new VBox();
        Label winner = new Label("Player " + p.getName() + "has won the game");
        layout.getChildren().addAll(winner);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        window.close();
        goBack();
    }

    static Node deleteColumn(DominoBox v, GridPane grid) {
        Node res = null;
        Integer resultposition = 100;
        for (Node n : grid.getChildren()) {
            Integer columnIndex = GridPane.getColumnIndex(n);
            int r = columnIndex == null ? 0 : columnIndex;
            if (v.getId().equals(n.getId())) {
                res = n;
                resultposition = columnIndex;
            } else if (r > resultposition) {
                GridPane.setColumnIndex(n, columnIndex - 1);
            }
        }
        grid.getChildren().remove(res);

        return res;
    }

    /**
     * Given the table we create the game for all the clients
     *
     * @param t
     */
    private void populateGame(boolean mix) {
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

        // System.out.println("Current player is "+ currentplayer.getName());
        for (int i = 0; i < players.size(); i++) {

            players.get(i).setTeam(i % 2);
            for (int j = 0; j < players.get(i).getDominoes().size(); j++) {
                DominoBox v = new DominoBox(players.get(i).getDominoes().get(j));
                if (players.get(i).getName().equals(currentplayer.getName())) {
                    v.createDomino(false);
                    yourDominoes.add(v, j, 0);
                    v.setOnMouseClicked((e) -> {
                        if (t.validMove(v.getDomino())) {
                            int end = moveDomino(v, currentplayer, false,10);
                            client.send(new Message("Movement " + v.getId() + " " + currentplayer.getName() + " " + t.getName() + " " + end, v));
                            v.setDisable(true);
                        }
                    });
                } else {
                    opponentName.setText(players.get(i).getName());
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
        System.out.println("It is turn for " + t.getPlayers().get(playerTurn).getName() + " to move");
        do {
            if (currentplayer.getName().equals(t.getPlayers().get(playerTurn).getName())) {
                currentPCircle.setFill(Color.GREEN);
                oppCircle.setFill(Color.RED);
                yourDominoes.setDisable(false);
                if (t.CanMakeaMove(t.getPlayers().get(playerTurn))) {
                    movement = true;
                    if (t.getStack().isEmpty()) {
                        System.out.println("CANT MAKE A MOVE SO IT HAS TO PASS THE TURN");
                    }
                    playerTurn++;
                } else {
                    ColumnConstraints added = new ColumnConstraints();
                    DominoBox drawedDomino = new DominoBox(t.getDominoFromStack(currentplayer));
                    drawedDomino.createDomino(false);
                    drawedDominoes.add(drawedDomino.getDomino());

                    System.out.print(currentplayer.getDominoes().size() + " dominoes in the gridpane");
                    drawedDomino.setOnMouseClicked((e) -> {
                        if (t.validMove(drawedDomino.getDomino())) {
                            Message m = new Message("Movement " + drawedDomino.getId() + " " + currentplayer.getName() + " " + t.getName(), drawedDomino, currentplayer);
                            client.send(m);
                            moveDomino(drawedDomino, currentplayer, false,10);
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

        if (!drawedDominoes.isEmpty()) {
            System.out.println("Player drawed :" + drawedDominoes.size());
            Message m = new Message("Draw " + "." + " " + currentplayer.getName() + " " + t.getName());
            m.setDrawedDominoes(drawedDominoes);
            client.send(m);
            stackedDominoes.setText("" + t.getStack().size());
        }
        playerTurn = playerTurn % 2;
    }

    /**
     * We create the game if the player is the first one i.e the server
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    private void handleClientActions(Client c) {
        c.setCallBack(data -> {
            Platform.runLater(() -> {
                String tokens[] = data.getText().split(" ");
                String cmd = tokens[0];
                if ("Start".equalsIgnoreCase(cmd)) {
                    setGame(data);
                } else if (cmd.equalsIgnoreCase("Movement")) {
                    data.getDominoMoved().setId(tokens[1]);
                    System.out.println("Opponent name" + tokens[2]);
                    moveDomino(data.getDominoMoved(), t.getPlayerinTable(tokens[2]), true,Integer.parseInt(tokens[4]));
                } else if (cmd.equalsIgnoreCase("Draw")) {
                    List<Domino> l = data.getDrawedDominoes();
                    int removedElement = t.getStack().size() - 1;
                    for (Domino d : l) {
                        DominoBox drawed = new DominoBox(d);
                        drawed.createDomino(true);
                        oppDominoes.addColumn(oppDominoes.getChildren().size(), drawed);
                        t.getStack().remove(removedElement--);
                    }
                    t.getPlayerinTable(tokens[2]).setDominoes(l, false);
                    stackedDominoes.setText("" + t.getStack().size());
                } else if (cmd.equalsIgnoreCase("Restart")) {
                    t.setShuffledDominoes(data.getTable().getShuffledDominoes());
                    newRound(t.getPlayerinTable(tokens[2]));
                    populateGame(false);
                } else if (cmd.equalsIgnoreCase("Blocked")) {
                    blockedGame(t.getPlayerinTable(tokens[2]));
                    populateGame(false);
                } else if (cmd.equalsIgnoreCase("End")) {
                    endGame(t.getPlayerinTable(tokens[2]));
                }
            });
        });
    }

    private void setGame(Message data) {
        System.out.println("Entering game " + data.getTable().getName());
        t = data.getTable();
        waitingLabel.setVisible(false);
        populateGame(false);
        /**
         * when there is more than two players if(t.getPlayers().size()>2){
         * GridPane Dominoesplay3 = new GridPane();
         *
         * } if(t.getPlayers().size()>3){ GridPane Dominoesplay3 = new
         * GridPane();
         *
         * }
         * for (Player p : t.getPlayers()) { if
         * (!currentplayer.getName().equals(p)) { opponents.add(p);
         * opponentName.setText(p.getName()); oppPoints.setText("" +
         * p.getGameScore()); } }*
         */
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
                moveDomino(v, p, false,10);
            }
        }
    }

    private void blockedGame(Player p) {
        List<Player> winners = t.winnerWhenBlocked(p);
        System.out.println(winners.get(0).getName() + " has won the game");
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("END OF GAME");
        window.setMinWidth(300);
        window.setMinHeight(200);
        VBox layout = new VBox();
        Label winner = new Label("The game is blocked. Player " + winners.get(0).getName() + " has won the round");

        layout.getChildren().addAll(winner);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        window.close();
        newRound(winners.get(0));
    }

    /**
     * When you can go either the end in the left or the one in the right
     *
     * @return
     */
    private int chooseEnd() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText("You can go both ways.Choose your option.");
        ButtonType end1 = new ButtonType("Left");
        ButtonType end0 = new ButtonType("Right");

        alert.getButtonTypes().setAll(end0, end1);

        Optional<ButtonType> result = alert.showAndWait();
        System.out.println("End chosen is " + result.get());
        if (result.get() == end1) {
            return 1;
        } else {
            return 0;
        }
    }
}
