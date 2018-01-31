package gamecomponents;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class Explosion implements Observer {
  protected Sprite explosionSprite;
  private int x;
  private int y;
  private int frameCount;
  protected int frameDisplayed;
  private boolean explosionComplete;
  private boolean isExploding;

  public Explosion( String explosionImage, int xCoordinate, int yCoordinate, int tileSize ) throws IOException {
    explosionSprite = new Sprite( explosionImage, tileSize );
    this.x = xCoordinate;
    this.y = yCoordinate;
    this.frameDisplayed = 0;
    this.frameCount = 0;
    this.explosionComplete = false;
    this.isExploding = true;
  }

  @Override
  public void update( Observable observable, Object o ) {
    if ( !this.explosionComplete ) {
      this.frameCount++;
      this.frameDisplayed = ( this.frameDisplayed + 1 ) % this.explosionSprite.frameCount();

      if ( this.frameDisplayed % 5 == 0 ) {
        this.frameDisplayed = ( this.frameDisplayed + 1 ) % this.explosionSprite.frameCount();
      }
      if ( frameCount >= 6 ) {
        observable.deleteObserver( this );
        this.explosionComplete = true;
        this.isExploding = false;
        this.frameCount = 0;
      }
    }
  }

  public BufferedImage getBufferedImage( ) {
    BufferedImage image = this.explosionSprite.getFrame( this.frameDisplayed );
    return image;
  }

  public int getX( ) {
    return x;
  }

  public int getY( ) {
    return y;
  }

  public boolean isExploding( ) {
    return this.isExploding;
  }
}