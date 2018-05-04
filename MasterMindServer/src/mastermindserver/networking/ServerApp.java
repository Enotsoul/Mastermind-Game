/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermindserver.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import mastermind.data.Game;
import mastermind.data.Lobby;
import mastermind.data.OnlinePlayer;
import mastermindserver.MasterMindServerView;
import mastermindserver.database.UsernameDAO;

/**
 *
 * @author Bram
 */
public class ServerApp implements Runnable {

    private ServerSocket servSock;
    private int PORT;

    private boolean statusServer;

    //pool
    private ExecutorService pool;
    private ArrayList<ClientHandler> handlers = new ArrayList<ClientHandler>();
    //List with games on the server...
    private ArrayList<Game> activeGames = new ArrayList<Game>();
    
    //form
    private MasterMindServerView frmServer;

    public void run() {
        startServer();
        while (statusServer)
        {
            acceptClient();
        }
    }

    public ServerApp(int PORT,MasterMindServerView frmServer) {
        this.PORT = PORT;
        this.frmServer = frmServer;
    }

    public void startServer()
    {
        try {

            servSock = new ServerSocket(PORT);
            statusServer=true;
            //thread pool
            pool = Executors.newCachedThreadPool();
            frmServer.printMessage("Server started");
        } catch (IOException ex) {
            Logger.getLogger(ServerApp.class.getName()).log(Level.SEVERE, null, ex);
            frmServer.printMessage("Error starting the server!");
        }
    }

