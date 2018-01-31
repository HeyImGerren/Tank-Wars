package gamecomponents;

import java.io.IOException;

public class Background extends GameObject {
  public Background( int x, int y, String rLocation ) throws IOException {
    super( x, y, rLocation, 240 );
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