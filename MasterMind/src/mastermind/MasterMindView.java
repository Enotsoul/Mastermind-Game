/*
 * MasterMindView.java
 */

package mastermind;

//import org.jdesktop.application.Action;
//import org.jdesktop.application.ResourceMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import mastermind.events.request.RequestEvent;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
//import org.jdesktop.application.TaskMonitor;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import javax.swing.Timer;
//import javax.swing.Icon;
//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import mastermind.controls.BPanel;
import mastermind.data.Game;
import mastermind.data.GameEnd;
import mastermind.data.GameMove;
import mastermind.data.GameResult;
import mastermind.data.Invite;
import mastermind.data.Lobby;
import mastermind.data.Login;
import mastermind.data.OnlinePlayer;
import mastermind.data.Register;
import mastermind.events.request.RequestEventListener;
import mastermind.networking.ClientApp;
import mastermind.panels.PnlLoadingView;
import mastermind.panels.PnlLobbyView;
import mastermind.panels.PnlLoginView;
import mastermind.panels.PnlMainView;
import mastermind.panels.PnlMultiPlayerView;
import mastermind.panels.PnlRegisterView;
import mastermind.panels.PnlSinglePlayerView;

/**
 * The application's main frame.
 */
public class MasterMindView extends FrameView {

    //-------------DECLARATIONS-------------

    //VIEW (panels,frame,...)
    // <editor-fold defaultstate="collapsed" desc="Panels">
    private PnlMainView pnlMainView = new PnlMainView();
    private PnlSinglePlayerView pnlSinglePlayerView = new PnlSinglePlayerView();
    private PnlLoginView pnlLoginView = new PnlLoginView();
    private PnlLobbyView pnlLobbyView = new PnlLobbyView();
    private PnlLoadingView pnlLoadingView = new PnlLoadingView();
    private PnlRegisterView pnlRegisterView = new PnlRegisterView();
    private PnlMultiPlayerView pnlMultiPlayerView = new PnlMultiPlayerView();

    private BPanel currentPanel=pnlMainView;// </editor-fold>
    private JFrame mainFrame;

    //NETWORKING
    private ClientApp client;


    //--------------FUNCTIONS---------------
    public MasterMindView(SingleFrameApplication app) {
        super(app);
        initComponents();
        initApplication();

    }
    
