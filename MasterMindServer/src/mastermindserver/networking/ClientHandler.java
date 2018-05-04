/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermindserver.networking;

import com.sun.org.apache.xpath.internal.axes.OneStepIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.spi.DirStateFactory.Result;
import mastermind.data.Game;
import mastermind.data.GameEnd;
import mastermind.data.GameMove;
import mastermind.data.GameResult;
import mastermind.data.Invite;
import mastermind.data.Lobby;
import mastermind.data.Login;
import mastermind.data.MiscFunctions;
import mastermind.data.OnlinePlayer;
import mastermind.data.Register;
import mastermind.data.WelcomeOnline;
import mastermind.logic.MasterMindGame;
import mastermindserver.MasterMindServerView;
import mastermindserver.database.GameDAO;
import mastermindserver.database.UsernameDAO;

/**
 *
 * @author Bram
 */
class ClientHandler extends OnlinePlayer  implements Runnable {

    private Socket link;
    private ObjectInputStream istream;
    private ObjectOutputStream ostream;
    private boolean statusClientHandler = false;
    private MasterMindServerView frm;
    private ServerApp server;
    
    public ClientHandler(Socket link, MasterMindServerView frm, ServerApp server) {
        //The player is not online
        super(false);
        this.link = link;
        this.frm = frm;
        this.server = server;
    }

