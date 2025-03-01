void setup() {
  size(800,800);  
}
void draw() {
  if (mousePressed) {
    return;
  }
  clear();
  int windowWidth = 800;
  int windowHeight = 800;
  int lineHeight = 0; // The height of each line
  // Loop over each line
  while (lineHeight < windowHeight) {
    int xOffset = 0;
    int innerLineHeight = round(random(10,50));
    
    // Draw rectangles on the line until we reach the end of the window
    while (xOffset < windowWidth) {    
      // Calculate the width of the rectangle
      int w = round(random(5,100));
      int h = round(random(1, innerLineHeight));
      
      // Draw the first rectangle of random height
      fill(random(0,255),random(0,255),random(0,255));
      rect(xOffset, lineHeight, xOffset+w, (lineHeight)+h);
      
      // Draw a rectangle to fill the remaining height
      fill(random(0,255),random(0,255),random(0,255));
      rect(xOffset, lineHeight+h, xOffset+w, lineHeight+innerLineHeight);
     
      // Push the offset to the right by the width of what was just drawn
      xOffset+=(w);
    }
    lineHeight+=innerLineHeight;
  }
}