    private void initApplication()
    {
        //make non resizable
        mainFrame = MasterMindView.super.getFrame();
        mainFrame.setResizable(false);

        //add Main panel
        mainFrame.add(pnlMainView);

        //add Requests
        addRequests();

        //repaint the app every 10msec (removes visual bugs)
        javax.swing.Timer t = new javax.swing.Timer(10, new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
              mainFrame.validate();
              mainFrame.repaint();
          }
       });
       t.start();

    }

    private void addRequests()
    {
        pnlMainView.addRequestListener(new RequestHandler());
        pnlSinglePlayerView.addRequestListener(new RequestHandler());
        pnlLoginView.addRequestListener(new RequestHandler());
        pnlLoadingView.addRequestListener(new RequestHandler());
        pnlLobbyView.addRequestListener(new RequestHandler());
        pnlRegisterView.addRequestListener(new RequestHandler());
        pnlMultiPlayerView.addRequestListener(new RequestHandler());
        
    }

    public class RequestHandler implements RequestEventListener
    {
        public void Requested(RequestEvent e) {
            String command = e.getCommand();
            String subCommand = command.substring(5,command.length());
            if (command.startsWith("show:"))
            {
                if (subCommand.equals("singleplayer"))
                {
                    showPanel(pnlSinglePlayerView);
                }
                else if (subCommand.equals("main"))
                {
                    showPanel(pnlMainView);
                }
                else if (subCommand.equals("login"))
                {
                    showPanel(pnlLoginView);

                }
                else if (subCommand.equals("lobby"))
                {
                    //only send it if you are connected
                   if (!client.isConnected()) {
                       showPanel(pnlLobbyView);
                       Lobby lobby = (Lobby) e.getObject();
                       pnlLobbyView.fillLobby(lobby);
                   }
                }
                else if (subCommand.equals("register"))
                {
                    showPanel(pnlRegisterView);
                }
                else if (subCommand.equals("loading"))
                {
                    showPanel(pnlLoadingView);
                }
                else if (subCommand.equals("invite")) {
                    Invite inv = (Invite) e.getObject();
               
                    //Check if other user isn't in game
                    if (inv.getStatus().equals("UserIsInGame")) {
                         JOptionPane.showMessageDialog(null, inv.getPlayer2() + " is currently in a game.");
                    }
                    if (inv.getStatus().equals("PlayerNotOnline")) {
                         JOptionPane.showMessageDialog(null, inv.getPlayer2() + " is not online.");
                    }
                    if (inv.getStatus().equals("PlayerRejectedRequest")) {
                        //player2 = the other player... because the status is back from the server via the user
                         JOptionPane.showMessageDialog(null, inv.getPlayer2() + " has rejected your request for a game.");
                    }
                    //if it's an invite then handle ask the user if he wants to play or not
                    if (inv.getStatus().equals("Invite")) {
                        String[] choices = {"Reject", "Accept"};
                        boolean accepted;
                        
                        //Set the type
                        String type = inv.isServerCombination()
                                ?"\nThe server generates one value which you both must find. "
                                :"\nYou may each chose your own value that the other one must try to find.";
                        //Player1 = the other player :)
                       int answer = JOptionPane.showOptionDialog(null, 
                           "You have been invited to play with " + inv.getPlayer1() + " The game type is: " + type,
                            inv.getPlayer1() + " wants to  play with you.",
                           JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                           null, choices, "Interesting");
                       
                       //Switch to true or false
                        if (answer == 1) {
                            accepted = true;
                        } else { accepted = false; }

                       //Accepted or not!
                        inv.setAccepted(accepted);
                        
                       //You reviewed the Invite object
                       inv.setStatus("ReviewedInvite");
                       
                       //Send theinvite back to the server
                       sendInvite(inv);
                    }

                    //The other player accepted.. 
                    if (inv.getStatus().equals("PlayerAcceptedRequest")) {
                        //Load the loadingview and wait for the Game Object with further instructions..
                       // showPanel(pnlMultiPlayerView);
                       showPanel(pnlLoadingView);
                    } 
                }
                else if (subCommand.equals("game")) {
                     Game game = (Game) e.getObject();

                     //Show the panel
                    showPanel(pnlMultiPlayerView);

                    //Let the multiplayerview handle the if the game may start or
                    //if the player must send the chosen solution
                    pnlMultiPlayerView.startGame(game);
                    
                }  else if (subCommand.equals("gameResult")) {
                    GameResult gr = (GameResult) e.getObject();

                    //Redirect the gameReqult to the panel
                    pnlMultiPlayerView.answerFromServer(gr);
                } else if (subCommand.equals("gameEnd")) {
                    GameEnd ge = (GameEnd) e.getObject();

                    //Redirect the GameEnd object to the panel
                    pnlMultiPlayerView.gameEnded(ge);
                }
            }
            else if (command.startsWith("regi:"))
            {
                if (subCommand.equals("registerplayer"))
                {
                    boolean isOk = true;
                    String errorMessage = "";
                    String username=pnlRegisterView.getUsername();
                    String password=pnlRegisterView.getPassword();
                    String email = pnlRegisterView.getEmail();
                    //Register here
                    //we create a connection..
                    if (username.length() < 3 || password.length() <3 || email.length()  < 3 ) {
                        isOk= false;
                        errorMessage += "You need to fill all fields & they have to be at least 3 characters long!\n";
                    }
                    //TODO email regex..

                    if (isOk)
                    {
                        try
                        {
                            client = new ClientApp("127.0.0.1", 8888);
                            client.addRequestListener(new RequestHandler());
                            client.makeConnection();
                            client.register(new Register(username,password,email));
                          //close connection 
                            client.closeConnection();
                        } catch (IOException ex)
                        {
                            //bij het mislukken van de verbinding, ga terug naar de main view
                            mainFrame.remove(currentPanel);
                            mainFrame.add(pnlMainView);
                            currentPanel=pnlMainView;
                            JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
                        }
                    } 
                    else
                    {
                        JOptionPane.showMessageDialog(null, errorMessage);
                    }
                } 
                else  if (subCommand.equals("registerOK"))
                {
                   JOptionPane.showMessageDialog(null, "Registration successful, you can now login.");
                   showPanel(pnlLoginView);
                }  
                else  if (subCommand.equals("existentUser"))
                {
                    JOptionPane.showMessageDialog(null, "The username you want to register already exists.");
                }   
                else  if (subCommand.equals("WrongPassword"))
                {
                    JOptionPane.showMessageDialog(null, "Wrong password for this username.");
                    try
                    {
                        //close connection cause we didn't login!
                        client.closeConnection();
                    } catch (IOException ex)
                    {
                        Logger.getLogger(MasterMindView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }  
                else  if (subCommand.equals("InexistentUsername"))
                {
                    JOptionPane.showMessageDialog(null, "That username doesn't exist.");
                    try
                    {
                        //close connection cause we didn't login!
                        client.closeConnection();
                    } catch (IOException ex) {
                        Logger.getLogger(MasterMindView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } 
                else  if (subCommand.equals("YourBanned"))
                {
                    JOptionPane.showMessageDialog(null, "You are banned from the server and can't login, sorry!");
                    try
                    {
                        //close connection cause we didn't login!
                        client.closeConnection();
                    } catch (IOException ex)
                    {
                        Logger.getLogger(MasterMindView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }  
            else if (command.startsWith("KickedFromServer"))
            {
                showPanel(pnlMainView);
                JOptionPane.showMessageDialog(null, "You where kicked from the server!");
            } 
            else if (command.startsWith("conn:"))
            {
                if (subCommand.equals("login"))
                {
                    String username = pnlLoginView.getUserName();
                    String password = pnlLoginView.getPassword();
                    boolean isOk = true;
                    String errorMessage = "";

                    if (username.length() < 3 || password.length() <3 ) {
                        isOk= false;
                        errorMessage += "You need to fill all fields & they have to be at least 3 characters long!\n";
                    }
                    if (isOk)
                    {
                        //connect
                        try {
                            client = new ClientApp("127.0.0.1", 8888);
                            client.addRequestListener(new RequestHandler());
                            client.makeConnection();
                            client.login(new Login(username,password,""));
                           //don't close the connection here.. wait till it's ok:)
                        } catch (IOException ex) {
                            //bij het mislukken van de verbinding, ga terug naar de main view
                            showPanel(pnlMainView);
                            JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
                        }
                    } else  {
                        JOptionPane.showMessageDialog(null, errorMessage);
                    }

                }   
                else  if (subCommand.equals("WelcomeOnline"))
                {
                    showPanel(pnlLoadingView);
                    //handle things to do when LOGIN is ok..
                    //RequestLobby from server
                    try
                    {
                        client.requestLobby();
                    }
                    catch (IOException ex)
                    {
                        //bij het mislukken van de verbinding, ga terug naar de main view
                        showPanel(pnlMainView);
                        JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
                    }
                }
                else if (subCommand.equals("logout"))
                {
                    //logout from game
                    try {
                         client.closeConnection();
                    } catch (Exception ex) {
                        System.out.println("couldn't close connection..");
                    }
                }
            } else if (command.startsWith("disconnected")) {
                 JOptionPane.showMessageDialog(null, "You have been disconnected from the server:).");
              //Send commands to server:)
            } else if (command.startsWith("send:")) {
                 if (subCommand.equals("invite"))  {
                    Invite i = (Invite) e.getObject();
                    sendInvite(i);
                } else if (subCommand.equals("game"))  {
                    Game g = (Game) e.getObject();
                    sendGame(g);
                } else if (subCommand.equals("gameMove"))  {
                    GameMove gm = (GameMove) e.getObject();
                    sendGameMove(gm);
                }else if (subCommand.equals("giveUp"))  {
                    // GiveUp sent to server
                    try {
                        client.giveUp();
                    } catch (IOException ex) {
                        showPanel(pnlMainView);
                        JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
                    }
                     //Go back to the lobby
                     showPanel(pnlLobbyView);
                }

            }          
        }
        //send the invite back to the Server
        private void sendInvite(Invite i) {
            try {
                client.sendInvite(i);
            } catch (IOException ex) {
                showPanel(pnlMainView);
                JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
            }
        }
        
        //Send the game object to the server..
        private void sendGame(Game g) {
            try {
                client.sendGame(g);
            } catch (IOException ex) {
                showPanel(pnlMainView);
                JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
            }
        }
        //Send the game move object to the server
        private void sendGameMove(GameMove gm) {
             try {
                client.sendGameMove(gm);
            } catch (IOException ex) {
                showPanel(pnlMainView);
                JOptionPane.showMessageDialog(null, "The connection to the server failed, please check your network.");
            }
        }
    }


    private void showPanel(BPanel b)
    {
        mainFrame.remove(currentPanel);//remove the current panel
        mainFrame.add(b);//add the new panel
        currentPanel=b;


        mainFrame.validate();
        mainFrame.repaint();
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    //@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                formPropertyChange(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_formPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_formPropertyChange

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables



}
