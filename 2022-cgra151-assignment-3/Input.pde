import java.util.Map;

boolean[] inputs = new boolean[400];

void keyPressed() {
  if (keyCode < inputs.length) inputs[keyCode] = true;
}

void keyReleased() {
  if (keyCode < inputs.length) inputs[keyCode] = false;
}

boolean getInput(int keyCode) {
  return keyCode < inputs.length && inputs[keyCode];
}

void debug_printInputs() {
  int pressed = 0;
  for (int i = 0; i < 400; i++) {
    if (getInput(i)) {
      String value = String.valueOf((char)i);
      if (i==RIGHT)value="R";
      if (i==LEFT)value="L";
      if (i==UP)value="U";
      if (i==DOWN)value="D";
      text(value, 20*pressed, 20);
      pressed++;
    }
  }
}
