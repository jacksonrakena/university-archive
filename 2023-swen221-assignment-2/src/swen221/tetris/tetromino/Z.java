package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class Z extends S{
  public Z(int x, int y, Color color) {
    super(x, y, color);
  }

  @Override
  public int getOffset(int i, boolean x) {
    if (x) {
      if (horizontal) {
        return switch (i) {
          case 0 -> -1;
          case 1,2 -> 0;
          case 3 -> 1;
          default -> throw new Error();
        };
      }
      return switch (i) {
        case 0,1 -> 0;
        case 2,3 -> -1;
        default -> throw new Error();
      };
    }

    // Y
    if (horizontal) {
      return switch (i) {
        case 0,1 -> 1;
        case 2,3 -> 0;
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
}
