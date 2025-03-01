class CompositeSpriteMap {
  String location;
  float scale;
  PImage master;
  PImage[][] maps;
  CompositeSpriteMap(String spriteMapLocation, int pieceWidth, int pieceHeight, float scale) { 
    location = spriteMapLocation;
    this.scale = scale;
    
    this.master = loadImage(spriteMapLocation);
    int numberOfRows = this.master.height/pieceHeight;
    
    maps = new PImage[numberOfRows][];
    for (int i = 0; i < numberOfRows; i++) {
      maps[i] = new PImage[this.master.width/pieceWidth];
      for (int h = 0; h < this.master.width/pieceWidth; h++) {
        PImage sprite = createImage(pieceWidth, pieceHeight, ARGB);
        sprite.copy(this.master, h*pieceWidth, i*pieceHeight, pieceWidth, pieceHeight, 0, 0, pieceWidth, pieceHeight);
        maps[i][h] = sprite; 
      }
    }
  }
  
  PImage[] getMap(int index) { return this.maps[index]; }
}
