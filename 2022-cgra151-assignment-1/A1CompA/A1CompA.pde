size(400,400);
int numberOfLines = 25; // The number of lines on the screen
int lineHeight = round(400/numberOfLines); // The height of each line
int widthOfSeparatorRect = 5; // The width of the grey rectangle between each rectangle

// Loop over each line
for (int line = 0; line < numberOfLines; line++) {
  int xOffset = 0;
  
  // Draw rectangles on the line until we reach the end of the window
  while (xOffset < 400) {    
    // Calculate the width of the rectangle
    int w = round(random(5,20));
    
    // Draw the white rectangle
    fill(255,255,255);
    rect(xOffset, line*lineHeight, xOffset+w, (line*lineHeight)+lineHeight);
    
    // Draw the gray separator rectangle
    fill(135, 135, 135);
    rect(xOffset+w, line*lineHeight, xOffset+w+widthOfSeparatorRect, (line*lineHeight)+lineHeight);
    
    // Push the offset to the right by the width of what was just drawn
    xOffset+=(w+widthOfSeparatorRect);
  }
}
