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
//invite object for the type of game

public class Invite implements Serializable {
    /* player1 sends InviteObject to server
    * if player2 is online & not in a game Server sends InviteObject to player2
    * if player2 is not online or in a game =>
            Server sends PlayerNotOnline or UserIsInGame to player1
    * player2 sends InviteObject back to server with status "ReviewedInvite"
            so the sever knows how to handle it it back..
    * IF accepted is false then Server sends a "PlayerRejectedRequest" back to the player1
    * IF accepted is true then server sends a "PlayerAcceptedRequest" back to player1
    *       and the game starts for both users while they wait for the server to set everything up..
    */
   private String player1,player2,status = "Invite";
 
    //true means the server generates a random combination..
    //false => each player choses the type of game
   private boolean serverCombination;
   private boolean accepted = false; // not accepted yet..

    public Invite(String player1, String player2, boolean serverCombination) {
        this.player1 = player1;
        this.player2 = player2;
        this.serverCombination = serverCombination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isServerCombination() {
        return serverCombination;
    }

    public void setServerCombination(boolean serverCombination) {
        this.serverCombination = serverCombination;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    

}
