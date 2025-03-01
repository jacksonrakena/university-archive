final int distanceBetweenShots = 50;

class FaceOffGame extends Game {
  GameMap foMap;
  ArrayList<PlayerProjectileController> controllers;
  PlayerHealthController player1;
  PlayerHealthController player2;
  
  String getName() { return "1v1"; }
  String getDescription() { return "Fight against your opponent with a variety of weapons."; }
  
  void init() {
    println("Initialising face-off.");
    this.foMap = readMap("map_faceoff.csv");
    player1 = new PlayerHealthController(state.player1(), 1);
    player2 = new PlayerHealthController(state.player2(), 2);
    state.objects.add(player1);
    state.objects.add(player2);
    controllers = new ArrayList<PlayerProjectileController>();
    controllers.add(new PlayerProjectileController(state.player1(), player1));
    controllers.add(new PlayerProjectileController(state.player2(), player2));
    for (MapMarker m : this.foMap.markers) {
      if (m.id == "w") state.objects.add(new Wall(m.x, m.y, m.width, m.height, new DefaultWallTexture()));
    }
    state.player1().entity.position = new PVector(100,100);
    state.player2().entity.position = new PVector(400,400);
    state.objects.add(state.player1().entity);
    state.objects.add(state.player2().entity);
  }
  void draw() {
    for (PlayerProjectileController controller : controllers) {
      controller.step();
    }
  }
}

PVector calculateForwardVector(float angle) {
  return new PVector((float)(-1 * Math.sin(radians(angle+180))), (float)(Math.cos(radians(angle+180))));
}

// Responsible for storing player health in Face Off, and
// drawing the health bar.
class PlayerHealthController extends GameObject {
  PlayerHealthController(PlayerState player, int number) {
    this.player = player;
    this.number = number;
  }
  PlayerState player;
  int number;
  final int PLAYER_MAX_HP = 100;
  final int PLAYER_PROJECTILE_DAMAGE = 10;
  int hp = PLAYER_MAX_HP;
  void receive() {
    hp -= PLAYER_PROJECTILE_DAMAGE;
    if (hp <= 0) switchGame(new RoundEndGame(this.number == 1 ? 2 : 1));
  }
  
  void draw() {
    float startDrawPos = number == 1 ? (0.03*width) : (0.85*width);
    float startDrawY = 0.9*height;
    noStroke();
    fill(0,0,100);
    rect(startDrawPos, startDrawY, 100, 5);
    fill(player.chosenColor);
    rect(startDrawPos, startDrawY, 100*((float)hp/PLAYER_MAX_HP), 5);
    textAlign(LEFT);
    text("Player " + number, startDrawPos, (startDrawY)+30);
  }
}

class PlayerProjectileController {
  PlayerState player;
  PlayerHealthController health;
  long lastFrameFired = 0;
  ArrayList<CircleEntity2D> projectiles = new ArrayList<CircleEntity2D>();
  public PlayerProjectileController(PlayerState player, PlayerHealthController health) {
    this.player = player;
    this.health = health;
  }
  void step() {
    if (this.player.controlScheme.fire()) {
      if (lastFrameFired == 0 || state.frame > lastFrameFired + distanceBetweenShots) {
        lastFrameFired = state.frame; 
        fire();
      }
    }
    for (CircleEntity2D projectile : projectiles) {
      
    }
  }
  
  void fire() {
    PVector position = this.player.entity.position.copy().add(
        calculateForwardVector(this.player.entity.angle).copy().mult(50)
    );
    CircleEntity2D projectile = new Projectile(position, 20, new PuckEntityTexture(this.player.chosenColor), this);
    projectile.setMomentum(1);
    //projectile.addVelocity(this.player.entity.calculateForwardVector().copy().normalize().mult(200));
    projectile.addVelocity(calculateForwardVector(this.player.entity.angle).copy().mult(50));
    projectiles.add(projectile);
    state.objects.add(projectile);
  }
}

class Projectile extends CircleEntity2D {
  PlayerProjectileController other;
  boolean enabled() { return !this.disabled; }
  Projectile(PVector startingPosition, float width, EntityTexture texture, PlayerProjectileController other) {
    super(startingPosition, width, texture);
    this.other = other;
  }
  void onCollide(GameObject other) {
    println("collide");
    if (other instanceof PlayerEntity && other == this.other.player.entity) {
      println("collide with player");
      this.disabled = true;
      this.other.health.receive();
    }
  }
}
