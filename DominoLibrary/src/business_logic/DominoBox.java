/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business_logic;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.layout.VBox;

/**
 *
 * @author Barra
 */
public class DominoBox extends VBox {
    
    Domino d ;
    public DominoBox(Domino domino) {
        super();
        d = domino;
    }
    
    public void setDomino(Domino dom){
        d = dom;
    }
    public Domino getDomino(){return this.d;}
     
    /**
     * returns the coordinates x and y of the current object4
     * 
     * 
     */
    public Map<Double,Double> getCoordinates(){
        
        Map<Double,Double> aux =new HashMap<>();
        aux.put(this.getLayoutX(), this.getLayoutY());
        
        return aux;
    }
    
    public String checkDirection(){
    return "";
    }
    
    
    
}
