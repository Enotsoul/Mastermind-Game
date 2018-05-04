/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.logic;

import java.util.Random;

/**
 *
 * @author lostone
 */
public class MasterMindGame {
     private String toGuess;
     private Random rn = new Random();
     private int max = 6;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getToGuess() {
        return toGuess;
    }

    public void setToGuess(String toGuess) {
        this.toGuess = toGuess;
    }
     

    public int rand(int lo, int hi)
    {
            int n = hi - lo + 1;
            int i = rn.nextInt() % n;
            if (i < 0)
                    i = -i;
            return lo + i;
    }

    public void newGuess() {
        toGuess = "";
        for (int i = 0; i < 4; i++) {
            toGuess += rand(1,max);
        }
    }
     public String returnPins(String guess) {
        int blackBall = 0;
        int whiteBall= 0;
        String toReturn = "";
        String currentGuess = "";
        boolean[] blackPosition = new boolean[guess.length()];
        int[] totalColors = new int[10];

        for (int i = 0 ; i <  guess.length() ; i++) {
            currentGuess = guess.substring(i,i+1);
            totalColors[Integer.parseInt(toGuess.substring(i,i+1))] += 1;
            if (currentGuess.equals(toGuess.substring(i,i+1))) {
               blackBall++;
               blackPosition[i] = true;
               totalColors[Integer.parseInt(toGuess.substring(i,i+1))] -= 1;
            } 
         }
        
         for (int i = 0; i <  guess.length(); i++) {
             currentGuess = guess.substring(i,i+1);
             //do not iterate loop if not needed
            if (!blackPosition[i]) {
                for (int j = 0; j < toGuess.length(); j++) {
                    if (currentGuess.equals(toGuess.substring(j,j+1)) && totalColors[Integer.parseInt(currentGuess)] > 0) {
                         whiteBall++;
                         totalColors[Integer.parseInt(currentGuess)] -= 1;
                        break; // so we don't re-get this colour again:)
                     }
               }
           }
       }
        //return the total of balls
        for (int i= 0; i < blackBall; i++) {
            toReturn += "z";
        }
        for (int i= 0; i < whiteBall; i++) {
            toReturn += "w";
        }

        return toReturn;
    }
}
