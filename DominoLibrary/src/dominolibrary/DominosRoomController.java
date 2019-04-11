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
import connection.NetworkConnection;
import connection.Server;
import java.net.URL;
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
    private Player currentplayer, opponent;

    //Variables for creating the  connection
    boolean isServer = true;
    private NetworkConnection connection = isServer ? createServer() : createClient();

    public void initStage(Stage stage, DataStore data, Player p) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        data = d;
        if ("Guest".equals(p.getName())) {
            int id = (int) (Math.random() * 100);
            String name = p.getName() + id;
            p.setName(name);
        }
        currentplayer = p;
        if (!isServer) {
            boolean sent = false;
            do {
                try {
                    Message newMessage = new Message("Connection", currentplayer);
                    connection.send(newMessage);
                    sent = true;
                } catch (Exception ex) {
                }
            } while (!sent);
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        primaryStage.setTitle(prevTitle);
        primaryStage.setScene(prevScene);
    }

    private void moveallDominoes(List<Domino> dominoes, Player p) {
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
            //moveDomino(v, p, false);
        }
    }

    /**
     * Moves the domino in the game. The boolean is to check who is the one
     * moving the domino
     *
     * @param v
     * @param p
     * @param from
     */
    @FXML
    private void moveDomino(DominoBox v, Player p, boolean opponent) {
        if (v.getPrefWidth() == 0) {
        }
        v.setMinSize(v.getPrefWidth(), v.getPrefHeight());
        Double[] aux = t.placeaDomino(p, v);
        System.out.print("Position: " + v.getId());
        dominoesplayed++;
        if (!opponent && aux[0] != 0 && aux[1] != 0) {
            v.relocate(aux[0], aux[1]);
            v.setRotate(aux[2]);
            dominoboard.getChildren().add(v);
            System.out.println("Position x" + aux[0]);
            System.out.println("Position y" + aux[1]);
            changeTurn();
            try {
                Message m = new Message("Movement" + v.getId(), v, currentplayer);
                connection.send(m);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        } else if (opponent && aux[0] != 0 && aux[1] != 0) {
            ObservableList<Node> dominoes = oppDominoes.getChildren();
            Node chosenDomino = null;
            for (Node n : dominoes) {
                System.out.println("ID"+ n.getId());
                System.out.println("V ID" + v.getId());
                if (n.getId().equals(v.getId())) {
                    chosenDomino = n;
                    chosenDomino.setStyle("-fx-background-color:white; -fx-border-color: black;");
                    chosenDomino.relocate(aux[0], aux[1]);
                    chosenDomino.setRotate(aux[2]);
                    }
            }
            if(chosenDomino!=null)dominoboard.getChildren().add(chosenDomino);
                
            changeTurn();
        }
        //v.rotateProperty().set(90);
        //Alert alert = new Alert(AlertType.INFORMATION);
        // alert.setTitle("WARNING");
        // alert.setHeaderText(null);
        // alert.setContentText("Try another tile");
        // DialogPane dialogPane = alert.getDialogPane();
        //dialogPane.getStylesheets().add(getClass().getResource("Dialog.css").toExternalForm());
        //dialogPane.getStyleClass().add("myDialog");
        //alert.showAndWait();          
    }

    private Server createServer() {
        System.out.print("Creating server");
        return new Server(8000, data -> {
            Platform.runLater(() -> {
                if (data.getMessage().equals("Connection")) {
                    opponent = data.getPlayer();
                    Table newtable = populateGame(opponent);
                    try {
                        Message m = new Message("Table", newtable);
                        connection.send(m);
                    } catch (Exception ex) {
                        System.out.print(ex.toString());
                    }
                } else if (data.getMessage().startsWith("Movement")) {
                    data.getDominoMoved().setId(data.getMessage().substring("Movement".length()));
                    moveDomino(data.getDominoMoved(), data.getPlayer(), true);
                } else if ("start".equals(data.getMessage())) {
                    System.out.println("GAME STARTED");
                    changeTurn();
                }
            });
        });
    }

    private Client createClient() {
        return new Client("127.0.0.1", 8000, data -> {
            Platform.runLater(() -> {
                if ("Table".equals(data.getMessage())) {
                    populateClientGame(data.getTable());
                } else if (data.getMessage().startsWith("Movement")) {
                    data.getDominoMoved().setId(data.getMessage().substring("Movement".length()));
                    moveDomino(data.getDominoMoved(), data.getPlayer(), true);
                }
            });
        });
    }

    /**
     * Given the opponent player we create the game for the Server
     *
     * @param p
     * @return
     */
    private Table populateGame(Player opponent) {

        System.out.println("Our player is" + currentplayer.getName());
        t = new Table(150, 730, 432);
        t.addPlayer(currentplayer);
        t.addPlayer(opponent);
        totalpoints.setText(Integer.toString(t.getMaxPoints()));
        playerpoints.setText(Integer.toString(currentplayer.getGameScore()));
        List<Domino> shuffleddominoes = t.mixDominoes();
        t.handleDominoes(shuffleddominoes, currentplayer, 0);
        List<Domino> stack = t.handleDominoes(shuffleddominoes, opponent, 7);
        if (stack != null) {
            stackedDominoes.setText("" + stack.size());
        }
        List<Player> players = t.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.get(i).getDominoes().size(); j++) {
                if (players.get(i).getName().equals(currentplayer.getName())) {
                    Domino d = players.get(i).getDominoes().get(j);
                    DominoBox v = new DominoBox(d);
                    v.setId("" + j);
                    v.setPrefSize(40, 80);
                    v.setStyle("-fx-background-color:white; -fx-border-color: black;");
                    v.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            moveDomino(v, currentplayer, false);
                        }
                    });
                    Separator s = new Separator();
                    s.setStyle("-fx-border-size:2px; -fx-color:black;");
                    yourDominoes.add(v, j, 0);
                    GridPane num1 = d.drawDomino(d.getNumber1());
                    num1.setPadding(new Insets(3, 3, 3, 3));
                    GridPane num2 = d.drawDomino(d.getNumber2());
                    num2.setPadding(new Insets(3, 3, 3, 3));
                    v.getChildren().add(num1);
                    v.getChildren().add(1, s);
                    v.getChildren().add(num2);
                } else {
                    Domino d = players.get(i).getDominoes().get(j);
                    DominoBox opponentDomino = new DominoBox(d);
                    opponentDomino.setId("" + j);
                    opponentDomino.setStyle("-fx-background-color:black; -fx-border-color:silver;");
                    opponentDomino.setPrefSize(40, 80);
                    oppDominoes.add(opponentDomino, j, 0);
                    Separator s = new Separator();
                    s.setStyle("-fx-border-size:2px; -fx-color:black;");
                    GridPane num3 = d.drawDomino(d.getNumber1());
                    num3.setPadding(new Insets(3, 3, 3, 3));
                    GridPane num4 = d.drawDomino(d.getNumber2());
                    num4.setPadding(new Insets(3, 3, 3, 3));

                    opponentDomino.getChildren().add(num3);
                    opponentDomino.getChildren().add(1, s);
                    opponentDomino.getChildren().add(num4);
                }
            }
        }
        playerTurn = t.getPlayerStarting();
        return t;
    }

    /**
     *
     * We populate the game for the client
     *
     * @param t
     */
    private void populateClientGame(Table t) {
        this.t = t;
        System.out.print("Current player is " + currentplayer.getName());
        List<Player> players = t.getPlayers();
        opponent = players.get(0);
        for (int i = 0; i < players.size(); i++) {
            for (int j = 0; j < players.get(i).getDominoes().size(); j++) {
                if (players.get(i).getName().equals(currentplayer.getName())) {
                    Domino d = players.get(i).getDominoes().get(j);
                    DominoBox v = new DominoBox(d);
                    v.setId("" + j);
                    v.setPrefSize(40, 80);
                    v.setStyle("-fx-background-color:white; -fx-border-color: black;");
                    v.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            try {
                                Message m = new Message("Movement" + v.getId(), v, currentplayer);
                                connection.send(m);
                            } catch (Exception ex) {
                                System.out.println(ex.getMessage());
                            }
                            moveDomino(v, currentplayer, false);
                        }
                    });
                    Separator s = new Separator();
                    s.setStyle("-fx-border-size:2px; -fx-color:black;");
                    yourDominoes.add(v, j, 0);
                    GridPane num1 = d.drawDomino(d.getNumber1());
                    num1.setPadding(new Insets(3, 3, 3, 3));
                    GridPane num2 = d.drawDomino(d.getNumber2());
                    num2.setPadding(new Insets(3, 3, 3, 3));
                    v.getChildren().add(num1);
                    v.getChildren().add(1, s);
                    v.getChildren().add(num2);
                } else {
                    Domino d = players.get(i).getDominoes().get(j);
                    DominoBox opponentDomino = new DominoBox(d);
                    opponentDomino.setId("" + j);
                    opponentDomino.setStyle("-fx-background-color:black; -fx-border-color:silver;");
                    opponentDomino.setPrefSize(40, 80);
                    oppDominoes.add(opponentDomino, j, 0);
                    Separator s = new Separator();
                    s.setStyle("-fx-border-size:2px; -fx-color:black;");
                    GridPane num3 = d.drawDomino(d.getNumber1());
                    num3.setPadding(new Insets(3, 3, 3, 3));
                    GridPane num4 = d.drawDomino(d.getNumber2());
                    num4.setPadding(new Insets(3, 3, 3, 3));
                    opponentDomino.getChildren().add(num3);
                    opponentDomino.getChildren().add(1, s);
                    opponentDomino.getChildren().add(num4);
                }
            }
        }
        try {
            Message startGame = new Message("start");
            connection.send(startGame);
        } catch (Exception ex) {
            Logger.getLogger(DominosRoomController.class.getName()).log(Level.SEVERE, null, ex);
        }
        playerTurn = t.getPlayerStarting();
        changeTurn();
    }

    /**
     * Method for playing the game. Checks if the player can play the tile , if
     * not he draws in the stack. Disables the gridpane of the opponent.
     */
    private void changeTurn() {
        boolean movement = false;
        System.out.println("CurrentPlayer is" + currentplayer.getName());
        System.out.println("Player turn is" + t.getPlayers().get(playerTurn).getName());
        do {
            if (currentplayer.getName().equals(t.getPlayers().get(playerTurn).getName())) {
                System.out.println("Its " + currentplayer.getName() + "turn");
                yourDominoes.setDisable(false);
                if (!t.PlayerPassesTurn(currentplayer)) {
                    movement = true;
                    playerTurn++;
                } else {
                    DominoBox b = new DominoBox(t.getDominoFromStack(opponent));
                    System.out.print("Robamos el domino" + b.toString());
                }
            } else {
                System.out.println("WAITING FOR NEXT TURN");
                yourDominoes.setDisable(true);
                movement = true;
                playerTurn++;
            }
        } while (!movement);
        if (playerTurn > 1) {
            playerTurn = 0;
        }
    }

    /**
     * We create the game if the player is the first one i.e the server
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boolean sent = false;
        try {
            connection.startConnection();
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        if (isServer) {
            System.out.print("Waiting for client");
        }
    }
}
