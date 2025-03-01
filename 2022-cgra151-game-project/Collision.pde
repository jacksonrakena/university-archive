interface BoxCollider {
  LineSegment[] getCollidableSegments();
}

interface LineCollider {
  LineSegment[] getLines();
}

LineSegment[] generateLineSegmentsFromBox(float x, float y, float w, float h) {
  LineSegment[] segments = new LineSegment[4];
  PVector topleft = new PVector(x, y);
  PVector topright = new PVector(x+w, y);
  PVector bottomleft = new PVector(x, y+h);
  PVector bottomright = new PVector(x+w,y+h);
  segments[0] = new LineSegment(topleft, topright);
  segments[1] = new LineSegment(topright, bottomright);
  segments[2] = new LineSegment(bottomright, bottomleft);
  segments[3] = new LineSegment(bottomleft, topleft);
  return segments;
}

class LineSegmentCollisionResult {
  boolean colliding = false;
  PVector pointOfCollision = null;
  private LineSegmentCollisionResult(boolean colliding, PVector pointOfCollision) {
    this.colliding = colliding;
    this.pointOfCollision = pointOfCollision;
  }
}

boolean isPointInsideTriangle(PVector point, PVector t1, PVector t2, PVector t3) {
  float x1 = t1.x;
  float y1 = t1.y;
  float x2 = t2.x;
  float y2 = t2.y;
  float x3 = t3.x;
  float y3 = t3.y;
  float px = point.x;
  float py = point.y;
  float area = abs((x2-x1)*(y3-y1) - (x3-x1)*(y2-y1));
  float area1 = abs( (x1-px)*(y2-py) - (x2-px)*(y1-py) );
  float area2 = abs( (x2-px)*(y3-py) - (x3-px)*(y2-py) );
  float area3 =  abs( (x3-px)*(y1-py) - (x1-px)*(y3-py) );
  return area1 + area2 + area3 == area;
}

class LineSegment {
  PVector origin;
  PVector destination;
  LineSegment(PVector origin, PVector destination) {
    this.origin = origin;
    this.destination = destination;
  }
  
  /**
    Attempts to determine if this line is intercepting with another line segment.
  */
  LineSegmentCollisionResult intercepting(LineSegment other) {
    PVector v1 = new PVector();
    PVector v2 = new PVector();
    
    PVector p0 = origin;
    PVector p1 = destination;
    PVector p2 = other.origin;
    PVector p3 = other.destination;
    
    v1.x = p1.x - p0.x; // line p0, p1 as vector
    v1.y = p1.y - p0.y; 
    v2.x = p3.x - p2.x; // line p2, p3 as vector
    v2.y = p3.y - p2.y;
    
    float cross = (v1.x * v2.y) - (v1.y * v2.x);
    if (cross == 0) return new LineSegmentCollisionResult(false, null);
    
    PVector v3 = new PVector(p0.x - p2.x, p0.y - p2.y);
    float u2 = ((v1.x * v3.y) - (v1.y * v3.x)) / cross;
    if (u2 >= 0 && u2 <= 1) {
      float u1 = ((v2.x * v3.y) - (v2.y * v3.x)) / cross;
      if (u1 >= 0 && u1 <= 1) {
        return new LineSegmentCollisionResult(true, new PVector(p0.x + v1.x * u1, p0.y + v1.y * u1));
      }
    }
    return new LineSegmentCollisionResult(false, null);
  }
}
abstract class CircleCollider2D extends Entity {
  abstract float getRadius();
  abstract void addVelocity(PVector difference);
  abstract boolean isMovable();
  abstract void onCollide(GameObject other);
  CircleCollider2D(PVector startingPosition, PVector dimensions, EntityTexture texture) {
    super(startingPosition, dimensions, texture);
  }
}


