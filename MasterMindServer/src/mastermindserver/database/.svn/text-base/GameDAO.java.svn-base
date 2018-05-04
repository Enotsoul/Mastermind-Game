/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermindserver.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import mastermind.data.Game;
import mastermind.data.GameMove;
import mastermind.data.MiscFunctions;

/**
 *
 * @author lostone
 */
public class GameDAO {
    private static GameDAO instance = null;

    private GameDAO(){}

    public static GameDAO getInstance(){
        if(instance==null){
            instance = new GameDAO();
            return instance;
        } else
            return instance;
    }

    /*
    public ArrayList<Login> getUsers() throws SQLException {
        ArrayList<Login> users = new ArrayList<Login>();

        String sql = "SELECT * FROM Players";

        Connection conn = MySqlDatabase.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);

        //Eventually put parameters... if any..?
        //ps.setObject(1, o);
        //Run the result..
        ResultSet res = ps.executeQuery();

        //Get the data.. for all the users..
        while (res.next()){
            int userID = res.getInt(1);
            String username = res.getString(2);
            String password = res.getString(3);
            String email = res.getString(4);
            Login l = new Login(username,password,email);
            l.setUserID(userID);
            users.add(l);
        }
        conn.close();
        return users;
    }

*/
    //Create a new game in the Database.. and get the unique gameID
    public int newGame(Game g) throws SQLException {
         int gameID;
        String sql = "INSERT INTO PlayerGames (player1ID,player2ID,gameStartTime,gameType) VALUES (?,?,?,?)";
        Connection conn = MySqlDatabase.getInstance().getConnection();
        //execute the SQL with ease
        MySqlDatabase.getInstance().executeSQL(sql,conn,g.getPlayer1ID(),g.getPlayer1ID(),g.getGameStartTime(),g.getGameType());

       sql = "SELECT gameID from PlayerGames WHERE player1ID=? AND player2ID=? AND gameStartTime=? AND gameType=?";
       ResultSet res = MySqlDatabase.getInstance().getResultSet(sql,conn,g.getPlayer1ID(),g.getPlayer1ID(),g.getGameStartTime(),g.getGameType());

       if (res.next()) {
           gameID = res.getInt(1);
       } else { gameID = 0; }

        //always close connection atm
         conn.close();
         return gameID;
    }
    //Function to update the DB at the end of the game
    public void updateGame(Game game) throws SQLException {
         String sql = "UPDATE PlayerGames "
                 + "SET winnerID=?, gameEndTime=?, player1Solution=?, player2Solution=? "
                 + "WHERE gameID=?";
         Connection conn = MySqlDatabase.getInstance().getConnection();

         MySqlDatabase.getInstance().executeSQL(sql,conn,game.getWinnerID(),
                 game.getGameEndTime(),game.getPlayer1Solution(),game.getPlayer2Solution(),game.getGameID());
         
       //always close connection atm
         conn.close();
    }
    
    //Add each move...
    public void addGameMove(GameMove gm) throws SQLException {
        String sql = "INSERT INTO GameMoves "
                + "(gameID,playerID,position,playerMove) "
                + "VALUES (?,?,?,?)";

         Connection conn = MySqlDatabase.getInstance().getConnection();

         MySqlDatabase.getInstance().executeSQL(sql,conn,gm.getGameID(),
                gm.getPlayerID(),gm.getPosition(),gm.getPlayerMove());

       //always close connection atm
         conn.close();
    }
    /*
    public void saveBoeken (ArrayList<Boek> boeken) throws SQLException {
       for(Login l: boeken) {
            saveBoek(b);
        }
    }
*/
    /*
    public void deleteUsername (Login l) throws SQLException {
        String sql = "DELETE FROM Players WHERE userID=?";
        Connection conn = MySqlDatabase.getInstance().getConnection();
       MySqlDatabase.getInstance().executeSQL(sql,conn,l.getUserID());
        //always close the connection
        conn.close();
    }

  //get the Login info  using the name
   public Login getUsername(String user) throws SQLException {
       String sql = "SELECT * FROM Players WHERE username=?";
       Connection conn = MySqlDatabase.getInstance().getConnection();
       ResultSet res = MySqlDatabase.getInstance().getResultSet(sql,conn,user);
       Login l;

       if (res.next()) {
           int userID = res.getInt(1);
           String username = res.getString(2);
           String password = res.getString(3);
           String email = res.getString(4);
           int banned = res.getInt(5);
           l = new Login(username,password,email);
           l.setUserID(userID);
           l.setBanned(banned);
       } else {
           //this is used when allowing a user to register/login so we don't
           //use too much code..
           l = new Login("inexistent","inexistent","intexistent");
           l.setUserID(0);
       }
       return l;
   }
   //ban the username
   public void banUsername(String user,boolean toggle) throws SQLException {
       String sql = "UPDATE Players SET banned=? WHERE username=?";
       int ban = 0;
       Connection conn = MySqlDatabase.getInstance().getConnection();
       if (toggle) {
           ban = 1;
       }
       MySqlDatabase.getInstance().executeSQL(sql,conn,ban,user);
        //always close the connection
        conn.close();
   }
*/
    public static void main(String[] args) throws Exception{
        System.out.println("Adding a game to the database..");
        Game game = new Game(1,3, 1, MiscFunctions.unixtime());
       int gameID = GameDAO.getInstance().newGame(game);
        System.out.println("The gameID of that game is " + gameID );

        game.setGameEndTime(MiscFunctions.unixtime());
        game.setPlayer1Solution("abcd");
        game.setPlayer1Solution("acbd");
        game.setWinnerID(3);

        GameDAO.getInstance().updateGame(game);
        System.out.println("Game updated ok");

        GameMove gm = new GameMove(gameID,3, 1,"acba");
        GameMove gm2 = new GameMove(gameID,3, 2,"dcba");
        GameMove gm3 = new GameMove(gameID,1, 1,"adba");
        
        GameDAO.getInstance().addGameMove(gm);
        GameDAO.getInstance().addGameMove(gm2);
        GameDAO.getInstance().addGameMove(gm3);
        System.out.println("Game Moves added:)");

    }

}
