package application;

import java.util.Observable;

import static java.lang.Thread.sleep;

public class GameClock extends Observable implements Runnable {
  private final int DURATION = 30;

  @Override
  public void run( ) {
    while ( true ) {
      try {
        sleep( DURATION );
        setChanged();
        notifyObservers();
      } catch ( InterruptedException e ) {
        e.printStackTrace();
      }
    }
  }
}