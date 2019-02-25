/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominolibrary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author Barra
 */
public class DominoLibrary {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        
        Table t = new Table(100);
        t.populateTable();
        System.out.println("How many players you want to play with");
        
        int playersNumber = input.nextInt();
        List<Player> players = new ArrayList<>();
        
        //this works fine for 1vs1 or 1vs1vs1 not 2vs2
        for (int i = 1; i <= playersNumber; i++) {
            String name = "Player " + i;
            Player n = new Player(name, i);
            players.add(n);
        }
        t.setPlayers(players);
        t.handleDominoes();
        
        System.out.println("There are " + players.size() + " players in this table");

        boolean endofgame = false;
        int counter = 0;
        do {
            if (counter >= players.size()) {
                counter = 0;
            }
            Player p = players.get(counter);
            System.out.println("It is " + p.getName() + " turn");
            if (t.getPlayedDominoes().size() == 0) {
                System.out.println("There is no dominoes in the table. Please make a move:");
            } else {
                System.out.print("The last dominoes played are:");
                
               for (Map.Entry<Integer, Domino> e : t.getLastDominoes().entrySet()) {    
                    System.out.printf(e.getValue().toString());
                }
            }
            System.out.println();
            System.out.println("Your dominoes are:");
            int numberaux = 0;
            String dominoesnumber = "";
            
            Iterator<Domino> iteratordominoes = p.getDominoes().iterator();
            
            while (iteratordominoes.hasNext()) {
                Domino d = iteratordominoes.next();
                System.out.printf(d.toString());
                dominoesnumber += " " + numberaux + "  ";
                numberaux++;
            }
            System.out.println();
            System.out.printf(dominoesnumber);
            System.out.println("Please choose a domino:");
            int choosenDominoNumber = input.nextInt();
            System.out.println("You are about to move the Domino" + p.getDominoes().get(choosenDominoNumber).toString());
            t.placeaDomino(p, p.getDominoes().get(choosenDominoNumber));
            System.out.printf("\n\n");
            //if(p.endofGame()){endofgame=true;}
            counter++;
        } while (!endofgame);
   }
}
