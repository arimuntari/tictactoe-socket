/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoe;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Muntari
 */
public class gameAction {
    JLabel[] label ;
    int[] cars = {2, 2, 2, 2, 2, 2, 2, 2, 2};
    public gameAction(JLabel[] label1){
        this.label = label1;
        gameResult(cars);
    }
    
    public void gameResult(int[] cars){
        String url ="";
        for(int i=0;i<cars.length;i++){
            //System.out.println(i+""+cars[i]);
            if(cars[i]==0){
                url = "/tictactoe/image/circle.png";
            }else if(cars[i]==1){
                url = "/tictactoe/image/cross.png";
            }else{
                url = "/tictactoe/image/bgwhite.png";
            }
            ImageIcon icon = new ImageIcon(this.getClass().getResource(url));
            label[i].setIcon(icon);
        }
    }
    
}
