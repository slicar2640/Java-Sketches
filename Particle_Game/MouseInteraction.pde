import java.util.function.BiConsumer;

interface MouseInteraction {
  public void mouseClick();
  public void mouseDown();
  public void mouseUp();
  public void updateScale(float delta);
  public void updateCount(int delta);
  public void show();
}

class PushInteraction implements MouseInteraction {
  float strength, radius;
  public PushInteraction(float strength, float radius) {
    this.strength = strength;
    this.radius = radius;
  }

  public void mouseClick() {
  }

  public void mouseDown() {
    if (particleManager.paused) return;
    PVector mousePos = new PVector(mouseX, mouseY);
    HashSet<Particle> parts = particleManager.getParticlesInRange(mouseX - radius, mouseY - radius, mouseX + radius, mouseY + radius);
    for (Particle p : parts) {
      PVector diff = PVector.sub(p.pos, mousePos);
      float dis = diff.mag();
      if (dis < 0.01) return;
      if (dis < radius) {
        if (mouseButton == RIGHT) {
          p.vel.mult(0.95);
        } else {
          PVector repulsion = diff.normalize()
            .mult((1 - dis / radius) * strength);
          p.applyForce(repulsion);
        }
      }
    }
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
    radius += delta;
    if (radius < 0) radius = 0;
  }

  public void updateCount(int delta) {
    strength += delta;
  }

  public void show() {
    float strong = 10;
    stroke(map(strength, 0, -strong, 0, 255), map(strength, 0, strong, 0, 255), 0, 200);
    strokeWeight(max(2, (abs(strength) - strong) / 3));
    noFill();
    circle(mouseX, mouseY, radius * 2);
  }
}

class DeleteInteraction implements MouseInteraction {
  float radius;
  public DeleteInteraction(float radius) {
    this.radius = radius;
  }

  public void mouseClick() {
    mouseDown();
  }

  public void mouseDown() {
    PVector mousePos = new PVector(mouseX, mouseY);
    HashSet<Particle> parts = particleManager.getParticlesInRange(mouseX - radius, mouseY - radius, mouseX + radius, mouseY + radius);
    for (Particle p : parts) {
      if (p.pos.dist(mousePos) < radius) {
        particleManager.removeParticle(p);
      }
    }
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
    radius += delta;
    if (radius < 0) radius = 0;
  }

  public void updateCount(int delta) {
  }

  public void show() {
    noFill();
    stroke(255, 0, 0);
    strokeWeight(1);
    circle(mouseX, mouseY, radius * 2);
  }
}

class AddParticleInteraction implements MouseInteraction {
  Class<? extends Particle> particleClass;
  float radius;
  int count;
  public AddParticleInteraction(Class<? extends Particle> clazz, float radius, int count) {
    particleClass = clazz;
    this.radius = radius;
    this.count = count;
  }

  public void mouseClick() {
    if (mouseButton == RIGHT) {
      addParticle();
    }
  }

  public void mouseDown() {
    if (mouseButton == LEFT) {
      addParticle();
    }
  }

  public void mouseUp() {
  }

  void addParticle() {
    try {
      PVector pos = new PVector();
      for (int i = 0; i < count; i++) {
        pos.set(0, 0);
        PVector.random2D(pos);
        pos.mult(sqrt(random(1))* radius);
        pos.add(mouseX, mouseY);
        particleManager.addParticle(pos.x, pos.y, 1, particleClass);
      }
    }
    catch (ReflectiveOperationException e) {
      e.printStackTrace();
    }
  }

  public void updateScale(float delta) {
    radius += delta;
    if (radius < 0) radius = 0;
  }

  public void updateCount(int delta) {
    count += delta;
    if (count < 1) count = 1;
  }

  public void show() {
    noFill();
    stroke(255);
    strokeWeight(1);
    circle(mouseX, mouseY, radius * 2);
  }
}

class AddConstructInteraction implements MouseInteraction {
  float scale;
  int count;
  BiConsumer<Float, Integer> constructFunction;
  BiConsumer<Float, Integer> showFunction;
  public AddConstructInteraction(BiConsumer<Float, Integer> constructFunction, float scale, int count, BiConsumer<Float, Integer> showFunction) {
    this.constructFunction = constructFunction;
    this.scale = scale;
    this.count = count;
    this.showFunction = showFunction;
  }

