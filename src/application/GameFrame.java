package application;

import javax.swing.*;

public class GameFrame extends JFrame {

  public GameFrame( GameWorld world ) {
    setTitle( "Tank Homies" );
    this.getContentPane().setPreferredSize( world.getDimension() );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setVisible( true );
    setResizable( false );

    add( world );
    pack();

    Thread worldThread = new Thread( world );
    worldThread.start();
  }
}
