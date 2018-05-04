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
//This class is used for variables that are needed by the server to correctly
// handle online users 
public class OnlinePlayer implements java.io.Serializable {
    private String username;
    private boolean inGame = false; // can't be playing if just created anyway
    private boolean isOnline;
    private int userID;
    //TODO implement userID here?
   
    public OnlinePlayer(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        String extra = "";
        if (inGame) { extra = "inGame"; }
        return extra + username;
    }


}
