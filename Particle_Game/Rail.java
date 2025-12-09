class Rail {
  HashSet<Particle> particles = new HashSet<>();
  ArrayList<Stick> sticks = new ArrayList<>();
  private Rail(ArrayList<Stick> sticks) {
    this.sticks = sticks;
    particles.add(sticks.get(0).p1);
    for(Stick s : sticks) {
      particles.add(s.p2);
    }
  }
  
  public static Rail from(ArrayList<Stick> sticks) {
    
  }
}
