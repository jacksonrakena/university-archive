void setup() {
  size(400,400);
}
void draw() {
  // Clear the screen and draw the background
  clear();
  background(135, 135, 135);
  
  // Set the color to blue if the mouse is pressed
  if (mousePressed) {
    fill(255, 0, 0);
  }
  // Otherwise set it to blue
  else {
    fill(0,0,255);
  }
  // Draw the circle
  circle(mouseX, mouseY, 50);
}
