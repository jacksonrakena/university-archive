class RaceMarker extends GameObject implements BoxCollider {
  float x, y, w, h;
  Animation icon;
  RaceMarker(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    icon = new Animation(new SpriteMap2D("flag.png", 60, 1), 50);
  }
  
  void draw() {
    fill(0, 93, 97);
    noStroke();
    //rect(this.x, this.y, this.w, this.h);
    icon.draw((int) this.x, (int) this.y);
  }
  
  LineSegment[] getCollidableSegments() {
    return generateLineSegmentsFromBox(x, y, w, h);
  }
}

class RoundEndGame extends Game {
  int winningPlayer;
  RoundEndGame(int playerWhoWon) {
    winningPlayer = playerWhoWon;
    (playerWhoWon == 1 ? state.player1() : state.player2()).points++;
  }
  String getName() { return null; }
  String getDescription() { return null; }
  void draw() {
    fill(0,0,100);
    text("Player " + winningPlayer + " won this round.\nGoing back to game select in 5 seconds.", height/2, width/2);
    if (this.getLifetimeMilliseconds() > 5000) switchGame(new GameSelect());
  }
}

class RaceGame extends Game {
  GameMap raceMap;
  String getName() { return "Maze Race"; }
  String getDescription() { return "Race against your opponent through a randomly-generated map."; }
  RaceMarker goal;
  void draw() {
    if (areBoxCollidersColliding(state.player1().entity, goal)) {
      handleWin(1);
    } else if (areBoxCollidersColliding(state.player2().entity, goal)) {
      handleWin(2);
    }
  }
  
  void handleWin(int player) {
    switchGame(new RoundEndGame(player));
  }
  
  void init() {
    println("Initialising race.");
    this.raceMap = readMap("map_race.csv");
    state.player1().entity.position = new PVector(20, 20);
    
    for (MapMarker marker : this.raceMap.markers) {
      if (marker.id.equals("w")) state.objects.add(new Wall(marker.x, marker.y, marker.width, marker.height, new DefaultWallTexture()));
      else {
       switch (marker.data) {
        case "start0":
          state.player1().entity.position = new PVector(marker.x, marker.y);
          state.player1().entity.angle = 180;
          state.objects.add(state.player1().entity);
          break;
        case "start1":
          state.player2().entity.position = new PVector(marker.x, marker.y);
          state.player2().entity.angle = 180;
          state.objects.add(state.player2().entity);
          break;
        case "goal":
          goal = new RaceMarker(marker.x, marker.y, marker.width, marker.height);
          break;
        default:
          println("Unknown marker type " + marker.data);
          break;
        } 
      }
    }
    state.objects.add(goal);
  }
}
