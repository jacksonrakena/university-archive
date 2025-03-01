package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class S extends Tetromino{
  /**
   * a S is either horizontal or vertical.
   * It is horizontal when looks like the 's' letter.
   * */
  boolean horizontal=true;

  /**
   * x coordinate: obtained by summing the center with an offset.
   * */
  @Override
  public int x(int i) {
    return centerX()+getOffset(i, true);
  }

  /**
   * Retrieves the offset for a given coordinate within this shape.
   * @param i The coordinate to get in this shape. (0..3)
   * @param x Whether to get the X or Y coordinate.
   */
  public int getOffset(int i, boolean x) {
    if (x) {
      // X horizontal
      if (horizontal) {
        return switch (i) {
          case 0 -> -1;
          case 1,2 -> 0;
          case 3 -> 1;
          default -> throw new Error();
        };
      }
      // X vertical
      return switch (i) {
        case 0,1 -> -1;
        case 2,3 -> 0;
        default -> throw new Error();
      };
    }

    // Y
    if (horizontal) {
      return switch (i) {
        case 0,1 -> 0;
        case 2,3 -> 1;
        default ->  throw new Error();
      };
    }
    return switch (i) {
      case 1,2 -> 0;
      case 0 -> 1;
      case 3 -> -1;
      default -> throw new Error();
    };
  }


  /**
   * y coordinate: obtained by summing the center with an offset.
   * */
  @Override
  public int y(int i) {
    return centerY()+getOffset(i, false);
  }
  public S(int x, int y, Color color) {
    super(x, y, color);
  }

  /**iterate between the two possibilities*/
  @Override
  public void rotateRight() {
    horizontal=!horizontal;
  }
}
