package gamecomponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;

public class Tank extends GameObject implements KeyListener {
  private Point safePoint;
  private int health = 100;
  private int numberOfLives = 3;
  private boolean isAlive = true;
  boolean bulletONSCREEN;
  boolean[] keys;
  int forward;
  int reverse;
  int turnLeft;
  int turnRight;
  int fire;
  String bulletPic;
  Projectile bullet;
  KeyStroke temp;

  public Tank( int x, int y, String rLocation, char forward, char reverse,
               char turnLeft, char turnRight, char fire, String bulletPic ) throws IOException {
    super( x, y, rLocation, 64 );

    hitbox = new Rectangle( this.x, this.y, this.getWidth() - 6, this.getHeight() - 6 );
    this.bulletPic = bulletPic;
    temp = KeyStroke.getKeyStroke( fire, 0 );
    this.fire = temp.getKeyCode();
    temp = KeyStroke.getKeyStroke( forward, 0 );
    this.forward = temp.getKeyCode();
    temp = KeyStroke.getKeyStroke( reverse, 0 );
    this.reverse = temp.getKeyCode();
    temp = KeyStroke.getKeyStroke( turnLeft, 0 );
    this.turnLeft = temp.getKeyCode();
    temp = KeyStroke.getKeyStroke( turnRight, 0 );
    this.turnRight = temp.getKeyCode();
    keys = new boolean[256];
    Arrays.fill( keys, Boolean.FALSE );
  }

  @Override
  public void keyTyped( KeyEvent e ) {}

  @Override
  public void keyPressed( KeyEvent e ) {
    keys[e.getKeyCode()] = true;
  }

  @Override
  public void keyReleased( KeyEvent e ) {
    keys[e.getKeyCode()] = false;
  }

  public Projectile getBullet( ) {
    return bullet;
  }

  public void updateP( ) {
    if ( keys[fire] ) {
      try {
        bulletONSCREEN = true;
        bullet = new Projectile( getBulletX(), getBulletY(), bulletPic, 24, currentFrame );
      } catch ( IOException e ) {
        e.printStackTrace();
      }
    } else if ( !keys[fire] ) {
      bulletONSCREEN = false;
    }

    if ( keys[forward] ) {
      this.setX( this.getX() + calcX( currentFrame ) );
      this.setY( this.getY() - calcY( currentFrame ) );
      this.updateHitbox( this.getX() + calcX( currentFrame ), this.getY() - calcY( currentFrame ) );
    }
    if ( keys[reverse] ) {
      this.setX( this.getX() - calcX( currentFrame ) % 640 );
      this.setY( this.getY() + calcY( currentFrame ) % 640 );
      this.updateHitbox( this.getX() + calcX( currentFrame ), this.getY() - calcY( currentFrame ) );
    }
    if ( keys[turnLeft] ) {
      if ( currentFrame == 59 ) {
        currentFrame = 0;
        this.setFrame( currentFrame + 1 );
      } else {
        this.setFrame( currentFrame + 1 );
      }
    }
    if ( keys[turnRight] ) {
      if ( currentFrame == 0 ) {
        currentFrame = 59;
        this.setFrame( currentFrame - 1 );
      } else {
        this.setFrame( currentFrame - 1 );
      }
    }
  }

  public int calcX( int currentFrame ) { return ( int ) ( ( Math.cos( Math.toRadians( currentFrame * 6 ) ) ) * 5 ); }

  public int calcY( int currentFrame ) {return ( int ) ( ( Math.sin( Math.toRadians( currentFrame * 6 ) ) ) * 5 ); }

  public void updateSafePoint( int x, int y ) { this.safePoint = new Point( x, y ); }

  public Point getSafePoint( ) {
    return this.safePoint;
  }

  @Override
  public void collisionCheck( Tank player ) {
    this.setX( ( int ) this.safePoint.getX() );
    this.setY( ( int ) this.safePoint.getY() );
    this.updateHitbox( ( int ) this.safePoint.getX(), ( int ) this.safePoint.getY() );
  }

  @Override
  public void collisionCheck( IndestructibleWall indestructibleWall ) {
    this.setX( ( int ) this.safePoint.getX() );
    this.setY( ( int ) this.safePoint.getY() );
    this.updateHitbox( ( int ) this.safePoint.getX(), ( int ) this.safePoint.getY() );
  }

  @Override
  public void collisionCheck( DestructibleWall destructible ) {
    this.setX( ( int ) this.safePoint.getX() );
    this.setY( ( int ) this.safePoint.getY() );
    this.updateHitbox( ( int ) this.safePoint.getX(), ( int ) this.safePoint.getY() );
  }

  @Override
  public void collisionCheck( Projectile bullet ) {
    this.updateTankHealth( bullet.getDamage() );
  }

  public boolean getBONSCREEN( ) {
    return bulletONSCREEN;
  }

  private int getBulletX( ) {
    return this.getX() + 20;
  }

  private int getBulletY( ) {
    return this.getY() + 20;
  }

  public int getCurrentHealth( ) {
    return health;
  }

  public void updateTankHealth( int damageDone ) { this.health = this.getCurrentHealth() - damageDone; }

  public void setTankHealth( int healthValue ) {
    this.health = healthValue;
  }

  public boolean isAlive( ) {
    return isAlive;
  }

  public void setIsAlive( boolean status ) {
    this.isAlive = status;
  }

  public int getNumberOfLives( ) {
    return numberOfLives;
  }

  public void subtractNumberOfLives( int livesLost ) { this.numberOfLives -= livesLost; }
}