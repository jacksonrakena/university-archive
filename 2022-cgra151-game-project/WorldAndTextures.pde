class BgStar {
  int x;
  int y;
  float w;
  float h;
  long duration;
  long currentFrame;
  boolean destroyed;
  void cp_star(float x, float y, float radius1, float radius2, int npoints) {
    noStroke();
    float angle = TWO_PI / npoints;
    float halfAngle = angle/2.0;
    beginShape();
    for (float a = 0; a < TWO_PI; a += angle) {
      float sx = x + cos(a) * radius2;
      float sy = y + sin(a) * radius2;
      vertex(sx, sy);
      sx = x + cos(a+halfAngle) * radius1;
      sy = y + sin(a+halfAngle) * radius1;
      vertex(sx, sy);
    }
    endShape(CLOSE);
  }
  void stepAndDraw() {
    currentFrame++;
    if (currentFrame > duration) {
      this.destroyed = true;
    }
    
    if (!this.destroyed) {
      fill(0,0,100,75);
      double pg = (currentFrame == 0 ? 1 : currentFrame)/(float)this.duration;
      float width = (float)(this.w*(sin((float)((Math.PI)*pg))));
      float height = (float)(this.h*(sin((float)((Math.PI)*pg))));
      cp_star(this.x, this.y, (width+height)/2, 0.4*((width+height)/2), 5);

    }
  }
  
  public BgStar(int x, int y, float w, float h, long duration, long durationOffset) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.duration = duration;
    this.currentFrame = durationOffset;
  }
}
ArrayList<BgStar> stars;

float rand(float min, float max) {
  return (float) ((Math.random()*(max-min))+min);
}
int randInt(int min, int max) {
  return (int) Math.floor((Math.random()*(max-min))+min);
}

class World extends GameObject implements LineCollider {
  LineSegment[] getLines() {
    LineSegment[] result = new LineSegment[4];
    result[0] = new LineSegment(new PVector(0,0), new PVector(width, 0));
    result[1] = new LineSegment(new PVector(0,0), new PVector(0, height));
    result[2] = new LineSegment(new PVector(width, 0), new PVector(width, height));
    result[3] = new LineSegment(new PVector(0, height), new PVector(width, height));
    return result;
  }
  
  void draw() {
    float starWmin = 5;
    float starWmax = 12;
    float starHmin = 5;
    float starHmax = 12;
    
    float progressionPct = (state.frame%Globals.backgroundCycleLength)/(float)Globals.backgroundCycleLength;
    if (stars == null) {
      stars = new ArrayList<BgStar>();
      for (int i = 0; i < 20; i++) {
        stars.add(new BgStar((int) Math.floor(Math.random()*width), (int) Math.floor(Math.random()*height), rand(starWmin, starWmax), rand(starHmin, starHmax), Globals.backgroundCycleLength, (long)(Math.random()*Globals.backgroundCycleLength)));
      }
    }
    ArrayList<BgStar> newStars = new ArrayList<BgStar>();
    for (BgStar s : stars) {
      s.stepAndDraw();
      if (s.destroyed) {
        newStars.add(new BgStar((int) Math.floor(Math.random()*width), (int) Math.floor(Math.random()*height), rand(starWmin, starWmax), rand(starHmin, starHmax), Globals.backgroundCycleLength, 0));
      } else newStars.add(s);
    }
    stars = newStars;
  }
}

class Wall extends GameObject implements BoxCollider {
  boolean enabled() { return true; }
  PVector dimensions;
  PVector position;
  EntityTexture texture;
 
  Wall(float x, float y, float width, float height, EntityTexture texture) { 
    this.dimensions = new PVector(width, height);
    this.position = new PVector(x, y);
    this.texture = texture;
  }
  
  void draw() {
    super.draw();
    texture.draw(position, dimensions);
  }
  
  LineSegment[] getCollidableSegments() {
    LineSegment[] result = new LineSegment[4];
    
    PVector topLeft = this.position.copy();
    PVector topRight = topLeft.copy().add(new PVector(dimensions.x, 0));
    PVector bottomRight = topLeft.copy().add(this.dimensions);
    PVector bottomLeft = topLeft.copy().add(new PVector(0, dimensions.y));
    
    // Top side
    result[0] = new LineSegment(topLeft, topRight);
    
    // Left side
    result[1] = new LineSegment(topLeft, bottomLeft);
    
    // Right side
    result[2] = new LineSegment(topRight, bottomRight);
    
    // Bottom side
    result[2] = new LineSegment(bottomLeft, bottomRight);
    
    return result;
  }
}

