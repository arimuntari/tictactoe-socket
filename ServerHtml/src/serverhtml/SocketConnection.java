/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverhtml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JTextPane;

/**
 *
 * @author Muntari
 */
public class SocketConnection{
    
    private static int port = 80;
    private ServerSocket server;
    static Vector player = new Vector();
    static Vector name = new Vector();
    private PrintWriter out ;
    JTextPane textPanel;
    protected void start(JTextPane textPanel) throws IOException {
        this.textPanel = textPanel;
        try {
            ServerSocket tmp = new ServerSocket(port);
            setConsole(textPanel, "Start on port 80");
            server = tmp;
        } catch (Exception e) {
            setConsole(textPanel, "Error: " + e);
            return;
        }
        while(true){
            Socket client = server.accept();
            String id = String.valueOf(player.size());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String name=in.readLine();
            setConsole( textPanel, name+" has joined! with id "+id);
            this.name.add(name);
            
            listPlayer list = new listPlayer(client, id, name);
            player.add(list);
            list.start();
        }
    }
    public void setConsole(JTextPane textPane, String message){
        String text = textPane.getText();
        textPane.setText(message+"...\n"+text);
    }
    protected void stop(JTextPane textPanel) {
        try {
            server.close();
            setConsole(textPanel, "Stop on port 80");
        } catch (Exception e) {
            setConsole(textPanel, "Error: " + e.getMessage());
        }
    }
    class listPlayer extends Thread{
        private String id;
        private String username; 
        private String enemy; 
        private Integer value;
        private Socket client;
        Integer[] game ={2, 2, 2, 2, 2, 2, 2, 2, 2};
        private BufferedReader in ;
        private PrintWriter out ;
        public listPlayer(Socket socket, String id, String name){
            this.id = id;
            this.username = name;
            client = socket;
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(),true);
            } catch (Exception e) {
                
            }
        }
        //cek game selesai atau belum;
        public boolean cekGame(){
            int cek= 0; 
            for(int i=0;i<game.length;i++){
                if(game[i] == 2){
                    cek++;
                }
            }
            if(game[0]==game[1] && game[1]== game[2] && game[0]!=2){
                cek = 0;
            }else if(game[0]==game[3] && game[3]== game[6] && game[0]!=2){
                cek = 0;
            }else if(game[1]==game[4] && game[4]== game[7] && game[1]!=2){
                cek = 0;
            }else if(game[2]==game[5] && game[5]== game[8] && game[2]!=2){
                cek = 0;
            }else if(game[3]==game[4] && game[4]== game[5] && game[3]!=2){
                cek = 0;
            }else if(game[6]==game[7] && game[7]== game[8] && game[7]!=2){
                cek = 0;
            }else if(game[0]==game[4] && game[4]== game[8] && game[1]!=2){
                cek = 0;
            }else if(game[2]==game[4] && game[4]== game[6] && game[2]!=2 ){
                cek = 0;
            }
            if(cek == 0){
                resetGame();
                return true;
            }
            return false;
        }
        
        public void resetGame(){
            for(int i=0;i<game.length;i++){
                game[i] = 2;
            }
        }
        @Override
        public void run() {
            try {
                String received;
                while(true){
                    received = in.readLine();
                    if(received.equals("listPlayer")){
                        String list = "#player";
                        setConsole(textPanel, ""+SocketConnection.name.size());
                        for(int i=0;i< SocketConnection.name.size();i++) {
                            if(!String.valueOf(i).equals(id) &&  SocketConnection.name.get(i) != null){
                                list += i+"-"+SocketConnection.name.get(i)+";";
                            }
                        }
                        out.println(list);
                    }else if(received.contains("#battle")){
                         int i  = Integer.valueOf(received.replace("#battle", ""));
                         listPlayer playerBattle = (listPlayer) SocketConnection.player.get(i);
                         playerBattle.out.println("#accept"+id);
                    }else if(received.contains("#yesBattle")){
                        int i  = Integer.valueOf(received.replace("#yesBattle", ""));
                        
                        listPlayer playerBattle = (listPlayer) SocketConnection.player.get(i);
                        if(playerBattle != null){
                            playerBattle.out.println("#confirmed"+id);
                            enemy = String.valueOf(i);
                            out.println("#start0");
                            value = 0;
                            setConsole(textPanel, "acc player"+enemy);
                        }
                    }else if(received.contains("#confirmed")){
                        int i  = Integer.valueOf(received.replace("#confirmed", ""));
                        enemy = String.valueOf(i);
                        out.println("#start1");
                        value = 1;
                    }else if(received.contains("#resetgame")){
                        resetGame();
                        value = 1;
                    }else if(received.contains("#run")){
                        setConsole(textPanel, ""+received);
                        String[] splitData = received.split("#return");
                        String running = splitData[0];
                        String data = splitData[1];
                        String[] listData = data.split(";");
                        for(int z = 0; z<listData.length;z++){
                            game[z] = Integer.valueOf(listData[z]);
                        }
                        int run  = Integer.valueOf(running.replace("#run", ""));
                        game[run] = value;
                        String list ;
                        int i = Integer.valueOf(enemy);
                        listPlayer playerBattle = (listPlayer) SocketConnection.player.get(i);
                        list = "#return";
                        for(int j=0;j<game.length;j++){
                           list+=game[j]+";";
                        }
                        playerBattle.out.println(list);
                        out.println(list);
                        
                        if(cekGame()){
                            list = "#done";
                            if(value == 0){
                                playerBattle.out.println(list+"1");
                            }else{
                                playerBattle.out.println(list+"0");
                            }
                            out.println(list+""+value);
                        }
                    }
                     setConsole(textPanel, ""+received);
                }
            } catch (Exception e) {
                setConsole(textPanel, "error: "+e);
            }finally{
                try{
                    if (client != null){
                        setConsole(textPanel, username+" Closing down connection");
                        name.set(Integer.valueOf(id), null);
                        player.set(Integer.valueOf(id), null);
                        if(enemy != null){
                            int i  = Integer.valueOf(enemy);
                            listPlayer playerBattle = (listPlayer) SocketConnection.player.get(i);
                            playerBattle.out.println("#quit"+id);
                        }
                        resetGame();
                        client.close();
                    }
                }catch(IOException e){
                }
            }
        }
    }
}
