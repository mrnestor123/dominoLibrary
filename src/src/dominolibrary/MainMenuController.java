/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import business_logic.Player;
import business_logic.Table;
import business_logic.TableModel;
import connection.Client;
import connection.Message;
import connection.Server;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Barra
 */
public class MainMenuController implements Initializable {

    Stage primaryStage;
    private Scene prevScene;
    private Player currentPlayer;
    private Client client;
    private Server server;
    private Table game;
    private String playerName;

    @FXML
    private TableView<TableModel> listOfGames;
    @FXML
    private TableColumn<TableModel, String> nameOfGame;

    @FXML
    private TableColumn<TableModel, Integer> numPlayers;

    @FXML
    private TableColumn<TableModel, Integer> maxPoints;

    @FXML
    private Button login;

    @FXML
    private Button join;

    private ObservableList<TableModel> listofGamesobsList = FXCollections.observableArrayList();

    public void initStage(Stage stage) throws InterruptedException {
        boolean sent = false;
        int i = 50;

        client = new Client("localhost", 8000, data -> {
            handleClientMessages(data);
        });
        //We try to check if the server is active
        if (client.connect()) {
            System.out.print("connection succesful");
        } else {
            server = new Server(8000, data -> {
                Platform.runLater(() -> System.out.print(""));
            });
            server.start();
            client.connect();
        }
        playerName = "Guest" + (int) (Math.random() * 100);

        client.send(new Message("Connect " + playerName));

        primaryStage = stage;
        primaryStage.setOnHiding(e -> {
            client.close();
            if (server != null) {
                System.out.println("closing server");
                server.close();
            }
        });
    }

    /**
     * To get to the other screen
     *
     * @param event
     */
    @FXML