    public void run() {
        try {

            frm.printMessage("Clienthandler active");
            statusClientHandler = true;

            istream = new ObjectInputStream(link.getInputStream());
            ostream = new ObjectOutputStream(link.getOutputStream());

            ostream.writeObject("Hello");
            ostream.flush();
            
            while (statusClientHandler)
            {
                Object o = istream.readObject();
                String s = o.toString();
                frm.printMessage("Packet received:" + s);
                if (s.equals("CloseClient"))
                {
                    statusClientHandler=false;
                    ostream.writeObject("CloseClientOK");
                    ostream.flush();
                    server.removeClientHandler(this);
                }
                else if (s.equals("CloseServerOK"))  {
                    statusClientHandler=false;
                } else if (o instanceof Login)  {
                    //If no error occurs then send the WelcomeOnline message
                    boolean isOk = true;
                    boolean dbError= false;
                    String message = "WelcomeOnline";
                    Login l = (Login) o;
                    Login lFromServer = new Login("x", "x","x");
                  //   "InexistentUsername" "WrongPassword" WelcomeOnline
                    try {
                        lFromServer = UsernameDAO.getInstance().getUsername(l.getUsername());
                    } catch (SQLException ex) {
                        dbError = true;
                        frm.printMessage("A SQL error occured.." + ex.getMessage());
                        stopConnection();
                    }
                    
                   if (!dbError) {
                        //the username isn't case sensitive:)
                        if (!l.getUsername().toLowerCase().equals(lFromServer.getUsername().toLowerCase())) {
                            isOk= false;
                            message = "InexistentUsername";
                        } else {
                            if (!l.getPassword().equals(lFromServer.getPassword())) {
                                isOk= false;
                                message = "WrongPassword";
                            }
                            if (lFromServer.getBanned() == 1) {
                                isOk= false;
                                message = "YourBanned";
                            }
                        }
                    
                        if (isOk) {
                            //handle login
                            //set everything to be correct..
                            this.setIsOnline(true);
                            this.setUsername(lFromServer.getUsername());
                            this.setUserID(lFromServer.getUserID()); // very important for the game..
                          //  server.updateClientHandler(oldThis, this);
                          //  server.updateOnlinePlayer(lFromServer.getUsername(), this);

                           /*
                          if (this.getUsername().equals("andrei")) {
                    
                            OnlinePlayer oplayer1 = server.getOnlinePlayer("lostone");
                            oplayer1.setInGame(true);
 
                            server.updateOnlinePlayer("lostone",oplayer1);

                          }*/
                            frm.printMessage(l.getUsername() + " has logged in! (userID: " + this.getUserID() + " )" );
                            server.sendLobbyToAll();// send all users a new version of their lobby:D

                            //Send the OnlinePlayer object to the Client so he has it to send it back
                            OnlinePlayer onlineClient = new OnlinePlayer(true);
                            onlineClient.setUserID(this.getUserID());
                            onlineClient.setUsername(this.getUsername());
                            onlineClient.setInGame(this.isInGame());
                            ostream.writeObject(new WelcomeOnline(onlineClient));
                            ostream.flush();
                          } else  {
                            frm.printMessage(l.getUsername() + " failed to login: " + message );
                        }

                       //send the message
                       ostream.writeObject(message);
                       ostream.flush();
                    }
                } else if (o instanceof Register)  {
                    boolean isOk = true;
                    boolean dbError= false;
                    String message = "RegisterOK"; 
                    Register r = (Register) o;
                    Login l = new Login("x", "x","x");

                    try {
                       l = UsernameDAO.getInstance().getUsername(r.getUsername());
                    } catch (SQLException ex) {
                        dbError = true;
                        isOk= false;
                        frm.printMessage("A SQL error occured.." + ex.getMessage());
                        stopConnection();
                    }
                    
                    if (!dbError) {
                        //check if username already exists
                        if (!l.getUsername().equals("inexistent")) {
                            message = "UserAlreadyExists";
                            isOk= false;
                        }
                       //TODO maybe check if email is the same..?
                    }
                    if (isOk) {
                        try {
                            UsernameDAO.getInstance().newUser(r);
                        } catch (SQLException ex) {
                            dbError = true;
                            frm.printMessage("A SQL error occured.." + ex.getMessage());
                            stopConnection();
                        }
                
                       frm.printMessage(r.getUsername() + "is now registered!" );
                    } else  {
                         frm.printMessage(r.getUsername() + "failed to register: " + message );
                    }
                    
                   //send the message
                   if (!dbError) {
                        ostream.writeObject(message);
                        ostream.flush();
                   }
                } else if (s.equals("RequestLobby")) {
                    //return the lisf of online players.. 
                    //this updates itself really often
                     sendLobby(server.getOnlineHandlers());
               } 
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
                else if (o instanceof Invite) {
                    Invite inv = (Invite) o;
                    String user = "";
                    boolean isOk = true;
                    boolean dbError = false;
                    
                    frm.printMessage("Received Invite object from " + inv.getPlayer1() +
                            " with status "  + inv.getStatus() + " destined for " + inv.getPlayer2() +
                            " Is Accepted? " +inv.isAccepted() );
                    //Get the players online information
                    OnlinePlayer player1 = server.getOnlinePlayer(inv.getPlayer1());
                    OnlinePlayer player2 = server.getOnlinePlayer(inv.getPlayer2());

                    //Check if user is ONLINE(exists) 
                    //and do the rest of the things Here
                    if (player2.isOnline() && !inv.getStatus().equals("ReviewedInvite")) {
                        //player2 is already in a game.. don't bother
                        if (player2.isInGame()) {
                             inv.setStatus("UserIsInGame");
                             user = player1.getUsername();
                             isOk = false;
                        }
                        if (isOk) {
                            user = player2.getUsername();
                        }
                    } else if (player2.isOnline() && inv.getStatus().equals("ReviewedInvite")) {
                        //handle if the player accepted the request or not..
                         user = player1.getUsername();
                        if (inv.isAccepted()) {
                            inv.setStatus("PlayerAcceptedRequest");
                            //At this point both players get the LoadingView at the client side
                              //and await further instructions from the server
                        } else {
                            inv.setStatus("PlayerRejectedRequest");
                        }
                    }   else {
                        // User isn't online (maybe inexistent..?) sned invite back to Player1
                        inv.setStatus("PlayerNotOnline");
                        user = player1.getUsername();
                    }

                    //send the correct object to the correct user:)
                   server.sendObjectToUser(user,inv);

                   //Send the GameObject to both users if the invite was TRULY accepted
                   //do this after the normal object
                   if (inv.isAccepted() && inv.getStatus().equals("PlayerAcceptedRequest")) {
                      // convert true to 1 and false to 0
                     int gameType = inv.isServerCombination() ? 1 : 0; 
                     Game game = new Game(player1.getUserID(), player2.getUserID(), gameType, MiscFunctions.unixtime());
                     int gameID = 0;

                     //New random guess:D
                      if (gameType == 1) {
                          MasterMindGame mg = new MasterMindGame();
                          mg.newGuess();
                          String solution = mg.getToGuess();
                          
                          game.setPlayer1Solution(solution);
                          game.setPlayer2Solution(solution);
                     }
                     
                    try {
                        // create the new game in the database!
                       gameID  = GameDAO.getInstance().newGame(game);
                    } catch (SQLException ex) {
                        dbError = true;
                        isOk= false;
                        frm.printMessage("A SQL error occured.." + ex.getMessage());
                        stopConnection(); // this will disconnect the client from the server
                    }

                     if (!dbError) {
                        // set the unique game id to identify it for later
                          game.setGameID(gameID);
                                   
                        //Set the players as inGame
                        OnlinePlayer oplayer1 = server.getOnlinePlayer(player1.getUsername());
                        OnlinePlayer oplayer2 = server.getOnlinePlayer(player2.getUsername());

                        oplayer1.setInGame(true);
                        oplayer2.setInGame(true);
                        
                        //update the info...
                        server.updateOnlinePlayer(player1.getUsername(),oplayer1);
                        server.updateOnlinePlayer(player2.getUsername(),oplayer2);

                        //Add the game to the gamelist!
                         server.addGameToList(game);
                        
                         //Send the Game objects to the users
                         server.sendObjectToUser(player1.getUsername(),game);
                         server.sendObjectToUser(player2.getUsername(),game);
                        }
                    }
                } else if (o instanceof Game) {
                    Game game = (Game) o;
                    frm.printMessage("Recieved GAME object from user..");
    /* Game object implementation
     * Server sends the Game Object to player1 & player2
     * If gameType = 1 (random generation) then they both can start guessing the answer
     *=> server neeeds to recieve game objects for the below things
     * if gameType = 0 (each player choses a string)
            then the players must wait untill the server has both solutions filled :)
     * Then the server sends another Game object with the status of "GameMayStart" to both users
     * Then the game is started.. and each player may send a GameMove object to the server..
     */
                 //Check gametype: if players can chose string
                 if (game.getGameType() == 0) {
                     Game serverGame = server.getGameFromList(game.getGameID());
                  //  server.removeGameFromList(serverGame); // Remove the game.. to add the new one
                    
                    //Get the players online information
                    OnlinePlayer player1 = server.getOnlinePlayerById(game.getPlayer1ID());
                    OnlinePlayer player2 = server.getOnlinePlayerById(game.getPlayer2ID());
                    
                    //Add the string to the gameObject 
                    if (this.getUserID() == game.getPlayer1ID()) {
                        serverGame.setPlayer1Solution(game.getPlayer1Solution());
                    } else if (this.getUserID() == game.getPlayer2ID()) {
                        serverGame.setPlayer2Solution(game.getPlayer2Solution());
                    }

                    //Ok both users set the solution.. Send the new gameObject.. to start the game:)
                    if (!serverGame.getPlayer1Solution().equals("") &&  !serverGame.getPlayer1Solution().equals("")) {
                        game.setStatus("GameMayStart");
                        serverGame.setStatus("GameMayStart");
                        
                        //Send the same packet we received back to the user:)
                        server.sendObjectToUser(player1.getUsername(),game);
                        server.sendObjectToUser(player2.getUsername(),game);
                    }
                    //Update the game with the changes made
                    server.updateGame(serverGame.getGameID(),serverGame);
                 }
                   
                } else if (o instanceof GameMove) {
                    //Control if the GameMove object is correct & send back information
                    //in a Result object to the server.. handle win/lose messages here..
                    //Set the object
                    GameMove gm = (GameMove) o;
                    //Get the game...
                    Game game = server.getGameFromList(gm.getGameID());

                   // Get the current online player..
                    OnlinePlayer player1 = server.getOnlinePlayerById(gm.getPlayerID());
                    OnlinePlayer player2 = new OnlinePlayer(true);
                    
                    //get the other player so he's correct & switch otherwise
                    if (player1.getUserID() == game.getPlayer1ID()) {
                        player2 = server.getOnlinePlayerById(game.getPlayer2ID());
                    } else {
                        player2 = server.getOnlinePlayerById(game.getPlayer1ID());
                    }
                 
                  //  OnlinePlayer player2 = server.getOnlinePlayer(game.getPlayer2ID());
                      //!!!!
                     //maybe later do extra checking like
                    //if the userID in the gameMove exists in the game with that GameID..
                    
                    //if the server chosed the combination
                    if (game.getGameType() == 1) {
                        //The player solutions are the same anyway
                        handleMultiPlayerMove(game, gm, game.getPlayer1Solution(), player1, player2);
                        
                        //if the players chosed.. AND the gameStatus is GameMayStart so that the players can't cheat:)
                    } else if (game.getGameType() == 0 && game.getStatus().equals("GameMayStart")) {
                        
                        //Check which player he is & switch between options
                        if (game.getPlayer1ID() == player1.getUserID()) {
                            //The second solution is the one he searches!
                            handleMultiPlayerMove(game, gm, game.getPlayer2Solution(), player1, player2);

                        } else {
                            //This is the second player.. First solution is what he searches
                            //And change the location of the OnlinePlayer objects:)
                            handleMultiPlayerMove(game, gm, game.getPlayer1Solution(),  player2,player1);
                        }
                    }         
                }  else if (s.equals("GiveUp"))  {
                    //The player gave up.. too bad HE LOST!
                    //The 2d player is the loser since he quit..
                    OnlinePlayer player2 = server.getOnlinePlayer(this.getUsername());

                    //Get the game from the server
                    Game game = server.getGameFromList(player2.getUserID());

                    //Get the first player.. the winner who got
                    //lucky just because he  didn't quit:P
                    OnlinePlayer player1 = new OnlinePlayer(true);
                    if (player2.getUserID() == game.getPlayer1ID()) {
                        player1 = server.getOnlinePlayerById(game.getPlayer2ID());
                    } else {
                        player1 = server.getOnlinePlayerById(game.getPlayer1ID());
                    }

                    //set the time and the winnerID
                    game.setGameEndTime(MiscFunctions.unixtime());
                    game.setWinnerID(player1.getUserID());

                    //The total seconds that the game lasted
                    long gameTime = game.getGameEndTime() - game.getGameStartTime()/100;

                    //Set both of the users OUT of a game & update them
                    player1.setInGame(false);
                    player2.setInGame(false);
                    server.updateOnlinePlayer(player1.getUsername(),player1);
                    server.updateOnlinePlayer(player2.getUsername(),player2);

                    //GameEnd status (YouWin - YouLose - OtherQuit)
                    // Send GameEnd to the winner
                    GameEnd geWin = new GameEnd(player1.getUsername(), "OtherQuit", "" + gameTime);
                    server.sendObjectToUser(player1.getUsername(),geWin);

                    //Send GameEnd to the loser and not sure if we should send any
                    //because the player quit anyway..?
                    GameEnd geLose = new GameEnd(player2.getUsername(), "YouLose", "" + gameTime);
                    server.sendObjectToUser(player2.getUsername(),geLose);

                    //Remove the game from the online list..
                    server.removeGameFromList(game);

                    //Add Game info to the Database
                    try {
                        GameDAO.getInstance().updateGame(game);
                    } catch (SQLException ex) {
                        System.out.println("A SQL db error occured:\n" + ex);
                        stopConnection(); // this will disconnect the client from the server
                    }
                }
                /*
                else if (o instanceof IETS)
                {
                    frm.printMessage("Object is een IETS object");
                    VERWERK OBJECT HIER
                    frm.printMessage("Object verwerkt en teruggestuurd");
                }
                */
                else   {
                    frm.printMessage("Invalid packet received: " + o.toString());
                }

            }
            frm.printMessage("Closing connection with client: " + link.getInetAddress().toString());

        } catch (Exception ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            try {
                link.close();
                //this is the best place to send everything to everyone.. 
                server.sendLobbyToAll();
              } catch (IOException ex) {
                frm.printMessage("Closing the connection failed!");
            }
        }
    }

