package application;

import gamecomponents.*;
import javafx.scene.input.KeyCode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Array;
import java.util.*;
import java.util.List;

public class GameWorld extends JPanel implements Observer, Runnable, KeyListener {
  public static final int WIDTH = 990;
  public static final int HEIGHT = 900;

  public static final String tankPic = "/resources/Tank_blue_basic_strip60.png";
  public static final String bgPic = "/resources/Background.png";
  public static final String youWinPic = "/resources/youWin.png";
  //public static final String levelPic = "/home/gerren/Desktop/TankWars/src/resources/level.txt";
  public static final String levelPic = "/home/gerren/Desktop/TankWars/src/resources/level.txt";
  public static final String destructibleWallPic = "/resources/Blue_wall2.png";
  public static final String indestructibleWallPic = "/resources/Blue_wall1.png";
  public static final String shellPic = "/resources/Shell_heavy_strip60.png";
  public static final String explosionPic = "/resources/Explosion_small_strip6.png";

  protected GameClock gameClock;
  protected Dimension dimension;
  protected Tank player1;
  protected Tank player2;
  protected ArrayList<Projectile> bulletsPlayerOne;
  protected ArrayList<Projectile> bulletsPlayerTwo;
  protected ArrayList<DestructibleWall> destructibleWallList;
  protected ArrayList<IndestructibleWall> indestructibleWallList;
  protected boolean bulletP1ONSCREEN;
  protected boolean bulletP2ONSCREEN;
  protected boolean tempFire;
  protected Background bg;
  protected BufferedImage youWin;
  protected IndestructibleWall wallIndestructible;
  protected DestructibleWall wallDestructible;
  protected Explosion explosionAnimation;
  int delay;
  int time;
  int time2;
  int gameOverDelay;
  protected int[][] levelArray;
  Graphics2D miniMap;
  BufferedImage map;

  public GameWorld( ) throws IOException {
    this.dimension = new Dimension( WIDTH, HEIGHT );
    this.gameClock = new GameClock();
    indestructibleWallList = new ArrayList<>();
    destructibleWallList = new ArrayList<>();
    bulletsPlayerOne = new ArrayList<>();
    bulletsPlayerTwo = new ArrayList<>();
    youWin = ImageIO.read( getClass().getResource( youWinPic ) );
    explosionAnimation = new Explosion( indestructibleWallPic, 0, 0, 32 );

    this.gameClock.addObserver( this );
    this.gameClock.addObserver( explosionAnimation );
    this.levelArray = Level.parseLevelFile( levelPic );

    spawnObjects();

    tempFire = false;
    delay = 500;
    time = 0;
    time2 = 0;
    gameOverDelay = 0;
    this.bg = new Background( WIDTH, HEIGHT, bgPic );
    this.addKeyListener( player1 );
    this.addKeyListener( player2 );
    new Thread( this.gameClock ).start();
    this.setFocusable( true );
  }

  public void spawnObjects( ) throws IOException {
    for ( int column = 0; column <= 39; column++ ) {
      for ( int row = 0; row < 39; row++ ) {
        if ( levelArray[column][row] == 1 ) {
          this.wallIndestructible = new IndestructibleWall( row * 32, column * 32, indestructibleWallPic, 32 );
          this.wallIndestructible.setX( row * 32 );
          this.wallIndestructible.setY( column * 32 );
          this.indestructibleWallList.add( this.wallIndestructible );
        }
        if ( levelArray[column][row] == 2 ) {
          this.wallDestructible = new DestructibleWall( row * 32, column * 32, destructibleWallPic, 32 );
          this.wallDestructible.setX( row * 32 );
          this.wallDestructible.setY( column * 32 );
          this.destructibleWallList.add( this.wallDestructible );
        }
        if ( levelArray[column][row] == 3 ) {
          this.player1 = new Tank( row * 32, column * 32, tankPic, 'W',
              'S', 'A', 'D', 'F', shellPic );
          this.player1.setX( row * 32 );
          this.player1.setY( column * 32 );
        }
        if ( levelArray[column][row] == 4 ) {
          this.player2 = new Tank( 60, 60, tankPic, 'I',
              'K', 'J', 'L', 'H', shellPic );
          this.player2.setX( 500 );
          this.player2.setY( 780 );
        }
      }
    }
  }

