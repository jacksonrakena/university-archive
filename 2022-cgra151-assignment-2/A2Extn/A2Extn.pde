float x0 = 0;
float y0 = 0;
float x1 = 0;
float y1 = 1;

void setup() {
  size(400,400);
  noSmooth();
}

void mousePressed() {
  x0 = mouseX;
  y0 = mouseY;
  x1 = mouseX;
  y1 = mouseY;
}


void customLine(float x0, float x1, float y0, float y1) {
  float a = -1 * (y1-y0);
  float b = (x1-x0);
  float c = (x0*y1)-(x1*y0);
  
  int x = round(x0);
  int y = round(((-a*x)-c)/b);
  println("x=" + x + "y=" + y);
  float k = a * (x+1) + b *(y+0.5)+c;
  
  while (x <= x1) {
    point(x,y);
    if (k > 0) {
      k = k+a;
    }
    else {
      k = k + a + b;
      y = y + 1;
    }
    x = x + 1;
  }
}

void mouseReleased() {
  x1 = mouseX;
  y1 = mouseY;
  
  float gradient = (x1-x0)/(y1-y0);
  
  stroke(0,0,0);
  customLine(x0,x1,y0,y1);
  
  stroke(255,0,0);
  
  line(x0,y0,x1,y1);
}

void draw() {
  
}
