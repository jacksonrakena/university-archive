int frame = 0;

// The class representing the player's bat.
class Bat {
  float width;
  float height;
  public Bat(float batWidth) {
    this.width = batWidth;
    this.height = (1.0/3)*this.width;
  }
  void draw() {
    fill(255,255,255);
    rect(mouseX-(this.width/2), mouseY-(this.height/2), this.width, this.height); 
  }

  void step() {
    // Handle collision between the bat and the ball.
    handleCollisionBetweenObjectAndBall(mouseX, mouseY, this.width, this.height);
  }
}

// The class representing a 'brick', or an enemy.
class Brick {
  
  // Whether to render and to handle collision.
  boolean enabled = true;
  
  // The number of hits that this brick has taken.
  int hits = 0;
  
  // The position of the brick.
  int x;
  int y;
  
  // The width of the brick.
  float width;
  
  public Brick(int x, int y, float width) {
    this.x=x;
    this.y=y;
    this.width = width;
  }
  
  void step() {
    if (!enabled) return;
    
    // Brick height is fixed at half of it's width.
    float bheight = 0.5*this.width;
    
    // If this brick collided with the ball in this frame, add one to the number of hits that it has taken.
    if (handleCollisionBetweenObjectAndBall(this.x, this.y, this.width, bheight)) {
       hits++; 
    }
     
    // If we took the fatal hit in this frame, stop rendering and taking collision.
    if (hits == 3) {
      enabled = false;
    }
  }
  
  void draw() {
    // Do not draw if we are dead.
    if (!enabled) return;
    
    // Based on the number of hits, set the fill color. Green = 0, yellow = 1, red = 2.
    switch (hits) {
      case 0:
        fill(0, 255, 0);
        break;
      case 1:
        fill(245, 239, 66);
        break;
      case 2:
        fill(230, 16, 16);
        break;
      default:
        break;
    }
    
    float bheight = round(0.5*this.width);
    
    // Draw the brick to the screen.
    rect(this.x-this.width/2, this.y-bheight/2, this.width, bheight);
  }
}

// The class representing the ball entity in the game.
class Ball {
  
  // The position of the ball is fixed at the center of the screen.
  float x = width/2;
  float y = height/2;
  
  // The width of the ball.
  float ballWidth;
  
  // The current velocity vector (pixels per frame),
  // as (velocityX, velocityY).
  float velocityX = 0;
  float velocityY = 0;
  
  public Ball(float width) {
     this.ballWidth = width;
     this.velocityX = 0.10 * width;
     this.velocityY = 0.08 * width;
  }
  
  void step() {
    float radius = this.ballWidth/2;
  
     // Adjust the current position by the velocity vector.
     this.x += velocityX;
     this.y += velocityY;
     
     // Bounce off the left and right of the screen.
     if ((this.x - radius) < 0 || (this.x + radius)  > width) {
        this.velocityX *= -1;
     }
     
     // Bounce off the top and bottom of the screen.
     if ((this.y + radius) > height || (this.y - radius) < 0) {
        this.velocityY *= -1;
     }
  }
  void draw() {
     fill(255,255,255);
     circle(this.x, this.y, this.ballWidth); 
  }
}

Ball ball;
Bat bat;