   //This function handles the multiplayer move..it's used a few times
   public void handleMultiPlayerMove(Game game,GameMove gm,String solution,OnlinePlayer player1,OnlinePlayer player2) {
         //New mastermind game object..
          MasterMindGame mmg  = new MasterMindGame();

          //set the toguess.. the solution..
          mmg.setToGuess(solution);
          String pins = mmg.returnPins(gm.getPlayerMove());

          //remember to check the position.. if exceding 9.. you lose
          //since the values are zero-based there are 10 rows:)
          if (gm.getPosition() >= 9) {
              //You Lose because you exceeded the 10 tries limit:(
              handleWin(game, gm, player2,player1);
          } else {
            //Add the GameMove to the Game
            game.addMove(gm);

            //The player won!
            if (pins.equals("zzzz")) {
                handleWin(game, gm, player1, player2);
            } else {
                //The player didn't win so we send him a GameResult back
                GameResult gr = new GameResult(gm.getPlayerMove(),pins);

                //Update the game on the server
                server.updateGame(game.getGameID(), game);

                //send the object to the user..
                server.sendObjectToUser(player1.getUsername(),gr);
                try {
                    //Add the GameMove to the DB
                    GameDAO.getInstance().addGameMove(gm);
                } catch (SQLException ex) {
                    System.out.println("A SQL db error occured:\n" + ex);
                    stopConnection(); // this will disconnect the client from the server
                }
            }
        }
    }
    
