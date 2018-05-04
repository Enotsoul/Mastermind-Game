/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author lostone
 */
/* Game object implementation 
 * Server sends the Game Object to player1 & player2
 * If gameType = 1 (random generation) then they both can start guessing the answer
 * if gameType = 0 (each player choses a string)
        then the players must wait untill the server has both solutions filled :)
 * Then the server sends another Game object with the status of "GameMayStart" to both users
 * Then the game is started.. and each player may send a GameMove object to the server..
 */
public class Game implements Serializable {
    private int gameID,player1ID,player2ID,winnerID,gameType;
    private long gameStartTime,gameEndTime;
    private String player1Solution,player2Solution,status="Waiting";

    private ArrayList<GameMove> gameMoves = new ArrayList<GameMove>();

    //this is used to initialize a normal game
    public Game(int player1ID, int player2ID, int gameType, long gameStartTime) {
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.gameType = gameType;
        this.gameStartTime = gameStartTime;
    }

    //This is initialized when sending the full object for viewing a
    // game played another time
    public Game(int gameID, int player1ID, int player2ID, int winnerID, int gameType, long gameStartTime, long gameEndTime, String player1Solution, String player2Solution) {
        this.gameID = gameID;
        this.player1ID = player1ID;
        this.player2ID = player2ID;
        this.winnerID = winnerID;
        this.gameType = gameType;
        this.gameStartTime = gameStartTime;
        this.gameEndTime = gameEndTime;
        this.player1Solution = player1Solution;
        this.player2Solution = player2Solution;
    }

    //
    public void addMove(GameMove gm) {
        gameMoves.add(gm);
    }
    public ArrayList<GameMove> getGameMoves() {
        return gameMoves;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGameMoves(ArrayList<GameMove> gameMoves) {
        this.gameMoves = gameMoves;
    }
    
    public long getGameEndTime() {
        return gameEndTime;
    }
    
    public void setGameEndTime(long gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(long gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public int getPlayer1ID() {
        return player1ID;
    }

    public void setPlayer1ID(int player1ID) {
        this.player1ID = player1ID;
    }

    public String getPlayer1Solution() {
        return player1Solution;
    }

    public void setPlayer1Solution(String player1Solution) {
        this.player1Solution = player1Solution;
    }

    public int getPlayer2ID() {
        return player2ID;
    }

    public void setPlayer2ID(int player2ID) {
        this.player2ID = player2ID;
    }

    public String getPlayer2Solution() {
        return player2Solution;
    }

    public void setPlayer2Solution(String player2Solution) {
        this.player2Solution = player2Solution;
    }

    public int getWinnerID() {
        return winnerID;
    }

    public void setWinnerID(int winnerID) {
        this.winnerID = winnerID;
    }


}
