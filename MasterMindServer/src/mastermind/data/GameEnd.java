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
/* GameEnd object tells each player that the game has ended so they can go back
 *  to the lobby & tells the winner etc.
 *      the winner is the username/ID
 *      the status is your status.. won.. lost.. (YouWin - YouLose - OtherQuit)
 */
public class GameEnd implements Serializable {
    private String winner,status,gameTime;

    public GameEnd(String winner, String status, String gameTime) {
        this.winner = winner;
        this.status = status;
        this.gameTime = gameTime;
    }

    public String getGameTime() {
        return gameTime;
    }

    public void setGameTime(String gameTime) {
        this.gameTime = gameTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
    
    
}
