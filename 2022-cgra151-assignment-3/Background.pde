class BackgroundLayer {
  int z;
  PImage file;
  float scale;
  
  BackgroundLayer(String name, float inscale, int z) {
    file = loadImage("background/" + name + "_sm.png");
    scale = inscale;
    this.z = z;
  }
  
  void draw(float x, float y) {
    float xoffset = (int) ((x == 0 ? 1 : x)/z);
    float yoffset = (int) ((y == 0 ? 1 : y)/z);
    float lastxend = width;
    //image(file, xoffset, yoffset);
    image(file, xoffset, yoffset, file.width*scale, file.height*scale);
    lastxend = xoffset+(file.width*scale);
    while (xoffset > 0) {
      xoffset = xoffset - (file.width*scale);
      image(file, xoffset, yoffset, file.width*scale, file.height*scale);
    }
    if (lastxend < width) {
      while (lastxend < width) {
        image(file, xoffset+(file.width*scale), yoffset, file.width*scale, file.height*scale);
        lastxend += (file.width*scale);
      }
    }
  }
}
