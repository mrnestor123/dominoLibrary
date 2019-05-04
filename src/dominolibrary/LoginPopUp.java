/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import business_logic.DataStore;
import business_logic.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Barra
 */
public class LoginPopUp {

    DataStore d = new DataStore();
   public void display(String title){
       Stage window = new Stage();
       
       window.initModality(Modality.APPLICATION_MODAL);
       window.setTitle(title);
       window.setMinWidth(300);
       window.setMinHeight(200);
       GridPane grid = new GridPane();
       
       Label name = new Label("Username");
       TextField input = new TextField();
       
       Label password = new Label("Password  ");
       PasswordField passwordinput = new PasswordField();
       
       grid.add(name, 0, 0);
       grid.add(input,1,0,2,1);
       grid.add(password,0,1);
       grid.add(passwordinput,1,1,2,1);
       
       Button login = new Button("Log in");
       Button closeButton = new Button("Cancel");
       closeButton.setOnAction(e -> window.close());
       closeButton.setAlignment(Pos.CENTER_RIGHT);
       login.setAlignment(Pos.CENTER_LEFT);
       login.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Player p = new Player(input.getText());
                d.addPlayer(p);
            }
          });
       
       
       grid.add(login,1,2);
       grid.add(closeButton,2,2);
       grid.setPadding(new Insets(15,15,15,15));
       grid.setAlignment(Pos.CENTER);
       
       VBox layout = new VBox();
       layout.getChildren().addAll(grid);
       layout.setAlignment(Pos.CENTER);
       
       Scene scene = new Scene(layout);
       window.setScene(scene);
       window.showAndWait();
   } 
}
