package gamecomponents;

import java.awt.*;
import java.io.IOException;

public class Projectile extends GameObject {
  int damage = 20;

  public Projectile( int x, int y, String rLocation, int tileSize, int tankFrame ) throws IOException {
    super( x, y, rLocation, tileSize );
    currentFrame = tankFrame;
    this.hitbox = new Rectangle( this.x, this.y, this.getWidth(), this.getHeight() );
  }

  @Override
  public void collisionCheck( Tank player ) {}

  @Override
  public void collisionCheck( IndestructibleWall indestructibleWall ) {}

  @Override
  public void collisionCheck( DestructibleWall destructible ) { this.isShowing = false; }

  public void collisionCheck( Projectile bullet ) {
  }

  public void updateP( ) {
    this.x += ( ( int ) ( ( Math.cos( Math.toRadians( currentFrame * 6 ) ) ) * 10 ) );
    this.y -= ( ( int ) ( ( Math.sin( Math.toRadians( currentFrame * 6 ) ) ) * 10 ) );
    this.updateHitbox( this.x, this.y );
  }

  public int getDamage( ) {
    return damage;
  }
}