  public Dimension getDimension( ) {
    return this.dimension;
  }

  @Override
  public void update( Observable o, Object arg ) {
    checkBulletCollisions();
    checkWallCollisions();
    checkTankCollisions();
    checkTankStatus();
    this.repaint();
    updateTankCoordinates();
  }

  @Override
  public void paintComponent( Graphics graphic ) {
    super.paintComponent( graphic );

    for ( int x = 0; x < WIDTH; x += bg.getWidth() ) {
      for ( int y = 0; y < HEIGHT; y += bg.getHeight() ) {
        bg.setX( x );
        bg.setY( y );
        bg.repaint( graphic );
      }
    }

    for ( IndestructibleWall iWalls : indestructibleWallList ) {
      iWalls.repaint( graphic );
    }

    for ( int currentWall = 0; currentWall < destructibleWallList.size(); currentWall++ ) {
      if ( destructibleWallList.get( currentWall ).isShowing() ) {
        destructibleWallList.get( currentWall ).repaint( graphic );
      }
    }

    if ( player1.isAlive() && player2.isAlive() ) {
      player1.updateP();
      player2.updateP();
      player1.repaint( graphic );
      player2.repaint( graphic );

      graphic.setColor( Color.yellow );
      int temp = 20;

      for ( int i = 0; i < player1.getNumberOfLives(); i++ ) {
        graphic.fillRect( player1.getX() + temp, player1.getY() + 70, 5, 5 );
        temp += 9;
      }

      temp = 20;

      for ( int i = 0; i < player2.getNumberOfLives(); i++ ) {
        graphic.fillRect( player2.getX() + temp, player2.getY() + 70, 5, 5 );
        temp += 9;
      }

      graphic.setColor( Color.red );
      graphic.fillRect( player1.getX() + 7, player1.getY() - 10, 50, 4 );

      graphic.setColor( Color.green );
      graphic.fillRect( player1.getX() + 7, player1.getY() - 10, player1.getCurrentHealth() / 2, 4 );

      graphic.setColor( Color.red );
      graphic.fillRect( player2.getX() + 7, player2.getY() - 10, 50, 4 );

      graphic.setColor( Color.green );
      graphic.fillRect( player2.getX() + 7, player2.getY() - 10, player2.getCurrentHealth() / 2, 4 );

      bulletP1ONSCREEN = player1.getBONSCREEN();
      bulletP2ONSCREEN = player2.getBONSCREEN();

      time = time - 20;
      time2 = time2 - 20;

      if ( time < 0 ) {
        if ( bulletP1ONSCREEN ) {
          Projectile bulletT = player1.getBullet();

          bulletsPlayerOne.add( bulletT );

          for ( Projectile b : bulletsPlayerOne ) {
            b.repaint( graphic );
          }
          tempFire = true;
          time = 500;
        }
      }

      if ( time2 < 0 ) {
        if ( bulletP2ONSCREEN ) {
          Projectile bulletT = player2.getBullet();
          bulletsPlayerTwo.add( bulletT );

          for ( Projectile b : bulletsPlayerTwo ) {
            b.repaint( graphic );
          }

          tempFire = true;
          time2 = 500;
        }
      }

      if ( tempFire ) {
        for ( Projectile b1 : bulletsPlayerOne ) {
          b1.updateP();
        }
        for ( Projectile b1 : bulletsPlayerOne ) {
          b1.repaint( graphic );
        }
        for ( Projectile b2 : bulletsPlayerTwo ) {
          b2.updateP();
        }
        for ( Projectile b2 : bulletsPlayerTwo ) {
          b2.repaint( graphic );
        }

      }
    } else {
      graphic.drawImage( this.youWin, 500, 200, this );
    }

    if ( this.explosionAnimation.isExploding() ) {
      graphic.drawImage( this.explosionAnimation.getBufferedImage(), this.explosionAnimation.getX(), this.explosionAnimation.getY(), null );
    }

    requestFocus();
  }

