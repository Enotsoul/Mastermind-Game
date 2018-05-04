/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mastermind.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import mastermind.data.Game;
import mastermind.data.GameEnd;
import mastermind.data.GameMove;
import mastermind.data.GameResult;
import mastermind.data.Invite;
import mastermind.data.Lobby;
import mastermind.data.Login;
import mastermind.data.OnlinePlayer;
import mastermind.data.Register;
import mastermind.data.WelcomeOnline;
import mastermind.events.request.RequestEvent;
import mastermind.events.request.RequestEventListener;

/**
 *
 * @author Bram
 */
public class ClientApp {

    private InetAddress host;
    private int port;
    private Socket link = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    //The OnlinePlayer object used by the client..
    public static OnlinePlayer me;
    //The current game you are in.. contains the info of the game needed by
    //objects like GameMove & Result 
    public static Game currentGame;
    
    //constructoren
    public ClientApp(InetAddress host, int port)
    {
        this.host = host;
        this.port = port;
    }

    public ClientApp(String sIP, int port) throws UnknownHostException
    {
        this(InetAddress.getByName(sIP), port);
    }

    public void makeConnection() throws IOException{
        if (link == null)
        {
            System.out.println("Connecting to server");

            link = new Socket(host,port);

            out = new ObjectOutputStream(link.getOutputStream());
            in = new ObjectInputStream(link.getInputStream());

            AcceptMessageHandler amh = new AcceptMessageHandler();
            Thread t = new Thread(amh);
            t.start();
        }
    }

    public boolean isConnected() {
        return link.isClosed();
    }

    public void closeConnection() throws IOException
    {
        if (link != null)
        {
            if (!link.isClosed())
            {
                out.writeObject("CloseClient"); //zend close naar client
            }
        }
    }

