size(400, 400);
background(255);

// Rectangle
fill(0, 0, 255);
rect(20, 20, 180, 100);

// Green elipse
fill(0, 255, 0);
ellipse(100, 200, 180, 100);

// Red triangle
fill(255, 0, 0);
triangle(20, 300, 180, 300, 90, 260);

// Yellow arrow
fill(245, 245, 66);
beginShape();
int xBase = 250; // x origin
int yBase = 60; // y origin
int widthOfArrow = 120; // width of complete shape
int heightOfArrow = round(0.6*widthOfArrow); // height of complete shape, change 0.6 to affect proportions
int widthOfBase = round(0.25*widthOfArrow); // width&height of the arrow base, change 0.25 to affect proportions

// Draw upper section of base
vertex(xBase, yBase-(widthOfBase/2));
vertex(xBase+widthOfBase, yBase-(widthOfBase/2));

// Draw vertical extremity
vertex(xBase+widthOfBase, yBase-(heightOfArrow/2));

// Draw tip
vertex(xBase+widthOfArrow, yBase);

// Draw vertical extremity
vertex(xBase+widthOfBase, yBase+(heightOfArrow/2));

// Draw lower section of base (reflection of upper section)
vertex(xBase+widthOfBase, yBase+(widthOfBase/2));
vertex(xBase, yBase+(widthOfBase/2));
endShape(CLOSE);

int lineXBase = xBase;
int lineYBase = yBase+120;
int lineXDist = 35;
int lineHeight = 180;
int distBetweenLines = 35;

strokeWeight(3);
stroke(240, 156, 38);
line(lineXBase, lineYBase, lineXBase+lineXDist, lineYBase+lineHeight);

strokeWeight(5);
stroke(119, 255, 0);
line(lineXBase+distBetweenLines, lineYBase, lineXBase+lineXDist+distBetweenLines, lineYBase+lineHeight);

strokeWeight(7);
stroke(162, 0, 255);
line(lineXBase+(distBetweenLines*2), lineYBase, lineXBase+lineXDist+(distBetweenLines*2), lineYBase+lineHeight);
