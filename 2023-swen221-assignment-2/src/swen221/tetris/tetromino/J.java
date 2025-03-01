package swen221.tetris.tetromino;

import swen221.tetris.logic.Color;

import java.util.HashMap;
import java.util.Map;

enum JOrientation {
  /*
    X
    XXX
   */
  HorizUp,

  /*
    XXX
      X
   */
  HorizDown,

  /*
    XX
    X
    X
   */
  VertRight,

  /*
    X
    X
   XX
   */
  VertLeft
}
public class J extends Tetromino{
  public J(int x, int y, Color color) {
    super(x,y,color);
  }

  JOrientation orientation = JOrientation.HorizDown;

  @Override
  public int x(int i) {
    return centerX() + getOffset(i, true);
  }

  @Override
  public int y(int i) {
    return centerY() + getOffset(i, false);
  }

  public int getOffset(int i, boolean x) {
    if(x) {
      if (horizontal()) {
        if (i == 0) {
          if (this.orientation == JOrientation.HorizUp) return -1; // Correct
          return 1; // Correct
        }

        return i-2;
      }

      if (i > 0) return 0;
      if (this.orientation == JOrientation.VertRight) return 1;
      return -1;
    }

    // Horizontal
    if (horizontal()) {
      if (i>0) return 0;
      if (this.orientation == JOrientation.HorizUp) return 1;
      return -1; // Problem
    }

    // Vertical
    if (i == 0) {
      if (this.orientation == JOrientation.VertRight) return 1;
      return -1;
    }
    return i-2;
  }

  public boolean horizontal() { return this.orientation == JOrientation.HorizUp || this.orientation == JOrientation.HorizDown; }
  public boolean vertical() { return !this.horizontal(); }

  @Override
  public void rotateRight() {
    this.orientation = switch (this.orientation) {
      case HorizUp -> JOrientation.VertRight;
      case HorizDown -> JOrientation.VertLeft;
      case VertRight -> JOrientation.HorizDown;
      case VertLeft -> JOrientation.HorizUp;
    };
  }
}