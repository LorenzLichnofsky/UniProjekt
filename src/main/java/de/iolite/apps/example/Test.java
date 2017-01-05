package de.iolite.apps.example;

import java.applet.Applet;

import java.awt.*;

public class Test extends Applet {

    public void init(){

    }
     
     public void stop(){
    	 
     }
     
     public void paint(Graphics g){

         g.setColor(Color.BLACK);
         g.fillRect(0, 0, 400, 400);
         
         g.setColor(Color.GREEN);
         g.drawRect(20, 20, 30, 50);

         g.setColor(Color.RED);
         g.fillOval(60, 70, 20, 50);
         
     }
}