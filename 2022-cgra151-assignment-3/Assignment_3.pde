ArrayList<BackgroundLayer> bgs;
Map<AnimationState, Animation> animationMap;
AnimationState state;
int cx = 0;
int px = 200;
int py = 290;
double walkSpeed = 2;
enum AnimationState {
  RunLeft,
  RunRight,
  IdleLeft,
  IdleRight,
  AttackLeft,
  AttackRight,
  JumpLeft,
  JumpRight
}
void setup() {
  size(800,350);
  noSmooth();
  println(cx);
  animationMap = new HashMap<AnimationState, Animation>();
  state = AnimationState.IdleLeft;
  animationMap.put(AnimationState.RunLeft, new Animation(new SpriteMap2D("character/run_l.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.RunRight, new Animation(new SpriteMap2D("character/run_r.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.IdleLeft, new Animation(new SpriteMap2D("character/idle_l.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.IdleRight, new Animation(new SpriteMap2D("character/idle_r.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.AttackLeft, new Animation(new SpriteMap2D("character/attack_l.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.AttackRight, new Animation(new SpriteMap2D("character/attack_r.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.JumpLeft, new Animation(new SpriteMap2D("character/jump_l.png", 56, 56, 1), 100));
  animationMap.put(AnimationState.JumpRight, new Animation(new SpriteMap2D("character/jump_r.png", 56, 56, 1), 100));
  bgs = new ArrayList<BackgroundLayer>();
  bgs.add(new BackgroundLayer("background_layer_1", 2, 1));
  bgs.add(new BackgroundLayer("background_layer_2", 2, 2));
  bgs.add(new BackgroundLayer("background_layer_3", 2, 3));
}

void draw() {
  clear();
  if ((state == AnimationState.JumpLeft || state == AnimationState.JumpRight) && animationMap.get(state).finished()) {
    state = (state == AnimationState.JumpLeft) ? AnimationState.IdleLeft : AnimationState.IdleRight;
  }
  else if ((state == AnimationState.AttackLeft || state == AnimationState.AttackRight) && animationMap.get(state).finished()) {
    state = (state == AnimationState.AttackLeft) ? AnimationState.IdleLeft : AnimationState.IdleRight;
  }
  else if (getInput('A')) {
    //if (x > px) x = px;
    if (px > (0.2*width)) px -= walkSpeed;
    else cx += walkSpeed;
    
    state = AnimationState.RunLeft;
  }
  else if (getInput('D')) {
    //if (x < px) x = px;
    if (px < (0.8*width)) px += walkSpeed;
    else cx -= walkSpeed;
    state = AnimationState.RunRight;
  }
  else if (getInput(' ')) {
     if (state == AnimationState.RunLeft || state == AnimationState.IdleLeft) {
      state = AnimationState.JumpLeft;
    }
    if (state == AnimationState.RunRight || state == AnimationState.IdleRight) {
      state = AnimationState.JumpRight;
    }
  }
  else {
    if (state == AnimationState.RunLeft) state = AnimationState.IdleLeft;
    if (state == AnimationState.RunRight) state = AnimationState.IdleRight;
  }
  if (mousePressed) {
    if (state == AnimationState.RunLeft || state == AnimationState.IdleLeft) {
      state = AnimationState.AttackLeft;
    }
    if (state == AnimationState.RunRight || state == AnimationState.IdleRight) {
      state = AnimationState.AttackRight;
    }
    animationMap.get(state).reset();
  }
  for (BackgroundLayer l : bgs) {
    l.draw(cx, 0);
  }
  animationMap.get(state).draw(px,py);
}
