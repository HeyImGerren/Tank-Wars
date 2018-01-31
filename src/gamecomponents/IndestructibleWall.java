package gamecomponents;

import java.awt.*;
import java.io.IOException;

public class IndestructibleWall extends GameObject {

  public IndestructibleWall( int x, int y, String rLocation, int tileSize ) throws IOException {
    super( x, y, rLocation, tileSize );
    hitbox = new Rectangle( this.x, this.y, this.getWidth(), this.getHeight() );
  }

  @Override
  public void collisionCheck( Tank player ) {}

  @Override
  public void collisionCheck( IndestructibleWall indestructibleWall ) {}

  @Override
  public void collisionCheck( DestructibleWall destructible ) {}

  @Override
  public void collisionCheck( Projectile bullet ) {}
}