  public void mouseClick() {
    constructFunction.accept(scale, count);
  }

  public void mouseDown() {
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
    scale += delta;
    if (scale < 0) scale = 0;
  }

  public void updateCount(int delta) {
    count += delta;
    if (count < 1) count = 1;
  }

  public void show() {
    showFunction.accept(scale, count);
  }
}

class AddStickInteraction implements MouseInteraction {
  float length;
  float stiffness;
  Particle p1;

  public AddStickInteraction(float length, float stiffness) {
    this.length = length;
    this.stiffness = stiffness;
  }

  private Particle closestParticle() {
    HashSet<Particle> neighbors = particleManager.neighborParticles(mouseX, mouseY);
    Particle closest = null;
    float closestDist = particleManager.repelDist;
    for (Particle p : neighbors) {
      if (dist(p.pos.x, p.pos.y, mouseX, mouseY) < closestDist) {
        closest = p;
        closestDist = dist(p.pos.x, p.pos.y, mouseX, mouseY);
      }
    }
    return closest;
  }

  public void mouseClick() {
    if (p1 != null) {
      Particle closest = closestParticle();
      if (closest != null && closest != p1) {
        if (shiftPressed) {
          particleManager.addStick(new Stick(p1, closest)).stiffness = stiffness;
        } else {
          particleManager.addStick(new Stick(p1, closest, length, stiffness));
        }
        p1 = null;
      }
    } else {
      Particle closest = closestParticle();
      if (closest != null) {
        p1 = closest;
      }
    }
  }

  public void mouseDown() {
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
    length += delta;
    if (length < 0) length = 0;
  }

  public void updateCount(int delta) {
    stiffness += 0.01 * delta;
    if (stiffness < 0) stiffness = 0;
  }

  public void show() {
    if (p1 != null) {
      if (shiftPressed) {
        stroke((2 - stiffness) * 255, stiffness * 255, stiffness * 255);
        strokeWeight(2);
        noFill();
        line(p1.pos.x, p1.pos.y, mouseX, mouseY);
      } else {
        PVector p2 = PVector.add(p1.pos, new PVector(mouseX, mouseY).sub(p1.pos).normalize().mult(length));
        stroke((2 - stiffness) * 255, stiffness * 255, stiffness * 255);
        strokeWeight(2);
        noFill();
        line(p1.pos.x, p1.pos.y, p2.x, p2.y);
      }

      stroke(255, 80);
      strokeWeight(5 + sin(frameCount / 15));
      noFill();
      ellipse(p1.pos.x, p1.pos.y, 20, 20);
    }


    Particle closest = closestParticle();
    if (closest != null) {
      stroke(255, 30);
      strokeWeight(6);
      noFill();
      ellipse(closest.pos.x, closest.pos.y, 20, 20);
    }
  }
}

class GrabInteraction implements MouseInteraction {
  float strength;
  HashSet<Particle> grabbedParticles = new HashSet<>();
  public GrabInteraction(float strength) {
    this.strength = strength;
  }

  private Particle closestParticle() {
    HashSet<Particle> neighbors = particleManager.neighborParticles(mouseX, mouseY);
    Particle closest = null;
    float closestDist = particleManager.repelDist;
    for (Particle p : neighbors) {
      if (dist(p.pos.x, p.pos.y, mouseX, mouseY) < closestDist) {
        closest = p;
        closestDist = dist(p.pos.x, p.pos.y, mouseX, mouseY);
      }
    }
    return closest;
  }

  public void mouseClick() {
    Particle closest = closestParticle();
    if (closest != null) {
      if (grabbedParticles.isEmpty() || shiftPressed) {
        grabbedParticles.add(closest);
      } else if (ctrlPressed) {
        if (grabbedParticles.contains(closest)) {
          grabbedParticles.remove(closest);
        } else {
          grabbedParticles.add(closest);
        }
      } else if (altPressed) {
        grabbedParticles.remove(closest);
      }
    }
  }

  public void mouseDown() {
    if (!shiftPressed) {
      PVector mousePos = new PVector(mouseX, mouseY);
      for (Particle p : grabbedParticles) {
        PVector diff = PVector.sub(mousePos, p.pos);
        p.applyForce(diff.mult(strength));
      }
      if (mouseButton == RIGHT) {
        for (Particle p : grabbedParticles) {
          p.vel.mult(0.8);
        }
      }
    }
  }