    private void joinGame(ActionEvent event) {
        try {
            if (listOfGames.getSelectionModel().getSelectedItem() != null) {
                TableModel t = listOfGames.getSelectionModel().getSelectedItem();
                game = t.getTable();
                Player p = new Player(playerName);
                game.addPlayer(p);
                listofGamesobsList.remove(listOfGames.getSelectionModel().getSelectedItem());
                FXMLLoader myLoader = new FXMLLoader(getClass().getResource("DominosRoom.fxml"));
                Parent root = (Parent) myLoader.load();
                DominosRoomController window1 = myLoader.<DominosRoomController>getController();
                window1.initStage(primaryStage, client, server, game, p);
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the create Game window
     */
    @FXML
    private void createGame(ActionEvent event) {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Create Game");
        window.setMinWidth(300);
        window.setMinHeight(400);
        GridPane grid = new GridPane();

        Label name = new Label("Name of the game");
        TextField inputname = new TextField();

        Label numPlayers = new Label("Num of Players  ");
        ObservableList<Integer> playerchoice = FXCollections.observableArrayList();
        playerchoice.addAll(2);
        ChoiceBox<Integer> nPlayers = new ChoiceBox(playerchoice);

        Label MaxPoints = new Label("Max points");
        ObservableList<Integer> points = FXCollections.observableArrayList();
        points.addAll(10, 100, 150, 200);
        ChoiceBox<Integer> mPoints = new ChoiceBox(points);

        Button createGame = new Button("Accept");
        Button closeButton = new Button("Cancel");
        createGame.setOnAction((ActionEvent e) -> {
            if (inputname.getText() != null) {
                Table game = new Table(inputname.getText(), mPoints.getValue(), nPlayers.getValue());
                currentPlayer = new Player(playerName);
                game.addPlayer(currentPlayer);
                client.send(new Message("Create " + playerName, game));
                window.close();
                try {
                    FXMLLoader myLoader = new FXMLLoader(getClass().getResource("DominosRoom.fxml"));
                    Parent root = (Parent) myLoader.load();
                    DominosRoomController window1 = myLoader.<DominosRoomController>getController();
                    window1.initStage(primaryStage, client, server, game, currentPlayer);
                    Scene nextScene = new Scene(root);
                    primaryStage.setScene(nextScene);
                } catch (IOException ex) {
                    ex.printStackTrace();

                } catch (Exception ex) {
                    Logger.getLogger(MainMenuController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );

        closeButton.setOnAction(e
                -> window.close());
        closeButton.setAlignment(Pos.CENTER_RIGHT);

        createGame.setAlignment(Pos.CENTER_LEFT);

        grid.add(name, 0, 0);
        grid.add(inputname, 1, 0, 2, 1);
        grid.add(numPlayers, 0, 1);
        grid.add(nPlayers, 1, 1, 2, 1);
        grid.add(MaxPoints, 0, 2);
        grid.add(mPoints, 1, 2, 2, 1);

        grid.add(createGame, 1, 3);
        grid.add(closeButton, 2, 3);
        grid.setPadding(new Insets(15, 15, 15, 15));
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        VBox layout = new VBox();

        layout.getChildren().addAll(grid);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);

        window.showAndWait();
    }

    /**
     * Opens a login window and then adds the player to the dataStore
     *
     * @param event
     */
    @FXML
    private void gotoLogin(ActionEvent event
    ) {

        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Login window");
        window.setMinWidth(300);
        window.setMinHeight(200);
        GridPane grid = new GridPane();

        Label name = new Label("Username");
        TextField input = new TextField();

        Label password = new Label("Password  ");
        PasswordField passwordinput = new PasswordField();

        grid.add(name, 0, 0);
        grid.add(input, 1, 0, 2, 1);
        grid.add(password, 0, 1);
        grid.add(passwordinput, 1, 1, 2, 1);

        Button login = new Button("Log in");
        Button closeButton = new Button("Cancel");
        closeButton.setOnAction(e -> window.close());
        login.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION, "You are about to create a player with name" + input.getText() + ".Do you want to continue?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                currentPlayer.setName(input.getText());
                Alert alert2 = new Alert(AlertType.INFORMATION, "Congratulations,you have created an user !");
                alert2.showAndWait();
                login.setDisable(true);
                window.close();
            }
        }
        );

        grid.add(login, 1, 2);
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        //BooleanBinding booleanBind = Bindings.equal(currentPlayer,null);
        //login.disableProperty().bind(booleanBind);

        nameOfGame.setCellValueFactory(new PropertyValueFactory<>("NameofGame"));
        numPlayers.setCellValueFactory(new PropertyValueFactory<>("NumPlayers"));
        maxPoints.setCellValueFactory(new PropertyValueFactory<>("MaxPoints"));

        join.disableProperty().bind(Bindings.isEmpty(listOfGames.getSelectionModel().getSelectedCells()));
        listOfGames.setItems(listofGamesobsList);
    }

    private void handleClientMessages(Message data) {
        Platform.runLater(() -> {
            String tokens[] = data.getText().split(" ");
            String cmd = tokens[0];
            if (cmd.equalsIgnoreCase("create")) {
                listofGamesobsList.add(data.getTable().createTableModel());
            } else if (cmd.equalsIgnoreCase("remove")) {
                TableModel aux = null;
                for (TableModel n : listofGamesobsList) {
                    if (n.getNameofGame().equals(data.getTable().getName())) {
                        aux = n;
                    };
                }
                listofGamesobsList.remove(aux);
            }
        }
        );
    }
}

/**
 * private Server createServer() { System.out.print("Creating server"); return
 * new Server(8000, data -> { Platform.runLater(() -> {
 * System.out.println("Server received: " + data.getMessage()); /** if
 * (data.getTable()) if (data.getMessage().equals("Connection")) { opponent =
 * data.getPlayer(); Table newtable = createTable(opponent); try { Message m =
 * new Message("Table", newtable); connection.send(m); } catch (Exception ex) {
 * System.out.print(ex.toString()); } } else if
 * (data.getMessage().startsWith("Movement")) {
 * data.getDominoMoved().setId(data.getMessage().substring("Movement".length()));
 * System.out.println("MOVING DOMINO with id " + data.getDominoMoved().getId());
 * moveDomino(data.getDominoMoved(), data.getPlayer(), true); } else if
 * ("start".equals(data.getMessage())) { System.out.println("GAME STARTED"); }*
 *
 * }); }); }
 *
 * private Client createClient() { System.out.print("Creating client"); return
 * new Client("127.0.0.1", 8000, data -> { Platform.runLater(() -> {
 * System.out.println("Client received: " + data.getMessage());
 *
 * /**
 * if ("Table".equals(data.getMessage())) { t = data.getTable(); currentplayer =
 * t.getPlayers().get(1); opponent = t.getPlayers().get(0); populateGame(t); }
 * else if (data.getMessage().startsWith("Movement")) {
 * data.getDominoMoved().setId(data.getMessage().substring("Movement".length()));
 * moveDomino(data.getDominoMoved(), data.getPlayer(), true); }* }); }); }*
 */