    public void handleWin(Game game, GameMove gm,OnlinePlayer player1, OnlinePlayer player2) {
      //set the time and the winnerID
        game.setGameEndTime(MiscFunctions.unixtime());
        game.setWinnerID(gm.getPlayerID());

        //The total seconds that the game lasted
        long gameTime = game.getGameEndTime() - game.getGameStartTime()/100;

        //Set both of the users OUT of a game & update them
        player1.setInGame(false);
        player2.setInGame(false);
        server.updateOnlinePlayer(player1.getUsername(),player1);
        server.updateOnlinePlayer(player2.getUsername(),player2);
        
        //GameEnd status (YouWin - YouLose - OtherQuit)
        // Send GameEnd to the winner
        GameEnd geWin = new GameEnd(player1.getUsername(), "YouWin", "" + gameTime);
        server.sendObjectToUser(player1.getUsername(),geWin);

        //Send GameEnd to the loser
        GameEnd geLose = new GameEnd(player2.getUsername(), "YouLose", "" + gameTime);
        server.sendObjectToUser(player2.getUsername(),geLose);

        //Remove the game from the online list..
        server.removeGameFromList(game);

        //Add Game & GameMove to DB
        try {
            GameDAO.getInstance().addGameMove(gm);
            GameDAO.getInstance().updateGame(game);

        } catch (SQLException ex) {
            System.out.println("A SQL db error occured:\n" + ex);
            stopConnection(); // this will disconnect the client from the server
        }
    }

