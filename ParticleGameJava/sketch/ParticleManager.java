package sketch;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import sketch.particle.Particle;
import sketch.stick.Stick;
import sketch.util.Vector;

public class ParticleManager {
  public final Particle defaultParticle;
  public final Stick defaultStick;
  public ArrayList<HashSet<Particle>> buckets = new ArrayList<>();
  private HashSet<Stick> sticks = new HashSet<>();
  public int width, height;
  public int cols, rows;
  public int numParticles;
  public float repelStrength = 0.25f;
  public float repelDist;
  public boolean paused = false;
  public ParticleGameJava sketch;

  public ParticleManager(int width, int height, float repelDist, ParticleGameJava sketch) {
    this.width = width;
    this.height = height;
    this.repelDist = repelDist;
    this.sketch = sketch;
    cols = (int) (sketch.width / repelDist);
    rows = (int) (sketch.height / repelDist);
    for (int i = 0; i < cols * rows; i++) {
      buckets.add(new HashSet<>());
    }
    defaultParticle = new Particle(0, 0, 1, this);
    defaultStick = new Stick(defaultParticle, defaultParticle, 0, 1);
  }

  public void update(float dt) {
    repelNeighbors();
    updateSticks();
    updateParticles(dt);
    resetBuckets();
  }

  public void update(float timestep, int substeps) {
    for (int i = 0; i < substeps; i++) {
      update(timestep / substeps);
    }
  }

  private void updateParticles(float dt) {
    for (Particle p : getAllParticles()) {
      p.update(dt);
    }
  }

  private void updateSticks() {
    synchronized (sticks) {
      for (Stick stick : new HashSet<Stick>(sticks)) {
        stick.update();
      }
    }
  }

  private void resetBuckets() {
    synchronized (buckets) {
      HashSet<Particle> particles = getAllParticles();
      for (HashSet<Particle> bucket : buckets) {
        bucket.clear();
      }
      for (Particle p : particles) {
        int col = Math.clamp((int) (p.pos.x / repelDist), 0, cols - 1);
        int row = Math.clamp((int) (p.pos.y / repelDist), 0, rows - 1);
        int index = col + row * cols;
        buckets.get(index).add(p);
        p.bucketIndex = index;
      }
    }
  }

  private void repelNeighbors() {
    synchronized (buckets) {
      for (HashSet<Particle> bucket : buckets) {
        for (Particle particle : new HashSet<Particle>(bucket)) {
          HashSet<Particle> neighbors = neighborParticles(particle.pos.x, particle.pos.y);
          for (Particle other : neighbors) {
            particle.repelFrom(other);
          }
        }
      }
    }
  }

  public HashSet<Particle> neighborParticles(float x, float y) {
    x = Math.clamp(x, 0, sketch.width - 1);
    y = Math.clamp(y, 0, sketch.height - 1);
    int leftCol = (int) (x / repelDist);
    int topRow = (int) (y / repelDist);
    HashSet<Particle> neighbors = new HashSet<>();
    synchronized (buckets) {
      for (int col = Math.max(0, leftCol - 1); col <= Math.min(leftCol + 1, cols - 1); col++) {
        for (int row = Math.max(0, topRow - 1); row <= Math.min(topRow + 1, rows - 1); row++) {
          int index = col + row * cols;
          neighbors.addAll(buckets.get(index));
        }
      }
    }
    return neighbors;
  }

  public HashSet<Particle> getAllParticles() {
    HashSet<Particle> particles = new HashSet<>();
    synchronized (buckets) {
      for (HashSet<Particle> bucket : buckets) {
        particles.addAll(bucket);
      }
    }
    return particles;
  }

  public HashSet<Stick> getAllSticks() {
    synchronized (sticks) {
      return new HashSet<>(sticks);
    }
  }

  public HashSet<Particle> getParticlesInRange(float x1, float y1, float x2, float y2) {
    HashSet<Particle> particles = new HashSet<>();
    int minX = Math.clamp((int) (x1 / repelDist), 0, cols - 1);
    int maxX = Math.clamp((int) (x2 / repelDist), 0, cols - 1);
    int minY = Math.clamp((int) (y1 / repelDist), 0, rows - 1);
    int maxY = Math.clamp((int) (y2 / repelDist), 0, rows - 1);
    synchronized (buckets) {
      for (int i = minX; i <= maxX; i++) {
        for (int j = minY; j <= maxY; j++) {
          int index = i + j * cols;
          particles.addAll(buckets.get(index));
        }
      }
    }
    return particles;
  }

  public void propagateSignals() {
    synchronized (sticks) {
      for (Stick s : new HashSet<Stick>(sticks)) {
        s.propagateSignal();
      }
    }
    for (Particle p : getAllParticles()) {
      p.updateSignal();
    }
  }

  public Particle randomParticle() {
    if (numParticles == 0)
      return null;
    HashSet<Particle> allParts = getAllParticles();
    int index = (int) (Math.random() * allParts.size());
    Iterator<Particle> iter = allParts.iterator();
    for (int i = 0; i < index; i++) {
      iter.next();
    }
    return (Particle) iter.next();
  }

  public <T extends Stick> T addStick(T s) {
    synchronized (sticks) {
      sticks.add(s);
    }
    return s;
  }

  public <T extends Stick> T addStick(Particle p1, Particle p2, Class<T> clazz) throws ReflectiveOperationException {
    Constructor<T> constructor = clazz.getConstructor(Particle.class, Particle.class);
    T stick = constructor.newInstance(p1, p2);
    addStick(stick);
    return stick;
  }

  public HashSet<Stick> sticksConnectedTo(Particle p) {
    synchronized (sticks) {
      return (HashSet<Stick>) sticks.stream().filter(s -> s.p1 == p || s.p2 == p).collect(Collectors.toSet());
    }
  }

