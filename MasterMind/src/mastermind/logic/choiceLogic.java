/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.logic;

/**
 *
 * @author lostone
 */
public class choiceLogic {

    private static String toGuess = "RRRG";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        System.out.println(controlRightLocation("GRGG"));

    }

    /* function: controlRightLocation
     *      input: string with numbers eg: 1234
     *      output: string with coloured balls ex: BWW
     *      side effects: none
     */
    public static String controlRightLocation(String guess) {
        int blackBall = 0;
        int whiteBall= 0;
        String toReturn = "";
        String currentGuess = "";
      //  String[] existentWhite = new String[guess.length()+1];
        String existentWhite ="";

        for (int i = 0 ; i <  guess.length() ; i++) {
            currentGuess = guess.substring(i,i+1);
            
            if (currentGuess.equals(toGuess.substring(i,i+1))) {
                //Position & Colour is correct
                blackBall++;
            } else {
                for (int j = 0; j < toGuess.length(); j++) {
                    if (currentGuess.equals(toGuess.substring(j,j+1))) {
                        //Colour is correct, position is wrong
                        // System.out.println("existentWhite " + existentWhite + " found white " + currentGuess);
                         if (existentWhite.contains(currentGuess)) {
                            System.out.println("Already containing this colour!");
                            break; //Already contain colour:-)
                         }
                         whiteBall++;
                         existentWhite += currentGuess;
                         break; // so we don't re-get this colour again:)
                     }
                }
            }
        }
      //  System.out.println("black " + blackBall + " white "  + whiteBall);
        //return the total of balls
        for (int i= 0; i < blackBall; i++) {
            toReturn += "B";
        }
        for (int i= 0; i < whiteBall; i++) {
            toReturn += "W";
        }
        
        return toReturn;
    }
}
