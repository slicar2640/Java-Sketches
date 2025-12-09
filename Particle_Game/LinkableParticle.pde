class LinkableParticle extends Particle {
  public boolean isLinked = false;
  public float connectDistance = 4;
  public LinkableParticle(float x, float y, float mass, int bucketIndex, ParticleManager manager) {
    super(x, y, mass, bucketIndex, manager);
  }

  @Override
    public void repelFrom(Particle other) {
    if (other == this) return;
    if (!isLinked && other instanceof LinkableParticle) {
      LinkableParticle linkableP = (LinkableParticle) other;
      if (pos.dist(linkableP.pos) < connectDistance) {
        manager.particlesToRemove.add(this);
        manager.particlesToRemove.add(linkableP);
        linkableP.isLinked = true;
        manager.particlesToAdd.add(new LinkedParticle(this, linkableP));
      }
    } else if (other instanceof LinkedParticle) {
      LinkedParticle linkedP = (LinkedParticle) other;
      if (pos.dist(linkedP.pos) < connectDistance && !linkedP.isLinked) {
        manager.particlesToRemove.add(this);
        manager.particlesToRemove.add(linkedP);
        linkedP.isLinked = true;
        manager.particlesToAdd.add(new MultiLinkedParticle(linkedP, this));
      }
    } else if (other instanceof MultiLinkedParticle) {
      MultiLinkedParticle multiLinkedP = (MultiLinkedParticle) other;
      if (pos.dist(multiLinkedP.pos) < connectDistance) {
        multiLinkedP.addParticle(this);
        manager.particlesToRemove.add(this);
      }
    } else {
      super.repelFrom(other);
    }
  }

  public void show() {
    if(litUp) {
      stroke(255, 255, 0);
      strokeWeight(4);
      point(pos.x, pos.y);
    }
    stroke(0, 255, 0);
    strokeWeight(2);
    point(pos.x, pos.y);
  }
}
