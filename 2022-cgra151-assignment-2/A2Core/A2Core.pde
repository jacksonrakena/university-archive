int frame = 0;
int ignoreCollisionUntilFrame = 0;
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
    if (ignoreCollisionUntilFrame == 0 || ignoreCollisionUntilFrame <= frame) {
        ignoreCollisionUntilFrame = 0;
        handleCollisionBetweenObjectAndBall(mouseX, mouseY, this.width, this.height);
    } 
  }
}

class Ball {
  float x = 100;
  float y = 100;
  float ballWidth;
  
  float velocityX = 2;
  float velocityY = 1;
  
  public Ball(float width) {
     this.ballWidth = width;
     this.velocityX = 0.06 * width;
     this.velocityY = 0.04 * width;
  }
  void step() {
    float radius = this.ballWidth/2;
     
     this.x += velocityX;
     this.y += velocityY;
     if ((this.x - radius) < 0 || (this.x + radius)  > width) {
        this.velocityX = 0 - this.velocityX;
     }
     if ((this.y + radius) > height || (this.y - radius) < 0) {
        this.velocityY = 0 - this.velocityY; 
     }
  }
  void draw() {
     fill(255,255,255);
     circle(this.x, this.y, this.ballWidth); 
  }
}
Ball ball;
Bat bat;

boolean handleCollisionBetweenObjectAndBall(float objectX, float objectY, float objectWidth, float objectHeight) {
  boolean collision = false;
  float ballRadius = ball.ballWidth/2.0;
  float objectHalfWidth = objectWidth/2.0;
  float objectHalfHeight = objectHeight/2.0;
  
  if ((ball.x - ballRadius) > (objectX - objectHalfWidth) && (ball.x + ballRadius) < (objectX + objectHalfWidth)
  && (ball.y + ballRadius) > (objectY - objectHalfHeight) && (ball.y + ballRadius < objectY)) {
     println("Top");
     collision = true;
     ball.velocityY *= -1;
     ignoreCollisionUntilFrame = frame + 30;
  }
  else if ((ball.x - ballRadius) > (objectX - objectHalfWidth)
    && (ball.x + ballRadius < objectX + objectHalfWidth)
    && (ball.y - ballRadius) < (objectY + objectHalfHeight)
    && (ball.y - ballRadius) > (objectY)) {
     println("Bottom");
     collision = true;
     ball.velocityY *= -1;
     ignoreCollisionUntilFrame = frame + 30;
  }
  else if (
    (ball.x - ballRadius ) < objectX + objectHalfWidth &&
    (ball.x - ballRadius) > objectX &&
    (ball.y > objectY - objectHalfHeight) &&
    (ball.y < objectY + objectHalfHeight)
  ) {
      println("Right");
      collision = true;
      ball.velocityX *= -1; 
      ignoreCollisionUntilFrame = frame + 30;
  }
  else if (
    (ball.x + ballRadius) > objectX - objectHalfWidth &&
    (ball.x + ballRadius) < objectX &&
    (ball.y) > objectY - objectHalfHeight &&
    (ball.y) < objectY + objectHalfHeight
  ) {
     println("Left");
     collision = true;
     ball.velocityX = 0 - ball.velocityX;
     ignoreCollisionUntilFrame = frame + 30;
  }
  return collision;
}

void setup() {
  size(1000,1000);
  ball = new Ball(0.04*width);
  bat = new Bat(0.125*width);
}

void draw() {
  clear();
  ball.step();
  bat.step();
  ball.draw();
  bat.draw();
  frame++;
}
