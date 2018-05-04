/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * pnlMasterMindGame.java
 *
 * Created on 11-Mar-2011, 12:36:36
 */

package mastermind.panels;

import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JOptionPane;
//import javax.swing.JPanel;
import mastermind.controls.BPanel;
import mastermind.controls.MasterMindRow;
import mastermind.controls.ZwartWitRow;
import mastermind.data.Game;
import mastermind.data.GameEnd;
import mastermind.data.GameMove;
import mastermind.data.GameResult;
import mastermind.events.request.RequestEvent;
import mastermind.logic.MasterMindGame;
import mastermind.networking.ClientApp;
/**
 *
 * @author lostone
 */
public class PnlMasterMindGameMultiPlayer extends BPanel {

    private ArrayList<ZwartWitRow> zwr = new ArrayList<ZwartWitRow>() ;
    private ArrayList<MasterMindRow> mmr = new ArrayList<MasterMindRow>();
    private int currentRow = 0;
    private MasterMindGame mmg = new MasterMindGame();
    private boolean gameMayStart = false;
    //Current game..
    private Game game;
    
    /** Creates new form pnlMasterMindGame */
    public PnlMasterMindGameMultiPlayer() {
        initComponents();
          this.setBackground(new Color(255,255,255,0));
          initRows();
         //Init the game
         initGame();
    }
    
    private void initRows() {

        for (int i = 0; i < 10; i++) {
            MasterMindRow m = new MasterMindRow();
            ZwartWitRow z = new ZwartWitRow();

            m.setBounds(10,50*i+10,200,50);
            m.setName("mmr"+i);
            jLayeredPane1.add(m,javax.swing.JLayeredPane.DEFAULT_LAYER);
            
            z.setBounds(230,50*i+16,46,46);
            z.setName("zwr"+i);
            jLayeredPane1.add(z,javax.swing.JLayeredPane.DEFAULT_LAYER);

            mmr.add(m);
            zwr.add(z);
        }
     
    }

    //init the game
    public void initGame() {
       currentRow = 0;
       btnCheck.setEnabled(false);
      // mmg.newGuess();
      // System.out.println(mmg.getToGuess());
      
      for (int i = 0; i < 10; i++) {
            mmr.get(i).setColors("1111");
            zwr.get(i).setAllNotVisible();
            mmr.get(i).setVisible(false);
            zwr.get(i).setVisible(true);
            mmr.get(i).setEnabled(true);
      }
       
        btnCheck.setLocation(btnCheck.getX(),10);
 
        jLayeredPane1.validate();
        jLayeredPane1.repaint();
        this.validate();
        this.repaint();

        jLayeredPane1.updateUI();
       this.updateUI();
       mmr.get(currentRow).setVisible(true);
    }

