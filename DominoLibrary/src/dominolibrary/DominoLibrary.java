/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Barra
 */
public class DominoLibrary extends Application {

    /**
     * @param args the command line arguments
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenu.fxml"));    
        Parent root = loader.load();
        Scene scene = new Scene(root);
        MainMenuController mainController =loader.<MainMenuController>getController();
        scene.getStylesheets().add(getClass().getResource("dominosroom.css").toExternalForm());
        mainController.initStage(stage);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}