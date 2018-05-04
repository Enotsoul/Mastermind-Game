package mastermindserver.database;

import java.sql.*;
import java.util.ArrayList;
import mastermind.data.Login;
import mastermind.data.Register;
/**
 *
 * @author lostone
 */
public class UsernameDAO {

    private static UsernameDAO instance = null;

    private UsernameDAO(){}

    public static UsernameDAO getInstance(){
        if(instance==null){
            instance = new UsernameDAO();
            return instance;
        }else
            return instance;
    }

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


    //TODO:
    /*
    public void selectSQL(String sql,Connection conn, Object... object) throws SQLException {
         PreparedStatement ps = conn.prepareStatement(sql);
     
         //execute query...
        ResultSet res = ps.executeQuery();
         while (res.next()){
            int userID = res.getInt(1);
            String username = res.getString(2);
            String password = res.getString(3);
            String email = res.getString(4);
            Login l = new Login(username,password,email);
            l.setUserID(userID);
            users.add(l);
        }
      return everything
    }
    */
    //this creates a new username.. we use the Register object here
    public void newUser(Register l) throws SQLException {
        String sql = "INSERT INTO Players (Username,password,email) VALUES (?,?,?)";
        Connection conn = MySqlDatabase.getInstance().getConnection();
        //execute the SQL with ease
        MySqlDatabase.getInstance().executeSQL(sql,conn,l.getUsername(),l.getPassword(),l.getEmail());

        //always close connection atm
         conn.close();
    }
    //maybe usefull
    public void registerUsername() throws SQLException {

    }
    /*
    public void saveBoeken (ArrayList<Boek> boeken) throws SQLException {
       for(Login l: boeken) {
            saveBoek(b);
        }
    }
*/
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

    //run this to test if username adding works..:D
     public static void main(String[] args) throws Exception{
        System.out.println("Adding a username to the database..");
      //  UsernameDAO.getInstance().newUser(new Login("Test3","test123","test3@testerminators.net"));
       Login l;
         l = UsernameDAO.getInstance().getUsername("TeSt");
        System.out.println(l.getPassword());
         l = UsernameDAO.getInstance().getUsername("TeSter");
        System.out.println(l.getPassword());
        System.out.println("Username added successfully/or other things completed..");
    }

}