  public void mouseUp() {
    if (mouseButton == CENTER) {
      grabbedParticles.clear();
    }
  }

  public void updateScale(float delta) {
    strength += delta / 150;
  }

  public void updateCount(int delta) {
  }

  public void show() {
    for (Particle p : grabbedParticles) {
      stroke(255, 80);
      strokeWeight(5 + sin(frameCount / 15));
      noFill();
      ellipse(p.pos.x, p.pos.y, 20, 20);
    }

    Particle closest = closestParticle();
    if (closest != null) {
      stroke(255, 30);
      strokeWeight(6);
      noFill();
      ellipse(closest.pos.x, closest.pos.y, 20, 20);
    }

    float strong = 0.1;
    stroke(map(strength, 0, -strong, 0, 255), map(strength, 0, strong, 0, 255), 0);
    strokeWeight(max(1.5, (abs(strength) - strong) * 4));
    noFill();
    ellipse(mouseX, mouseY, 30, 30);
  }
}

class IgniteInteraction implements MouseInteraction {
  public void mouseClick() {
    Particle closest = particleManager.closestParticleToPoint(mouseX, mouseY);
    if (closest != null) {
      closest.litUp = true;
    }
  }

  public void mouseDown() {
  }

  public void mouseUp() {
  }

  public void updateScale(float delta) {
  }

  public void updateCount(int delta) {
  }

  public void show() {
    Particle closest = particleManager.closestParticleToPoint(mouseX, mouseY);
    if (closest != null) {
      stroke(255, 30);
      strokeWeight(6);
      noFill();
      ellipse(closest.pos.x, closest.pos.y, 20, 20);
    }

    stroke(255, 180, 0);
    strokeWeight(2);
    noFill();
    ellipse(mouseX, mouseY, 30, 30);
  }
}

class AddChainInteraction implements MouseInteraction {
  float addDist;
  float addDistSq;
  Particle particleTemplate;
  Stick stickTemplate;
  Particle lastParticle;
  int showAddDistTime = 0;
  public AddChainInteraction(float addDist, Particle particleTemplate, Stick stickTemplate) {
    this.addDist = addDist;
    addDistSq = addDist * addDist;
    this.particleTemplate = particleTemplate;
    this.stickTemplate = stickTemplate;
  }

  public void mouseClick() {
    try {
      Particle closest = particleManager.closestParticleToPoint(mouseX, mouseY);
      if (shiftPressed && closest != null) {
        lastParticle = closest;
      } else {
        lastParticle = particleManager.copyParticle(mouseX, mouseY, particleTemplate);
      }
    }
    catch (ReflectiveOperationException e) {
      println(e.toString());
    }
  }

  public void mouseDown() {
    if (new PVector(mouseX, mouseY).sub(lastParticle.pos).magSq() > addDistSq) {
      try {
        Particle nextParticle = particleManager.copyParticle(mouseX, mouseY, particleTemplate);
        particleManager.copyStick(lastParticle, nextParticle, stickTemplate);
        lastParticle = nextParticle;
      }
      catch (ReflectiveOperationException e) {
        println(e.toString());
      }
    }
  }

  public void mouseUp() {
    lastParticle = null;
  }

  public void updateScale(float delta) {
    addDist += delta;
    if (addDist < 1) addDist = 1;
    addDistSq = addDist * addDist;
    showAddDistTime = 30;
  }

  public void updateCount(int delta) {
  }

  public void show() {
    if (shiftPressed) {
      Particle closest = particleManager.closestParticleToPoint(mouseX, mouseY);
      if (closest != null) {
        stroke(255, 30);
        strokeWeight(6);
        noFill();
        ellipse(closest.pos.x, closest.pos.y, 20, 20);
      }
    }
    if(showAddDistTime > 0) {
      stroke(255);
      strokeWeight(1);
      noFill();
      ellipse(mouseX, mouseY, addDist * 2, addDist * 2);
      showAddDistTime--;
    }
    if (lastParticle != null) {
      stroke(255);
      strokeWeight(1);
      noFill();
      ellipse(lastParticle.pos.x, lastParticle.pos.y, addDist * 2, addDist * 2);
    }
  }
}
