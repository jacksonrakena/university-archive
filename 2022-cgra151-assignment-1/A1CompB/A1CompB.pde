size(400,400);
background(135, 135, 135);

int triangleSizeMin = -80; // The minimum delta between the triangle origin and its extremes
int triangleSizeLimit = 80; // The maximum delta between the triangle origin and its extremes
int numberOfTriangles = round(random(200,500)); // The number of triangles to draw to the screen
fill(255,255,255); // Set the background
for (int i = 0; i < numberOfTriangles; i++) {
  // The first co-ordinate of the triangle (the ase
  int x0 = round(random(0, 400));
  int y0 = round(random(0, 400)); 
  
  // The second co-ordinate of the triangle
  int x1 = x0 +round(random(triangleSizeMin,triangleSizeLimit)); 
  int y1 = y0 + round(random(triangleSizeMin,triangleSizeLimit));
  
  // The third co-ordinate of the triangle
  int x2 = x0 + round(random(triangleSizeMin,triangleSizeLimit));
  int y2 = y0+ round(random(triangleSizeMin,triangleSizeLimit));
  
  // Draw the shape
  beginShape();
  vertex(x0, y0);
  vertex(x1, y1);
  vertex(x2, y2);
  endShape(CLOSE);
}
