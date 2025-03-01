abstract class Game extends GameObject {
  void draw() {
    
  }
  
  void init() {
    
  }
  
  abstract String getName();
  abstract String getDescription();
}

ArrayList<Game> gameOptions = new ArrayList<Game>();

void initGames() {
  gameOptions.add(new RaceGame());
  //gameOptions.add(new FaceOffGame());
  gameOptions.add(new CollectGame());
}

void switchGame(Game g) {
  if (state.game.getName() == null) state.navigationTreeHistory.push(state.game);
  switchGameNoSave(g);
}

void switchGameNoSave(Game g) {
  state.game = g;
  state.stage = Stage.Load;
}

enum Stage {
  Load,
  Paused,
  Play
}

class MainMenu extends Game {
  String getName() { return null; }
  String getDescription() { return null; }
  void draw() {
    textSize(20);
    
    textAlign(CENTER);
    fill(0,0,100);
    textSize(40);
    text("COSMOS CONFLICT", 0.5*width, 0.1*height);
    
    float buttonStartingH = 0.3*height;
    float buttonW = 0.3*width;
    float buttonStartingWidth = (width-buttonW)/2;
    
    button(buttonStartingWidth, buttonStartingH, buttonW, 100, "PLAY", new GameSelect());
    button(buttonStartingWidth, buttonStartingH+150, buttonW, 100, "CONTROLS", new ControlsMenu());
    button(buttonStartingWidth, buttonStartingH+300, buttonW, 100, "QUIT", null);
  }
  
  void init() {}
}

class ControlsMenu extends Game {
  String getName() { return null; }
  String getDescription() { return null; }
  void draw() {
    textAlign(CENTER);
    fill(0,0,100);
    textSize(40);
    text("THE CONTROLS", 0.5*width, 0.1*height);
    
    textSize(25);
    String about = "Cosmos Conflict is a multiplayer event-based game.\nPick a game to play, or click Random.";
    String player1 = "Player 1 (the leftie), uses W-A-S-D to move.";
    String player2 = "Player 2 (the rightie) uses the arrow-keys to move.";
    String shoot = "";
    String separator = "\n\n";
    text(combine(about,player1,player2,shoot,separator,"Press backspace at any time to go back."), 0.1*width, 0.3*height, 0.8*width, 0.7*height);
  }
  
  String combine(String... inputs) {
    return String.join("\n\n", inputs);
  }
  
  void init() {}
}

class GameSelect extends Game {
  String getName() { return null; }
  String getDescription() { return null; }
  float sectionStart = 0.1*width;
  float sectionWidth = width-(sectionStart*2);
  float sectionSeparator = 0.2*sectionWidth;
  float vheight = 0.5*height;
  float selectSize = (sectionWidth-sectionSeparator)/2;

  void init() {
    state.objects.clear();
    state.objects.add(new World());
    state.player1().entity.position = new PVector((0.2*width)+(selectSize/4), (0.2*vheight)+(selectSize/2));
    state.player1().entity.dimensions = new PVector(80,80);
    state.player1().entity.angle = 180;
    
    state.player2().entity.position = new PVector((0.2*width)+(sectionSeparator)+(selectSize)+(selectSize/4), (0.2*vheight)+(selectSize/2));
    state.player2().entity.dimensions = new PVector(80,80);
    state.player2().entity.angle = 180;
    state.objects.add(state.player1().entity);
    state.objects.add(state.player2().entity);
  }
  void draw() {
    textSize(20);
    
    textAlign(CENTER);
    fill(0,0,100);
    textSize(40);
    text("Choose your color", 0.5*width, 0.1*height);
    textSize(20);
    fill(0,0,100);

    state.player1().entity.angle = state.frame%360;
    state.player2().entity.angle = state.frame%360;
    text("Player 1", sectionStart+(selectSize/2), (0.2*vheight)+selectSize+25);
    text("Use A and D to change", sectionStart+(selectSize/2), (0.2*vheight)+selectSize+50);

    text("Player 2", (sectionStart+selectSize+sectionSeparator)+(selectSize/2), (0.2*vheight)+selectSize+25);
    text("Use left and right arrow to change", (sectionStart+selectSize+sectionSeparator)+(selectSize/2), (0.2*vheight)+selectSize+50);
    
    for (PlayerState p : state.allPlayers) {
      if (p.controlScheme.left()) p.previousColor();
      if (p.controlScheme.right()) p.nextColor();
    }
    
    drawGameSelectSection();
  }
 
  
  void drawGameSelectSection() {
    fill(0,0,100);
    textSize(40);
    text("Choose your event", 0.5*width, 0.55*height);
    textSize(20);
    float gameSelectStartH = (0.6*height);
    float gameSelectSectionH = (height-gameSelectStartH);
    float randomSelectHeight = 100;
    float gameSelectBoxHeight = (0.8*gameSelectSectionH) - randomSelectHeight;
    float gameSelectStartW = 0.1*width;
    float widthOfGSelectBox = (width-(gameSelectStartW*2))/(gameOptions.size());
    for (int i = 0; i < gameOptions.size(); i++) {
      Game g = gameOptions.get(i);
      
      float x = 10+gameSelectStartW+(i*(widthOfGSelectBox));
      float y = gameSelectStartH;
      float w = widthOfGSelectBox-10;
      float h = gameSelectBoxHeight;
      
      if (regionClicked(x,y,w,h)) {
        switchGame(g);
      }
      if (!mouseInRegion(x,y,w,h)) fill(0,0,100);
      else fill(0,0,44); 
      noStroke();
      rect(x, y, w, h);
      fill(color(0,0,0));
      
      float textPadding = 0.95;
      text("\"" + g.getName() + "\"", x+(w/2), y+(0.3*h));
      text(g.getDescription(), x+(((1-textPadding)/2)*w), y+(0.3*h)+10, textPadding*w, h/2);
    }
    float x = gameSelectStartW;
    float y = gameSelectStartH+gameSelectBoxHeight+10;
    float w = width-(gameSelectStartW*2);
    float h = randomSelectHeight;
    
    if (!mouseInRegion(x,y,w,h)) fill(0,0,100);
    else fill(0,0,44); 
    rect(x,y,w,h);
    noStroke();
    fill(color(0,0,0));
    textAlign(CENTER, CENTER);
    textSize(40);
    text("RANDOM", x, y, w, h);
    
    fill(0,0,100);
    textSize(20);
    text("Player 1     " + state.player1().points + "      -      " + state.player2().points + "     Player 2", x, y+h, w, h/2);
    if (regionClicked(x,y,w,h)) {
      switchGame(gameOptions.get(randInt(0, gameOptions.size())));
    }
  }
}

class EndGame {
  PlayerState winner;
  long start = 0;
  EndGame(PlayerState winner) {
    start = System.currentTimeMillis();
  }
  
  void draw() {
    if (System.currentTimeMillis() - start < 2000) {
     text((state.player1() == winner ? "Player 1" : "Player 2") + " wins!", width/2, height/2);
    } else switchGame(new MainMenu());
  }
}
