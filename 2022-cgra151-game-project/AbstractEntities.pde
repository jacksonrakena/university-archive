/**
  GameObject is the root of all entities in Cosmos Conflict.
  
  It stores helper functions and data such as the frame at which
  this object was created, when it died/was disabled, and how long it has lived for.
  
  All objects in Cosmos Conflict descend from GameObject.
*/
abstract class GameObject {
  long wasSpawnedAtFrame;
  long wasSpawnedAtTime;
  long wasDisabledAtFrame = -1;
  long lastOperationFrame = 0;
  
  long getLifetimeMilliseconds() { return System.currentTimeMillis() - this.wasSpawnedAtTime; }
  long getLifetime() { return state.frame - wasSpawnedAtFrame; }
  boolean isEnabled() { return wasDisabledAtFrame == -1; }
  
  void disable() { wasDisabledAtFrame = state.frame; }
  void draw() {}
  void step() {}
  
  GameObject() {
    this.wasSpawnedAtTime = System.currentTimeMillis();
    this.wasSpawnedAtFrame = state.frame;
  }
}

/**
  Entity represents an in-world GameObject with physics and texture behaviour.
  
  It stores velocity, position, and dimensions vectors, as well as an EntityTexture,
  that is rendered every frame.
  
  GameObjects that are not Entity objects are usually non-diegetic or ephemeral,
  and do not live in the game world.
*/
abstract class Entity extends GameObject {
   PVector velocity = new PVector(0,0);
   PVector position;
   PVector dimensions;
   EntityTexture texture;
   
   Entity(PVector position, PVector dimensions, EntityTexture texture) {
     this.position = position;
     this.dimensions = dimensions;
     this.texture = texture;
   }
   
   void draw() {
     super.draw();
     
     // Draw the linked texture.
     this.texture.draw(this.position, this.dimensions);
   }
}

class TriangleEntity extends Entity implements BoxCollider {
  float momentumCoefficient = 0.97;
  int acceleratingStatus = 0;
  float angle = 0;
  
  TriangleEntity(PVector startingPosition, PVector dimensions, EntityTexture texture) {
    super(startingPosition, dimensions, texture);
  }
  
  void step() {
    super.step();
    this.position.add(this.velocity);
    
    // Apply momentum
    velocity = velocity.mult(momentumCoefficient);
  }
  
  LineSegment[] getCollidableSegments() {
    float nw = this.dimensions.x;
    float nh = 1.2*nw;
    
    float halfw = nw/2;
    float halfh = nh/2;
    PVector position = this.position.copy();
    float rads = radians(this.angle);
    LineSegment[] result = new LineSegment[4];
    PVector v0 = new PVector(0, 0-halfh).rotate(rads).add(position);
    PVector v1 = new PVector(0-halfw, 0+halfh).rotate(rads).add(position);
    PVector v2 = new PVector(0, halfh/2).rotate(rads).add(position);
    PVector v3 = new PVector(0+halfw, 0+halfh).rotate(rads).add(position);
    result[0] = new LineSegment(v0, v1);
    result[1] = new LineSegment(v1, v2);
    result[2] = new LineSegment(v2, v3);
    result[3] = new LineSegment(v3, v0);
    return result;
  }
}

abstract class CircleEntity2D extends CircleCollider2D  {
  float momentumCoefficient = 0.98;
  float maximumVelocity = 5;
  float acceleration = 0.05;
  float width;
  EntityTexture texture;
  boolean disabled = false;
  
  CircleEntity2D(PVector startingPosition, float width, EntityTexture texture) {
    super(startingPosition, new PVector(width, width), texture);

    this.width = width;
    this.texture = texture;
  }
  
  boolean isMovable() { return true; }
  
  float getRadius() { return this.width/2; }
  
  void setMomentum(float m) {
    this.momentumCoefficient = m;
  }
  
    
  void addVelocity(PVector difference) {
    this.velocity.x = max(0-maximumVelocity, min(maximumVelocity, this.velocity.x+difference.x));
    this.velocity.y = max(0-maximumVelocity, min(maximumVelocity, this.velocity.y+difference.y));
  }
  
  boolean colliding(CircleCollider2D other) {
    if (disabled || (other instanceof CircleEntity2D && ((CircleEntity2D) other).disabled)) return false;
    return Math.hypot(abs(this.position.x-other.position.x), abs(this.position.y-other.position.y)) <= (this.getRadius()+other.getRadius());
  }
  
  void step() {
    if (disabled) return;
    this.position.add(this.velocity);
    
    if (!DEBUG_disableAccelerationRampAndMomentum) this.velocity.x *= momentumCoefficient;
    else this.velocity.x = 0;
    
    if (abs(this.velocity.x) < 0.05) this.velocity.x = 0;
    
    if (!DEBUG_disableAccelerationRampAndMomentum) this.velocity.y *= momentumCoefficient;
    else this.velocity.y = 0;
    
    if (abs(this.velocity.y) < 0.05) this.velocity.y = 0;
  }
  
  void draw() {
    if (disabled) return;
    super.draw();
  }
}
