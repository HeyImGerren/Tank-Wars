package gamecomponents;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sprite {
  int tileSize;
  String spriteFile;

  private BufferedImage[] images;

  public Sprite( String rLocation, int tileSize ) throws IOException {
    this.spriteFile = rLocation;
    this.tileSize = tileSize;

    BufferedImage image = ImageIO.read( getClass().getResource( rLocation ) );

    this.images = new BufferedImage[image.getWidth() / tileSize];

    for ( int index = 0; index < this.images.length; index++ ) {
      this.images[index] = image.getSubimage( index * this.tileSize, 0, this.tileSize, this.tileSize );
    }
  }

  public BufferedImage getFrame( int frame ) {
    return this.images[frame];
  }

  public int frameCount( ) {
    return this.images.length;
  }
}