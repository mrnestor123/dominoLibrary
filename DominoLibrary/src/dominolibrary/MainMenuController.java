/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import business_logic.DataStore;
import business_logic.Player;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    Player currentPlayer = new Player("Guest");

    DataStore d = new DataStore();

    @FXML
    private Button login;

    public void initStage(Stage stage) {
        primaryStage = stage;
    }

    @FXML
    private void goToStudentsManagement(ActionEvent event) {

        if (currentPlayer.getName().equals("Guest")) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("You have not registered yet. Do you want to register?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                gotoLogin(event);
            } else {
                try {
                    FXMLLoader myLoader = new FXMLLoader(getClass().getResource("DominosRoom.fxml"));
                    Parent root = (Parent) myLoader.load();
                    DominosRoomController window1 = myLoader.<DominosRoomController>getController();
                    window1.initStage(primaryStage, d, currentPlayer);
                    Scene scene = new Scene(root);
                    primaryStage.setScene(scene);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * Opens a login window and then adds the player to the dataStore
     *
     * @param event
     */
    @FXML
    private void gotoLogin(ActionEvent event) {

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
    public void initialize(URL url, ResourceBundle rb) {
        //BooleanBinding booleanBind = Bindings.equal(currentPlayer,null);
        //login.disableProperty().bind(booleanBind);

    }
}