    //This function decides if the game may start
    //or if the player first needs to chose a solution to send to the server:)
    void startGame(Game game) {
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
            //Check if status is GameMayStart
            if (game.getStatus().equals("GameMayStart")) {
                //The game can start (users can send GameMove to the server)
                System.out.println("You can start playing now and find the other player's solution:D");
                gameMayStart = true;
                btnCheck.setName("OK");
                btnCheck.setEnabled(true);
            } else {
                //Show the user the panel where he may chose a solution to send to the server
                gameMayStart = false;
                btnCheck.setName("Chose solution");
                btnCheck.setEnabled(true);
            }
        } else if(game.getGameType() == 1) {
            //Start playing now
            //Show the multiplayerview and start the game
            System.out.println("You can start playing now and find the server solution");
            gameMayStart = true;
            btnCheck.setName("OK");
            btnCheck.setEnabled(true);
        }
    }

    //Send the "answer" to the server to let it be checked..
    private void checkAnswer() {
        //Get the current choice
        String solution = mmr.get(currentRow).getColors();

        if (gameMayStart) {
            GameMove gameMove = new GameMove(game.getGameID(),ClientApp.getMe().getUserID(),currentRow, solution);

            fireRequestEvent(new RequestEvent(this, "send:gameMove",gameMove));
            
            //Disable the button till we get answer back from the server
            btnCheck.setEnabled(false);
        } else {

            //Set the right solution..
            if (ClientApp.getMe().getUserID() == game.getPlayer1ID()) {
                game.setPlayer1Solution(solution);
            } else {
                game.setPlayer2Solution(solution);
            }

            //Send the solution you've chosen to the server
            fireRequestEvent(new RequestEvent(this, "send:game",game));
        }
    }
    public void answerFromServer(GameResult gr) {
         //Set the collors of the pins & disable the CURRENT mastermindrow
          zwr.get(currentRow).setKleuren(gr.getBlackWhitePions());
          mmr.get(currentRow).setEnabled(false);

          //place the button & the new row
          currentRow += 1;
          btnCheck.setVisible(false);
          btnCheck.setLocation(btnCheck.getX(),btnCheck.getY()+50);
          btnCheck.setVisible(true);
          mmr.get(currentRow).setVisible(true);
            
          //invalidation of existent things..
          btnCheck.invalidate();
          for (int i = 0; i < 10; i++)
          {
                mmr.get(i).invalidate();
                zwr.get(i).invalidate();
          }
          this.setBackground(new Color(255,255,255,0));
    }
    //The game ended.. don't worry it's not so bad.. After all the world still exists
    void gameEnded(GameEnd ge) {
       //First stop everything so the player can't send Moves to the server
        mmr.get(currentRow).setEnabled(false);
        btnCheck.setEnabled(false);

/* GameEnd object tells each player that the game has ended so they can go back
 *  to the lobby & tells the winner etc.
 *      the winner is the username/ID
 *      the status is your status.. won.. lost.. (YouWin - YouLose - OtherQuit)
 */
        if (ge.getStatus().equals("YouWin")) {
            JOptionPane.showMessageDialog(null, "Congratulations! You won the game!"
                    + "\n You've found the right combination in " + ge.getGameTime() + " seconds.");
        } else  if (ge.getStatus().equals("YouLose")) {
            JOptionPane.showMessageDialog(null, "You lost the game:( sorry! The winner is " + ge.getWinner()
                      + "\n (s)he found the right combination in " + ge.getGameTime() + " seconds.");
        } else  if (ge.getStatus().equals("OtherQuit")) {
            JOptionPane.showMessageDialog(null, ge.getWinner() + " quit the game and you win because of that!");
        }
        fireRequestEvent(new RequestEvent(this, "show:main"));
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        btnCheck = new javax.swing.JButton();

        setName("Form"); // NOI18N

        jLayeredPane1.setName("jLayeredPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mastermind.MasterMindApp.class).getContext().getResourceMap(PnlMasterMindGameMultiPlayer.class);
        btnCheck.setText(resourceMap.getString("btnCheck.text")); // NOI18N
        btnCheck.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        btnCheck.setName("btnCheck"); // NOI18N
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });
        btnCheck.setBounds(270, 10, 50, 40);
        jLayeredPane1.add(btnCheck, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        checkAnswer();
    }//GEN-LAST:event_btnCheckActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JLayeredPane jLayeredPane1;
    // End of variables declaration//GEN-END:variables

    // Disable this..?
    public void showSolution() {
        mmr.get(currentRow).setEnabled(false);
        btnCheck.setEnabled(false);
        mmr.get(currentRow).setColors(mmg.getToGuess());
    }

    //Not needed on the server side... remove later
    public void showColorChooser() {
        String kleuren = JOptionPane.showInputDialog("Geef het aantal kleuren (2 tot 6):");
        if(functions.Math.isIntNumber(kleuren))
        {
            int iKleuren = Integer.parseInt(kleuren);
            if(iKleuren <=6 && iKleuren >= 2)
            {
                mmg.setMax(iKleuren);
                mmg.newGuess();
                initGame();
                for (int i = 0; i < 10; i++)
                {
                    mmr.get(i).setAmountOfColors(iKleuren);
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Enkel een numerieke waarde tussen 2 en 6 toegestaan!");
    }

//Game setter & getter
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

}
