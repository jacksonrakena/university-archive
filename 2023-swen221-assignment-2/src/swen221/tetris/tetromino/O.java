package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class O extends Tetromino{
  public O(int x, int y, Color color) {
    super(x, y, color);
  }

  @Override
  public int x(int i) {
    // Point 0 -> (x, y)
    // Point 1 -> (x+1, y)
    // Point 2 -> (x+1, y+1)
    // Point 3 -> (x, y+1)
    return switch (i) {
      case 0, 3 -> centerX();
      case 1, 2 -> centerX()+1;
      default -> throw new Error();
    };
  }

  @Override
  public int y(int i) {
    return switch (i) {
      case 0, 1 -> centerY();
      case 2, 3 -> centerY()+1;
      default -> throw new Error();
    };
  }

  @Override
  public void rotateRight() {}
}
