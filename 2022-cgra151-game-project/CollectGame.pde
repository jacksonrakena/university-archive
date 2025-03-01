class CollectGame extends Game {
  GameMap foMap;
  ArrayList<PlayerProjectileController> controllers;
  CollectFlag flag0;
  CollectFlag flag1;
  ArrayList<CollectionZone> cz0 = new ArrayList<CollectionZone>();
  ArrayList<CollectionZone> cz1 = new ArrayList<CollectionZone>();
  
  String getName() { return "Capture The Flag"; }
  String getDescription() { return "Capture your opponent's flag before they get yours."; }
  
  void init() {
    println("Initialising ctf.");
    this.foMap = readMap("map_ctf.csv");
    controllers = new ArrayList<PlayerProjectileController>();
    controllers.add(new PlayerProjectileController(state.player1(), null));
    controllers.add(new PlayerProjectileController(state.player2(), null));
    for (MapMarker m : this.foMap.markers) {
      if (m.data.equals("start0")) state.player1().entity.position = new PVector(m.x, m.y);
      if (m.data.equals("start1")) state.player2().entity.position = new PVector(m.x, m.y);
      if (m.data.equals("flag0")) flag0 = new CollectFlag(m.x, m.y, m.width, m.height, state.player1(), state.player2(), this);
      if (m.data.equals("flag1")) flag1 = new CollectFlag(m.x, m.y, m.width, m.height, state.player2(), state.player1(), this);
      if (m.data.equals("cz0")) cz0.add(new CollectionZone(1, m.x, m.y, m.width, m.height));
      if (m.data.equals("cz1")) cz1.add(new CollectionZone(2, m.x, m.y, m.width, m.height));
      if (m.id.equals("w")) {
        if (m.data.equals("p0")) state.objects.add(new Wall(m.x, m.y, m.width, m.height, new ColoredWallTexture(state.player1().chosenColor)));
        else if (m.data.equals("p1")) state.objects.add(new Wall(m.x, m.y, m.width, m.height, new ColoredWallTexture(state.player2().chosenColor)));
        else state.objects.add(new Wall(m.x, m.y, m.width, m.height, new DefaultWallTexture())); 
      }
    }
    state.objects.add(state.player1().entity);
    state.objects.add(state.player2().entity);
    state.objects.addAll(cz0);
    state.objects.addAll(cz1);
    state.objects.add(flag0);
    state.objects.add(flag1);
  }
  void draw() {
    for (PlayerProjectileController controller : controllers) {
      controller.step();
    }
    for (PlayerState p : state.allPlayers) {
      if (p == state.player1()) {
        if (flag1.heldByEnemy) {
          for (CollectionZone c : cz0) {
            if (p.entity.position.x > c.x && p.entity.position.x < c.x+c.w && p.entity.position.y > c.y && p.entity.position.y < c.y+c.h) {
              flag1.heldByEnemy = false;
              flag1.inEnemyBase = true;
             
            }
          }
        }
        
        if (flag1.inEnemyBase && flag0.inHomeBase) switchGame(new RoundEndGame(1));
      }
      if (p == state.player2()) {
        if (flag1.heldByPlayer) {
          for (CollectionZone c : cz1) {
            if (p.entity.position.x > c.x && p.entity.position.x < c.x+c.w && p.entity.position.y > c.y && p.entity.position.y < c.y+c.h) {
              flag1.heldByPlayer = false;
              flag1.inHomeBase = true;
              flag1.inEnemyBase = false;
            }
          }
        }
        if (flag0.heldByEnemy) {
          for (CollectionZone c : cz1) {
            if (p.entity.position.x > c.x && p.entity.position.x < c.x+c.w && p.entity.position.y > c.y && p.entity.position.y < c.y+c.h) {
              flag0.heldByEnemy = false;
              flag0.inEnemyBase = true;
            }
          }
        }
        
        if (flag0.inEnemyBase && flag1.inHomeBase) switchGame(new RoundEndGame(2));
      }
      
      for (PlayerState o : state.allPlayers) {
        if (o == p) continue;
        if (areBoxCollidersColliding(o.entity,p.entity)) {
          if (flag0.heldByEnemy) {
            flag0.heldByEnemy = false;
            println("Flag 0 dropped.");
          }
          if (flag1.heldByEnemy) flag1.heldByEnemy = false;
        }
      }
    }
  }
}

interface SelfColliderHandler {
  void onCollide(GameObject other);
}

class CollectFlag extends GameObject implements BoxCollider, SelfColliderHandler {
  float x, y, w, h;
  PlayerState owner;
  PlayerState enemy;
  boolean heldByEnemy;
  boolean heldByPlayer;
  boolean inEnemyBase = false;
  boolean inHomeBase = true;
  boolean moved = false;
  double lastPickUp;
  CollectGame game;
  CollectFlag(float x, float y, float w, float h, PlayerState owner, PlayerState enemy, CollectGame controller) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.enemy = enemy;
    this.game = controller;
    this.owner = owner;
  }
  
  void step() {
    if (this.heldByEnemy) {
      this.x = this.enemy.entity.position.x;
      this.y = this.enemy.entity.position.y;
    }
    if (this.heldByPlayer) {
      this.x = this.owner.entity.position.x; 
      this.y = this.owner.entity.position.y;
    }
  }
  
  void draw() {
    fill(this.owner.chosenColor);
    noStroke();
    rect(this.x, this.y, this.w, this.h);
  }
  
  void onCollide(GameObject other) {
    if (other instanceof PlayerEntity && other == this.enemy.entity) {
      if (System.currentTimeMillis() - lastPickUp > 500) {
        println("Flag picked up by enemy");
        this.heldByEnemy = true;
        this.moved = true;
        this.inHomeBase = false; 
        lastPickUp = System.currentTimeMillis();
      }
    } else if (other instanceof PlayerEntity && other == this.owner.entity && this.moved) {
      if (System.currentTimeMillis() - lastPickUp > 500) {
        println("Flag picked up by owner");
        this.heldByPlayer = true;
        this.heldByEnemy = false;
        lastPickUp = System.currentTimeMillis();
      }
    }
  }
  
  LineSegment[] getCollidableSegments() {
    return (heldByEnemy || heldByPlayer) ?  new LineSegment[3] : generateLineSegmentsFromBox(x, y, w, h);
  }
}

class CollectionZone extends GameObject {
  int player;
  float x, y, w, h;
  CollectionZone(int p, float x, float y, float w, float h) {
    player = p;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  void draw() {
    fill(0,0,100);
    rect(x,y,w,h);
  }
}
