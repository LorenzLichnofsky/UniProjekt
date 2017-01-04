package de.iolite.apps.example;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Test extends Applet {
	
//	  Font   f1 = new Font( "Helvetica", Font.BOLD, 11 );
//	  Font   f2 = new Font( "Helvetica", Font.BOLD, 48 );
//	  Color  c1 = new Color( 255, 0, 0 );
//	  Color  c2 = new Color( 0, 0, 255 );
	  String s;

	  public void init()
	  {
		  s = getParameter( "Zeichenkette" );
		  System.out.println(s);
	  }

	  public void paint( Graphics g )
	  {
		  
		  g.drawString("Hello World", 50, 25);
//	    g.setColor( c1 );
//	    if( null == s || 0 >= s.length() ) {
//	      g.setFont( f1 );
//	      g.drawString( "Keine 'Zeichenkette' gefunden: " +
//	        "wahrscheinlich wurde Applet ohne HTML-Datei gestartet.",
//	        50, 65 );
//	    } else {
//	      g.setFont( f2 );
//	      g.drawString( s, 50, 65 );
//	    }
//	    g.setColor( c2 );
//	    g.drawOval( 20, 0, 205, 95 );
	  }

}
