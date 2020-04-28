/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

/**
 *
 * @author Muntari
 */
public class Connection implements Runnable {
    Socket socket;
    PrintWriter writer; 
    ComboItem[] comboBox = new ComboItem[100];
    gameAction gameAction;
    int[] game = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    Thread t ;
    game gme;
    String user ;
    JComboBox combo;
    JRootPane pane;
    BufferedReader in;
    int value;
    
    public Connection(){
         t  = new Thread(this);
    }
    public void starts(JRootPane frame, String user){
        try {
            this.user = user;
            InetAddress host = InetAddress.getLocalHost();
            System.out.println("host: " + host);
            
            socket = new Socket(host.getHostName(), 80);
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            writer.println(user);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            t.start();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, e);
        }
        
    }
    
    public void send(String text){
       writer.println(text);
    }

    @Override
    public void run() {
        String received;
        while(true){
            try {
                received = in.readLine();
                if(received.contains("#player") && received.contains("-")){
                    received = received.replace("#player", "");
                    String[] splitData = received.split(";");
                    for (int i = 0; i < splitData.length; i++) {
                        String[] text = splitData[i].split("-");
                        String id = text[0];
                        String value = text[1];
                        combo.addItem(new ComboItem(id, value));
                        //System.out.print(id+" | "+value);
                    }
                }else if(received.contains("#accept")){
                    int i  = Integer.valueOf(received.replace("#accept", ""));
                    int jawab = JOptionPane.showOptionDialog(pane, 
                    "Player mengajak bermain!?", 
                    "Ajakan Bermain", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, null, null, null);

                    if(jawab == JOptionPane.YES_OPTION){
                        this.send("#yesBattle"+i);
                    }
                }else if(received.contains("#confirmed")){
                    int i  = Integer.valueOf(received.replace("#confirmed", ""));
                    this.send("#confirmed"+i);
                }else if(received.contains("#start")){
                    int i  = Integer.valueOf(received.replace("#start", ""));
                    value = i;
                    gme = new game(this, i, user );
                    gme.setVisible(true);
                }else if(received.contains("#return")){
                    received = received.replace("#return", "");
                    String[] splitData = received.split(";");
                    for (int i = 0; i < splitData.length; i++) {
                        System.out.print(splitData[i]+" | ");
                        game[i] = Integer.valueOf(splitData[i]);
                    }
                    gameAction.gameResult(game);
                }else if(received.contains("#done")){
                    int i  = Integer.valueOf(received.replace("#done", ""));
                    cekWinner(i);
                }else if(received.contains("#quit")){
                    JOptionPane.showMessageDialog(combo, "Enemy Quit!!");
                    gme.dispose();
                    this.send("#resetgame");
                }
                System.out.println(received);
            } catch (IOException ex) {
                Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void setComboBox(JComboBox box){
       combo = box;
    }
    public void setRootPane(JRootPane rootPane){
        pane = rootPane;
    }
    public void setGameAction(gameAction game){
     this.gameAction = game;
    }
    public int[] getGame(){
        return game;
    }
    public void runGame(int block){
        int currentRunning = 0;
        
        for(int j=0;j<game.length;j++){
           if(game[j] != 2){
               currentRunning++;
           }
        }
        if(value == 0 && currentRunning%2==1 || value == 1 && currentRunning%2==0  ){
            JOptionPane.showMessageDialog(pane, "Not Your Turn!");
        }else{
            if(game[block]==2){
                String list ="#return";
                for(int j=0;j<game.length;j++){
                   list+=""+game[j]+";";
                }
                this.send("#run"+block+list);
                System.out.println(list);
            }else{
                JOptionPane.showMessageDialog(combo, "Block ini sudah diisi!");
            }
        }
    }
    public void resetGame(){
        for(int i=0;i<game.length;i++){
            game[i] = 2;
        }
    }
    public void cekWinner(int value){
        int cek =1 ;
        
        if(game[0]==game[1] && game[1]== game[2] && game[0]==value){
            cek = 0;
        }else if(game[0]==game[3] && game[3]== game[6] && game[0]==value){
            cek = 0;
        }else if(game[1]==game[4] && game[4]== game[7] && game[1]==value){
            cek = 0;
        }else if(game[2]==game[5] && game[5]== game[8] && game[2]==value){
            cek = 0;
        }else if(game[3]==game[4] && game[4]== game[5] && game[3]==value){
            cek = 0;
        }else if(game[6]==game[7] && game[7]== game[8] && game[7]==value){
            cek = 0;
        }else if(game[0]==game[4] && game[4]== game[8] && game[1]==value){
            cek = 0;
        }else if(game[2]==game[4] && game[4]== game[6] && game[2]==value ){
            cek = 0;
        }
        if(cek == 0){
            JOptionPane.showMessageDialog(combo, "You Winner!!");
            gme.dispose();
            resetGame();
            this.send("#resetgame");
        }else{
            JOptionPane.showMessageDialog(combo, "You Lose!!");
            gme.dispose();
            resetGame();
            this.send("#resetgame");
        }
    }
    
}