/**
  This method calculates and determines collision between the current ball and an arbitrary object,
  described as an entity with an (x,y) coordinate, a width and a height.
*/
boolean handleCollisionBetweenObjectAndBall(float objectX, float objectY, float objectWidth, float objectHeight) {
  boolean collision = false;
  float ballRadius = ball.ballWidth/2.0;
  
  // Calculate the half-width and half-height of the object.
  float objectHalfWidth = objectWidth/2.0;
  float objectHalfHeight = objectHeight/2.0;
  
  
  // Collision zone 1 (top)
  if ((ball.x - ballRadius) > (objectX - objectHalfWidth) && (ball.x + ballRadius) < (objectX + objectHalfWidth)
  && (ball.y + ballRadius) > (objectY - objectHalfHeight) && (ball.y + ballRadius < objectY)) {
     println("Top");
     collision = true;
     ball.velocityY = -1 * Math.abs(ball.velocityY);
  }
  
  // Collision zone 5 (bottom)
  else if ((ball.x - ballRadius) > (objectX - objectHalfWidth)
    && (ball.x + ballRadius < objectX + objectHalfWidth)
    && (ball.y - ballRadius) < (objectY + objectHalfHeight)
    && (ball.y - ballRadius) > (objectY)) {
     println("Bottom");
     collision = true;
     ball.velocityY = Math.abs(ball.velocityY);
  }
  
  // Collision zone 3 (right)
  else if (
    (ball.x - ballRadius) < objectX + objectHalfWidth &&
    (ball.x - ballRadius) > objectX &&
    (ball.y > objectY - objectHalfHeight) &&
    (ball.y < objectY + objectHalfHeight)
  ) {
      println("Right");
      collision = true;
      ball.velocityX = Math.abs(ball.velocityX); 
  }
  
  // Collision zone 7 (left)
  else if (
    (ball.x + ballRadius) > objectX - objectHalfWidth &&
    (ball.x + ballRadius) < objectX &&
    (ball.y) > objectY - objectHalfHeight &&
    (ball.y) < objectY + objectHalfHeight
  ) {
     println("Left");
     collision = true;
     ball.velocityX = -1 * Math.abs(ball.velocityY);
  }
  
  // Collision zone 8 (corner in top-left)
  else if ((ball.x + ballRadius) > (objectX - objectHalfWidth)
      && (ball.x + ballRadius) < (objectX)
      && (ball.y + ballRadius) > (objectY - objectHalfHeight)
      && (ball.y + ballRadius) < objectY
  ) {
    println("Top-left");
    ball.velocityX = -1 * Math.abs(ball.velocityX);
    ball.velocityY = -1 * Math.abs(ball.velocityY);
    collision = true;
  }
  
  // Collision zone 2 (corner in top-right)
  else if (
    (ball.x - ballRadius) < (objectX + objectHalfWidth)
    && (ball.x - ballRadius) > objectX
    && (ball.y + ballRadius) > (objectY - objectHalfHeight)
    && (ball.y + ballRadius) < objectY
  ) {
    println("Top-right");
    ball.velocityX = Math.abs(ball.velocityX);
    ball.velocityY = -1 * Math.abs(ball.velocityY);
    collision = true;
  }
  
  // Collision zone 6 (corner in bottom-left)
  else if (
    (ball.x + ballRadius) > (objectX - objectHalfWidth)
    && (ball.x + ballRadius) < objectX
    && (ball.y - ballRadius) < (objectY + objectHalfHeight)
    && (ball.y - ballRadius) > objectY
  ) {
    println("Bottom-left");
    ball.velocityX = -1 * Math.abs(ball.velocityX);
    ball.velocityY = Math.abs(ball.velocityY);
    collision = true;
  }
  
  // Collision zone 4 (corner in bottom-right)
  else if (
    (ball.x -ballRadius) < objectX + objectHalfWidth
    && (ball.x-ballRadius) > objectX
    && (ball.y-ballRadius) < objectY + objectHalfHeight
    && (ball.y-ballRadius) > objectY
  ) {
    println("Bottom-right");
    ball.velocityX = Math.abs(ball.velocityX);
    ball.velocityY = Math.abs(ball.velocityY);
    collision = true;
  }
  
  return collision;
}

ArrayList<Brick> bricks = new ArrayList<Brick>();
void setup() {
  size(1000,1000);
  ball = new Ball(0.04*width);
  bat = new Bat(0.125*width);
  
  // The width of a brick is fixed at 12.5% of the width of the screen.
  int brickWidth = round(0.125*width);
  
  // The number of brick rows to draw.
  int rows = 3;
  
  // The number of pixels between each brick in a row.
  int separationBetweenBricks = 10;
  
  // The number of pixels between each row.
  int separationBetweenRows = 30;
  
  // Draw the rows.
  for (int row = 0; row < rows; row++) {
    for (int i = 0; i < (width/(brickWidth+separationBetweenBricks)); i++) {
      bricks.add(new Brick((brickWidth/2)+(i*(brickWidth+separationBetweenBricks)), round(0.05*width)+(row*((brickWidth/2)+separationBetweenRows)), brickWidth));
    } 
  }
}

void draw() {
  // Clear the screen.
  clear();
  
  // Advance the frame counter.
  frame++;
  
  // Step forward in the physics simulation.
  ball.step();
  bat.step();
  
  // Draw the ball and bat.
  ball.draw();
  bat.draw();
  
  // Loop over each brick.
  for (Brick brick : bricks) {
    // Step forward in the physics simulation for the brick.
    brick.step();
    
    // Draw the brick.
    brick.draw();
  }
}
