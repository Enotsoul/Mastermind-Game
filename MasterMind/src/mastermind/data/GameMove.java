/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.data;

import java.io.Serializable;

/**
 *
 * @author lostone
 */
public class GameMove implements Serializable {
    /* GameMove class has multiple purposes:
     *      Storing the Game Moves for the Game
     *      Sending it as an object to see if it's ok
     *  the position variable is used to sort the object according the position
     */
    private int gameID,playerID,position;
    private String playerMove;

    public GameMove() {
    }

    public GameMove(int gameID, int playerID, int position, String playerMove) {
        this.gameID = gameID;
        this.playerID = playerID;
        this.position = position;
        this.playerMove = playerMove;
    }

    public GameMove(int gameID, int playerID, String playerMove) {
        this.gameID = gameID;
        this.playerID = playerID;
        this.playerMove = playerMove;
    }

    public GameMove(int playerID, String playerMove) {
        this.playerID = playerID;
        this.playerMove = playerMove;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public String getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(String playerMove) {
        this.playerMove = playerMove;
    }

}