void button(float x, float y, float w, float h, String label, Game action) {
  textAlign(CENTER, CENTER);
  if (!mouseInRegion(x,y,w,h)) fill(0,0,100);
  else fill(0,0,44); 
  rect(x, y, w, h);
  fill(0,0,0);
  text(label, x, y, w, h);
  if (regionClicked(x,y,w,h)) {
    if (action == null) {
      exit();
    }
    switchGame(action);
  }
}

/**
 Represents an interface for a generic entity texture.
*/
interface EntityTexture {
  void draw(PVector position, PVector dimensions);
}

color changeBrightness(color in, float factor) {
  float r = min(255, red(in)*factor);
  float g = min(255, green(in)*factor);
  float b = min(255, blue(in)*factor);
  return color(r,g,b);
}

/*
 Represents the texture for drawing player spaceships to the screen.
*/
class PlayerSpaceshipTexture implements EntityTexture {
  PlayerState player;
  PlayerSpaceshipTexture(PlayerState p) {
    this.player = p;
  }
  
  void draw(PVector position, PVector dimensions) {
    if (this.player.entity == null) return;
    LineSegment[] textures = this.player.entity.getCollidableSegments();
    
    stroke(this.player.chosenColor);
    strokeWeight(0.25*Globals.playerWidth);

    for (LineSegment l : textures) {
      line(l.origin.x, l.origin.y, l.destination.x, l.destination.y);
    }
    
    float acceleratingStatus = this.player.entity.velocity.mag() / 5;
    strokeWeight(0);
    if (acceleratingStatus != 0) {
      float radius = 8;
      if (acceleratingStatus >= 0.3) {
        fill(55, 100, 100);
        PVector pos = new PVector(0, 25).rotate(radians(this.player.entity.angle)).add(position);
        circle(pos.x, pos.y, radius);
      }
      if (acceleratingStatus >= 0.6) {
        fill(46, 100, 100);
        PVector pos = new PVector(0, 40).rotate(radians(this.player.entity.angle)).add(position);
        circle(pos.x, pos.y, radius);
      }
      if (acceleratingStatus >= 0.9) {
        fill(0, 100, 100);
        PVector pos = new PVector(0, 55).rotate(radians(this.player.entity.angle)).add(position);
        circle(pos.x, pos.y, radius);
      }
    }
  }
}

class PuckEntityTexture implements EntityTexture {
  int baseColor;
  PuckEntityTexture(int base) {
    this.baseColor = base;
  }
  void draw(PVector position, PVector dimensions) {
    noStroke();
    color black = color(0,0,0);
    
    fill(baseColor);
    circle(position.x,position.y,dimensions.x);
    
    fill(black);
    circle(position.x,position.y,dimensions.x*(2.0/3));
    
    fill(baseColor);
    circle(position.x,position.y,dimensions.x*(1.0/3));
  }
}

class ColoredWallTexture implements EntityTexture {
  color wallColor;
  ColoredWallTexture(color c) {
    this.wallColor = c;
  }
  void draw(PVector position, PVector dimensions) {
    fill(this.wallColor);
    noStroke();
    rect(position.x, position.y, dimensions.x, dimensions.y);
  }
}

class DefaultWallTexture implements EntityTexture {
  void draw(PVector position, PVector dimensions) {
    fill(color(0,0,100));
    noStroke();
    rect(position.x, position.y, dimensions.x, dimensions.y);
  }
}

class Animation {
  int frame;
  SpriteMap2D map;
  float animationSpeed;
  
  Animation(SpriteMap2D map, float animationSpeed) {
    this.map = map;
    this.animationSpeed = animationSpeed;
  }
  
  void draw(int x, int y) {
    int sprite = 0;

    frame++;
    double progress = frame/animationSpeed;
    if (progress >= 1) {
      frame = 0;
      sprite = 0;
    }
    else sprite = (int) Math.floor((map.sprites.length)*progress);
    map.draw(sprite, x, y);
  }
  
  void reset() {
    frame = 0;
  }
  
  boolean finished() {
    return (frame/animationSpeed) >= 0.98;
  }
}

class SpriteMap2D {
  PImage[] sprites;
  String location;
  float scale;
  
  SpriteMap2D(String spriteMapLocation, int pieceWidth, float scale) { 
    location = spriteMapLocation;
    this.scale = scale;
    
    PImage masterImage = loadImage(spriteMapLocation);
    int numberOfHorizontalSprites = masterImage.width/pieceWidth;
    
    sprites = new PImage[numberOfHorizontalSprites];
    for (int i = 0; i < numberOfHorizontalSprites; i++) {
      PImage sprite = createImage(pieceWidth, masterImage.height, ARGB);
      sprite.copy(masterImage, i*pieceWidth, 0, pieceWidth, masterImage.height, 0, 0, pieceWidth, masterImage.height);
      sprites[i] = sprite; 
    }
  }
  
  
  void draw(int n, int x, int y) {
    image(this.sprites[n], x, y);
  }
}