    public void login(Login l) throws IOException {
        if (link!=null)    {
            if (!link.isClosed())  {
                out.writeObject(l);
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }

    //Handle register..
    public void register(Register r) throws IOException   {
        if (link!=null)  {

            if (!link.isClosed()) {    
                out.writeObject(r);
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }
    //request the lobby..
    public void requestLobby() throws IOException {
        if (link!=null)  {

            if (!link.isClosed())  {
                out.writeObject("RequestLobby");
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }
    
    //Handle the invite request sending
    public void sendInvite(Invite i) throws IOException {
          if (link!=null)   {
            if (!link.isClosed())   {
                out.writeObject(i);
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }
    //Send the game object back to the server
    public void sendGame(Game g) throws IOException {
         if (link!=null)   {
            if (!link.isClosed())   {
                out.writeObject(g);
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }
    //Send game move..
    public void sendGameMove(GameMove gm) throws IOException {
        if (link!=null)   {
            if (!link.isClosed())   {
                out.writeObject(gm);
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }
    //Give up in a cowardly manner.. just quit
    public void giveUp() throws IOException {
        if (link!=null)   {
            if (!link.isClosed())   {
                out.writeObject("GiveUp");
                out.flush(); // send it
                out.reset(); //no cache
            }
        }
    }
    private void closeConnectionFromServer() throws IOException
    {
        if (link != null)
        {
            out.writeObject("CloseServerOK");
            out.close();
            in.close();
            link.close();
            System.out.println("Connection closed by the server");
            fireRequestEvent(new RequestEvent(this, "disconnected"));
        }
    }

    private void closeConnectionFromClient() throws IOException
    {
        if (link != null)
        {
            out.close();
            in.close();
            link.close();
            System.out.println("Connection closed by this client");
        }
    }



    //Client's OnlinePlayer object
    public static OnlinePlayer getMe() {
        return me;
    }

    public static void setMe(OnlinePlayer me) {
        ClientApp.me = me;
    }

    //Game object:)
    public static Game getCurrentGame() {
        return currentGame;
    }

    public static void setCurrentGame(Game currentGame) {
        ClientApp.currentGame = currentGame;
    }


     //
    //inner class
    public class AcceptMessageHandler implements Runnable
    {
        private boolean statusAcceptMessageHandler=false;

        public void run()
        {
            while (statusAcceptMessageHandler)
            {
                try {
                    Object o = in.readObject();
                    String s = o.toString();
                    System.out.println("Packet received: " + s);

                    if (s.startsWith("CloseServer"))
                    {
                        statusAcceptMessageHandler=false;
                        closeConnectionFromServer();
                    }
                    else if (s.startsWith("CloseClientOK")) {
                        statusAcceptMessageHandler=false;
                        closeConnectionFromClient();
                    }  else if (s.startsWith("Hello"))      {
                        System.out.println("Server said Hello");
                    }  else if (s.startsWith("RegisterOK"))  {
                        System.out.println("Register OK!");
                        fireRequestEvent(new RequestEvent(this, "regi:registerOK"));
                    }  else if (s.startsWith("UserAlreadyExists"))    {
                        System.out.println("UserAlreadyExists");
                        fireRequestEvent(new RequestEvent(this, "regi:existentUser"));
                    }  else if (s.startsWith("InexistentUsername"))   {
                        System.out.println("InexistentUsername");
                        fireRequestEvent(new RequestEvent(this, "regi:InexistentUsername"));
                    }  else if (s.startsWith("WrongPassword")) {
                        System.out.println("UserAlreadyExists");
                        fireRequestEvent(new RequestEvent(this, "regi:WrongPassword"));
                    } else if (s.startsWith("YourBanned"))   {
                        System.out.println("You are banned, not welcome here!");
                        fireRequestEvent(new RequestEvent(this, "regi:YourBanned"));
                    }   else if (s.startsWith("KickedFromServer"))   {
                        System.out.println("You've been kicked from the server! naughty naughty!");
                        fireRequestEvent(new RequestEvent(this, "KickedFromServer"));
                    }else if (o instanceof WelcomeOnline)   {
                        WelcomeOnline wo = (WelcomeOnline) o;
                        setMe(wo.getMe());
                        System.out.println("Welcome Online my friend " + me.getUsername() +"(" + me.getUserID() + ")");
                        fireRequestEvent(new RequestEvent(this, "conn:WelcomeOnline"));
                    } else  if (o instanceof Lobby)   {

                        System.out.println("Refreshing lobby..?");
                        Lobby lobby = (Lobby) o;
                       //send object
                        fireRequestEvent(new RequestEvent(this, "show:lobby",lobby));
             
                    }  else  if (o instanceof Invite)   {                    
                        Invite invite = (Invite) o;
                        System.out.println("Got Invite Object");

                        //send the object to the client to handle it
                        fireRequestEvent(new RequestEvent(this, "show:invite",invite));
                        
                    } else  if (o instanceof Game)   {
                        Game game = (Game) o;
                        System.out.println("Got a Game Object");

                        //update the info we have about the current game:D
                        setCurrentGame(game);
                        
                        //send the object to the client to handle it
                        fireRequestEvent(new RequestEvent(this, "show:game",game));

                    } else if (o instanceof GameResult)   {
                        GameResult gr = (GameResult) o;
                        System.out.println("Got a Game Result Object");
                                          
                        //send the object to the client/view to handle it
                        fireRequestEvent(new RequestEvent(this, "show:gameResult",gr));
                        
                    } else if (o instanceof GameEnd)   {
                        GameEnd ge = (GameEnd) o;
                        System.out.println("Got a Game End Object");

                        //send the object to the client/view to handle it
                        fireRequestEvent(new RequestEvent(this, "show:gameEnd",ge));

                    } else  {

                        /* 
                        if (o instanceof IETS)
                        {
                            System.out.println("Packet is a IETS");
                            DO SOMETHING WITH PACKET HERE
                        }
                        else */
                        
                            System.out.println("Invalid packet: " + o.toString());
                        
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ClientApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public AcceptMessageHandler()
        {
            statusAcceptMessageHandler=true;
        }

    }


    //----------------EVENTS--------------------
        // Create the listener list
    protected javax.swing.event.EventListenerList listE =
        new javax.swing.event.EventListenerList();

    public void addRequestListener(RequestEventListener listener) {
        listE.add(RequestEventListener.class, listener);
    }

    public void removeRequestListener(RequestEventListener listener) {
        listE.remove(RequestEventListener.class, listener);
    }

    protected void fireRequestEvent(RequestEvent evt) {
        Object[] listeners = listE.getListenerList();
        // Each listener occupies two elements - the first is the listener class
        // and the second is the listener instance
        for (int i=0; i<listeners.length; i+=2) {
            if (listeners[i]==RequestEventListener.class) {
                ((RequestEventListener)listeners[i+1]).Requested(evt);
            }
        }
    }

}
