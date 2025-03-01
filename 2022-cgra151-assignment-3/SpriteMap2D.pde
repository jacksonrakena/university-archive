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
    else sprite = (int) Math.floor((map.sprites[0].length)*progress);
    map.draw(0, sprite, x, y);
  }
  
  void reset() {
    frame = 0;
  }
  
  boolean finished() {
    return (frame/animationSpeed) >= 0.98;
  }
}

class SpriteMap2D {
  PImage[][] sprites;
  String location;
  float scale;
  
  SpriteMap2D(String spriteMapLocation, int pieceWidth, int pieceHeight, float scale) { 
    location = spriteMapLocation;
    this.scale = scale;
    
    PImage masterImage = loadImage(spriteMapLocation);
    int numberOfVerticalSprites = masterImage.height/pieceHeight;
    int numberOfHorizontalSprites = masterImage.width/pieceWidth;
    
    sprites = new PImage[numberOfVerticalSprites][numberOfHorizontalSprites];
    for (int i = 0; i < numberOfVerticalSprites; i++) {
      for (int h = 0; h < numberOfHorizontalSprites; h++) {
        PImage sprite = createImage(pieceWidth, pieceHeight, ARGB);
        sprite.copy(masterImage, h*pieceWidth, i*pieceHeight, pieceWidth, pieceHeight, 0, 0, pieceWidth, pieceHeight);
        sprites[i][h] = sprite; 
      }
    }
    rows = sprites.length;
  }
  
  PImage get(int row, int col) {
    return sprites[row][col];
  }
  
  int cols(int row) {
    return sprites[row].length;
  }
  
  int rows;
  
  
  void draw(int row, int col, int x, int y) {
    //scale(scale);
    image(get(row, col), x, y);
  }
}
