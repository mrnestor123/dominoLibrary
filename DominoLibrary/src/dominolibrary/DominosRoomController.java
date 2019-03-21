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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Barra
 */
public class DominosRoomController implements Initializable {

    @FXML
    private GridPane yourDominoes;

    @FXML
    private HBox oppponentDominoes;

    @FXML
    private GridPane oppDominoes;

    @FXML
    private Label totalpoints, stackedDominoes, playerpoints;

    @FXML
    private Pane dominoboard;

    @FXML
    private AnchorPane mainpannel;

    private Table t;

    private Stage primaryStage;
    private Scene prevScene;
    private String prevTitle;

    private DataStore d;

    private ObservableList<Domino> playeroneDominoes, playertwoDominoes;
    private int dominoesplayed = 0;

    public void initStage(Stage stage, DataStore data) {
        primaryStage = stage;
        prevScene = stage.getScene();
        prevTitle = stage.getTitle();
        data = d;
    }

    @FXML
    private void goBack(ActionEvent event) {
        primaryStage.setTitle(prevTitle);
        primaryStage.setScene(prevScene);
    }

    @FXML
    private void moveDomino(DominoBox v, Player p) {
        
        
        v.setMinSize(v.getPrefWidth(), v.getPrefHeight());

        Double[] aux = t.placeaDomino(p, v);

        v.setRotate(aux[2]);
       
        dominoesplayed++;
        v.setAlignment(Pos.CENTER);

        System.out.println("Position y" + aux[0]);
        System.out.println("Position x" + aux[1]);

        v.relocate(aux[0], aux[1]);

        //v.setLayoutX(mainpannel.getWidth()/2);
        //v.setLayoutY(mainpannel.getHeight()/2);
        // v.setTranslateY(mainpannel.getHeight()/2);
        //v.setTranslateX(mainpannel.getWidth()/2);
        dominoboard.getChildren().add(v);

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

    /**
     * Initializing the class for player one Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Player one = new Player("jose");
        Player two = new Player("ernest");

        List<Player> players = new ArrayList<>();
        players.add(one);
        players.add(two);

        t = new Table(150,730 , 432);

        totalpoints.setText(Integer.toString(t.getMaxPoints()));
        playerpoints.setText(Integer.toString(one.getGameScore()));

        List<Domino> dominoes = t.mixDominoes();

        List<Domino> stack = t.handleDominoes(dominoes, players);

        if (stack != null) {
            stackedDominoes.setText(Integer.toString(stack.size()));
        }

        playeroneDominoes = FXCollections.observableArrayList(one.getDominoes());
        playertwoDominoes = FXCollections.observableArrayList(two.getDominoes());

        for (int i = 0; i < playeroneDominoes.size(); i++) {
            Domino d = playeroneDominoes.get(i);
            DominoBox v = new DominoBox(d);

            VBox opponent = new VBox();
            v.setId("domino" + i);
            v.setPrefSize(40, 80);
            v.setStyle("-fx-background-color:white; -fx-border-color: black;");

            v.setOnMouseClicked(new EventHandler<MouseEvent>() {

                public void handle(MouseEvent event) {
                    moveDomino(v, one);
                }
            });

            opponent.setStyle("-fx-background-color:black; -fx-border-color:silver");
            Separator s = new Separator();
            s.setStyle("-fx-border-size:2px; -fx-color:black;");
            yourDominoes.add(v, i, 0);

            oppDominoes.add(opponent, i, 0);

            GridPane num1 = d.drawDomino(d.getNumber1());
            num1.setPadding(new Insets(3, 3, 3, 3));
            GridPane num2 = d.drawDomino(d.getNumber2());
            num2.setPadding(new Insets(3, 3, 3, 3));

            v.getChildren().add(num1);
            v.getChildren().add(1, s);
            v.getChildren().add(num2);
        }
    }
}