  public <T extends Particle> T addParticle(T p) {
    synchronized (buckets) {
      buckets.get(p.bucketIndex).add(p);
    }
    numParticles++;
    return p;
  }

  public <T extends Particle> T addParticle(float x, float y, float mass, Class<T> clazz)
      throws ReflectiveOperationException {
    Constructor<T> constructor = clazz.getConstructor(float.class, float.class, float.class, getClass());
    T p = constructor.newInstance(x, y, mass, this);
    addParticle(p);
    return p;
  }

  public Particle addParticle(float x, float y, float mass) {
    Particle p = new Particle(x, y, mass, this);
    return addParticle(p);
  }

  public Particle addParticle(float x, float y) {
    return addParticle(x, y, 1);
  }

  public Particle addParticle(Vector p) {
    return addParticle(p.x, p.y);
  }

  public void addParticles(Collection<Particle> parts) {
    synchronized (buckets) {
      for (Particle p : parts) {
        buckets.get(p.bucketIndex).add(p);
        numParticles++;
      }
    }
  }

  public Particle closestParticleToPoint(float x, float y) {
    HashSet<Particle> neighbors = neighborParticles(x, y);
    Particle closest = null;
    float closestDist = repelDist;
    for (Particle p : neighbors) {
      if (Vector.dist(p.pos.x, p.pos.y, x, y) < closestDist) {
        closest = p;
        closestDist = Vector.dist(p.pos.x, p.pos.y, x, y);
      }
    }
    return closest;
  }

  public <T extends Particle> T closestParticleToPoint(float x, float y, Class<T> clazz) {
    HashSet<Particle> neighbors = neighborParticles(x, y);
    T closest = null;
    float closestDist = repelDist;
    for (Particle p : neighbors) {
      if (clazz.isInstance(p)) {
        if (Vector.dist(p.pos.x, p.pos.y, x, y) < closestDist) {
          closest = clazz.cast(p);
          closestDist = Vector.dist(p.pos.x, p.pos.y, x, y);
        }
      }
    }
    return closest;
  }

  public Stick closestStickToPoint(float x, float y, Vector closestPoint) {
    Vector mouse = new Vector(sketch.mouseX, sketch.mouseY);
    Stick closestStick = null;
    float shortestDist = width * 2;
    synchronized (sticks) {
      for (Stick stick : sticks) {
        Vector stickClosestPoint = stick.closestPoint(mouse);
        float dist = stickClosestPoint.dist(mouse);
        if (dist < shortestDist) {
          closestStick = stick;
          closestPoint.set(stickClosestPoint);
          shortestDist = dist;
        }
      }
    }
    return closestStick;
  }

  public <T extends Stick> T closestStickToPoint(float x, float y, Vector closestPoint, Class<T> clazz) {
    HashSet<T> filtered = new HashSet<>();
    synchronized (sticks) {
      for (Stick stick : sticks) {
        if (clazz.isInstance(stick)) {
          filtered.add(clazz.cast(stick));
        }
      }
    }
    if (filtered.isEmpty())
      return null;
    Vector mouse = new Vector(sketch.mouseX, sketch.mouseY);
    T closestStick = null;
    float shortestDist = width * 2;
    synchronized (sticks) {
      for (T stick : filtered) {
        Vector stickClosestPoint = stick.closestPoint(mouse);
        float dist = stickClosestPoint.dist(mouse);
        if (dist < shortestDist) {
          closestStick = stick;
          closestPoint.set(stickClosestPoint);
          shortestDist = dist;
        }
      }
    }
    return closestStick;
  }

  public HashSet<Stick> reconnectSticks(Particle oldP, Particle newP) {
    synchronized (sticks) {
      HashSet<Stick> reconnected = new HashSet<>();
      for (Stick stick : sticks) {
        if (stick.reconnect(oldP, newP)) {
          reconnected.add(stick);
        }
      }
      return reconnected;
    }
  }

  public void removeParticle(Particle p) {
    synchronized (buckets) {
      buckets.get(p.bucketIndex).remove(p);
      p.delete();
      numParticles--;
    }
    synchronized (sticks) {
      for (Stick stick : new HashSet<Stick>(sticks)) {
        if (stick.p1 == p || stick.p2 == p) {
          sticks.remove(stick);
        }
      }
    }
  }

  public void removeParticles(Collection<Particle> parts) {
    for (Particle p : parts) {
      removeParticle(p);
    }
  }

  public void removeStick(Stick s) {
    synchronized (sticks) {
      sticks.remove(s);
    }
  }

  public Particle copyParticle(float x, float y, Particle template) throws ReflectiveOperationException {
    Particle p = addParticle(x, y, template.mass, template.getClass());
    p.isStatic = template.isStatic;
    return p;
  }

  public Stick copyStick(Particle p1, Particle p2, Stick template) throws ReflectiveOperationException {
    Stick s = addStick(p1, p2, template.getClass());
    s.stiffness = template.stiffness;
    s.breakLengthRatio = template.breakLengthRatio;
    return s;
  }

  public int getBucketIndex(float x, float y) {
    int col = Math.clamp((int) (x / repelDist), 0, cols - 1);
    int row = Math.clamp((int) (y / repelDist), 0, rows - 1);
    return col + row * cols;
  }

  public int numSticks() {
    synchronized (sticks) {
      return sticks.size();
    }
  }

  public void show(DrawManager dm) {
    synchronized (sticks) {
      for (Stick stick : sticks) {
        stick.show(dm);
      }
    }
    synchronized (buckets) {
      for (HashSet<Particle> bucket : buckets) {
        for (Particle particle : bucket) {
          particle.show(dm);
        }
      }
    }
  }
}