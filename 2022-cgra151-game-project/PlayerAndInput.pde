import java.util.Map;

/**
  PlayerState is a persistent struct that contains options for each player.
  
  It also stores a reference to the current world entity representing that player,
  which can be null if we are in a loading state, or not playing a game.
*/
class PlayerState {
  ControlScheme controlScheme;
  color chosenColor;
  long lastFrameChanged = 0;
  int requiredChangeGap = 10;
  PlayerEntity entity;
  int points;
  
  PlayerState(ControlScheme control, color c) {
    this.controlScheme = control;
    this.chosenColor = c;
  }
  
  void previousColor() {
    if ((state.frame - lastFrameChanged) > requiredChangeGap) {
      chosenColor = Globals.colors.get(Globals.colors.indexOf(chosenColor) == 0 ? Globals.colors.size()-1 : Globals.colors.indexOf(chosenColor)-1);
      lastFrameChanged = state.frame;
    }
  }
  
  void nextColor() {
    if ((state.frame - lastFrameChanged) > requiredChangeGap) {
      chosenColor = Globals.colors.get(Globals.colors.indexOf(chosenColor) == 0 ? Globals.colors.size()-1 : Globals.colors.indexOf(chosenColor)-1);
      lastFrameChanged = state.frame;
    }
  }
}

/*
  PlayerEntity represents a real, in-world, depiction of the player.
  
  It is linked to the player's current PlayerState, and is responsible for
  controlling the player's spaceship velocity and position.
*/
class PlayerEntity extends TriangleEntity  {
  PlayerState playerState;
  void addVelocity(PVector difference) {
    this.velocity.x = max(0-Globals.playerMaximumVelocity, min(Globals.playerMaximumVelocity, this.velocity.x+difference.x));
    this.velocity.y = max(0-Globals.playerMaximumVelocity, min(Globals.playerMaximumVelocity, this.velocity.y+difference.y));
  }
  
  PlayerEntity(PlayerState state, PVector startingPosition) {
    super(startingPosition, new PVector(Globals.playerWidth, 0), new PlayerSpaceshipTexture(state));
    this.playerState = state;
  }
  
  PVector calculateForwardVector() {
    return new PVector((float)(-1 * Math.sin(radians(angle+180))), (float)(Math.cos(radians(angle+180))));
  }
  
  void step() {
    super.step();
    float localAcceleration = DEBUG_disableAccelerationRampAndMomentum ? 1 : Globals.playerAccelerationSpeed;
    if (state.game instanceof GameSelect) return;
    
    if (this.playerState.controlScheme.right()) angle=((angle+5)%360);
    if (this.playerState.controlScheme.left()) angle=(angle-5)%360;
    
    float rad = radians(angle+180);
    
    acceleratingStatus = 0;
    if (this.playerState.controlScheme.y()) {
      // Calculate the forward vector (in the direction the ship is facing)
      PVector forward = new PVector((float)(-1 * Math.sin(rad)), (float)(Math.cos(rad)));
      
      // Set the acceleration of this vector
      forward.setMag((this.playerState.controlScheme.down() ? -1 : 1)*(localAcceleration*Globals.playerMaximumVelocity));
      
      // Add the forward vector to the ship's trajectory
      velocity.add(forward);
      
      // Normalize the vector to a length of maximumVelocity if it's too fast
      if (velocity.mag() >= Globals.playerMaximumVelocity) velocity.setMag(Globals.playerMaximumVelocity);
      
      float vPct = velocity.mag()/Globals.playerMaximumVelocity;
      acceleratingStatus = vPct > 0.3 ? vPct > 0.6 ? 3 : 2 : 1;
    }
  }
}

/*
  The Cosmos Conflict input control system.
*/

boolean[] inputs = new boolean[400];
Map<Integer, Long> inputDebouncing = new HashMap<Integer,Long>();
long debouncingDelay = 10;

void keyPressed() {
  if (keyCode < inputs.length) inputs[keyCode] = true;
}

void keyReleased() {
  if (keyCode < inputs.length) inputs[keyCode] = false;
}

/*
  Returns true is the requestey keycode is pressed.
  This method does not affect [getInputDebounced].
*/

boolean getInput(int keyCode) {
  return keyCode < inputs.length && inputs[keyCode];
}

/**
  Returns true if the selected input is pressed,
  and it has also been [debouncingDelay] frames since the last press.
*/
boolean getInputDebounced(int keyCode) {
  if (!getInput(keyCode)) return false;
  if (inputDebouncing.containsKey(keyCode)) {
    if (state.frame > inputDebouncing.get(keyCode) + debouncingDelay) {
      inputDebouncing.put(keyCode,state.frame);
      return true;
    }
    return false;
  } else {
    inputDebouncing.put(keyCode, state.frame);
    return true;
  }
}

void mousePressed(MouseEvent event) {
  Globals.clickX = event.getX();
  Globals.clickY = event.getY();
}
void mouseReleased() {
  Globals.clickX = -1;
  Globals.clickY = -1;
}

/*
  Returns true if the mouse has been clicked,
  and the mouse is within the region bounded by (x1, y1) to (x1+w, y1+h)
*/
boolean regionClicked(float x1, float y1, float w, float h) {
  return Globals.clickX > x1 && Globals.clickX < x1+w && Globals.clickY > y1 && Globals.clickY < y1+h;
}

/*
  Returns true if the mouse is witin the region bounded by (x1, y1) to (x1+w, y1+h)
*/
boolean mouseInRegion(float x1, float y1, float w, float h) {
  return mouseX > x1 && mouseX < x1+w && mouseY > y1 && mouseY < y1+h;
}

/*
  Represents a simple control abstraction.
  Each PlayerState has an individual control scheme.
*/
class ControlScheme {
  int moveLeft;
  int moveRight;
  int moveUp;
  int moveDown;
  int fire;
  ControlScheme(int moveLeft, int moveRight, int moveUp, int moveDown, int fire) {
    this.moveLeft = moveLeft;
    this.moveRight = moveRight;
    this.moveUp = moveUp;
    this.moveDown = moveDown;
    this.fire = fire;
  }
  boolean x() { return left() || right(); }
  boolean y() { return up() || down(); }
  boolean left() { return getInput(this.moveLeft); }
  boolean right() { return getInput(this.moveRight); }
  boolean up() { return getInput(this.moveUp); }
  boolean down() { return getInput(this.moveDown); } 
  boolean fire() { return getInput(this.fire); }
}
