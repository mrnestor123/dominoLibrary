/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for emulating a database
 * @author Barra
 */
public class DataStore {
    
    private Player p;
    
    List<Table> tables = new ArrayList<>();
   
    //List of all the active players in the game
    List<Player>players = new ArrayList<>();  
    List<Domino> dominoes = new ArrayList<>();
    
    public DataStore(){
        
    }
    public void addPlayer(Player p){
        players.add(p);
    }
    
    public void createGame(){
        
        
    }
    
    
    
    
    
    
    
}