    public void stopServer()
    {
        try {
            frmServer.printMessage("Stopping server, please wait...");
            statusServer = false;
            if (pool != null) {
                pool.shutdownNow();


            }
            for (ClientHandler i : handlers) {
                    i.stopConnection();
                }
            handlers.clear();
            servSock.close();
            servSock = null;
            frmServer.printMessage("Server stopped succesfully");
        } catch (IOException ex) {
            Logger.getLogger(ServerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void acceptClient()
    {
        try {
            Socket link = servSock.accept();
            frmServer.printMessage("Client connected: " + link.getInetAddress().toString());

            ClientHandler clh = new ClientHandler(link,frmServer,this);

            pool.execute(new Thread(clh));
            handlers.add(clh);
        } catch (IOException ex) {
            if (statusServer)
            {
                frmServer.printMessage("Connection with client failed!");
            }
        }
    }

    public int getPORT() {
        return PORT;
    }

    public ArrayList<ClientHandler> getHandlers() {
        return handlers;
    }
    
    //Return the list of online players
    //this is usually smaller than the handlers one and is correct
    //because the handlers might have a connection that was a register or fail to login
   
    public Lobby getOnlineHandlers() {
        ArrayList<OnlinePlayer> onlinePlayers = new  ArrayList<OnlinePlayer>();
        for (ClientHandler clh : handlers)  {
            if (clh.isOnline()) {
                OnlinePlayer op = new OnlinePlayer(true);
                op.setInGame(clh.isInGame());
                op.setUsername(clh.getUsername());
                onlinePlayers.add(op);
            }
        }

        return new Lobby(onlinePlayers);
    }
    

    //Send message to other Client Handler
    public void sendObjectToUser(String username,Object obj) {
         for (ClientHandler clh : handlers) {
            if (clh.getUsername().toLowerCase().equals(username.toLowerCase())) {
                clh.sendObject(obj);
                break;
            }
        }

    }
    public boolean isStatusServer() {
        return statusServer;
      
    }
    //Update the client Handler of the current user with new data
    public void updateClientHandler(ClientHandler c,ClientHandler newC) {
           int index = handlers.indexOf(c);
           handlers.set(index, newC);
    }
    public void removeClientHandler(ClientHandler c)
    {
        handlers.remove(c);
    }

    //!!!!!!
    //TODO Add new update client handler on the basis of the OnlinePlayer OBJECT!!!!!!!
    //!!!!!!
    //Update online player set the OnlinePLayer object in the clienthaldner
    public void updateOnlinePlayer(String username, Object obj) {
        for (ClientHandler ch : handlers) {
            if (ch.getUsername().toLowerCase().equals(username.toLowerCase())) {
                OnlinePlayer op = (OnlinePlayer) obj;
                ch.setInGame(op.isInGame());
                ch.setIsOnline(op.isOnline());
                ch.setUserID(op.getUserID());
                ch.setUsername(op.getUsername());
                
                break;
            }
        }
    }
    //Get the information for ONE online user
    public OnlinePlayer getOnlinePlayer(String username) {
          //\\ ArrayList<OnlinePlayer> onlinePlayers = getOnlineHandlers().getOnlinePlayers();
         //``\\User is not online (used for double control)
         OnlinePlayer op = new OnlinePlayer(false);

         for (ClientHandler clh : handlers) {
            if (clh.getUsername().toLowerCase().equals(username.toLowerCase())) {
                op = (OnlinePlayer) clh;
                break;
            }
        }
        return op;
    }
        public OnlinePlayer getOnlinePlayerById(int userID) {
         OnlinePlayer op = new OnlinePlayer(false);

         for (ClientHandler clh : handlers) {
            if (clh.getUserID() == userID) {
                op = (OnlinePlayer) clh;
                break;
            }
        }
        return op;
    }
    //update the lobby when a new user joins,quits or changes state from in a game to just lobby
    public void sendLobbyToAll() {
        Lobby lobby = getOnlineHandlers();
        if (handlers.size()>0) {
            for (ClientHandler ch : handlers) {
                try {
                    ch.sendLobby(lobby);
                } catch (IOException ex) {
                    Logger.getLogger(ServerApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
         frmServer.fillLobby(lobby); //update list with every online user for the server
    }

    public void banPlayer (OnlinePlayer pl) {
        //TODO implement message.. "player has been kicked" & send it to everyone:D
        for (ClientHandler ch : handlers) {
            if (ch.getUsername().toLowerCase().equals(pl.getUsername().toLowerCase())) {
                ch.kickFromServer();
                 handlers.remove(ch);//remove
                 sendLobbyToAll(); // update lobby after player has been kicked
                 try {
                    UsernameDAO.getInstance().banUsername(pl.getUsername(), true);
                } catch (SQLException ex) {
                  frmServer.printMessage("SQL Error while trying to ban user! Does he have super powers? He's been disconnected anyway..");
                  ch.stopConnection();
                }
                break;
            }
        }
       // sendLobbyToAll(); // update lobby after player has been kicked
    }
    public void kickPlayer(OnlinePlayer pl) {
        //TODO implement message.. "player has been kicked" & send it to everyone:D
        for (ClientHandler ch : handlers) {
            if (ch.getUsername().toLowerCase().equals(pl.getUsername().toLowerCase())) {
                ch.kickFromServer();
                handlers.remove(ch);//remove
                sendLobbyToAll(); // update lobby after player has been kicked
                break;
            }
        }
    }

 
    /*Games Handling
     *
     */
    public ArrayList<Game> getActiveGames() {
        return activeGames;
    }
    //Add a game to a list
    public void addGameToList(Game g) {
        activeGames.add(g);
    }
    public void removeGameFromList(Game g) {
        activeGames.remove(g);
    }
    //remove the game from the online list
    public void removeGameFromList(int gameID) {
        for (Game g : activeGames) {
            if (g.getGameID() == gameID) {
               activeGames.remove(g);
                break;
            }
        }
    }
    //Update Game 
    public void updateGame(int gameID,Game newGame) {
     //      int index = activeGames.indexOf(game);
     //    activeGames.set(index, newGame);
        for (Game g : activeGames) {
            if (g.getGameID() == gameID) {
                g = newGame;
                break;
            }
        }
    }
    //Return the game from the list..
    public Game getGameFromList(int gameID) {
        Game game = null;
         for (Game g : activeGames) {
            if (g.getGameID() == gameID) {
                game = g;
                break;
            }
        }
         return game;
    }
    public void setActiveGames(ArrayList<Game> activeGames) {
        this.activeGames = activeGames;
    }
}
