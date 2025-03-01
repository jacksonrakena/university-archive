package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

public class L extends J{
  public L(int x, int y, Color color) {
    super(x,y,color);
  }

  @Override
  public int getOffset(int i, boolean x) {
    if (x) {
      if (horizontal()) {
        if (i == 0) {
          if (this.orientation == JOrientation.HorizUp) return 1;
          return -1;
        }

        return i - 2;
      }

      if (i > 0) return 0;
      if (this.orientation == JOrientation.VertRight) return 1;
      return -1;
    }

    // Horizontal
    if (horizontal()) {
      if (i>0) return 0;
      if (this.orientation == JOrientation.HorizUp) return 1;
      return -1;
    }

    // Vertical
    if (i == 0) {
      if (this.orientation == JOrientation.VertRight) return -1;
      return 1;
    }
    return i-2;
  }
}
