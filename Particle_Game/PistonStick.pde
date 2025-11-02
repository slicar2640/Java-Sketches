class PistonStick extends Stick {
  float poweredStretchFactor;
  float poweredRestLength, unpoweredRestLength;
  float stretchSpeed;
  boolean powered = false;
  public PistonStick(Particle p1, Particle p2, float restLength, float stiffness, float poweredStretchFactor, float stretchSpeed, float breakLength) {
    super(p1, p2, restLength, stiffness, breakLength);
    this.poweredStretchFactor = poweredStretchFactor;
    unpoweredRestLength = restLength;
    poweredRestLength = restLength * poweredStretchFactor;
    this.stretchSpeed = stretchSpeed;
  }
  
  public PistonStick(Particle p1, Particle p2) {
    this(p1, p2, p1.pos.dist(p2.pos), 0.95, 2, 0.01, -1);
  }
  
  @Override
  public void propagateSignal() {
    super.propagateSignal();
    if(litUp) {
      powered = !powered;
    }
  }
  
  @Override
  public void update() {
    super.update();
    float targetRestLength = powered ? poweredRestLength : unpoweredRestLength;
    if(abs(restLength - targetRestLength) < stretchSpeed) {
      restLength = targetRestLength;
    }
    if(targetRestLength < restLength) {
      restLength -= stretchSpeed;
    } else if(targetRestLength > restLength) {
      restLength += stretchSpeed;
    }
  }
}
