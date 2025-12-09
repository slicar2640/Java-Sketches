class PlantStick extends Stick {
  PlantParticle p1, p2;
  public PlantStick(PlantParticle p1, PlantParticle p2) {
    super(p1, p2);
    this.p1 = p1;
    this.p2 = p2;
  }
  
  public PlantStick(Particle p1, Particle p2) {
    this((PlantParticle) p1, (PlantParticle) p2);
  }

  @Override
    public void breakSelf() {
    p1.connected--;
    p2.connected--;
  }

  @Override
    public void show() {
    if (litUp) {
      strokeWeight(3);
    } else {
      strokeWeight(1);
    }
    stroke(0, 185, 30);
    line(p1.pos.x, p1.pos.y, p2.pos.x, p2.pos.y);
  }
}