    public void stopConnection() {
        if (!link.isClosed())
        {
            try {
                statusClientHandler = false;
                ostream.writeObject("CloseServer");
                ostream.flush();
            } catch (IOException ex) {
                frm.printMessage("Failed closing a clienthandler!");
            }
        }
    }
    
    //kick player from server with a different Protocol so the client knows he was kicked
    //use this for banning too
    public void kickFromServer() {
        if (!link.isClosed())
        {
            try {
                statusClientHandler = false;
                ostream.writeObject("KickedFromServer");
                ostream.flush();
            } catch (IOException ex) {
                frm.printMessage("Failed closing a clienthandler!");
            }
        }
    }
    //Individual lobby
    public void sendLobby(Lobby onlineHandlers) throws IOException {
        ostream.writeObject(onlineHandlers);
        ostream.flush();
    }

    //Sending objects to other handlers...
    public void sendObject(Object obj)  {
        try {
            ostream.writeObject(obj);
            ostream.flush(); //no cache
            ostream.reset(); //no cache
        } catch (IOException ex) {
            frm.printMessage("Error while trying to use sendObject");
        }
    }
   public void sendObject(ClientHandler clh, Object obj) throws IOException {
          clh.ostream.writeObject(obj);
          clh.ostream.flush(); // send it
          clh.ostream.reset(); //no cache
    }
}