  @Override
  public void run( ) {}

  @Override
  public void keyTyped( KeyEvent e ) {}

  @Override
  public void keyPressed( KeyEvent e ) {}

  @Override
  public void keyReleased( KeyEvent e ) {}

  public void updateTankCoordinates( ) {
    player1.updateSafePoint( player1.getX(), player1.getY() );
    player1.updateHitbox( ( int ) player1.getSafePoint().getX(), ( int ) player1.getSafePoint().getY() );
    player2.updateSafePoint( player2.getX(), player2.getY() );
    player2.updateHitbox( ( int ) player2.getSafePoint().getX(), ( int ) player2.getSafePoint().getY() );
  }

  public void checkWallCollisions( ) {
    for ( IndestructibleWall indestructibleWall : indestructibleWallList ) {
      if ( player1.getHitbox().intersects( indestructibleWall.getHitbox() ) ) {
        player1.collisionCheck( indestructibleWall );
      }
      if ( player2.getHitbox().intersects( indestructibleWall.getHitbox() ) ) {
        player2.collisionCheck( indestructibleWall );
      }
      for ( int currentBullet = 0; currentBullet < bulletsPlayerOne.size(); currentBullet++ ) {
        if ( bulletsPlayerOne.get( currentBullet ).getHitbox().intersects( indestructibleWall.getHitbox() ) && indestructibleWall.isShowing() ) {
          try {
            this.explosionAnimation = new Explosion( explosionPic, indestructibleWall.getX(), indestructibleWall.getY(), 32 );
            this.gameClock.addObserver( this.explosionAnimation );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          indestructibleWall.collisionCheck( bulletsPlayerOne.get( currentBullet ) );
          bulletsPlayerOne.remove( bulletsPlayerOne.get( currentBullet ) );
        }
      }
      for ( int currentBullet = 0; currentBullet < bulletsPlayerTwo.size(); currentBullet++ ) {
        if ( bulletsPlayerTwo.get( currentBullet ).getHitbox().intersects( indestructibleWall.getHitbox() ) && indestructibleWall.isShowing() ) {
          try {
            this.explosionAnimation = new Explosion( explosionPic, indestructibleWall.getX(), indestructibleWall.getY(), 32 );
            this.gameClock.addObserver( this.explosionAnimation );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          indestructibleWall.collisionCheck( bulletsPlayerTwo.get( currentBullet ) );
          bulletsPlayerTwo.remove( bulletsPlayerTwo.get( currentBullet ) );
        }
      }
    }

    for ( DestructibleWall destructibleWall : destructibleWallList ) {
      if ( player1.getHitbox().intersects( destructibleWall.getHitbox() ) && destructibleWall.isShowing() ) {
        player1.collisionCheck( destructibleWall );
        destructibleWall.collisionCheck( player1 );
      }

      if ( player2.getHitbox().intersects( destructibleWall.getHitbox() ) && destructibleWall.isShowing() ) {
        player2.collisionCheck( destructibleWall );
        destructibleWall.collisionCheck( player2 );
      }


      for ( int currentBullet = 0; currentBullet < bulletsPlayerOne.size(); currentBullet++ ) {
        if ( bulletsPlayerOne.get( currentBullet ).getHitbox().intersects( destructibleWall.getHitbox() ) && destructibleWall.isShowing() ) {
          try {
            this.explosionAnimation = new Explosion( explosionPic, destructibleWall.getX(), destructibleWall.getY(), 32 );
            this.gameClock.addObserver( this.explosionAnimation );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          destructibleWall.collisionCheck( bulletsPlayerOne.get( currentBullet ) );
          bulletsPlayerOne.remove( bulletsPlayerOne.get( currentBullet ) );
        }
      }
      for ( int currentBullet = 0; currentBullet < bulletsPlayerTwo.size(); currentBullet++ ) {
        if ( bulletsPlayerTwo.get( currentBullet ).getHitbox().intersects( destructibleWall.getHitbox() ) && destructibleWall.isShowing() ) {
          try {
            this.explosionAnimation = new Explosion( explosionPic, destructibleWall.getX(), destructibleWall.getY(), 32 );
            this.gameClock.addObserver( this.explosionAnimation );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          destructibleWall.collisionCheck( bulletsPlayerTwo.get( currentBullet ) );
          bulletsPlayerTwo.remove( bulletsPlayerTwo.get( currentBullet ) );
        }
      }
    }
  }

  public void checkTankCollisions( ) {
    if ( player1.getHitbox().intersects( player2.getHitbox() ) ) {
      player1.collisionCheck( player2 );
    }
    if ( player2.getHitbox().intersects( player1.getHitbox() ) ) {
      player2.collisionCheck( player1 );
    }
  }

  public void checkBulletCollisions( ) {
    for ( int currentBullet = 0; currentBullet < bulletsPlayerOne.size(); currentBullet++ ) {
      if ( bulletsPlayerOne.get( currentBullet ).getHitbox().intersects( player2.getHitbox() ) ) {
        try {
          this.explosionAnimation = new Explosion( explosionPic, bulletsPlayerOne.get( currentBullet ).getX(), bulletsPlayerOne.get( currentBullet ).getY(), 32 );
          this.gameClock.addObserver( this.explosionAnimation );
        } catch ( IOException e ) {
          e.printStackTrace();
        }
        player2.collisionCheck( bulletsPlayerOne.get( currentBullet ) );
        bulletsPlayerOne.remove( bulletsPlayerOne.get( currentBullet ) );
      }
    }
    for ( int currentBullet = 0; currentBullet < bulletsPlayerTwo.size(); currentBullet++ ) {
      if ( bulletsPlayerTwo.get( currentBullet ).getHitbox().intersects( player1.getHitbox() ) ) {
        try {
          this.explosionAnimation = new Explosion( explosionPic, bulletsPlayerTwo.get( currentBullet ).getX(),
              bulletsPlayerTwo.get( currentBullet ).getY(), 32 );
          this.gameClock.addObserver( this.explosionAnimation );
        } catch ( IOException e ) {
          e.printStackTrace();
        }
        player1.collisionCheck( bulletsPlayerTwo.get( currentBullet ) );
        bulletsPlayerTwo.remove( bulletsPlayerTwo.get( currentBullet ) );
      }
    }
  }

  public void checkTankStatus( ) {
    if ( player1.isAlive() && player2.isAlive() ) {
      if ( this.player1.getCurrentHealth() <= 0 ) {
        this.player1.subtractNumberOfLives( 1 );

        if ( this.player1.getNumberOfLives() > 0 ) {
          this.player1.setTankHealth( 100 );
          this.player1.setX( 544 );
          this.player1.setY( 64 );
        } else {
          try {
            this.explosionAnimation = new Explosion( explosionPic, player1.getX(), player1.getY(), 32 );
            this.gameClock.addObserver( this.explosionAnimation );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          player1.setIsAlive( false );
        }
      }

      if ( this.player2.getCurrentHealth() <= 0 ) {
        this.player2.subtractNumberOfLives( 1 );
        if ( this.player2.getNumberOfLives() > 0 ) {
          this.player2.setTankHealth( 100 );
          this.player2.setX( 60 );
          this.player2.setY( 60 );
        } else {
          try {
            this.explosionAnimation = new Explosion( explosionPic, player2.getX(), player2.getY(), 32 );
            this.gameClock.addObserver( this.explosionAnimation );
          } catch ( IOException e ) {
            e.printStackTrace();
          }
          this.player2.setIsAlive( false );
        }
      }
    }
  }
}