/**
  This method calculates and determines collision between the current ball and an arbitrary object,
  described as an entity with an (x,y) coordinate, a width and a height.
  
  Returns a 2D force vector representing the new velocity of the ball.
*/
PVector collideRectangleAndBall(PVector objectPos, 
PVector objectDimensions, 
PVector ballPos, float ballWidth,
PVector ballVelocity) {
  
  float ballRadius = ballWidth/2.0;
  
  float collisionCoefficient = 0.95;
  
  // Calculate the half-width and half-height of the object.
  float objectHalfWidth = objectDimensions.x/2.0;
  float objectHalfHeight = objectDimensions.y/2.0;
  
  float ballX = ballPos.x;
  float ballY = ballPos.y;
  float ballVx = ballVelocity.x;
  float ballVy = ballVelocity.y;
  float objectX = objectPos.x;
  float objectY = objectPos.y;
  
  // Collision zone 1 (top)
  if ((ballX - ballRadius) > (objectX - objectHalfWidth) && (ballX + ballRadius) < (objectX + objectHalfWidth)
  && (ballY + ballRadius) > (objectY - objectHalfHeight) && (ballY + ballRadius < objectY)) {
    return new PVector(ballVx, collisionCoefficient * -1 * Math.abs(ballVy));
  }
  
  // Collision zone 5 (bottom)
  else if ((ballX - ballRadius) > (objectX - objectHalfWidth)
    && (ballX + ballRadius < objectX + objectHalfWidth)
    && (ballY - ballRadius) < (objectY + objectHalfHeight)
    && (ballY - ballRadius) > (objectY)) {
      return new PVector(ballVx, collisionCoefficient * Math.abs(ballVy));
  }
  
  // Collision zone 3 (right)
  else if (
    (ballX - ballRadius) < objectX + objectHalfWidth &&
    (ballX - ballRadius) > objectX &&
    (ballY > objectY - objectHalfHeight) &&
    (ballY < objectY + objectHalfHeight)
  ) {
    return new PVector(collisionCoefficient * Math.abs(ballVx), ballVy);
  }
  
  // Collision zone 7 (left)
  else if (
    (ballX + ballRadius) > objectX - objectHalfWidth &&
    (ballX + ballRadius) < objectX &&
    (ballY) > objectY - objectHalfHeight &&
    (ballY) < objectY + objectHalfHeight
  ) {
    println("l");
     return new PVector(collisionCoefficient * -1 * Math.abs(ballVx), ballVy);
  }
  
  // Collision zone 8 (corner in top-left)
  else if ((ballX + ballRadius) > (objectX - objectHalfWidth)
      && (ballX + ballRadius) < (objectX)
      && (ballY + ballRadius) > (objectY - objectHalfHeight)
      && (ballY + ballRadius) < objectY
  ) {
    return new PVector(collisionCoefficient * -1 * Math.abs(ballVx), collisionCoefficient * -1 * Math.abs(ballVy));
  }
  
  // Collision zone 2 (corner in top-right)
  else if (
    (ballX - ballRadius) < (objectX + objectHalfWidth)
    && (ballX - ballRadius) > objectX
    && (ballY + ballRadius) > (objectY - objectHalfHeight)
    && (ballY + ballRadius) < objectY
  ) {
    return new PVector(collisionCoefficient * Math.abs(ballVx), collisionCoefficient * -1 * Math.abs(ballVy));
  }
  
  // Collision zone 6 (corner in bottom-left)
  else if (
    (ballX + ballRadius) > (objectX - objectHalfWidth)
    && (ballX + ballRadius) < objectX
    && (ballY - ballRadius) < (objectY + objectHalfHeight)
    && (ballY - ballRadius) > objectY
  ) {
    return new PVector(collisionCoefficient * -1 * Math.abs(ballVx), collisionCoefficient * Math.abs(ballVy));
  }
  
  // Collision zone 4 (corner in bottom-right)
  else if (
    (ballX -ballRadius) < objectX + objectHalfWidth
    && (ballX-ballRadius) > objectX
    && (ballY-ballRadius) < objectY + objectHalfHeight
    && (ballY-ballRadius) > objectY
  ) {
    return new PVector(collisionCoefficient * Math.abs(ballVx), collisionCoefficient * Math.abs(ballVy));
  }
  
  return new PVector(ballVx, ballVy);
}
