/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package functions;

/**
 *
 * @author Bram
 */
public class Math {


    public static boolean isIntNumber(String num){
    try{
    Integer.parseInt(num);
    } catch(NumberFormatException nfe) {
    return false;
    }
    return true;
    }
}
