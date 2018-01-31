package gamecomponents;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Level {

  public Level( ) {}

  public static int[][] parseLevelFile( String resourceLocation ) {
    Path path = FileSystems.getDefault().getPath( resourceLocation );
    int[][] levelMap = new int[0][0];

    try {
      List<String> levelRows = Files.readAllLines( path );
      levelMap = new int[levelRows.size()][levelRows.get( 0 ).length()];

      for ( int i = 0; i < levelRows.size(); i++ ) {
        String row = levelRows.get( i );

        for ( int j = 0; j < row.length(); j++ ) {
          String currentCharacter = String.valueOf( row.charAt( j ) );
          int value;

          if ( currentCharacter.matches( "\\d+" ) ) {
            value = Integer.parseInt( currentCharacter );
          } else {
            value = 0;
          }
          levelMap[i][j] = value;
        }
      }
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return levelMap;
  }
}